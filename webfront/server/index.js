const Ws = require('ws');

((Ws)=>{

    const server = new Ws.Server({port: 8025})

    const init = () =>  {
        bindEvent();
    }

    function bindEvent(){
        server.on('open', handleOpen);
        server.on('close', handleClose);
        server.on('error', handleError);
        server.on('connection', handleConnection);
    }

    function handleOpen(){
        console.log('BE:WebSocket open');
    }

    function handleClose(){
        console.log('BE:WebSocket close');
    }

    function handleError(){
        console.log('BE:WebSocket error');
    }

    function handleConnection(ws){
        console.log('BE:WebSocket connection');
        ws.on('message', handleMessage);
    }

    function handleMessage(msg){
        console.log(msg);
        var e = JSON.parse(msg);
        server.clients.forEach((c) => {
            // c.send(msg);
            if (e.event_id != 17)
                c.send(JSON.stringify({
                    "event_id": e.event_id,
                    "data":{
                        "ack": 1,
                    }
                }))
            else
                c.send(JSON.stringify({
                    "event_id": 17,
                    "data":{
                        "Room_id": "001",
                        "cstate": 0,
                        "Rmode": 0,
                        "tem": 25,
                        "wind": 2
                    }
                }))
        })
    }


    init();
})(Ws);