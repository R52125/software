package server.mapper;

import server.DataBaseConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomStateMapper {

    public static boolean insert(String room_id)
    {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String SQL =
                String.format("insert RoomState(room_id, isUp, UpTime) values('%s',1,'%s')", room_id, time);
        return DataBaseConnect.noneQuery(SQL);
    }

    public static int getTimes(String startTime, String overTime, String Room_id) {
        Connection connection = DataBaseConnect.getConnection();
        String sql = String.format(
                "select count(isUp) from RoomState as A where  A.room_id = '%s' and A.upTime > '%s' and " +
                        "A.upTime < '%s' and isUp=1", Room_id,startTime,overTime);
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
        return times+1;
    }
}
