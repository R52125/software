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
        // 工作模式：0: 制冷，1: 供暖
        mode: 0,
        // 刷新频率，单位毫秒
        frequency: 1000,
        // 管理员账号
        master: '',
        // 管理员密码
        masterpassword: '',
        // 开关机，0: 关机，1: 开机
        onoff: 0,
        // 房间名
        Room_id: '',
        // 用户身份证号
        user_id: '',
        // 报表查询的开始时间
        startdata: '',
        // 报表查询的结束时间
        enddata: '',
        // 模式:  0：日报表; 1：周报表; 2：月报表
        formmodel: '',
        // 从主机接收到的消息
        receive: '',
    },
    mutations:{
        handlemode(state, newmode){
            this.state.mode = newmode;
        },
        // 改变频率
        handlefre(state, newfre){
            this.state.frequency = newfre;
        },
        // 发送配置
        sendconfig(state){
            ws.send(JSON.stringify({
                event_id: 15,
                data:{
                    mode: this.state.mode,
                    frequency: this.state.frequency,
                }
            }))
            // console.log(typeof (this.state.receive))
            // router.push('/master')
        },
        check_id(state){
            ws.send(JSON.stringify({
                event_id: 16,
                data:{
                    id: this.state.master,
                    password: this.state.masterpassword,
                }
            }));
            router.push('/master');

        },
        handle_master(state, newmaster){
            this.state.master = newmaster;
        },
        handle_masterpassword(state, newpassword){
            this.state.masterpassword = newpassword;
        },
        switchreq(state){
            if (this.state.onoff == 1)
                this.state.onoff = 0;
            else
                this.state.onoff = 1;
            ws.send(JSON.stringify({
                "event_id": 11,
                "data":{
                    "onoff": this.state.onoff,
                }
            }))
        },
        handleRoomId(state, newRoom){
            this.state.Room_id = newRoom;
        },
        handleUserId(state, newUser){
            this.state.user_id = newUser;
        },
        addnewuser(state){
            ws.send(JSON.stringify({
                "event_id": 12,
                "data":{
                    "Room_id": this.state.Room_id,
                    "user_id": this.state.user_id,
                }
            }));
            alert('登记成功');
            router.push('/master');
        },
        // 退房
        reduceuser(state){
            ws.send(JSON.stringify({
                "event_id": 13,
                "data":{
                    "Room_id": this.state.Room_id,
                }
            }))
        },
        handlestartdata(state, newstartdata){
            this.state.startdata = newstartdata;
        },
        handleenddata(state, newenddata){
            this.state.enddata = newenddata;
        },
        handleformmodel(state, newmodel){
            this.state.formmodel = newmodel;
        },
        getForm(state){
            ws.send(JSON.stringify({
                "eventid": 14,
                "data":{
                    "Room_id": this.state.Room_id,
                    "startdata": this.state.startdata,
                    "enddata": this.state.enddata,
                    "formmodle": this.state.formmodel,
                }
            }))
        },
        WebSocket_Receive(state, receive_msg){
            this.state.receive = receive_msg;
        }
    },
    actions:{
        receivemsg(context){
            ws.onmessage = function(callBack){
                var e = JSON.parse(callBack.data);
                switch(e.event_id){
                    case 15:
                        context.commit('WebSocket_Receive', e);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    default:
                        console.log(e.event_id);
                };
            }
        }
    }
    // plugins:[createPersistedState({
    //     storage:window.sessionStorage
    // })]
})
