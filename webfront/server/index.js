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
        // ws.send(JSON.stringify({
        //     "event_id": 14,
        //     "data":{
        //         "Room_id": "001",
        //         "up_times": 5,
        //         "temp": [{
        //             "id": 1,
        //             "start_time": "2021-05-05 8:00:00",
        //             "stop_time": "2021-05-05 8:00:15",
        //             "start_temp": 23,
        //             "end_temp": 25,
        //             "wind_power": 100,
        //             "cost": 200,
        //             "electricity": 100.0,
        //         },
        //             {
        //                 "id": 2,
        //                 "start_time": "2021-05-06 8:00:00",
        //                 "stop_time": "2021-05-06 8:00:15",
        //                 "start_temp": 24,
        //                 "end_temp": 26,
        //                 "wind_power": 200,
        //                 "cost": 400,
        //                 "electricity": 100.0
        //             }],
        //         "total_cost": 600,
        //     }
        // }))
        // ws.send(JSON.stringify({
        //     "event_id": 17,
        //     "data":[
        //         {
        //             "Room_id": "001",
        //             "cstate": 1,
        //             "Rmode": 0,
        //             "tem": 25,
        //             "wind": 2
        //         },
        //         {
        //             "Room_id": "002",
        //             "cstate": 1,
        //             "Rmode": 0,
        //             "tem": 23,
        //             "wind": 1
        //         },
        //         {
        //             "Room_id": "003",
        //             "cstate": 0,
        //             "Rmode": 0,
        //             "tem": 23,
        //             "wind": 1
        //         }
        //     ],
        // }))
        ws.on('message', handleMessage);
    }

    function handleMessage(msg){
        console.log(msg);
        var e = JSON.parse(msg);
        server.clients.forEach((c) => {
            // c.send(msg);
            // c.send(JSON.stringify({
            //     "event_id": e.event_id,
            //     "data":{
            //         "ack": 1,
            //     }
            // }))
            c.send(JSON.stringify({
                "event_id": 14,
                "data":{
                    "Room_id": "001",
                    "up_times": 5,
                    "temp": [{
                        "id": 1,
                        "start_time": "2021-06-03 8:00:00",
                        "stop_time": "2021-06-03 8:00:15",
                        "start_temp": 23,
                        "end_temp": 25,
                        "wind_power": 100,
                        "cost": 200,
                        "electricity": 100.0,
                    },
                        {
                            "id": 2,
                            "start_time": "2021-06-03 8:00:20",
                            "stop_time": "2021-06-03 8:00:25",
                            "start_temp": 25,
                            "end_temp": 27,
                            "wind_power": 100,
                            "cost": 200,
                            "electricity": 100.0,
                        },
                        {
                            "id": 3,
                            "start_time": "2021-06-05 8:00:00",
                            "stop_time": "2021-06-05 8:00:15",
                            "start_temp": 24,
                            "end_temp": 26,
                            "wind_power": 200,
                            "cost": 400,
                            "electricity": 100.0
                        },
                        {
                            "id": 4,
                            "start_time": "2021-07-06 8:00:00",
                            "stop_time": "2021-07-06 8:00:15",
                            "start_temp": 24,
                            "end_temp": 26,
                            "wind_power": 200,
                            "cost": 400,
                            "electricity": 100.0
                        },
                        {
                            "id": 5,
                            "start_time": "2021-08-06 8:00:20",
                            "stop_time": "2021-08-06 8:00:25",
                            "start_temp": 24,
                            "end_temp": 26,
                            "wind_power": 200,
                            "cost": 400,
                            "electricity": 100.0
                        },
                        {
                            "id": 6,
                            "start_time": "2021-10-06 9:00:20",
                            "stop_time": "2021-10-06 9:00:25",
                            "start_temp": 24,
                            "end_temp": 26,
                            "wind_power": 200,
                            "cost": 400,
                            "electricity": 100.0
                        }
                        ],
                    "total_cost": 600,
                }
            }))
        })
    }


    init();
})(Ws);