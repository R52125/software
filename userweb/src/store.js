import Vue from 'vue'
import Vuex from 'vuex'

//持久化store里面的数据
import createPersistedState from 'vuex-persistedstate'
import router from './router'
import {useWebSocket} from "./hooks";

Vue.use(Vuex)

const ws = useWebSocket();

export default new Vuex.Store({
    state:{
        // 目标温度
        temperature: 25,
        // 当前温度
        currenttemperature: 25,
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
        Ctemp: 0,
        // 当前累计开销
        currentcost: 0.0,
        // 本次使用总开销
        currentcost_total: 0.0,
        // 状态汇报频率
        state_interval: 1000,
        // 控制监控状态的ID值
        control_state: ''
    },
    mutations:{
        addtemperature(state){
            if (this.state.switchbtn == 1)
                this.state.temperature++;
        },
        reducetemperature(state){
            if (this.state.switchbtn == 1)
                this.state.temperature--;
        },
        changeswitchbtn(state){
            if (this.state.switchbtn == 0)
                this.state.switchbtn = 1;
            else
                this.state.switchbtn = 0;
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
            if (this.state.Rmode == 1 && this.state.switchbtn == 1)
                this.state.Rmode = 0;
        },
        warmmode(state){
            if(this.state.Rmode == 0 && this.state.switchbtn == 1)
                this.state.Rmode = 1;
        },
        // 检查身份证号数字对不对
        check_id(state, logmes){
            this.state.room = logmes.roomnumber;
            this.state.id = logmes.username;
            ws.send(JSON.stringify({
                room: this.state.room,
                id: this.state.id
            }));
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
            ws.send(JSON.stringify({
                "event_id": 7,
                "data":{
                    "cur_temp": this.state.currenttemperature,
                    "tar_temp": this.state.temperature,
                    "mode": this.state.Rmode,
                    "speed": this.state.wind,
                }
            }))
        },
    },
    plugins:[createPersistedState({
        storage:window.sessionStorage
    })]
})