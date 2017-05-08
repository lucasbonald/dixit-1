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
  CHAT_MSG: 13,
  END_OF_ROUND: 14,
  LOAD:15,
  RESTART: 16
};

let conn;
let storyteller = -1;
let myId = -1;

//set up socket connection and define types
const setup_update = () => {
  conn = new WebSocket("ws://localhost:4567/connect");
//  conn = new WebSocket("ws://104.196.191.156/connect");  
	conn.onerror = err => {
    	console.log('Connection error:', err);
  };

  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    const payload = data.payload;
    console.log(data.type)
    console.log(data.payload);
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.MULTI_TAB:
        alert('multi tab opened! Only one tab is allowed');
      case "set_uid":
        updateCookie(payload.cookies[0].name, payload.cookies[0].value)
        updateCookie(payload.cookies[1].name, payload.cookies[1].value)
        break;
      case MESSAGE_TYPE.LOAD:
        $("table.table-hover tbody").html("");
        if(payload.gamearray != "none"){
          for(let game in payload.gamearray){
            $("table.table-hover tbody").append("<tr><td id=\"" + payload.gamearray[game].id + "\">" + payload.gamearray[game].name + "</td><td class=\"num_players\" id=\"" + payload.gamearray[game].id + "\">" + payload.gamearray[game].player + "/" + payload.gamearray[game].capacity + "</td></tr>");
          }
        }
        break;
      case MESSAGE_TYPE.NEW_GAME:
        const table = $("table.table-hover tbody");
        table.append("<tr><td id=\"" + payload.game_id + "\">" + payload.lobby_name + "</td><td class=\"num_players\" id=\"" + payload.game_id + "\">" + payload.num_players + "/" + payload.capacity + "</td></tr>");
        break;
      case MESSAGE_TYPE.CONNECT:
        let currurl = window.location.toString();
        const urlMessage = {
          type: MESSAGE_TYPE.CONNECT,
          payload: {
            url: currurl
          }
        }
        conn.send(JSON.stringify(urlMessage));
        break;
      case MESSAGE_TYPE.JOIN:
        window.location = window.location.href + "play";
        break;
      
      case MESSAGE_TYPE.ALL_JOINED:
        const hand = payload.hand;
        console.log('------')
        console.log(hand);
        // change the img of each hand-card div
        for (card of Object.keys(hand)) {
          let cardInfo = hand[card].split(":");
          let url = cardInfo[3];
          let cardId = cardInfo[1];
          let $card = $("#card" + card);
          $card.empty();
          $card.append("<div class = \"image\" id=\"" + cardId + "\" style = \"background-image: url(" + url + ");\"></div>" );
        }

        const players = payload.players;
        $("#scoreboard-body").empty();
        for (player of Object.keys(players)) {
          let player_name = players[player].user_name;
          let player_id = players[player].user_id;
          $("#scoreboard-body").append("<tr><td>" + player_name + "</td><td id=\"" + player_id + "status\"></td><td id=\"" + player_id + "points\">0</td></tr>");
          
          myId = getElementFromCookies("userid");
          if (myId == player_id) {
            $("#user-name").html(player_name);
          }
        }
        
        setStoryTeller(payload.storyteller);

        // dialog box for each player's screen to see if their ready
        setStatus("Storytelling");
        
        myId = getElementFromCookies("userid");
        if (myId == storyteller) {
          initStorytellerBoard($("#board"));
        } else {
          initGuesserBoard($("#board"));
        }
        
        break;
        
      case MESSAGE_TYPE.ST_SUBMIT:
        let prompt = payload.prompt;
        let cardId = payload.card_id;
        let cardUrl = payload.card_url;
        $("#promptValue").html("\"" + prompt + "\"" );
        setStatus("Guessing");
        myId = getElementFromCookies("userid");
        if (myId != storyteller) {
          console.log("timer starting!")
          startTimer(15);  
        }
        break;
        
      case MESSAGE_TYPE.STATUS:
    	  let statusMap = {};
    	  let statuses = JSON.parse(data.payload.statuses);
    	  let playerIds = JSON.parse(data.payload.player_ids);

    	  for (let i = 0; i < statuses.length; i ++) {
    		  statusMap[playerIds[i]] = statuses[i];
    	  }
        updateStatus(statusMap);
        break;
    	
      case MESSAGE_TYPE.ALL_GUESSES:
        console.log("all guesses received");
        setStatus("Voting");
    	  const answerCardId = payload.answer;
        const answerCardUrl = "../img/img" + answerCardId + ".jpg";
        //const guessedCardId = payload.guessed;
        //const guessedCardUrl = "../img/img" + guessedCardId + ".jpg";
          $(".picked-cards").html("<div class=\"card\"><div class = \"image bigimg\" id=\"" + answerCardId + "\" style = \"background-image: url(" + answerCardUrl + "); background-size: cover; background-repeat: no-repeat;\"></div><div class=\"voters\"></div></div>").hide().show('slow', 'swing');
        let guessedCards = payload.guessed;
        for (card in Object.keys(guessedCards)) {
          let cardId = guessedCards[card];
          $(".picked-cards").append("<div class=\"card\"><div class = \"image bigimg\" id=\"" + cardId + "\" style = \"background-image: url(" + "../img/img"+cardId+".jpg"+ "); background-size: cover; background-repeat: no-repeat;\"></div><div class=\"voters\"></div></div>").hide().show('slow', 'swing');
        }

        myId = getElementFromCookies("userid");
        if (myId != storyteller) {
          console.log("timer starting!")
          startTimer(30);  
        }
        
        break;
      
      case MESSAGE_TYPE.VOTE:
        let imgId = payload.card_id;
        let votedCardDiv = $("#" + imgId).parent().find(".voters");
        votedCardDiv.append("<span class=\"voter\">" + payload.user_name + "</span>");
        break;

      case MESSAGE_TYPE.RESULTS:
        updatePoints(payload.points);
        
        console.log(payload.winner);
        if (payload.winner.winner_id != "") {
          console.log ("we have a winner");
          displayWinner(payload.winner);
        } else {
          console.log ("no winner");
          displayPoints(payload.points);
          setTimeout(function() { newRound(payload); }, 5000);
        }
        
        console.log(payload.hand);
    	  break;
      
      case MESSAGE_TYPE.CHAT_UPDATE:
    	let messages = JSON.parse(payload.messages);
    	let length = messages.username.length;
    	$(".chatList").empty();
    	for (let i = 0; i < length ; i ++ ) {
        	$(".chatList").append("<li> <span style=\"color: grey\">" + messages.username[i] + "</span> : " + messages.body[i]  + "</li>");
    	} 
    	$(".chatList").scrollTop($(".chatList")[0].scrollHeight);

    }

  };
}



function setuserid(data){
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
    let cookies = document.cookie.split(";");
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        let eqPos = cookie.indexOf("=");
        let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        if(name!="userid"){
          document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
        }
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
  const newcookie = cookiename + "="+cookievalue;
  document.cookie = newcookie;
}

function isStoryteller() {
  return getElementFromCookies("userid") == storyteller;
}
