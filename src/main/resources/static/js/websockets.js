const MESSAGE_TYPE = {
  CONNECT: 0,
  CREATE: 1,
  JOIN: 2,
  NEW_GAME: 3,
  ALL_JOINED: 4,
  ST_SUBMIT: 5,
  GS_SUBMIT: 6,
  ALL_GUESSES: 7,
  VOTE: 8,
  STATUS: 9,
  MULTI_TAB: 10,
  RESULTS: 11,
  CHAT_UPDATE: 12,
  CHAT_MSG: 13
};

let conn;
let storyteller = -1;

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
    console.log("message got!");
    console.log("payload" + payload);
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.MULTI_TAB:
        alert('multi tab opened! Only one tab is allowed');
      case "set_uid":
        updateCookie(payload.cookies[0].name, payload.cookies[0].value)
        updateCookie(payload.cookies[1].name, payload.cookies[1].value)
        //console.log(document.cookies.userid)
        //setgameid(data.payload);
      // connect: get the connected user's ID and use as list of users currently connected
      case MESSAGE_TYPE.CONNECT:
        
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
      
      
      case MESSAGE_TYPE.JOIN:
        if(payload.role == "teller"){
          window.location = window.location.href + "storytelling";
        } else if(payload.role == "guessor"){
            window.location = window.location.href + "guessing";
        }
      break;
      
      case MESSAGE_TYPE.ALL_JOINED:
        console.log("all joined sent");
        const hand = payload.hand;
        console.log(payload.storyteller)
        console.log("storyteller is " + payload.storyteller.user_id);
        // change the img of each hand-card div

        for (card of Object.keys(hand)) {
          console.log("card number: " + card);
          let cardInfo = hand[card].split("url:");
          let url = cardInfo[1];
          let cardId = cardInfo[0].replace("id:", "");
          let $card = $("#card" + card);
          $card.empty();
          $card.append("<div class = \"image\" id=\"" + cardId + "\" style = \"background-image: url(" + url + ");\"></div>" );
          //$card.append("<img id=\"" + cardId + "\" src=\"" + url + "\"></img>");
        }

        setStoryTeller(payload.storyteller);

        // dialog box for each player's screen to see if their ready
        setStatus("Storytelling");
        $("#status-indicator-text").text("Storytelling");

        break;
        
      case MESSAGE_TYPE.ST_SUBMIT:
        let prompt = payload.prompt;
        let cardId = payload.card_id;
        let cardUrl = payload.card_url;
        $("#promptvalue").html("\"" + prompt + "\"" );
        setStatus("Guessing");
        let myId = getElementFromCookies("userid");
        if (myId != storyteller) {
          startTimer(15);  
        }
        break;
      case MESSAGE_TYPE.GS_SUBMIT:
//        let prompt = data.payload.prompt;
//        let answer = data.payload.answer;
      
        break;
      case MESSAGE_TYPE.STATUS:
    	  console.log("updating status, at websockets");
    	  let statusMap = {};
    	  let statuses = JSON.parse(data.payload.statuses);
    	  let playernames = JSON.parse(data.payload.playernames);
    	  console.log(playernames);
        console.log(statuses);

    	  for (let i = 0; i < statuses.length; i ++) {
    		  statusMap[playernames[i]] = statuses[i];
    		  console.log("player names" + playernames[i]);
    	  }
    	  updateStatus(statusMap);
        break;
    	
      case MESSAGE_TYPE.ALL_GUESSES:
        console.log("voting");
        setStatus("Voting");
    	  const answerCardId = payload.answer;
        const guessedCardId = payload.guessed;
        const answerCardUrl = "../img/img" + answerCardId + ".jpg";
        const guessedCardUrl = "../img/img" + guessedCardId + ".jpg";
        $(".picked-cards").empty();
        //$(".picked-cards").append("<div class=\"card picked\"><img id=\"" + answerCardId + "\" src=\"" + answerCardUrl + "\"><div class=\"voters\"></div></div>");

        $(".picked-cards").append("<div class=\"card picked\"><div class = \"image bigimg\" id=\"" + answerCardId + "\" style = \"background-image: url(" + answerCardUrl + "); background-size: cover; background-repeat: no-repeat;\"></div><div class=\"voters\"></div></div>");
        $(".picked-cards").append("<div class=\"card picked\"><div class = \"image bigimg\" id=\"" + guessedCardId + "\" style = \"background-image: url(" + guessedCardUrl + "); background-size: cover; background-repeat: no-repeat;\"></div><div class=\"voters\"></div></div>");
        break;
      
      case MESSAGE_TYPE.VOTE:
        console.log(payload);
        let imgId = payload.card_id;
        let votedCardDiv = $("#" + imgId).parent().find(".voters");
        votedCardDiv.append("<span class=\"voter\">" + payload.user_name + "</span>");
        break;

      case MESSAGE_TYPE.RESULTS:
      console.log(payload)  
    	  
      case MESSAGE_TYPE.CHAT_UPDATE:
    	let messages = JSON.parse(payload.messages);
    	let length = messages.username.length;
    	$(".chatList").empty();
    	for (let i = 0; i < length ; i ++ ) {
        	//$(".chatList").prepend("<li> <span style=\"color: grey\">" + messages.username[length-i-1] + "</span> : " + messages.body[length-i-1]  + "</li>");
        	$(".chatList").append("<li> <span style=\"color: grey\">" + messages.username[i] + "</span> : " + messages.body[i]  + "</li>");

    	} 
    }
  };
}



function setuserid(data){
  console.log("set user id called?")
  console.log(data);
  for(let i=0;i<data.cookies.length; i++){
    if(data.cookies[i].name == "userid"){
      const cook = data.cookies[i];
      setCookie(cook.name, cook.value);
      myId = cook.value;
    }
    if(data.cookies[i].name == "gameid"){
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
function deleteAllCookies() {
  console.log("all cookies deleted");
    let cookies = document.cookie.split(";");
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        let eqPos = cookie.indexOf("=");
        let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
}

function getStoryteller() {
	const storyMessage = {
			type: MESSAGE_TYPE.STORY,
		}
	conn.send(JSON.stringify(storyMessage));
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
  let cookies = document.cookie.split(";");
  for (let i = 0; i < cookies.length; i++) {
    let eqPos = cookies[i].indexOf("=");
    let name = eqPos > -1 ? cookies[i].substr(0, eqPos) : cookies[i];
    if (name == element) {
      let value = eqPos > -1 ? cookies[i].substr(eqPos+1) : "";
      return value;
    }
  }
}

function setCookie(cookiename, cookievalue){
  console.log
  const newcookie = cookiename + "="+cookievalue;
  //console.log(cookiename);
  document.cookie = newcookie;
}

