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
        WebSocket_sendstate();
        // // 按一定频率发送状态汇报
        // var State = setInterval('WebSocket_sendstate', store.state.state_interval);
        // console.log(State, typeof(State));
        // store.state.control_state = State;
    };
    function handleClose(e){
        console.log('webSocket close', e);
        clearTimeout(store.state.control_state);
    };
    function handleError(e){
        console.log('webSocket error', e);
    };
    function handleMessage(callBack){
        var e = JSON.parse(callBack.data);
        console.log(e)
        switch (e.event_id){
            // 中央空调状态反馈
            case 1:
                WebSocket_centralconfig(e);
                break;
            case 3:
                WebSocket_sendwind(e);
                break;
            case 5:
                WebSocket_stopwind(e);
                break;
            case 6:
                WebSocket_interval(e);
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
    };
    // 从机向主机发状态
    function WebSocket_sendstate(){
        var State = setInterval(()=>{
            store.dispatch('handle_sendstate')
        }, store.state.state_interval);
        // console.log(State, typeof(State));
        store.state.control_state = State;
        // store.dispatch('handle_sendstate');
        //console.log('hello')
    }

    return ws;
}

export default useWebSocket;