package server.simpleclass;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/*
 * Request: 温控请求类，包含一次温控请求恩基本信息
 */
public class Request {
    private String roomId = "";
    private String startTime  = "";
    private String stopTime = "";
    private float startTemp = 0;
    private float endTemp = 0;
    private int targetTemp = 0;
    private int windPower = 0;
    private float cost = 0;
    private float electricity = 0;

    public Request(String roomId, String startTime, float startTemp) {
        this.roomId = roomId;
        this.startTime = startTime;
        this.startTemp = startTemp;
        this.cost = 0;
        this.electricity=0;
    }

    public Request(String roomId, String startTime, String stopTime, int startTemp, int endTemp, int windPower, float cost, float electricity){
        this.roomId =roomId;
        this.startTime=startTime;
        this.stopTime=stopTime;
        this.startTemp=startTemp;
        this.endTemp=endTemp;
        this.windPower=windPower;
        this.cost=cost;
        this.electricity=electricity;
    }

    public Request(JSONObject jsonObject, String roomId) throws JSONException {
        this.roomId = roomId;
        this.startTemp = jsonObject.getInt("current_temp");
        this.targetTemp = jsonObject.getInt("target_temp");
        this.windPower = Integer.parseInt(jsonObject.getString("wind_power"));
        this.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Request() {

    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setTargetTemp(int targetTemp) {
        this.targetTemp = targetTemp;
    }

    public void setEndTemp(float endTemp) {
        this.endTemp = endTemp;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setStartTemp(float startTemp) {
        this.startTemp = startTemp;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public void setWindPower(int windPower) {
        this.windPower = windPower;
    }

    public int getWindPower() {
        return windPower;
    }

    public String getRoomId() {
        return roomId;
    }

    public float getCost() {
        return cost;
    }

    public float getEndTemp() {
        return endTemp;
    }

    public float getStartTemp() {
        return startTemp;
    }

    public int getTargetTemp() {
        return targetTemp;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public float getElectricity() {
        return electricity;
    }

    public void setElectricity(float electricity) {
        this.electricity = electricity;
        this.cost = electricity * Master.getPrice();
    }
}
