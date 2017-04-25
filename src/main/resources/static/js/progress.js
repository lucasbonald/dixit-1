currState = "Storytelling";
$(document).ready(function() {


	console.log(document.cookie);

	$('#promptForm').on('submit', function(e) {
		console.log(documnet.cookie)
		console.log("prompt in storytellingjs called");

		console.log("form is submitted!!")
		e.preventDefault();
		submitPrompt($("#promptField").value, 12345);

	});

	$('#exit').on('submit', function(e) {
		console.log("exiting");
		e.preventDefault();
		$(this).close();
	});

	//should be according to each game
	setStoryTeller("Player 0");
	
	updateStatus();
	startTimer(2);


});
// seconds in the form 15

function startTimer(seconds) {
	$("#stopwatchvalue").html(seconds);
	let time = seconds;
	setInterval(function() {
		if (time > 0) {
			time -= 1;
			$("#stopwatchvalue").html(time);
		} else {
			$("#stopwatchvalue").html("<span style = \'font-size: 4vw; color: red;\'>Time's Up!</span>");
			//do some alert? send data back to server so they can alert everyone
		}
	}, 1000);
}

function setStoryTeller (storyTeller) {
	$("#userinfovalue").html(storyTeller);
}

function updateStatus() {
	let statusMap = {};
	statusMap["player1"] = "voting";
	statusMap["player2"] = "whatevering";
	statusMap["player3"] = "vsting";
	statusMap["player4"] = "votsfwing";
	$(".feedList").empty();
	let players = Object.keys(statusMap);
	for (let i = 0 ; i < players.length; i ++) {
		$(".feedList").append("<li> <span style=\"color: grey\">" + players[i] + "</span> : " + statusMap[players[i]] + "</li>")
	}
} 

