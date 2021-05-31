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
    function handleMessage(e){
    }

    return ws;
}

export default useWebSocket;