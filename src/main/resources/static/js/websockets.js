const MESSAGE_TYPE = {
  CONNECT: 0,
  CREATE: 1,
  JOIN: 2,
  NEW_GAME: 3,
  ALL_JOINED: 4,
  ST_SUBMIT: 5,
  GS_SUBMIT: 6,
  VOTING: 7,
  STATUS: 8
};

const STATUS_TYPE = {
	WAITING: 0,
	STORYTELLING: 1,
	GUESSING: 2,
	GUESSED: 3,
	VOTING: 4,
	VOTED: 5
}

let conn;
let myId = -1;

//set up socket connection and define types
const setup_update = () => {
	console.log("setup update called")
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
      case "set_uid":
        console.log("set uid");
        deleteirrCookies();
        setuserid(data.payload);
        //console.log(document.cookies.userid)
        //setgameid(data.payload);
      // connect: get the connected user's ID and use as list of users currently connected
      case MESSAGE_TYPE.CONNECT:
        //myId = payload.user_id;
        //console.log("session Id?: " + myId)
//        console.log(myId);
//        console.log('conn '+ conn);
//        console.log('cookie '+ conn.cookie);
//        console.log('document ' + document.cookie);
//        document.id = myId;
//        console.log()
        break;
      case MESSAGE_TYPE.NEW_GAME:
        console.log("new game");
        console.log(payload.game_id);
        console.log(payload.num_players);
        
        if(payload.num_players == 1) {
          $("table.table-hover tbody").append("<tr><td id=\"" + payload.game_id + "\">" + payload.lobby_name + "</td><td class=\"num_players\" id=\"" + payload.game_id + "\">" + payload.num_players + "/" + payload.capacity + "</td></tr>");
        } else if (payload.num_players > 1) {
          $("table.table-hover tbody").find($(".num_players")).text(payload.num_players + "/" + payload.capacity);
        }
        break;
      case MESSAGE_TYPE.ALL_JOINED:
        alert('you ready?')
        console.log(payload.deck)
        // dialog box for each player's screen to see if their ready
        setStatus("STORYTELLING");

        break;
      case MESSAGE_TYPE.ST_SUBMIT:
        let prompt = data.payload.prompt;
        let answer = data.payload.answer;
        $("#promptvalue").html("\"" + prompt + "\"" );
        setStatus("STORYTELLING");
        startTimer(15);
        break;
      case MESSAGE_TYPE.GS_SUBMIT:
//        let prompt = data.payload.prompt;
//        let answer = data.payload.answer;
        break;
    }
  };
}

//function create_game(createMessage) {
//  $(".create-error-message").empty();
//  if($(".lobby-name").val() == "" || $("#username").val() == "") {
//    $(".create-error-message").append("<p style=\"color:red;margin-top:30px;margin-left:30px;\">Please fill in all details before proceeding.</p>")
//    return false;
//  } else {
//
//    let gameInit = {
//      type: MESSAGE_TYPE.CREATE,
//      payload: {
//        game_id: newGameId,
//        user_name: $(".username").val(),
//        lobby_name: $(".lobby-name").val(),
//        num_players: Number($(".num-players").val()),
//        victory_pts: $(".victory-points").val(),
//        cards: $(".configure-cards.active").text().trim(),
//        story_types: {
//          text: $("#story-text").attr("class").includes("active"),
//          audio: $("#story-audio").attr("class").includes("active"),
//          video: $("#story-video").attr("class").includes("active")  
//        }
//      }
//    }
//
//    // send new game information to backend
//    conn.send(JSON.stringify(gameInit));
//    newGameId++;
//
//    // display new available game to allow joining
//    $('table.table-hover tbody').append("<tr><td id=\"" + gameInit.payload.game_id + "\">" + gameInit.payload.lobby_name + "</td><td id=\"" + gameInit.payload.game_id + "\">1/" + gameInit.payload.num_players + "</td></tr");
//    
//    
//    return true;
//  }
//}
//
//function join_game(gameId) {
//  $(".join-error-message").empty();
//  if(currSelected == undefined ) {
//    $(".join-error-message").append("<p style=\"color:red;margin-top:30px;margin-left:30px;\">Please select an available lobby.</p>");
//    return false;
//  } else {
//    console.log(currSelected.attr('id'));
//    const joinMessage = {
//      type: MESSAGE_TYPE.JOIN,
//      payload: {
//        user_id: myId,
//        game_id: gameId  
//      }
//    }
//    conn.send(JSON.stringify(joinMessage));
//    return true;
//  }
//
//}

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

function setuserid(data){
  console.log(data);
  for(let i=0;i<data.cookies.length; i++){
    if(data.cookies[i].name == "userid"){
      const cook = data.cookies[i];
      setCookie(cook.name, cook.value);

    }
    if(data.cookies[i].name == "gameid"){
      const cook = data.cookies[i];
      setCookie(cook.name, cook.value);
    }
  }
}

function deleteirrCookies() {
    let cookies = document.cookie.split(";");

    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        let eqPos = cookie.indexOf("=");
        let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        if(name!="userid" && name != "gameid"){
          document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
        }
    }
}



function setCookie(cookiename, cookievalue){
  const newcookie = cookiename + "="+cookievalue;
  document.cookie = newcookie;
}
