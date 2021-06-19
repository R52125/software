import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import server.manager.CustomerManager;
import server.manager.RequestManager;
import server.manager.StateManager;
import server.mapper.CustomerMapper;
import server.mapper.ReportorMapper;
import server.mapper.RoomStateMapper;
import server.simpleclass.Master;

import javax.swing.Timer;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//该注解用来指定一个URI，客户端可以通过这个URI来连接到WebSocket。类似Servlet的注解mapping。无需在web.xml中配置。
@ServerEndpoint("/ws/")
@Component
public class WebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    private static int RequestCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    //private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<String, WebSocket> webSocketSet = new ConcurrentHashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private String room_id = "";
    private String user_id = "";
    private Session session;
    List<JSONObject> WaitingInfo = new LinkedList<>();
    List<String> User_id;
    HashMap<String, Date> SlaveTimes = new HashMap<>();
    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen( Session session) {
        this.room_id = GetUtil(session.getRequestParameterMap().get("room_id").toString());
        this.user_id = GetUtil(session.getRequestParameterMap().get("user_id").toString());
        //this.user_id = GetUserId(id);
        /*this.room_id=room_id;
        this.room_id=user_id;*/

        System.out.println(room_id);
        System.out.println(user_id);
        this.session = session;
        webSocketSet.put(room_id, this);     //加入set中
        //Clients.put(user_id,session);
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount() + " id为： " + user_id);
        SendFrequency(Master.getQuery());
        SendState2Slave();
        if(!room_id.equals("000"))
        {
            StateManager.getInstance().getState(room_id).setIsOn(1);
            CustomerManager.getInstance().getCustomerMap().put(room_id,user_id);
            RoomStateMapper.insert(room_id);
        }
        if (webSocketSet.size() == 1) {
            //System.out.println(111);
            Runnable r = () ->//给前端发房间状态的线程
            {
                while (true) {
                    try {
                        SendState2View();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            };
            new Thread(r).start();

            Runnable p = () ->//给从机发送风包的线程
            {

                try {
                    while (true) {
                        SendWind2Slave();
                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            new Thread(p).start();
            //MonitorSlaveIsLive();
        }

        //SendState2Slave();
        //sendtoall();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this.room_id);    //从set中删除
        if(webSocketSet.size()>0)
            subOnlineCount();            //在线数减1
        System.out.println(this.user_id);
        if (RequestManager.getInstance().getRequest(room_id) != null)
        {
            String stop_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            RequestManager.getInstance().removeRequest(room_id, stop_time);
        }
        if(room_id!=null)
            if(StateManager.getInstance().getState(room_id)!=null)
                StateManager.getInstance().getState(room_id).setIsOn(0);
        System.out.println("有一连接关闭！当前客户端人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println(message);
        JSONObject info = new JSONObject(message);
        int type = info.getInt("event_id");
        JSONObject Data = info.getJSONObject("data");
        HandlePackage(type,Data,user_id);

        /*try {
            sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public static void send2User(String room_id, String message) throws IOException {
        System.out.println(message);
        if(webSocketSet.get(room_id)!=null&&webSocketSet.size()>0)
        {
            /*System.out.println(room_id);
            System.out.println(webSocketSet.get(room_id).session);

             */
            webSocketSet.get(room_id).session.getBasicRemote().sendText(message);
        }
    }

    public void sendMessage(String message) throws IOException {
        System.out.println(message);
        this.session.getBasicRemote().sendText(message);
    }

    /*
     * 发送查询信息给所有从机
     * @param message
     * @throws IOException
     */
    public static void SendFrequency(int interval) {
        if(webSocketSet.size()>0)
        {
            for (String key : webSocketSet.keySet()) {
                try {
                    JSONObject package_info = new JSONObject();
                    JSONObject data_info = new JSONObject();
                    package_info.put("event_id", 6);
                    data_info.put("interval", interval);
                    package_info.put("data", data_info);
                    send2User(key,String.valueOf(package_info));
                    /*if(webSocketSet.get(key)!=null)
                        webSocketSet.get(key).sendMessage();
                     */
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void SendState2Slave() {
        JSONObject package_info = new JSONObject();
        JSONObject data_info = new JSONObject();
        package_info.put("event_id", 1);
        data_info.put("mode", Master.getMode());
        data_info.put("temp", Master.getTemp());
        package_info.put("data",data_info);
        try {
            for (String key : webSocketSet.keySet()) {
                send2User(key,String.valueOf(package_info));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 处理来自客户端端的所有消息
     * 前端也当作客户端之一
     */
    public void HandlePackage(int type, JSONObject Data, String user_id) {
        JSONObject data = Data;
        JSONObject package_info = new JSONObject();
        JSONObject data_info = new JSONObject();
        switch (type) {
            //此为来自从机的信息，把前端也视为从机处理
            case 2://从机改变风速
                if (Master.getIsOn() == 1) {
                    StateManager.getInstance().getState(this.room_id).setSpeed(data.getInt("speed"));
                    /*if (RequestCount < 3 && RequestManager.getInstance().isValid(this.room_id)) {
                        if (data.getInt("mode") == Master.getMode()) {
                            package_info.put("event_id", 3);
                            data_info.put("temp", Master.getTemp());
                            data_info.put("speed", data.getInt("speed"));
                            data_info.put("mode", Master.getMode());
                            data_info.put("cost", RequestManager.getInstance().getRequestMap().get("cost"));
                            package_info.put("data", data_info);
                            try {
                                send2User(String.valueOf(package_info),user_id);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            addRequestCount();
                        }
                    } else {
                        WaitingInfo.add(data);
                        User_id.add(user_id);
                    }*/
                    break;
                } else
                    break;
            case 4://从机请求停止送风，生成一条账单记录
                if (Master.getIsOn() == 1) {
                    String stop_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    RequestManager.getInstance().removeRequest(this.room_id, stop_time);
                    StateManager.getInstance().getState(this.room_id).setSpeed(-1);
                    package_info.put("event_id", 5);
                    data_info.put("cost", RequestManager.getInstance().getRequestMap().get("cost"));
                    package_info.put("data", data_info);
                    try {
                        sendMessage(String.valueOf(package_info));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (RequestCount < 3 && !WaitingInfo.isEmpty()) {
                        HandlePackage(2, WaitingInfo.get(0), User_id.get(0));
                        WaitingInfo.remove(0);
                        User_id.remove(0);
                        subRequestCount();
                    }
                    break;
                }
                break;
            case 7:
                SlaveTimes.put(user_id, new Date());
                int mode = data.getInt("mode");
                RequestManager.getInstance().getIsValid().replace(room_id,1);
                StateManager.getInstance().updateState(data, this.room_id);
                if(mode == 2)//不送风请求
                {
                    if (RequestManager.getInstance().getRequest(room_id) != null)
                    {
                        String stop_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        RequestManager.getInstance().removeRequest(room_id, stop_time);
                    }
                    RequestManager.getInstance().getIsValid().replace(room_id,0);
                }
                /*package_info.put("event_id", 17);
                data_info.put("Room_id", this.room_id);
                data_info.put("cstate", 1);
                data_info.put("Rmode", data.getInt("mode"));
                data_info.put("temp", data.getInt("temp"));
                data_info.put("wind", data.getInt("speed"));
                package_info.put("data", data_info);
                try {
                    send2User("001", String.valueOf(package_info));
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                break;
            case 8:
                if (RequestManager.getInstance().getRequest(this.room_id) != null) {
                    String stop_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    RequestManager.getInstance().removeRequest(this.room_id, stop_time);
                }
                StateManager.getInstance().getState(this.room_id).setIsOn(data.getInt("IsOn"));
                //以下是处理前端的包
            case 11:
                Master.setIsOn(data.getInt("onoff"));
                package_info.put("event_id", 11);
                data_info.put("ack", 1);
                package_info.put("data", data_info);
                try {
                    sendMessage(String.valueOf(package_info));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 12:
                try {
                    boolean flag = CustomerMapper.insert(data);//当前客户
                    if(flag){
                        CustomerMapper.insertHistory(data);
                        package_info.put("data", data_info);
                        package_info.put("event_id", 12);
                        data_info.put("ack", 1);

                        try {
                            sendMessage(String.valueOf(package_info));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        CustomerMapper.insertHistory(data);
                        package_info.put("event_id", 12);
                        data_info.put("ack", 0);
                        package_info.put("data", data_info);
                        try {
                            sendMessage(String.valueOf(package_info));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLServerException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case 13:
                try {
                    boolean flag = CustomerMapper.delete(data);
                    if(flag == true){
                        package_info.put("event_id", 13);
                        data_info.put("ack", 1);
                        package_info.put("data", data_info);
                        try {
                            sendMessage(String.valueOf(package_info));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                   else{
                        package_info.put("event_id", 13);
                        data_info.put("ack", 0);
                        package_info.put("data", data_info);
                        try {
                            sendMessage(String.valueOf(package_info));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLServerException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case 14:
                String room_id = data.getString("Room_id");
                String sTime = data.getString("startdata");
                String etime = data.getString("enddata");
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date End = simpleFormat.parse(etime);
                    Calendar cld = Calendar.getInstance();
                    cld.setTime(End);
                    cld.add(Calendar.DATE,1);
                    End = cld.getTime();
                    String nextDay = simpleFormat.format(End);
                    package_info.put("event_id", 14);
                    data_info.put("Room_id", room_id);
                    //int uptimes = StateManager.getInstance().getState(room_id).getUptimes();
                    int uptimes = RoomStateMapper.getTimes(sTime,nextDay,room_id);
                    //int uptimes =
                    float cost = ReportorMapper.getSum(sTime, nextDay, room_id);
                    List<JSONObject> the_data = null;
                    the_data = ReportorMapper.GetReportor(sTime, nextDay, room_id);
                    for (int i = 0; i < the_data.size(); i++) {
                        the_data.get(i).put("id",i+1);
                    }
                    data_info.put("up_times", uptimes);
                    data_info.put("total_cost", cost);
                    data_info.put("temp", the_data);
                    package_info.put("data", data_info);
                    try {
                        send2User("000",String.valueOf(package_info));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 15:
                Master.setMode(data.getInt("mode"));
                Master.setQuery(data.getInt("frequency"));
                Master.setTemp(data.getInt("temp"));
                package_info.put("event_id", 15);
                data_info.put("ack", 1);
                package_info.put("data", data_info);
                SendFrequency(data.getInt("frequency"));
                SendState2Slave();
                try {
                    sendMessage(String.valueOf(package_info));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 16:
                boolean flag = Master.login(data);
                if(flag){
                    package_info.put("event_id", 16);
                    data_info.put("ack", 1);
                    package_info.put("data", data_info);
                }
                else
                {
                    package_info.put("event_id", 16);
                    data_info.put("ack", 0);
                    package_info.put("data", data_info);
                }
                try {
                    sendMessage(String.valueOf(package_info));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /*
     * 以下数量计算
     * RequestCount为从机请求的数量
     * OnlineCount为连接的客户端的数量
     */
    public static synchronized void addRequestCount() {
        WebSocket.RequestCount++;
    }

    public static synchronized void subRequestCount() {
        WebSocket.RequestCount--;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocket.onlineCount--;
    }

    /*
     * 以下方法均为定时向前端/从机发送信息
     * SendState2View为定时发送给前端房间状态的信息
     * SendWind2Slave为定时给从机送风
     */
    public static void SendState2View() {
        JSONObject package_info = new JSONObject();
        JSONObject data_info = new JSONObject();
        List<JSONObject> RoomState = new LinkedList<>();
        package_info.put("event_id", 17);
        List<String> Room = CustomerMapper.getRoomId();
        for (int i = 0; i < Room.size(); i++) {
            JSONObject temp = new JSONObject();
            temp.put("Room_id", Room.get(i));
            temp.put("cstate", StateManager.getInstance().getState(Room.get(i)).getisOn());
            temp.put("Rmode", StateManager.getInstance().getState(Room.get(i)).getMode());
            temp.put("temp", StateManager.getInstance().getState(Room.get(i)).getCurrentTemperature());
            temp.put("wind", StateManager.getInstance().getState(Room.get(i)).getSpeed());
            temp.put("ss",StateManager.getInstance().getState(Room.get(i)).getWind());
            RoomState.add(temp);
        }
        data_info.put("data", RoomState);
        package_info.put("data", RoomState);
        try {
            //System.out.println(package_info);
            if(getOnlineCount()>0)
                send2User("000", String.valueOf(package_info));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SendWind2Slave() {
        JSONObject package_info = new JSONObject();
        JSONObject data_info = new JSONObject();
        List<String> Room = CustomerMapper.getRoomId();
        int WindPack=0;
        assert Room != null;
        for (String s : Room) {
            //System.out.println(s);
            if (WindPack < 3) {
                if (RequestManager.getInstance().Valid(s)==1) {
                    StateManager.getInstance().getState(s).setWind(1);
                    if (RequestManager.getInstance().getRequest(s) == null) {
                        String start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        RequestManager.getInstance().newRequest(s, start_time, StateManager.getInstance().getStateMap().get(s).getCurrentTemperature());
                        //System.out.println(formatter.format(start_time));
                    }
                    NumberFormat nf3 = NumberFormat.getNumberInstance();
                    nf3.setMaximumFractionDigits(2);
                    package_info.put("event_id", 3);
                    data_info.put("temp", Master.getTemp());
                    data_info.put("speed", StateManager.getInstance().getState(s).getSpeed());
                    data_info.put("mode", Master.getMode());
                    data_info.put("cost", Float.parseFloat(nf3.format(RequestManager.getInstance().getRequest(s).getCost())));
                    package_info.put("data", data_info);
                    try {
                        send2User(s, String.valueOf(package_info));
                        System.out.println(CustomerManager.getInstance().getUserId(s));
                        RequestManager.updateRequestMap(s,StateManager.getInstance().getState(s).getSpeed(),1);
                        WindPack++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    StateManager.getInstance().getState(s).setWind(0);
                    /*NumberFormat nf3 = NumberFormat.getNumberInstance();
                    nf3.setMaximumFractionDigits(2);
                    package_info.put("event_id", 3);
                    data_info.put("temp", Master.getTemp());
                    data_info.put("speed", -1);
                    data_info.put("mode", Master.getMode());
                    data_info.put("cost", 0);
                    package_info.put("data", data_info);
                    try {
                        if(!s.equals("000"))
                        {
                            send2User(s, String.valueOf(package_info));
                            //System.out.println(CustomerManager.getInstance().getUserId(s));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                     */
                }


            }
            else {
                StateManager.getInstance().getState(s).setWind(0);
                if (RequestManager.getInstance().Valid(s) == 1) {
                    if (RequestManager.getInstance().getRequest(s) == null) {
                        String start_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        RequestManager.getInstance().newRequest(s, start_time, StateManager.getInstance().getStateMap().get(s).getCurrentTemperature());
                        //System.out.println(formatter.format(start_time));
                    }
                    NumberFormat nf3 = NumberFormat.getNumberInstance();
                    nf3.setMaximumFractionDigits(2);
                    package_info.put("event_id", 3);
                    data_info.put("temp", Master.getTemp());
                    data_info.put("speed", -1);
                    data_info.put("mode", Master.getMode());
                    data_info.put("cost", 0);
                    package_info.put("data", data_info);
                    try {
                        //if(s != null)
                        if (!s.equals("000")) {
                            send2User(s, String.valueOf(package_info));
                            //System.out.println(CustomerManager.getInstance().getUserId(s));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void sendtoall() {
        System.out.println("hello");
        for (String key : webSocketSet.keySet()) {
            try {
                webSocketSet.get(key).sendMessage("hello");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void MonitorSlaveIsLive() {
        Timer t = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long now = new Date().getTime();
                List<String> Room = CustomerMapper.getRoomId();
                for (int i = 0; i < Room.size(); i++) {
                    if(StateManager.getInstance().getState(Room.get(i)).getisOn() == 1)
                    {
                        if(SlaveTimes.get(Room.get(i))!=null)
                            if(now-SlaveTimes.get(Room.get(i)).getTime() > 5000)
                            {
                                if (RequestManager.getInstance().getRequest(Room.get(i)) != null)
                                {
                                    String stop_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                    RequestManager.getInstance().removeRequest(Room.get(i),stop_time);
                                    StateManager.getInstance().getState(Room.get(i)).setIsOn(0);
                                    StateManager.getInstance().getState(Room.get(i)).addUptimes();
                                }
                            }
                    }
                }
                // code below is not a good way
            }
        });
        t.start();
    }
//?room_id=002&?user_id=administer
public static String GetRoomId(String message){
    char[] info = message.toCharArray();
    String UserId = "";
    //int index = message.lastIndexOf("?room_id=");
    int i = 9;
    while(info[i] != '&'){
        UserId = UserId + info[i];
        i++;
    }
    return UserId;
}

    public static String GetUserId(String message){
        char[] info = message.toCharArray();
        String UserId = "";
        int index = message.indexOf("&")+9;
        while(index<message.length()){
            UserId = UserId + info[index];
            index++;
        }
        return UserId;
    }

    public static String GetUtil(String A){
        char[] ch = A.toCharArray();
        String re = "";
        int i=1;
        while(ch[i] != ']')
        {
            re = re + ch[i];
            i++;
        }
        return re;
    }
}
