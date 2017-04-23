const MESSAGE_TYPE = {
  CONNECT: 0,
  CREATE: 1,
  JOIN: 2,
  ALL_JOINED: 3,
  ST_SUBMIT: 4,
  GS_SUBMIT: 5,
  VOTING: 6,

};


let conn;
let myId = -1;

//set up socket connection and define types
const setup_update = () => {
	conn = new WebSocket("ws://localhost:4567/play");

  console.log(conn);
  
	conn.onerror = err => {
    	console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    const payload = data.payload;
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
        
      // connect: get the connected user's ID and use as list of users currently connected
      case MESSAGE_TYPE.CONNECT:
        myId = payload.user_id;
        break;
      case MESSAGE_TYPE.ALL_JOINED:
        // dialog box for each player's screen to see if their ready
        break;
      case MESSAGE_TYPE.ST_SUBMIT:
//        let prompt = data.payload.prompt;
//        let answer = data.payload.answer;
        break;
      case MESSAGE_TYPE.GS_SUBMIT:
//        let prompt = data.payload.prompt;
//        let answer = data.payload.answer;
        break;
    }
  };
}

function new_game(connectMessage) {
  connectMessage.user_id = myId;
  console.log(connectMessage);
  conn.send(JSON.stringify(connectMessage));
}

function join_game(gameId) {
  const joinMessage = {
    user_id: myId,
    game_id: gameId
  }
  conn.send(JSON.stringify(joinMessage));
  
}