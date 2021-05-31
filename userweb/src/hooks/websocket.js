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
        console.log('webSocket open', e);
    }
    function handleClose(e){
        console.log('webSocket close', e);
    }
    function handleError(e){
        console.log('webSocket error', e);
    }
    function handleMessage(callBack){
        var e = JSON.parse(callBack.data);
        // console.log(e)
        switch (e.event_id){
            // 中央空调状态反馈
            case 1:
                WebSocket_centralconfig(e);
                break;
            case 2:
                break;
            case 3:
                WebSocket_sendwind(e);
                break;
            case 4:
                break;
            case 5:
                WebSocket_stopwind(e);
                break;
            case 6:
                WebSocket_interval(e);
                break;
            case 7:
                break;
            default:
                console.log(e);
        }
    };

    function WebSocket_centralconfig(newdata){
        store.dispatch('handle_centralConfig', newdata);
    };
    function WebSocket_sendwind(newdata){
        store.dispatch('handle_sendwind', newdata);
    };
    function WebSocket_stopwind(newdata){
        store.dispatch('handle_stopwind_ack', newdata);
    };
    // 主机向从机汇报频率
    function WebSocket_interval(newdata){
        store.dispatch('handle_interval', newdata);
    }



    return ws;
}

export default useWebSocket;