package server.simpleclass;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * RoomState: 房间状态类，包含一个房间的温度信息等
 */
public class RoomState {
    private String roomId = null;
    private float currentTemperature;
    private float targetTemperature;
    private int wind_power = 0;//风量
    private int isOn = 0;
    private int mode=0;//制热模式
    private int speed;//风速
    private int uptimes;
    private int wind;

    public RoomState(JSONObject jsonObject, String roomId) throws JSONException {
        this.roomId = roomId;
        this.currentTemperature = jsonObject.getInt("cur_temp");
        this.targetTemperature = jsonObject.getInt("tar_temp");
        this.mode = jsonObject.getInt("mode");
        this.speed=jsonObject.getInt("speed");
    }

    public RoomState(String roomId) {
        this.roomId = roomId;
        this.currentTemperature = 0.0f;
        this.targetTemperature = 0.0f;
        this.wind_power=0;
        this.mode= 0;
        this.isOn=0;
        this.speed=-1;
        this.uptimes=0;
        this.wind = 0;
    }

    public float getCurrentTemperature() {
        return currentTemperature;
    }
    public String getRoomId() {
        return roomId;
    }

    public float getTargetTemperature() {
        return targetTemperature;
    }

    public int getWind_power() {
        return wind_power;
    }

    public int getisOn() {
        return isOn;
    }

    public int getMode() {
        return mode;
    }

    public int getSpeed(){
        return speed;
    }

    public void setCurrentTemperature(float currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public void setTargetTemperature(float targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public void setWind_power(int wind_power) {
        this.wind_power = wind_power;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setWind(int a){
        this.wind=a;
    }

    public int  getWind(){
        return this.wind;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setIsOn(int isOn) {
        this.isOn = isOn;
    }
    public void addUptimes(){
        this.uptimes++;
    }

    public int getUptimes() {
        return uptimes;
    }
}
