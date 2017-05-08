function initStorytellerBoard(board) {
  if($("#board").find("#promptField").attr("id") == undefined) {
    $(".promptField-container").prepend("<input type=\"text\" name=\"prompt\" id=\"promptField\" placeholder=\"Please enter your interesting story here\">");
  }
//  $(".picked-cards").html("<div class=\"card picked\" ondrop =\"drop(event)\" ondragover=\"allowDrop(event)\"><div class=\"image bigimg\" style=\"background-image: url(../img/blank.jpg)\"></div></div>");
  $("#board") .find("#player-submit").val("Submit"); 
  $("#playerInput").removeClass("hidden");

}

function initGuesserBoard() {
  $("#promptField").remove();
  $("#board").find(".formSubmit").val("Guess");
  $("#playerInput").removeClass("hidden");
//
//  $(".picked-cards").html("<div class=\"card picked\" ondrop =\"drop(event)\" ondragover=\"allowDrop(event)\"><div class=\"image bigimg\" style=\"background-image: url(../img/blank.jpg)\"></div></div>");
}

let timer = 0;

function startTimer(seconds) {
//	$("#stopwatchvalue").html(seconds);
  console.log(timer);
	let time = seconds;
	timer = setInterval(function() {
    if (time > 0) {
			$("#stopwatchvalue").html(time >= 10 ? "00:" + time : "00:0" + time);
      time -= 1;
      if (time <= 5) {
        $("#stopwatchvalue").append("<br><span style=\"font-size:1vw;color:red;\">A decision is going to be made for you in " + time + "...</span>");
      }
		} else {
			$("#stopwatchvalue").html("<span style = \'font-size: 4vw; color: red;\'>Time's Up!</span>");
      
      if (currState == "Guessing") {
        //send random guess
        hand = [];
        $(".hand-card").each(function() {
          hand.push(getCardInfo($(this).find(".image")));
        })
        const randomCard = hand[Math.floor(Math.random()*hand.length)];
        sendGuess(randomCard.id);
      }
      
      if (currState == "Voting") {
    	  hand = [];
          $(".hand-card").each(function() {
            hand.push(getCardInfo($(this).find(".image")));
          })
          const randomCard = hand[Math.floor(Math.random()*hand.length)];
          sendVote(randomCard.id);
      }
		}
	}, 1000);
}

function stopTimer() {
  clearInterval(timer);
}

function setStoryTeller (st) {
  storyteller = st.user_id;
	$("#st-identity").html(st.user_name);
}


function setStatus (status) {
  currState = status;
	$("#status-indicator-text").html(status);
  if (status == "Storytelling") {
    $("#status-indicator").css("background-color", "#FFDF3C");
  } else if (status == "Guessing") {
    $("#status-indicator").css("background-color", "#16C69E");
  } else if (status == "Voting") {
    $("#status-indicator").css("background-color", "#FF9494");
  }
  
}

function updateStatus(statusMap) {
	let players = Object.keys(statusMap);
	for (let i = 0; i < players.length; i ++) {
    $("#scoreboard").find("#" + players[i] + "status").html(statusMap[players[i]]);
	}
} 

function updatePoints(points) {
  for (player of Object.keys(points)) {
    let currPoints = parseInt($("#" + player + "points").html());
    $("#" + player + "points").html(points[player] + currPoints); 
  }
  
}



function displayPoints(points) {
  for (id of Object.keys(points)) {
    if (id == myId) {
      console.log(id)
      $("#results-message").html("You received " + points[id] + " points!");
    }
  }
  $(".results-overlay").removeClass("hidden");
  
}

function displayWinner(winner){
  $("#results-message").html(winner.winner_name + " won, with " + $("#" + winner.winner_id + "points").html() + " points!");
  $(".results-overlay").removeClass("hidden");
  $("#play-again-button").removeClass("hidden");
}

function sendRestartIntent() {
  const restartIntent = {
    type: MESSAGE_TYPE.RESTART,
    payload: {
      game_id: getElementFromCookies("gameid")
    }
  }
  conn.send(JSON.stringify(restartIntent));
}

function newRound(details) {
  $(".results-overlay").toggleClass("hidden");
  let newHand = details.hand;
  let oldHand = $(".hand").html();
  
  // if you were the old storyteller
  if (myId == storyteller) {
    initGuesserBoard();
  // if you're now the new storyteller
  } else if (myId == details.storyteller.user_id) {
    initStorytellerBoard();
  } 

  // clear stopwatch, prompt and picked cards
  $(".picked-cards").html("<div class=\"card\"><div class=\"image bigimg\" ondrop =\"drop(event)\" ondragover=\"allowDrop(event)\" style=\"background-image: url(../img/blank.jpg)\"></div></div></div>");
  $("#promptValue").empty();
  $("#stopwatchvalue").empty();
  
  // set new storyteller
  setStoryTeller(details.storyteller);
  setStatus("Storytelling");
  
  // add the new card
  for (card in Object.keys(newHand)) {
    let cardDetails = newHand[card].split(":");
    let cardId = cardDetails[1];
    let cardUrl = cardDetails[3]
    if ($(".hand").find("#" + cardId).attr("id") == undefined) {
      let newCard = ["<div class=\"card hand-card\" draggable = \"true\" ondragstart=\"drag(event)\" data-toggle=\"modal\" data-target=\"#myModal\"><div class=\"image\" id=\"", cardId, "\" style=\"background-image:url(", cardUrl, ");\"></div></div>"]
      $(".hand").append(newCard.join(""));
    }
  }
  
}