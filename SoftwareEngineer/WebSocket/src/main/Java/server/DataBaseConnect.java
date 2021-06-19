package server;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseConnect {
    private static String user = "sa";
    private static String database = "hotel_db";
    private static String password = "123";
    private static String address = "//localhost:1433";
    private static String driver = "jdbc:sqlserver";

    /********************静态块可以提高效率***********/
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 加载驱动程序
     */

    public static Connection getConnection() {
        Connection con = null;
        Statement sta;
        String url = "jdbc:sqlserver://localhost:1433;DatabaseName=hotel_db";
        try {
            con = DriverManager.getConnection(url, "sa", "123");
            sta = con.createStatement();
            //System.out.println("链接成功");
        } catch (SQLException e) {
            System.out.println("连接失败");
            e.printStackTrace();
        }
        return con;
    }


    /*public static Connection getConnection() {
        String connectionUrl = driver + ":" + address + ";" + "databaseName=" + database;
        Connection con = null;
        try {
            // Establish the connection.
            con = DriverManager.getConnection(connectionUrl, user, password);
            return con;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }*/
    /*
     * @Description 获取查询语句的结果, 存储在 ArrayList 中
     * @Param querySQL SQL语句
     * @Return ArrayList<HashMap<String, String>> 查询结果
     */
    public static ArrayList<HashMap<String, String>> query(String querySQL) {
        Connection con = getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, String>> resultList = null;
        new ArrayList<HashMap<String, String>>();
        if (con != null) {
            try {
                resultList = new ArrayList<>();
                stmt = con.createStatement();
                rs = stmt.executeQuery(querySQL);
                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    HashMap<String, String> tuple = new HashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); ++i) {
                        String columnName = metaData.getColumnName(i);
                        String data = rs.getString(i);
                        System.out.println(columnName + ":" + data);
                        tuple.put(columnName, data);
                    }
                    resultList.add(tuple);
                }
                return resultList;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (rs != null) try {
                    rs.close();
                } catch (Exception e) {
                }
                if (stmt != null) try {
                    stmt.close();
                } catch (Exception e) {
                }
                if (con != null) try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static boolean noneQuery(String SQL) {
        Connection con = getConnection();
        Statement stmt = null;
        boolean result = true;

        if (con != null) {
            try {
                stmt = con.createStatement();
                stmt.execute(SQL);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (stmt != null) try {
                    stmt.close();
                } catch (Exception e) {
                }
                if (con != null) try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

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
        int index = message.indexOf("&")+10;
        while(index<message.length()){
            UserId = UserId + info[index];
            index++;
        }
        return UserId;
    }

    public static void main(String[] args) {
        String s1 = "2021-06-16";
        String s2 = "2021-06-17";
        String Room_id = "004";
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        String s3 = "?room_id=002&?user_id=administer";
        Connection connection = DataBaseConnect.getConnection();
        String sql = String.format(
                "select count(isUp) from RoomState as A where  A.room_id = '%s' and A.upTime > '%s' and " +
                        "A.upTime < '%s' and isUp=1", Room_id,s1,s2);
        Statement statement = null;
        ResultSet res = null;
        int times = 0;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            //times= res.getInt(0);

            if (res.next()) {
                times = res.getInt(1);
                System.out.println(times);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}