$(document).ready(function() {
  
});
// seconds in the form 15
counter = 1;
function startTimer(seconds) {
	$("#stopwatchvalue").html(seconds);
	let time = seconds;
	let timer = setInterval(function() {
		if (time > 0) {
			time -= 1;
			$("#stopwatchvalue").html(time);
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
      
			clearInterval(timer);
		}
	}, 1000);
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
	console.log("statuses: ");
  console.log(statusMap);
//	$(".feedList").empty();
	let players = Object.keys(statusMap);
	for (let i = 0; i < players.length; i ++) {
    console.log($("#scoreboard").attr("id"));
    $("#scoreboard").find("#" + players[i] + "status").html(statusMap[players[i]]);
    
    console.log($("#" + players[i] + "status"));
    
//		$(".feedList").append("<li> <span style=\"color: grey\">" + players[i] + "</span> : " + statusMap[players[i]] + "</li>")
	}
} 

function updatePoints(points) {
  for (player of Object.keys(points)) {
    $("#" + player + "points").html(points[player]); 
  }
  
}


