let currState = "Storytelling";

$(document).ready(function(){
  
  // selecting a card from the hand for storytelling/voting
  $(".hand-card").click(function(event) {
    
    console.log("clicked card div: " + event.target.id);
    const card = $(this).find("div");
    const img = card.attr("style");
    const url = img.replace(/.*\s?url\([\'\"]?/, '').replace(/[\'\"]?\).*/, '');


    console.log("clicked card url " + url);
     
    //     console.log("url is " + url);

    console.log("card id of licked " + card.attr('id'));
    let myId = getElementFromCookies("userid");
    if ((currState == "Storytelling" && myId == storyteller) || (currState == "Guessing" && myId != storyteller)) {
      $(".picked").empty();
     // $(".picked").append("<img id=\"" + card.attr('id') + "\" src=\"" + card.attr('src') + "\"></img>");
      $(".picked").append("<div class = \"image bigimg\" id=\"" + card.attr('id') + "\" style = \"background-image: url(" + url + "); background-size: cover; background-repeat: no-repeat;\"></div>"
)
      
    } 

  });
  
  // submitting a story, with its associated card
	$('#promptForm').on('submit', function(e) {
		console.log("prompt in storytellingjs called");
		console.log("form is submitted!!")
		e.preventDefault();
    const pickedId = $(".picked").find("div").attr("id");
    const prompt = $("#promptField").val();
    if(pickedId == undefined) {
      $("#board-error-message").text("Please pick a card.");
    } else if (prompt == "") {
      $("#board-error-message").text("Please submit a prompt.");
    } else {
      const img = $(".picked").find("div").attr("style");
      const url = img.replace(/.*\s?url\([\'\"]?/, '').replace(/[\'\"]?\).*/, '');
      console.log("id of prompt here" + pickedId + "url of prompt" + url);
      submitPrompt(prompt, pickedId, url); 
      // remove the selected card
      $(".hand").find("#" + pickedId).parent().remove();
      $("#board-error-message").text("");
      $("#promptForm").toggleClass("hidden");
    }
    sendUpdate();

    
    
    
	});
  
  $(".picked-cards").click(function(event) {
    
    let myId = getElementFromCookies("userid");
    if (currState == "Voting" && myId != storyteller ) {
      console.log("condition met");
      if($(event.target).attr("class") == undefined){
        console.log("class undefined");
        $(".picked").each(function() {
          $(this).removeClass("vote-selected");
        });
        $(event.target).parent().toggleClass("vote-selected");
      } else if ($(event.target).attr("class") == "image bigimg") {
                console.log("class is image bigimg");

        $(".image").each(function() {
          $(this).removeClass("vote-selected");
        });
        $(event.target).toggleClass("vote-selected");
      }
      else {
      console.log("wtf class is " + $(event.target).attr("class"));
    }
    } 
  });
  
  // submitting a guessed card
  $("#guessForm").on('submit', function (e) {
    console.log("guessed");
    e.preventDefault();
    
    if (currState == "Guessing") {
      const pickedId = $(".picked").find("div").attr("id");
      if (pickedId != undefined) {
        sendGuess(pickedId);
        $(".hand").find("#" + pickedId).parent().remove();
        $("#guesser-button").val("Vote");  
      }
    } else if (currState == "Voting") {
      
      const votedId = $(".vote-selected").attr("id");
      console.log("vote id is" + votedId);
      if (votedId != undefined) {
        sendVote(votedId);
        $("#guesser-button").toggleClass("hidden");
      }
    }
    
  });
  
  $(document).click(function (){
    console.log(storyteller);
  })
    
});


function submitPrompt(inputPrompt, card_id, card_url) {
	const promptMessage = {
		type: MESSAGE_TYPE.ST_SUBMIT,
		payload: {
			prompt: inputPrompt,
			card_id: card_id,
      card_url: card_url
		}
	}
  console.log("story: " + promptMessage.toString());
	conn.send(JSON.stringify(promptMessage));
}

function sendGuess(card_id) {
  const guess = {
    type: MESSAGE_TYPE.GS_SUBMIT,
    payload: {
      user_id: getElementFromCookies("userid"),
      card_id: card_id
    }
  }
  conn.send(JSON.stringify(guess));
}


function sendVote(card_id) {
  const vote = {
    type: MESSAGE_TYPE.VOTE,
    payload: {
      user_id: getElementFromCookies("userid"),
      card_id: card_id
    }
  }
  conn.send(JSON.stringify(vote));
}