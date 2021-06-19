package server.manager;

import org.json.JSONObject;
import server.mapper.CustomerMapper;
import server.simpleclass.RoomState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateManager {
    private static StateManager stateManager;
    private StateManager(){

    }
    // 房间状态map, key为room_id, value为房间状态RoomState
    private HashMap<String, RoomState> stateMap = initStateManager();

    public HashMap initStateManager(){
        HashMap<String,RoomState> init = new HashMap<>();
        List<String> Room = CustomerMapper.getRoomId();
        for(int i=0; i<Room.size(); i++)
        {
            RoomState R = new RoomState(Room.get(i));
            //System.out.println(R);
            //System.out.println(Room);
            init.put(Room.get(i), R);
        }
        return init;
    }

    public static StateManager getInstance() {
        if(stateManager==null){
            stateManager = new StateManager();
        }
        return stateManager;
    }

    public void updateState(JSONObject jsonObject, String room_id) {
        float ctemp = jsonObject.getFloat("cur_temp");
        float ttemp = jsonObject.getFloat("tar_temp");
        int mode = jsonObject.getInt("mode");
        int speed = jsonObject.getInt("speed");
        stateMap.get(room_id).setSpeed(speed);
        stateMap.get(room_id).setCurrentTemperature(ctemp);
        stateMap.get(room_id).setTargetTemperature(ttemp);
        if(mode != 2 )
        stateMap.get(room_id).setMode(mode);
        //System.out.println(stateMap.toString());
    }

    public RoomState removeRoom(String room_id) {
        RoomState state = stateMap.remove(room_id);
        return state;
    }

    public void addRoomState(RoomState state) {
        String room_id = state.getRoomId();
        if (!stateMap.containsKey(room_id)) stateMap.put(room_id, state);
    }

    /*
     * @Description getStateMap 返回当前连接的所有房间的状态
     * @Param
     * @Return Map<String, RoomState>
     */
    public Map<String, RoomState> getStateMap() {
        return stateMap;
    }

    /*
     * @Description getState 得到指定房间号的房间状态
     * @Param room_id 房间号
     * @Return RoomState 房间状态
     */
    public RoomState getState(String room_id) {
        return stateMap.get(room_id);
    }

}
