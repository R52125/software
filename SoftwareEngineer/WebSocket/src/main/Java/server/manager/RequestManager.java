package server.manager;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import server.mapper.CustomerMapper;
import server.mapper.RequestMapper;
import server.simpleclass.Master;
import server.simpleclass.Request;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

public class RequestManager {
    private static RequestManager requestManager = null;

    // 返回当前正在进行的温控请求map, key 为 room_id, value 为 Request
    private HashMap<String, Request> requestHashMap = new HashMap<>();
    private static HashMap<String,Integer> IsValid = initValid();

    public static RequestManager getInstance() {
        if (requestManager == null) requestManager = new RequestManager();
        return requestManager;
    }

    public void newRequest(String room_id, String start_time, float start_temp){
        Request nr = new Request(room_id,start_time,start_temp);
        requestHashMap.put(room_id,nr);
    }

    public static HashMap<String,Integer>initValid(){
        HashMap<String,Integer> init = new HashMap<>();
        List<String> Room = CustomerMapper.getRoomId();
        for(int i=0; i<Room.size(); i++)
        {
            //System.out.println(R);
            //System.out.println(Room);
            init.put(Room.get(i), 0);
        }
        return init;
    }
/*
 此方法用于每次向从机送风是更新表中的账单，单位以秒计，times默认为一秒
 */
    public static void updateRequestMap(String room_id, int speed, int times){
        switch (speed){
            case 1:
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);
                float cost1 = (float) 0.01*Master.getPrice()*times+RequestManager.getInstance().getRequest(room_id).getCost();
                cost1 = Float.parseFloat(nf.format(cost1));
                BigDecimal b  =   new  BigDecimal(cost1);
                cost1   =  b.setScale(2,  BigDecimal.ROUND_HALF_UP).floatValue();
                float electricity1 = (float) 0.01*times+RequestManager.getInstance().getRequest(room_id).getElectricity();
                int wind1 = 2*times+RequestManager.getInstance().getRequest(room_id).getWindPower();
                RequestManager.getInstance().getRequest(room_id).setCost(cost1);
                RequestManager.getInstance().getRequest(room_id).setElectricity(electricity1);
                RequestManager.getInstance().getRequest(room_id).setWindPower(wind1);
                System.out.println(cost1);
                break;
            case 0:
                NumberFormat nf2 = NumberFormat.getNumberInstance();
                nf2.setMaximumFractionDigits(2);
                float c2 = 0.008f*Master.getPrice()+RequestManager.getInstance().getRequest(room_id).getCost();
                c2 = Float.parseFloat(nf2.format(c2));
                float e2 = 0.0008f+RequestManager.getInstance().getRequest(room_id).getElectricity();
                int w2 = 1+RequestManager.getInstance().getRequest(room_id).getWindPower();
                RequestManager.getInstance().getRequest(room_id).setCost(c2);
                RequestManager.getInstance().getRequest(room_id).setElectricity(e2);
                RequestManager.getInstance().getRequest(room_id).setWindPower(w2);
                break;
            case 2:
                NumberFormat nf3 = NumberFormat.getNumberInstance();
                nf3.setMaximumFractionDigits(2);
                float c3 = 0.012f*Master.getPrice()+RequestManager.getInstance().getRequest(room_id).getCost();
                c3 = Float.parseFloat(nf3.format(c3));
                float e3 = 0.012f+RequestManager.getInstance().getRequest(room_id).getElectricity();;
                int w3 = 3+RequestManager.getInstance().getRequest(room_id).getWindPower();
                RequestManager.getInstance().getRequest(room_id).setCost(c3);
                RequestManager.getInstance().getRequest(room_id).setElectricity(e3);
                RequestManager.getInstance().getRequest(room_id).setWindPower(w3);
        }
    }

    public boolean isValid(String room_id){
        if(StateManager.getInstance().getState(room_id).getMode()!= Master.getMode())
            return false;
       /* if(StateManager.getInstance().getState(room_id).getCurrentTemperature()==StateManager.getInstance().getState(room_id).getTargetTemperature())
            return false;*/
        if(StateManager.getInstance().getState(room_id).getisOn()==0)
            return false;
        /*if(Master.getIsOn()==0)
            return  false;
         */
        else
            return true;
    }

    public static void updata(String room_id, int Valid){
        IsValid.replace(room_id,Valid);
    }
    /*private boolean isValid(Request request){
        // 需要制热
        if (SummerModeCheck(request.getTargetTemp()) && 
        		new Configuration().getSummer().equals(Master.getMode())) return true;
        // 需要制冷
        if (WinterModeCheck(request.getTargetTemp()) &&
        		new Configuration().getWinter().equals(Master.getMode())) return true;
        return false;
    }*/
    
    public boolean SummerModeCheck(int temp)
    {
    	if(temp > 24 && temp < 31)
    		return true;
    	return false;
    }
    
    public boolean WinterModeCheck(int temp)
    {
    	if(temp > 18 && temp < 25)
    		return true;
    	return false;
    }

    public boolean removeRequest(String room_id, String end_time) {
        float temp = StateManager.getInstance().getState(room_id).getCurrentTemperature();
        RequestManager.getInstance().getRequest(room_id).setEndTemp(temp);
        RequestManager.getInstance().getRequest(room_id).setStopTime(end_time);
        boolean result = true;
        try {
            result=RequestMapper.insert(RequestManager.requestManager.getRequest(room_id));
        } catch (SQLServerException throwables) {
            throwables.printStackTrace();
        }
        requestHashMap.remove(room_id);
        /*RoomState state = StateManager.getInstance().getState(request.getRoomId());
            request.setStopTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            int seconds;
            try {
                long from = simpleFormat.parse(request.getStartTime()).getTime();
                long to = simpleFormat.parse(request.getStopTime()).getTime();
                seconds = (int) ((to - from) / 1000);
            } catch (ParseException e) {
                e.printStackTrace();
                seconds = 0;
            }
            //request.setElectricity(getWindElectricity(request.getWindPower()) * seconds / 60);
            if (state != null) request.setEndTemp(state.getCurrentTemperature());
            try {
                if (new RequestMapper().insert(request))
                    System.out.println("request存入数据库:" + request.toString());
            } catch (SQLServerException e) {
                e.printStackTrace();
            }
        }*/
        return result;
    }

    /*
     * @Description getRequest 返回指定房间当前的温控请求
     * @Param room_id 房间号
     * @Return Request 对应房间号的Request, 如果当前没有Request则返回null
     */
    public Request getRequest(String room_id) { return requestHashMap.get(room_id); }

    /*
     * @Description getRequestMap 返回当前正在进行的温控请求map
     * @Param
     * @Return HashMap<String, Request>
     */
    public HashMap<String, Request> getRequestMap() {
        return requestHashMap;
    }

    public HashMap<String, Integer> getIsValid() {
        return IsValid;
    }

    public int Valid(String room_id){
        if(getIsValid().get(room_id)==1 && isValid(room_id))
            return 1;
        else
            return 0;
    }

    /*private float getWindElectricity(String wind_power) {
        if ("high".equals(wind_power)) return server.getHigh();
        if ("medium".equals(wind_power)) return server.getMedium();
        else return server.getLow();
    }

     */
}
