package server.mapper;

import org.json.JSONObject;
import server.DataBaseConnect;
import server.simpleclass.Bill;
import server.simpleclass.Request;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReportorMapper {

    private String roomId;
    private String startTime;
    private String endTime;

    private float totalCost;
    private int startTimes;
    private List<Request> requestList;
    private List<Bill> billList;

    /*public ReportorMapper(String roomId, String startTime, String endTime) {
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        requestList = queryRequests();
        totalCost = queryTotalCost();
        startTimes = queryStartTimes();
    }

     */

    public static List<JSONObject> GetReportor(String startTime, String overTime, String Room_id){
        Connection connection = DataBaseConnect.getConnection();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (connection == null) return null;
        String sql1 = String.format(
                "select * from reportor as A " +
                        "where A.start_time >= '%s' and A.stop_time <= '%s' and A.room_id = '%s'",
               startTime,overTime,Room_id
        );

        /*String sql2 = String.format("select * from reportor as A " +
                "where A.start_time >= '%s' and A.stop_time <= '%s' and A.room_id = '%s'");*/
        Statement statement = null;
        List<JSONObject> ReporterBill= new ArrayList<>();
        ResultSet res = null;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql1);
            String roomId = "", stime = "", stopTime = "";
            float startTemp = 0, endTemp = 0;
            int windPower =0;
            float cost = 0, electricity = 0;
            while (res.next()) {
                JSONObject RequestBill=new JSONObject();
                roomId = res.getString("room_id");
                stime = res.getString("start_time");
                stopTime = res.getString("stop_time");
                startTemp = res.getFloat("start_temp");
                endTemp = res.getFloat("end_temp");
                windPower = res.getInt("wind_power");
                cost = res.getFloat("cost");
                electricity = res.getFloat("electricity");
                RequestBill.put("Room_id",roomId);
                RequestBill.put("start_time",stime);
                RequestBill.put("stop_time",stopTime);
                RequestBill.put("start_temp",startTemp);
                RequestBill.put("end_temp",endTemp);
                RequestBill.put("wind_power",windPower);
                RequestBill.put("cost",cost);
                RequestBill.put("electricity",electricity);
                ReporterBill.add(RequestBill);
            }
        } catch (SQLException e)
        {
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
        return ReporterBill;
    }

    public static JSONObject Date(String day){
        Date d1 = java.sql.Date.valueOf(day);
        Connection connection = DataBaseConnect.getConnection();
        if (connection == null) return null;
        String sql = String.format(
                "select * from request_bill as A" +
                        "where A.start_time >= %s and A.stop_time <= %s",
                d1
        );
        Statement statement = null;
        JSONObject RequestBill = new JSONObject();
        ResultSet res = null;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            String roomId = "", stime = "", stopTime = "";
            int startTemp = 0, endTemp = 0, windPower =0;
            float cost = 0, electricity = 0;
            if (res.next()) {
                roomId = res.getString("room_id");
                stime = res.getString("start_time");
                stopTime = res.getString("stop_time");
                startTemp = res.getInt("start_temp");
                endTemp = res.getInt("end_temp");
                windPower = res.getInt("wind_power");
                cost = res.getFloat("cost");
                electricity = res.getFloat("electricity");
                RequestBill.put("Room_id",roomId);
                RequestBill.put("start_time",stime);
                RequestBill.put("stop_time",stopTime);
                RequestBill.put("start_temp",startTemp);
                RequestBill.put("end_temp",endTemp);
                RequestBill.put("wind_power",windPower);
                RequestBill.put("cost",cost);
                RequestBill.put("electricity",electricity);
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
        return RequestBill;
    }

    public static int getTimes(String startTime, String overTime, String Room_id){
        Connection connection = DataBaseConnect.getConnection();
        String sql = String.format(
                "select count(upTime) from request_bill as A" +
                        "where  A.room_id = '%s' and A.start_time > '%s' and A.stop_time < '%s' and upTime=1",
                Room_id,startTime,overTime
        );
        Statement statement = null;
        ResultSet res = null;
        int times = 0;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            if(res.next()){
                times= res.getInt(1);
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
        return times;
    }

    public static float getSum(String startTime, String overTime, String Room_id){
        Connection connection = DataBaseConnect.getConnection();
        String sql = String.format(
                "select sum(cost) from reportor as A where A.room_id = '%s' and A.start_time >= '%s' and A.stop_time <= '%s'",
                Room_id,startTime,overTime
        );
        Statement statement = null;
        ResultSet res = null;
        float sum = 0.0f;
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(sql);
            if(res.next()){
                sum= res.getFloat(1);
                System.out.println(sum);
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
        return sum;
    }
    /*
    private List<Request> queryRequests(){
        String condition = String.format(
                "room_id = '%s' " +
                        "and start_time > '%s' " +
                        "and start_time < '%s'",
                roomId, startTime, endTime
        );
        try {
            return new RequestMapper().gets(condition);
        } catch (SQLServerException e) {
            e.printStackTrace();
            return null;
        }
    }

     */


    private int queryStartTimes(){
        String SQL = String.format(
                "select count(distinct start_time) as start_times " +
                        "from slave " +
                        "where room_id = '%s' " +
                        "and start_time > '%s' " +
                        "and start_time < '%s'",
                roomId, startTime, endTime
        );
        ArrayList<HashMap<String, String>> list = DataBaseConnect.query(SQL);
        if (list == null || list.size() == 0) return 0;
        HashMap<String, String> map = list.get(0);
        int times;
        try {
            times = Integer.parseInt(map.get("start_times"));
        } catch (NumberFormatException e){
            times = 0;
            e.printStackTrace();
        }
        return times;
    }

    private float queryTotalCost() {
        String SQL = String.format(
                "select sum(cost) as total_cost " +
                        "from request, room_request " +
                        "where room_id = '%s' " +
                        "and id = request_id " +
                        "and start_time > '%s' " +
                        "and start_time < '%s'",
                roomId, startTime, endTime
        );
        ArrayList<HashMap<String, String>> list = DataBaseConnect.query(SQL);
        if (list == null || list.size() == 0) return 0;
        HashMap<String, String> map = list.get(0);
        float total_cost;
        try {
            total_cost = Float.parseFloat(map.get("total_cost"));
        } catch (NumberFormatException e){
            total_cost = 0;
            e.printStackTrace();
        } catch (NullPointerException e){
            total_cost = 0;
//            e.printStackTrace();
        }
        return total_cost;
    }

    public List<Request> getRequestList() {
        return requestList;
    }

    public List<Bill> getBillList() {
        return billList;
    }

    public int getStartTimes() {
        return startTimes;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public boolean exportReport(){
        String reportSheetPath = "server/doc/" 
         + roomId + "-" + startTime + "-" + endTime + ".txt";
        FileWriter writer = null;
        boolean result = false;
        try {
            writer = new FileWriter(reportSheetPath);
            writer.write(String.format("start times: %d\n", startTimes));
            writer.write(String.format("total cost: %f\n", totalCost));
            writer.write("request list:\n");
            writer.write("start_time\t\t\tend_time\t\t\tstart_temp\tend_temp\ttarget_temp\twind_power\tcost\telectricity\n");
            for (Request r : requestList) {
                writer.write(String.format(
                        "%s\t%s\t%d\t\t\t%d\t\t\t%d\t\t\t%s\t\t%f\t\t%f\n",
                        r.getStartTime(), r.getStopTime(), r.getStartTemp(), r.getEndTemp(), r.getTargetTemp(), r.getWindPower(), r.getCost(), r.getElectricity()
                ));
            }
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
