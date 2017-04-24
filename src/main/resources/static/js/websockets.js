const MESSAGE_TYPE = {
  CONNECT: 0,
  CREATE: 1,
  JOIN: 2,
  GAME_JOINED: 3,
  ALL_JOINED: 4,
  ST_SUBMIT: 5,
  GS_SUBMIT: 6,
  VOTING: 7

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
        console.log("session Id?: " + myId)
//        console.log(myId);
//        console.log('conn '+ conn);
//        console.log('cookie '+ conn.cookie);
//        console.log('document ' + document.cookie);
//        document.id = myId;
//        console.log()
        console.log(document.id);
        break;
      case MESSAGE_TYPE.GAME_JOINED:
        if(payload.num_players == 1) {
          $("table.table-hover tbody").append("<tr><td id=\"" + payload.game_id + "\">" + payload.lobby_name + "</td><td class=\"num_players\" id=\"" + payload.game_id + "\">" + payload.num_players + "/" + payload.capacity + "</td></tr>");
        } else if (payload.num_players > 1) {
          $("table.table-hover tbody").find($(".num_players")).text(payload.num_players + "/" + payload.capacity);
        }
        
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
  connectMessage.payload.user_id = myId;
  console.log(connectMessage);
  conn.send(JSON.stringify(connectMessage));
  document.id = myId;
  console.log(document.id);
}

function submitPrompt(inputPrompt, inputAnswer) {
	const promptMessage = {
		type: MESSAGE_TYPE.ST_SUBMIT,
		payload: {
			prompt: inputPrompt,
			answer: inputAnswer
		}
	}
	conn.send(JSON.stringify(promptMessage));
}

function join_game(gameId) {
  const joinMessage = {
    user_id: myId,
    game_id: gameId
  }
  conn.send(JSON.stringify(joinMessage));
  
}