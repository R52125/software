package server.manager;

import server.DataBaseConnect;
import server.simpleclass.Customer;

import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerManager{
    private static CustomerManager customerManager = null;

    // 当前连接在服务器上的用户的map, key为room_id, value为user_id
    private HashMap<String, String> customerMap = new HashMap<>();
//    private Map<Socket, Long> sockets = new HashMap<>();
    private int querySeq = 0;
    private int billSeq = 0;
    private final long keepAliveInterval = 60;

    private CustomerManager(){
    }

    public static CustomerManager getInstance() {
        if (customerManager == null) customerManager = new CustomerManager();
        return customerManager;
    }

    /*
     * login 处理登录信息, 给从机返回结果
     * @param customer 登录的用户
     * @param socket 用户的socket
     */

    /*
     * @Description isRegistered 判断用户是否登记, 房间号和身份证号是否对应
     * @param customer 待判断的用户
     * @return boolean 用户是否在数据库中, room_id 和 id 是否对应
     */
    private boolean isRegistered(Customer customer) {
        String roomId = customer.getRoom_id();
        String id = customer.getId();
        ArrayList<HashMap<String, String>> result = DataBaseConnect.
                query("select id from customer where room_id = " + "'" + roomId + "'");
        
        if (result != null && result.size() != 0 && id.equals(result.get(0).get("id").trim()))
                return true;
        return false;
    }

    public HashMap getCustomerMap(){
        return customerMap;
    }

    public String getRoomId(Socket socket) {
        return customerMap.get(socket);
    }

    public String getUserId(String RoomId){
        HashMap A= getCustomerMap();
        String userId = (String) A.get(RoomId);
        return userId;
    }


    public String removeCustomer(Socket socket) {
        String room_id = customerMap.remove(socket);
        StateManager.getInstance().removeRoom(room_id);
        return room_id;
    }

    public boolean Register(Customer customer) {
        String room_id = customer.getRoom_id();
        Connection connection = DataBaseConnect.getConnection();
        if (connection != null) {
            Statement statement = null;
            ResultSet res = null;
            int is_booked = 1;
            try {
                statement = connection.createStatement();
                res = statement.executeQuery(
                        String.format(
                                "select is_booked from room where room_id = '%s'", room_id
                        ));
                if (res.next()) {
                    is_booked = res.getInt("is_booked");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (res != null) res.close();
                    if (statement != null) statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (is_booked == 1) return false;
        }
        boolean result = false;
        connection = DataBaseConnect.getConnection();
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        try {
            connection.setAutoCommit(false);
            String sql1 = String.format(
                    "update room set is_booked = 1 where room_id = '%s'",
                    room_id
            );
            String sql2 = String.format(
                    "insert customer(room_id, id) " +
                            "values('%s', '%s')",    //id的%s加了单引号
                    customer.getRoom_id(), customer.getId()
            );
            ps1 = connection.prepareStatement(sql1);
            ps1.execute();
            ps2 = connection.prepareStatement(sql2);
            ps2.execute();
            connection.commit();
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (ps1 != null) ps1.close();
                if (ps2 != null) ps2.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public List<String> getAvailableRoom() {
        Connection connection = DataBaseConnect.getConnection();
        List<String> availableRoomList = null;
        if (connection != null) {
            Statement statement = null;
            ResultSet res = null;
            try {
                statement = connection.createStatement();
                res = statement.executeQuery("select room_id from room where is_booked = 0");
                availableRoomList = new ArrayList<>();
                while (res.next()) {
                    availableRoomList.add(res.getString("room_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (res == null) res.close();
                    if (statement == null) statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return availableRoomList;
            }
        } else return availableRoomList;
    }

    private boolean checkoutDatabase(String room_id) {
        boolean result = false;
        Connection connection = DataBaseConnect.getConnection();
        if (connection != null) {
            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            try {
                connection.setAutoCommit(false);
                String sql1 = String.format(
                        "update room set is_booked = 0 where room_id = '%s'",
                        room_id
                );
                String sql2 = String.format(
                        "delete customer where room_id = '%s'",
                        room_id
                );
                ps1 = connection.prepareStatement(sql1);
                ps1.execute();
                ps2 = connection.prepareStatement(sql2);
                ps2.execute();
                connection.commit();
                result = true;
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    if (ps1 != null) ps1.close();
                    if (ps2 != null) ps2.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

//    public synchronized void refreshSocket(Socket socket) {
//        if (sockets.containsKey(socket)) {
//            sockets.replace(socket, System.currentTimeMillis());
//        }
//    }
}
