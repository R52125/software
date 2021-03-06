package server.mapper;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import server.DataBaseConnect;
import server.simpleclass.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestMapper{

    public static boolean insert(Object o) throws SQLServerException {
        Request request = (Request) o;
        boolean result = true;
        Connection connection = DataBaseConnect.getConnection();
        if (connection != null) {
            String room_id = request.getRoomId();
            float start_temp = request.getStartTemp();
            float end_temp = request.getEndTemp();
            String start_time = request.getStartTime();
            String stop_time = request.getStopTime();
            float cost = request.getCost();
            int wind_power =request.getWindPower();
            float electricity = request.getElectricity();
            PreparedStatement ps1 = null;
            PreparedStatement ps2 = null;
            try {
                connection.setAutoCommit(false);
                String sql1 = String.format(
                        "insert reportor(start_time, stop_time, start_temp, end_temp, wind_power, cost, electricity, room_id) " +
                                "values ('%s', '%s', %f, %f, %d, %f, %f, '%s')",
                        start_time,
                        stop_time,
                        start_temp,
                        end_temp,
                        wind_power,
                        cost,
                        electricity,
                        room_id
                );
                ps1 = connection.prepareStatement(sql1);
                ps1.execute();
                // 获得自增下标
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                result = false;
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

    public boolean delete(String condition) {
        return false;
    }

    public boolean update(Object o) {
        return false;
    }

    public Object get(String condition) throws SQLServerException {
        Connection connection = DataBaseConnect.getConnection();
        if (connection == null) return null;
        String sql =
                "select room_id, convert(varchar(100), start_time, 20) as start_time," +
                        " convert(varchar(100), stop_time, 20) as stop_time," +
                        " start_temp, end_temp, target_temp, wind_power, cost, electricity " +
                        "from request, room_request " +
                        "where request.id = room_request.request_id and " + condition;
        Statement statement = null;
        ResultSet res = null;
        Request request = null;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            if (res.next()) {
                request = new Request();
                request.setRoomId(res.getString("room_id"));
                request.setStartTemp(res.getInt("start_temp"));
                request.setEndTemp(res.getInt("end_temp"));
                request.setTargetTemp(res.getInt("target_temp"));
                request.setStartTime(res.getString("start_time"));
                request.setStopTime(res.getString("stop_time"));
                request.setCost(res.getFloat("cost"));
                request.setElectricity(res.getFloat("electricity"));
                request.setWindPower(res.getInt("wind_power"));
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
        return request;
    }

    public List gets(String condition) throws SQLServerException {
        Connection connection = DataBaseConnect.getConnection();
        if (connection == null) return null;
        String sql =
                "select room_id, convert(varchar(100), start_time, 20) as start_time," +
                        " convert(varchar(100), stop_time, 20) as stop_time," +
                        " start_temp, end_temp, target_temp, wind_power, cost, electricity " +
                "from request, room_request " +
                "where request.id = room_request.request_id and " + condition;
        Statement statement = null;
        ResultSet res = null;
        List<Request> list = new ArrayList<>();
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            while (res.next()) {
                Request request = new Request();
                request.setRoomId(res.getString("room_id"));
                request.setStartTemp(res.getInt("start_temp"));
                request.setEndTemp(res.getInt("end_temp"));
                request.setTargetTemp(res.getInt("target_temp"));
                request.setStartTime(res.getString("start_time"));
                request.setStopTime(res.getString("stop_time"));
                request.setCost(res.getFloat("cost"));
                request.setElectricity(res.getFloat("electricity"));
                request.setWindPower(res.getInt("wind_power"));
                list.add(request);
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
        return list;
    }
}
