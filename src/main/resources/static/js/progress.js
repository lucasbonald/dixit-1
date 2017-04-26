$(document).ready(function() {

	console.log(document.cookie);

	$('#promptForm').on('submit', function(e) {
		console.log("prompt in storytellingjs called");
		console.log("form is submitted!!")
		e.preventDefault();
		submitPrompt($("#promptField").val(), $(".picked").find("img").attr("id"), $(".picked").find("img").attr("src"));
		
	});

  $("#guessForm").on('submit', function (e) {
    console.log("guessed");
    e.preventDefault();
    sendGuess($(".picked").find("img").attr("id"));
  });

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
	$("#status-indicator-text").html(status);
}
function updateStatus(statusMap) {
	console.log("update status, at progress.js");
	$(".feedList").empty();
	let players = Object.keys(statusMap);
	for (let i = 0 ; i < players.length; i ++) {
		$(".feedList").append("<li> <span style=\"color: grey\">" + players[i] + "</span> : " + statusMap[players[i]] + "</li>")
	}
} 




