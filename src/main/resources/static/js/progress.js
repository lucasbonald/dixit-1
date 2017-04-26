currState = "Storytelling";
$(document).ready(function() {


	console.log(document.cookie);

	$('#promptForm').on('submit', function(e) {
		console.log("prompt in storytellingjs called");
		console.log("form is submitted!!")
		e.preventDefault();
		submitPrompt($("#promptField").val(), 12345);

	});


	//should be according to each game
	setStoryTeller("Player 0");
	
	updateStatus();


});
// seconds in the form 15

function startTimer(seconds) {
	$("#stopwatchvalue").html(seconds);
	let time = seconds;
	let timer = setInterval(function() {
		if (time > 0) {
			time -= 1;
			$("#stopwatchvalue").html(time);
		} else {
			$("#stopwatchvalue").html("<span style = \'font-size: 4vw; color: red;\'>Time's Up!</span>");
			clearInterval(timer);
		}
	}, 1000);
}

function setStoryTeller (storyTeller) {
	$("#userinfovalue").html(storyTeller);
}

function setStatus (status) {
	$("#statuscardelement").html(status);
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



