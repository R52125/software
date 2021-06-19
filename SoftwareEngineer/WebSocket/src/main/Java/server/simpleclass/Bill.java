package server.simpleclass;

import server.StringUtils;

public class Bill {
    private String roomId;
    private float electricity;
    private float cost;
    private String createTime;
    //不同风速下的收费倍率
    private static float high=1.2f;
    private static float medium=1.0f;
    private static float low=0.8f;

    public Bill(String roomId) {
        this.roomId = roomId;
        this.electricity = 0;
        this.cost = 0;
        this.createTime = StringUtils.getTimeString();
    }

    public void setElectricity(float electricity) {
        this.electricity = electricity;
        this.cost = Master.getPrice() * electricity;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public float getElectricity() {
        return electricity;
    }

    public float getCost() {
        return cost;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public static float getHigh() {
        return high;
    }

    public static float getLow() {
        return low;
    }

    public static float getMedium() {
        return medium;
    }
}
