const Ws = require('ws');

((Ws)=>{

    const server = new Ws.Server({port: 8000})

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
        // var State = setInterval(() =>{
        //     ws.send(JSON.stringify({
        //         "event_id": 3,
        //         "data":{
        //             "temp": 22,
        //             "speed": 1,
        //             "mode": 0,
        //             "cost": 5.00
        //         }
        //     }))
        // }, 1000)


        // ws.send(JSON.stringify({
        //     "event_id": 6,
        //     "data":{
        //         "interval": 1000
        //     }
        // }))

        ws.on('message', handleMessage);
    }

    function handleMessage(msg){
        console.log(msg);
        server.clients.forEach((c) => {
            c.send(msg);
        })
    }


    init();
})(Ws);