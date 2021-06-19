package server.mapper;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.json.JSONObject;
import server.DataBaseConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerMapper{

    public static boolean insert(Object o) throws SQLServerException {
        //Customer customer = (Customer)o;
        JSONObject customer = (JSONObject)o;
        String id = customer.getString("user_id");
        String room_id = customer.getString("Room_id");
        String SQL =
                String.format("insert customer(id, room_id) values('%s','%s')", id, room_id);
        return DataBaseConnect.noneQuery(SQL);
    }
    //记录下所有住过的客户
    public static boolean insertHistory(JSONObject customer){
        String id = customer.getString("user_id");
        String room_id = customer.getString("Room_id");
        String SQL =
                String.format("insert HistoryCustomer(user_name, room_id) values('%s','%s')", id, room_id);
        return DataBaseConnect.noneQuery(SQL);
    }
    public static boolean delete(String condition) {
        return DataBaseConnect.noneQuery(condition);
    }

    public static boolean delete(JSONObject customer) throws SQLServerException{
        String room_id = customer.getString("Room_id");
        String SQL =
                String.format("delete from customer where customer.room_id = '%s'", room_id);
        return DataBaseConnect.noneQuery(SQL);
    }

    public static List<String> getRoomId(){
        Connection connection = DataBaseConnect.getConnection();
        if (connection == null) return null;
        String sql = "select room_id from room";
        Statement statement = null;
        ResultSet res = null;
        List<String> room = new ArrayList<>();
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            String room_id;
            while (res.next()) {
                room_id = res.getString("Room_id").trim();
                room.add(room_id);
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
        return room;
    }

    public boolean update(Object o) {
        return false;
    }

    /*public Object get(String condition) {
        List<Customer> list = (List<Customer>) gets(condition);
        if (list ==null || list.size() == 0) return null;
        return list.get(0);
    }

    public List gets(String condition) {
        List<HashMap<String, String>> list = DataBaseConnect.query(condition);
        List<Customer> customerList = new ArrayList<>();
        for (HashMap<String, String> map : list) {
            Customer customer = new Customer(map);
            customerList.add(customer);
        }
        return customerList;
    }

     */
}
