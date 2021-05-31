import Vue from 'vue'
import VueRouter from 'vue-router'

import Main from "../views/Main"

import Master from "../views/Master";
import CentralConfigs from "../components/CentralConfigs";
import CentralMonitor from "../views/CentralMonitor";
import CentralMonitoroption from "../components/CentralMonitoroption";
import FormOption from "../components/FormOption";
import Formmes from "../views/Formmes";
import Mastermes from "../views/Mastermes";
import addUser from "../components/addUser";

//安装路由
Vue.use(VueRouter);


//配置导出路由
export default new VueRouter({
    routes:[
        {
            //路由路径
            path:'/',
            name:'main',
            //跳转的组件
            component: Main,
        },
        {
            path:'/master',
            name:'master',
            component: Master,
            children:[
                {
                    path: '/master/CentralConfigs',
                    name: 'centralconfigs',
                    component: CentralConfigs
                },
                {
                    path: '/master/centralmonitoroption',
                    name: 'centralmonitoroption',
                    component: CentralMonitoroption
                },
                {
                    path: '/master/formoption',
                    name: 'formoption',
                    component: FormOption
                },
                {
                    path: '/master/adduser',
                    name: 'adduser',
                    component: addUser
                }
            ]
        },
        {
            path: '/master/centralmonitor',
            name: 'centralmonitor',
            component: CentralMonitor
        },
        {
            path: '/master/formmes',
            name: 'formmes',
            component: Formmes
        },
        {
            path: '/master/mastermes',
            name: 'mastermes',
            component: Mastermes
        }
    ]
});
