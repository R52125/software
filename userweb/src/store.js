import Vue from 'vue'
import Vuex from 'vuex'

//持久化store里面的数据
import createPersistedState from 'vuex-persistedstate'
import router from './router'
import {useWebSocket} from "./hooks";

Vue.use(Vuex)

// const ws = useWebSocket();

export default new Vuex.Store({
    state:{
        // 目标温度
        temperature: 22,
        // 当前温度
        currenttemperature: 22,
        // 外界温度
        outtemperature: 25,
        // switchbtn: 0: 关机；1: 开机
        switchbtn: 0,
        // wind 1,2,3: 低 中 高
        wind: 1,
        // 从机工作模式: 0: 制冷；1: 暖气
        Rmode: 0,
        // 身份证号
        id: '',
        // 房间号
        room: '',
        // 主机工作模式: 0:制冷 1: 供暖
        Cmode: 0,
        // 主机工作温度
        Ctemp: 22,
        // 当前累计开销
        currentcost: 0.0,
        // 本次使用总开销
        currentcost_total: 0.0,
        // 状态汇报频率
        state_interval: 1000,
        // 控制监控状态的ID值
        control_state: '',
        // 标记送风状态：0:停止送风，1:正在送风
        sendwind_state: 0,
        // websocket连接
        ws: '',
    },
    mutations:{
        addtemperature(state){
            if (this.state.Rmode == 0){
                if (this.state.switchbtn == 1 && this.state.temperature != 25)
                    this.state.temperature++;
            }
            else{
                if (this.state.switchbtn == 1 && this.state.temperature != 30)
                    this.state.temperature++;
            }
        },
        reducetemperature(state){
            if (this.state.Rmode == 0){
                if (this.state.switchbtn == 1 && this.state.temperature != 18)
                    this.state.temperature--;
            }
            else{
                if (this.state.switchbtn == 1 && this.state.temperature != 25)
                    this.state.temperature--;
            }
        },
        changeswitchbtn(state){
            if (this.state.switchbtn == 0){
                this.state.ws = useWebSocket();
                this.state.switchbtn = 1;
            }
            else{
                this.state.switchbtn = 0;
                this.state.ws.close();
            }
        },
        addwind(state){
            if (this.state.wind != 3 && this.state.switchbtn == 1)
                this.state.wind++;
        },
        reducewind(state){
            if (this.state.wind != 1 && this.state.switchbtn == 1)
                this.state.wind--;
        },
        coldmode(state){
            if (this.state.Rmode == 1 && this.state.switchbtn == 1){
                this.state.Rmode = 0;
                this.state.temperature = 22;
            }
        },
        warmmode(state){
            if(this.state.Rmode == 0 && this.state.switchbtn == 1){
                this.state.Rmode = 1;
                this.state.temperature = 28;
            }
        },
        // 检查身份证号数字对不对
        check_id(state, logmes){
            this.state.room = logmes.roomnumber;
            this.state.id = logmes.username;
            // ws.send(JSON.stringify({
            //     room: this.state.room,
            //     id: this.state.id
            // }));
            router.push('/user')
        },
        // 中央空调状态反馈
        WebSocket_config(state, newdata){
            this.state.Cmode = newdata.data.mode;
            this.state.Ctemp = newdata.data.temp;
        },
        // 处理主机送风
        WebSocket_wind(state, newdata){
            this.state.Ctemp = newdata.data.temp;
            this.state.wind = newdata.data.speed;
            this.state.Cmode = newdata.data.mode;
            this.state.currentcost = newdata.data.cost;
            // console.log(this.state.Ctemp, this.state.wind,this.state.Cmode,this.state.currentcost)
        },
        // 处理主机同意停止送风
        WebSocket_stopwind(state, newdata){
            this.state.currentcost_total = newdata.data.cost;
            // console.log(this.state.currentcost_total)
        },
        // 获取监视频率
        WebSocket_interval(state, newdata){
            this.state.state_interval = newdata.data.interval;
            // console.log(this.state.state_interval)
        },
        // 温度聚拢函数
        temperature_auto(state){

        }

    },
    actions:{
        handle_centralConfig(context, newdata){
            context.commit('WebSocket_config', newdata)
        },
        handle_sendwind(context, newdata){
            context.commit('WebSocket_wind', newdata);
        },
        handle_stopwind_ack(context, newdata){
            context.commit('WebSocket_stopwind', newdata);
        },
        handle_interval(context, newdata){
            context.commit('WebSocket_interval', newdata);
        },
        handle_sendstate(context){
            // 从控机开机的情况才会汇报状态
            if (this.state.switchbtn == 1){
                this.state.ws.send(JSON.stringify({
                    "event_id": 7,
                    "data":{
                        "cur_temp": this.state.currenttemperature,
                        "tar_temp": this.state.temperature,
                        "mode": this.state.Rmode,
                        "speed": this.state.wind,
                    }
                }));
                // 如果发现目标温度与当前温度不同,且处于停止送风状态，则发起送风请求
                if(this.state.currenttemperature != this.state.temperature && this.state.sendwind_state == 0){
                    this.state.ws.send(JSON.stringify({
                        "event_id": 2,
                        "data": {
                            "speed": this.state.wind,
                            "mode": this.state.Rmode,
                        }
                    }));
                    // 将状态转为正在送风
                    this.state.sendwind_state = 1;
                }
                // 如果发现目标温度与当前温度相同,且处于正在送风状态，则发起停止送风请求
                if(this.state.currenttemperature == this.state.temperature && this.state.sendwind_state == 1){
                    this.state.ws.send(JSON.stringify({
                        "event_id": 4,
                        "data": {
                        }
                    }));
                    this.state.sendwind_state = 0;
                }
                // // 如果当前处于待机状态（停止送风）,房间的温度会向外界温度变化
                // if(this.state.sendwind_state == 0){
                //     this.state.currenttemperature = (this.state.currenttemperature+this.state.outtemperature)/2
                // }
                // 如果当前处于送风状态，且目标温度与当前温度不同，则房间温度向当前温度变化
                if (this.state.sendwind_state == 1 && this.state.currenttemperature != this.state.temperature){
                    // 低风
                    if (this.state.wind == 1){
                        // 如果差值小于0.1，一步到位
                        if (Math.abs(this.state.temperature-this.state.currenttemperature) < 0.25){
                            this.state.currenttemperature= this.state.temperature;
                        }
                        else{
                            if (this.state.currenttemperature < this.state.temperature){
                                this.state.currenttemperature = (this.state.currenttemperature*10 + 0.1*10)/10;
                            }
                            else{
                                this.state.currenttemperature = (this.state.currenttemperature*10 - 0.1*10)/10;
                            }
                        }
                    }
                    // 中风
                    else if (this.state.wind == 2){
                        // 如果差值小于0.2，一步到位
                        if (Math.abs(this.state.temperature-this.state.currenttemperature) < 0.2){
                            this.state.currenttemperature = this.state.temperature;
                        }
                        else{
                            if (this.state.currenttemperature < this.state.temperature){
                                this.state.currenttemperature = (this.state.currenttemperature*10 + 0.2*10)/10;
                            }
                            else{
                                this.state.currenttemperature = (this.state.currenttemperature*10 - 0.2*10)/10;
                            }
                        }
                    }
                    // 高风
                    else{
                        // 如果差值小于0.4，一步到位
                        if (Math.abs(this.state.temperature-this.state.currenttemperature) < 0.4){
                            this.state.currenttemperature = this.state.temperature;
                        }
                        else{
                            if (this.state.currenttemperature < this.state.temperature){
                                this.state.currenttemperature = (this.state.currenttemperature*10 - 0.4*10)/10;
                            }
                            else{
                                this.state.currenttemperature = (this.state.currenttemperature*10 - 0.4*10)/10;
                            }
                        }
                    }
                }
            }
        },
    },
    plugins:[createPersistedState({
        storage:window.sessionStorage
    })]
})