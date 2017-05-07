function initStorytellerBoard(board) {
  if($("#board").find("#promptField").attr("id") == undefined) {
    $("#board").find("#playerInput").prepend("<input type=\"text\" name=\"prompt\" id=\"promptField\" placeholder=\"Please enter your interesting story here\">");
  }
  $("#board") .find(".formSubmit").val("Submit"); 
  $("#playerInput").removeClass("hidden");
}

function initGuesserBoard() {
  $("#promptField").remove();
  $("#board").find(".formSubmit").val("Guess");
  $("#playerInput").removeClass("hidden");
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
		} else {
			$("#stopwatchvalue").html("<span style = \'font-size: 4vw; color: red;\'>Time's Up!</span>");
      
      if (currState == "Guessing") {
        //send random guess
        hand = [];
        $(".hand-card").each(function() {
          hand.push(getCardInfo($(this).find(".image")));
        })
        const randomCard = hand[Math.floor(Math.random()*hand.length)];
        $(".picked").empty();
        $(".picked").append("<div class = \"image bigimg\" id=\"" + randomCard.id + "\" style = \"background-image: url(" + randomCard.url + "); background-size: cover; background-repeat: no-repeat;\"></div>");
        sendGuess(randomCard.id);
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
      $("#received-points").html(points[id]);
    }
  }
  $(".results-overlay").removeClass("hidden");
  
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
  $(".picked-cards").html("<div class=\"card picked\"><div class=\"image bigimg\" style=\"background-image: url(../img/blank.jpg)\"></div></div>");
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
      let newCard = ["<div class=\"card hand-card\"><div class=\"image\" id=\"", cardId, "\" style=\"background-image:url(", cardUrl, ");\"></div></div>"]
      $(".hand").append(newCard.join(""));
    }
  }
  
}