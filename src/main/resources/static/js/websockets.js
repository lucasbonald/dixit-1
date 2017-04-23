const MESSAGE_TYPE = {
  CONNECT: 0,
  CREATE: 1,
  JOIN: 2,
  ALL_JOIN: 3,
  ST_SUBMIT: 4,
  GS_SUBMIT: 5,
  VOTING: 6,


};


let conn;
let userId = -1;


//set up socket connection and define types
const setup_update = () => {
	conn = new WebSocket("whatever address we use");

	conn.onerror = err => {
    	console.log('Connection error:', err);
  };


	 conn.onmessage = msg => {
	    const data = JSON.parse(msg.data);
	    switch (data.type) {
	      	default:
	        	console.log('Unknown message type!', data.type);
	        	break;
	        
	        // connect: get the connected user's ID and use as list of users currently connected
		    case MESSAGE_TYPE.JOIN:
		    	let gameId = data.payload.gameId;
		    	let userId = data.payload.userId;
		        break;
	        // create: when create button is pressed, send room info
		    case MESSAGE_TYPE.CREATE:
		    	let roomId = data.payload.roomId;
		   		let lobbyName = data.payload.lobbyName;
		    	let numPlayers = data.payload.numPlayers;
		    	let victoryPts = data.payload.victoryPts;
		    	let card = data.payload.card;
		    	let story = data.payload.story;
		    	break;
	    	case MESSAGE_TYPE.ALL_JOIN
	    		let roomId = data.payload.roomId;
	    		// let's start
	    		break;
	    	case MESSAGE_TYPE.ST_SUBMIT:
	    		let prompt = data.payload.prompt;
	    		let answer = data.payload.answer;
	    		break;
	    	case MESSAGE_TYPE.GS_SUBMIT:
	    		let prompt = data.payload.prompt;
	    		let answer = data.payload.answer;
	    		break;
	    }
	  };
}