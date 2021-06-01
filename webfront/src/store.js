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
        // 开关机，0: 请求关机，1: 请求开机
        onoff: 1,
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
        ack: '',
        // 从控机开机次数
        up_times: 0,
        // 温控请求的起始时间
        start_time: '',
        // 温控请求的结束时间
        stop_time: '',
        // 温控请求的起始温度
        start_temp: '',
        // 温控请求的结束温度
        end_temp: '',
        // 风量消耗大小
        wind_power: '',
        // 每次温控请求所需费用
        cost: 0,
        // 总费用
        total_cost: 0,
        // state：0: 从控机开机，1: 从控机关机
        // cstate: 0,
        // Rmode：0: 从控机制冷模式，1:从控机供暖模式
        // Rmode: 0,
        // 从控机温度
        // tem: 25,
        // 风速
        // wind: 0,
        // 报表列表
        formdatalist: '',
        // 监控信息列表
        monitorlist: '',
        // 报表耗电量
        electricity: 0.0,
        // 记录报表的请求有多少天
        days: 0,
        formArray:[],
        form_count:[],
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
            router.push('/master')
        },
        handle_master(state, newmaster){
            this.state.master = newmaster;
        },
        handle_masterpassword(state, newpassword){
            this.state.masterpassword = newpassword;
        },
        switchreq(state){
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
            var split1 = this.state.startdata.split('-');
            var split2 = this.state.enddata.split('-');
            var date1 = new Date(split1[0], (split1[1]-1), split1[2]);
            var date2 = new Date(split2[0], (split2[1]-1), split2[2]);
            var minusdate = Math.floor(date2.getTime()-date1.getTime())/(1000*60*60*24);
            var date = Math.abs(minusdate);
            this.state.days = date + 1;
            console.log('date: ' + this.state.days);
            ws.send(JSON.stringify({
                "eventid": 14,
                "data":{
                    "Room_id": this.state.Room_id,
                    "startdata": this.state.startdata,
                    "enddata": this.state.enddata,
                    "formmodle": this.state.formmodel,
                }
            }));

            router.push('/master/formmes')
        },
        // 开关机回应包
        WebSocket_switch_ack(state, newdata){
            if (newdata.data.ack == 1){
                // 收到确认包
                // 改变中央空调开关机状态
                if (this.state.onoff == 1)
                    this.state.onoff = 0;
                else
                    this.state.onoff = 1;
            }
            else{
                // 回应包未确认
                if(this.state.onoff == 1)
                    alert('开机未确认'),
                    console.log(data.ack)
                else
                    alert('关机未确认')
            }
        },
        // 用户登记回应包
        WebSocket_adduser_ack(state, newdata){
            if (newdata.data.ack == 1){
                alert('用户登记成功\n房间号为 ' + this.state.Room_id)
            }
            else{
                alert('用户登记失败，请重试')
            }
        },
        // 用户退房回应包
        WebSocket_reduceuser_ack(state, newdata){
            if (newdata.data.ack == 1){
                alert('用户退房成功\n房间号为 ' + this.state.Room_id)
            }
            else{
                alert('用户退房失败，请重试')
            }
        },
        // 报表回应包
        WebSocket_getform_ack(state, newdata){
            this.state.Room_id = newdata.data.Room_id;
            this.state.up_times = newdata.data.up_times;
            this.state.total_cost = newdata.data.total_cost;
            this.state.electricity = newdata.data.electricity;
            this.state.formdatalist = newdata.data.temp;
            var i = 0;
            var rem_j = 0;
            var startDate = this.state.startdata;
            for (i = 0; i < this.state.days; i++){
                // console.log('i: ' + i)
                // var split1 = this.state.startdata.split('-');
                // var startDate = new Date(split1[0], split1[1], split1[2]);
                this.state.formArray[i] = startDate;
                // console.log('startDate: ' + startDate)
                // console.log('formArray[i]: ' + this.state.formArray[i]);
                var count = 0;
                for (var j = rem_j; this.state.formdatalist[j]!=null; j++){
                    console.log('j: ' + j);
                    var check_time = this.state.formdatalist[j].start_time.split(' ');
                    // console.log(this.state.formArray[i]);
                    // console.log(check_time[0]);
                    // console.log("hello");
                    if (this.state.formArray[i] == check_time[0]){
                        count++;
                    }
                    else{
                        rem_j = j;
                        break;
                    }
                }
                this.state.form_count[i] = count;
                startDate = new Date(startDate);
                startDate = +startDate +1000*60*60*24;
                startDate = new Date(startDate);
                var nextDate = startDate.getFullYear() + "-" + (startDate.getMonth() + 1).toString().padStart(2, '0') + "-" +startDate.getDate().toString().padStart(2, '0');
                startDate = nextDate;
            }
            console.log(this.state.formArray)
            console.log(this.state.form_count)
            // console.log(this.state.formdatalist)
        },
        // 中央空调配置回应包
        WebSocket_config_ack(state, newdata){
            if (newdata.data.ack == 1){
                alert('中央空调配置成功');
            }
            else{
                alert('中央空调配置失败，请重试');
            }
        },
        // 管理员登录回应包
        Websocket_login_ack(state, newdata){
            if (newdata.data.ack == 1){
                router.push('/master')
            }
            else{
                alert('管理员登陆成功')
            }
        },
        test(state){
          ws.send(JSON.stringify({
              "event_id": 17
          }));
          router.push('/master/centralmonitor')
        },
        // 房间监测接收包
        Websocket_monitor(state, newdata){
            this.state.monitorlist = newdata.data;
            // this.state.Room_id = newdata.data.Room_id;
            // this.state.cstate = newdata.data.cstate;
            // this.state.Rmode = newdata.data.Rmode;
            // this.state.tem = newdata.data.tem;
            // this.state.wind = newdata.data.wind;
            // console.log(this.state.Room_id, this.state.cstate, this.state.Rmode, this.state.tem, this.state.wind);
        }
    },
    actions:{
        handle_switch(context, newdata){
            context.commit('WebSocket_switch_ack', newdata)
        },
        handle_adduser(context, newdata){
            context.commit('WebSocket_adduser_ack', newdata)
        },
        handle_reduceuser(context, newdata){
            context.commit('WebSocket_reduceuser_ack', newdata)
        },
        handle_getform(context, newdata){
            context.commit('WebSocket_getform_ack', newdata)
        },
        handle_config(context, newdata){
            context.commit('WebSocket_config_ack', newdata)
        },
        handle_login(context, newdata){
            context.commit('Websocket_login_ack', newdata)
        },
        handle_monitor(context, newdata){
            context.commit('Websocket_monitor', newdata)
        },
        // receivemsg(context){
        //     ws.onmessage = function(callBack){
        //         var e = JSON.parse(callBack.data);
        //         // console.log(e);
        //         switch(e.event_id){
        //             case 11:
        //                 context.commit('WebSocket_switch_ack', e);
        //                 break;
        //             case 12:
        //                 context.commit('WebSocket_adduser_ack', e);
        //                 break;
        //             case 13:
        //                 context.commit('WebSocket_reduceuser_ack', e);
        //                 break;
        //             case 14:
        //                 context.commit('WebSocket_getform_ack', e);
        //                 break;
        //             case 15:
        //                 context.commit('WebSocket_config_ack', e);
        //                 break;
        //             case 16:
        //                 context.commit('Websocket_login_ack', e);
        //                 break;
        //             case 17:
        //                 context.commit('Websocket_monitor', e);
        //                 break;
        //             default:
        //                 console.log(e.event_id);
        //         };
        //     }
        // }
    }
    // plugins:[createPersistedState({
    //     storage:window.sessionStorage
    // })]
})
