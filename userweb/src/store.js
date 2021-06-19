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
        // 上一次的目标温度
        last_temperature: 25,
        // 目标温度
        temperature: 25,
        // 当前温度
        currenttemperature: 25,
        // 外界温度
        outtemperature: 25,
        // switchbtn: 0: 关机；1: 开机
        switchbtn: 0,
        // wind 0,1,2: 低 中 高
        wind: 1,
        // 从机工作模式: 0: 制冷；1: 暖气；
        Rmode: 0,
        // 发给主机的状态包的模式：0: 送冷风；1: 送暖风；2: 不送风
        Smode: 0,
        // 身份证号
        id: '',
        // 房间号
        room: '',
        // 主机工作模式: 0:制冷 1: 供暖
        Cmode: 1,
        // 主机工作温度
        Ctemp: 28,
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
        // 是否是第一次点击温度调节按钮
        flag: 0,
        // 控制模式转换的ID值
        timer_change: '',
        // 新加的speed，主机显示通知从机不送风
        speed: 0,
    },
    mutations:{
        addtemperature(state){
            if (this.state.Rmode == 0){
                if (this.state.switchbtn == 1 && this.state.temperature != 25)
                    this.state.temperature++;
            }
            else{
                // console.log('Ctemp: ' + this.state.Ctemp)
                if (this.state.switchbtn == 1 && this.state.temperature != this.state.Ctemp)
                    this.state.temperature++;
            }
            // this.state.temperature ++;
        },
        reducetemperature(state){
            if (this.state.Rmode == 0){
                if (this.state.switchbtn == 1 && this.state.temperature != this.state.Ctemp)
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
                // console.log('ws: ', this.state.ws);
                this.state.switchbtn = 1;
            }
            else{
                this.state.switchbtn = 0;
                this.state.ws.close();
            }
        },
        addwind(state){
            if (this.state.wind != 2 && this.state.switchbtn == 1)
                this.state.wind++;
        },
        reducewind(state){
            if (this.state.wind != 0 && this.state.switchbtn == 1)
                this.state.wind--;
        },
        coldmode(state){
            if (this.state.Rmode == 1 && this.state.switchbtn == 1){
                this.state.Rmode = 0;
                this.state.temperature = 22;
                this.state.currenttemperature = 22;
            }
        },
        warmmode(state){
            if(this.state.Rmode == 0 && this.state.switchbtn == 1){
                this.state.Rmode = 1;
                this.state.temperature = 28;
                this.state.currenttemperature = 28;
            }
        },
        // 检查身份证号数字对不对
        check_id(state, logmes){
            console.log(logmes.roomnumber)
            if(logmes.roomnumber!='001' && logmes.roomnumber!='002' && logmes.roomnumber!='003' && logmes.roomnumber!='004'
                && logmes.roomnumber!='120' && logmes.roomnumber!='121' && logmes.roomnumber!='122' && logmes.roomnumber != '123'){
                alert('房间号错误！')
            }
            else{
                this.state.room = logmes.roomnumber;
                this.state.id = logmes.username;
                router.push('/user')
            }
            // ws.send(JSON.stringify({
            //     room: this.state.room,
            //     id: this.state.id
            // }));

        },
        // 中央空调状态反馈
        WebSocket_config(state, newdata){
            this.state.Cmode = newdata.data.mode;
            this.state.Ctemp = newdata.data.temp;
        },
        // 处理主机送风
        WebSocket_wind(state, newdata){
            this.state.Ctemp = newdata.data.temp;
            this.state.speed = newdata.data.speed;
            this.state.Cmode = newdata.data.mode;
            this.state.currentcost = newdata.data.cost.toFixed(2);
            // console.log(this.state.Ctemp, this.state.wind,this.state.Cmode,this.state.currentcost)
            if (this.state.speed != -1){
                // 改变房间温度
                this.commit('change_roomtemp');
            }
            else{
                // 室内温度向室外改变
                //  制冷
                if(this.state.Rmode == 0){
                    this.state.currenttemperature = (this.state.currenttemperature*10 + 0.1*10)/10;
                }
                // 供暖
                else {
                    this.state.currenttemperature = (this.state.currenttemperature*10 - 0.1*10)/10;
                }
            }
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
        // 房间温度改变
        change_roomtemp(state){
            // 如果当前处于送风状态，且目标温度与当前温度不同，则房间温度向当前温度变化
            // if (this.state.sendwind_state == 1 && this.state.currenttemperature != this.state.temperature){
                // 高风
                if (this.state.wind == 2){
                    // 如果差值小于0.1，一步到位
                    if (Math.abs(this.state.temperature-this.state.currenttemperature) < 0.067){
                        this.state.currenttemperature= this.state.temperature;
                    }
                    else{
                        if (this.state.currenttemperature < this.state.temperature){
                            this.state.currenttemperature = (this.state.currenttemperature*1000 + 0.067*1000)/1000;
                        }
                        else{
                            this.state.currenttemperature = (this.state.currenttemperature*1000 - 0.067*1000)/1000;
                        }
                    }
                }
                // 中风
                else if (this.state.wind == 1){
                    // 如果差值小于0.2，一步到位
                    if (Math.abs(this.state.temperature-this.state.currenttemperature) < 0.05){
                        this.state.currenttemperature = this.state.temperature;
                    }
                    else{
                        if (this.state.currenttemperature < this.state.temperature){
                            this.state.currenttemperature = (this.state.currenttemperature*100 + 0.05*100)/100;
                        }
                        else{
                            this.state.currenttemperature = (this.state.currenttemperature*100 - 0.05*100)/100;
                        }
                    }
                }
                // 低风
                else{
                    // 如果差值小于0.4，一步到位
                    if (Math.abs(this.state.temperature-this.state.currenttemperature) < 0.04){
                        this.state.currenttemperature = this.state.temperature;
                    }
                    else{
                        if (this.state.currenttemperature < this.state.temperature){
                            this.state.currenttemperature = (this.state.currenttemperature*100 + 0.04*100)/100;
                        }
                        else{
                            this.state.currenttemperature = (this.state.currenttemperature*100 - 0.04*100)/100;
                        }
                    }
                }
            // }
        },
        change_mode_children(state){
            this.state.Smode = this.state.Rmode;
            this.state.flag = 0;
        },
        change_mode(state){
            // 第一次
            if (this.state.flag == 0){
                // console.log('flag: ' + this.state.flag)
                // console.log('enter_time1: ' + Date())
                this.state.flag = 1;
                this.state.timer_change =  setTimeout(()=>{
                    this.commit('change_mode_children')
                    }, this.state.state_interval);
            }
            // 其他
            else{
                if (this.state.temperature != this.state.last_temperature){
                    // console.log('enter_time2: ' + Date())
                    // console.log('flag2: ', this.state.flag)
                    clearTimeout(this.state.timer_change);
                    this.state.timer_change =  setTimeout(()=>{
                        this.commit('change_mode_children')
                    }, this.state.state_interval);
                }
            }
            this.state.sendwind_state = 1;
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
            // console.log('interval: ', this.state.state_interval)
            // 从控机开机的情况才会汇报状态
            if (this.state.switchbtn == 1){
                // 两种不用发送风请求的情况
                if ((Math.abs(this.state.temperature-this.state.currenttemperature) < 1 &&this.state.sendwind_state == 0) ||
                    (this.state.temperature == this.state.currenttemperature && this.state.sendwind_state == 1)){
                    this.state.sendwind_state = 0;
                    this.state.Smode = 2;
                    // console.log(this.state.Rmode)
                }
                else{
                    context.commit('change_mode')
                }
                var a = JSON.stringify({
                    "event_id": 7,
                    "data":{
                        "cur_temp": this.state.currenttemperature,
                        "tar_temp": this.state.temperature,
                        "mode": this.state.Smode,
                        "speed": this.state.wind,
                    }
                })
                this.state.last_temperature = this.state.temperature;
                this.state.ws.send(a);
                console.log(a);
                // 如果发现目标温度与当前温度不同,且处于停止送风状态，则发起送风请求
                // if(this.state.currenttemperature != this.state.temperature && this.state.sendwind_state == 0){
                //     this.state.ws.send(JSON.stringify({
                //         "event_id": 2,
                //         "data": {
                //             "speed": this.state.wind,
                //             "mode": this.state.Rmode,
                //         }
                //     }));
                //     // 将状态转为正在送风
                //     this.state.sendwind_state = 1;
                // }
                // 如果发现目标温度与当前温度相同,且处于正在送风状态，则发起停止送风请求
                // if(this.state.currenttemperature == this.state.temperature && this.state.sendwind_state == 1){
                //     this.state.ws.send(JSON.stringify({
                //         "event_id": 4,
                //         "data": {
                //         }
                //     }));
                //     this.state.sendwind_state = 0;
                // }
                // 如果当前处于待机状态（停止送风）,房间的温度会向外界温度变化
                if(this.state.sendwind_state == 0){
                    //  制冷
                    if(this.state.Rmode == 0){
                        this.state.currenttemperature = (this.state.currenttemperature*10 + 0.1*10)/10;
                    }
                    // 供暖
                    else {
                        this.state.currenttemperature = (this.state.currenttemperature*10 - 0.1*10)/10;
                    }
                }
            }
        },
    },
    plugins:[createPersistedState({
        storage:window.sessionStorage
    })]
})