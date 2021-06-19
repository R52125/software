package server.simpleclass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

public class Master {
    private static int mode=1;//0是制冷，1是制热
    private static int temp=28 ;
    private static int query=1000;//主机查询从机的频率,单位是毫秒
    private static int isOn;//关机后不响应任何请求
    private static float price = 5.0f;
    public static String View_id="View";
    //管理员信息
    private static String id = "001";
    private static String pd = "001";

    public static int getQuery() {return query;}

    public static int getMode() {
        return mode;
    }

    public static int getTemp() {
        return temp;
    }

    public static void setMode(int mode) {
        Master.mode = mode;
    }

    public static void setQuery(int query) {
        Master.query = query;
    }

    public static void setTemp(int temp) {
        Master.temp = temp;
    }

    public static int getTemp(int temp) {
        return temp;
    }


    public void setPrice(float price) {
        this.price = price;
    }

    public static int getIsOn() {
        return isOn;
    }

    public static void setIsOn(int isOn) {
        Master.isOn = isOn;
    }

    public static float getPrice() {
        return price;
    }

    public static String getId() {
        return id;
    }

    public static String getPd() {
        return pd;
    }

    private String configFilePath = "server/src/server/configure.json";

    public void init() {
        JSONObject configJson = readConfig();
        int queryInterval = 1000;
        float pprice = 5;
        int mmode = 0;
        if (configJson != null) {
            try {
                queryInterval = configJson.getInt("query_interval");
                mmode = configJson.getInt("mode");
                pprice = configJson.getFloat("price");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.price = pprice;
        this.mode = mmode;
    }
    private JSONObject readConfig() {
        File file = new File(configFilePath);
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[10];
            String jsonString = "";
            int byteRead;
            while ((byteRead = in.read(buffer)) != -1) {
                jsonString = jsonString + new String(buffer);
            }
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static boolean login(JSONObject administor)
    {
        String id = administor.getString("id");
        String password = administor.getString("password");
        if(Master.getId().equals(id) && Master.getPd().equals(password))
        {
            System.out.println("log in server succeed");
            return true;
        }
        System.out.println("log in server failed");
        return false;
    }
}
