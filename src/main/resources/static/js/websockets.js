const MESSAGE_TYPE = {
  CONNECT: 0,
  CREATE: 1,
  JOIN: 2,
  NEW_GAME: 3,
  ALL_JOINED: 4,
  ST_SUBMIT: 5,
  GS_SUBMIT: 6,
  VOTING: 7,
  STATUS: 8,
  QUERRY: 9
};


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
      case MESSAGE_TYPE.MULTI_TAB:
        alert('multi tab opened! Only one tab is allowed');
      case "set_uid":
        console.log("set uid");
        deleteirrCookies();
        setuserid(data.payload);
        //console.log(document.cookies.userid)
        //setgameid(data.payload);
      // connect: get the connected user's ID and use as list of users currently connected
      case MESSAGE_TYPE.CONNECT:
        
        break;
      case MESSAGE_TYPE.NEW_GAME:
        console.log("new game");
        console.log(payload.game_id);
        updateCookie("gameid", payload.game_id);
        console.log(payload.num_players);
        
        if(payload.num_players == 1) {
          $("table.table-hover tbody").append("<tr><td id=\"" + payload.game_id + "\">" + payload.lobby_name + "</td><td class=\"num_players\" id=\"" + payload.game_id + "\">" + payload.num_players + "/" + payload.capacity + "</td></tr>");
        } else if (payload.num_players > 1) {
          $("table.table-hover tbody").find($(".num_players")).text(payload.num_players + "/" + payload.capacity);
        }
        break;
      case MESSAGE_TYPE.ALL_JOINED:
        console.log("all joined sent");
        alert('you ready?');

        console.log(payload.hand);
        // console.log(JSON.parse(payload.deck))
        const hand = payload.hand;
        
        // change the img of each hand-card div
        for (card of Object.keys(hand)) {
          let cardInfo = hand[card].split("url:");
          let url = cardInfo[1];
          let cardId = cardInfo[0].replace("id:", "");
          let $card = $("#card" + card);
          $card.empty();
          $card.append("<img id=\"" + cardId + "\" src=\"" + url + "\"></img>");
        }
        
        if (payload.storyteller == getElementFromCookies("userid", document.cookie)) {
          $("st-identity").text("You");
        } else {
          
        }
        
        // dialog box for each player's screen to see if their ready
        setStatus("STORYTELLING");
        $("#status-indicator-text").text("Storytelling");

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
  console.log("set user id called?")
  console.log(data);
  for(let i=0;i<data.cookies.length; i++){
    if(data.cookies[i].name == "userid"){
      const cook = data.cookies[i];
      setCookie(cook.name, cook.value);
    }
  }
}


function updateCookie(cookiename, cookievalue){
    let cookies = document.cookie.split(";");
    console.log("update cookies called");

    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        let eqPos = cookie.indexOf("=");
        let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        if(name==cookiename){
          document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
        }
    }
    setCookie(cookiename, cookievalue);
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

function sendQuery(){
  let uid = getElementFromCookies("userid");
  let gid = getElementFromCookies("gameid"); 
  const queryMessage = {
    type: MESSAGE_TYPE.QUERRY,
    payload: {
      userid: uid,
      gameid: gid
    }
  }
  conn.send(JSON.stringify(queryMessage));
}

function getElementFromCookies(element) {
  let cookies = cookie.split(";");
  for (let i = 0; i < cookies.length; i++) {
    let eqPos = cookies[i].indexOf("=");
    let name = eqPos > -1 ? cookies[i].substr(0, eqPos) : cookies[i];
    if (name == element) {
      let value = eqPos >-1? cookies[i].substr(eqPos+1) : "";
      return value;
    }
  }
}



function setCookie(cookiename, cookievalue){
  const newcookie = cookiename + "="+cookievalue;
  //console.log(cookiename);
  document.cookie = newcookie;
}

