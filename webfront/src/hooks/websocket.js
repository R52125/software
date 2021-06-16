import {WS_ADDRESS} from "../configs";
import store from "../store";

function useWebSocket(){
    const ws = new WebSocket(WS_ADDRESS);

    const init = () => {
        bindEvent();
    }

    init();

    function bindEvent(){
        ws.addEventListener('open', handleOpen, false);
        ws.addEventListener('close', handleClose, false);
        ws.addEventListener('error', handleError, false);
        ws.addEventListener('message', handleMessage, false);
    }

    function handleOpen(e){
        console.log('FE: WebSocket open', e);
    }
    function handleClose(e){
        console.log('FE: WebSocket close', e);
    }
    function handleError(e){
        console.log('FE: WebSocket error', e);
    }
    function handleMessage(callBack){
        var e = JSON.parse(callBack.data);
        console.log("e: ", e)
        // console.log("temp: ", e.data.temp)
        switch(e.event_id){
            case 11:
                WebSocket_switch(e);
                break;
            case 12:
                WebSocket_adduser(e);
                break;
            case 13:
                WebSocket_reduceuser(e);
                break;
            case 14:
                WebSocket_getform(e);
                break;
            case 15:
                WebSocket_config(e);
                break;
            case 16:
                WebSocket_login(e);
                break;
            case 17:
                WebSocket_monitor(e);
                break;
            default:
                console.log(e.event_id);
        };
    }

    function WebSocket_switch(newdata){
        store.dispatch('handle_switch', newdata);
    }
    function WebSocket_adduser(newdata){
        store.dispatch('handle_adduser', newdata);
    }
    function WebSocket_reduceuser(newdata){
        store.dispatch('handle_reduceuser', newdata);
    }
    function WebSocket_getform(newdata){
        store.dispatch('handle_getform', newdata);
    }
    function WebSocket_config(newdata){
        store.dispatch('handle_config', newdata);
    }
    function WebSocket_login(newdata){
        store.dispatch('handle_login', newdata);
    }
    function WebSocket_monitor(newdata){
        store.dispatch('handle_monitor', newdata);
    }
    return ws;
}

export default useWebSocket;