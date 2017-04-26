let currState = "Guessing";

$(document).ready(function(){
  
  // submitting a card
  $(".hand-card").click(function(event) {
    
    console.log("clicked card div: " + event.target.id);
    const card = $(this).find("img");
    
    if (currState == "Storytelling" && myId == storyteller) {
      
      $(".picked").empty();
      $(".picked").append("<img id=\"" + card.attr('id') + "\" src=\"" + card.attr('src') + "\"></img>");
      
      console.log("storytelling?");
      
    } else if (currState == "Guessing" && myId != storyteller) {
      
      console.log("guessing?");
    
      // send guess message
      sendCard(myId, card_id, MESSAGE_TYPE.GS_SUBMIT);
    }
    
  });
  
  
  // Voting
  $(".drawn").click(function() {
    console.log($(this).attr('id'));
    
    const postParameters = {
      player: "Session ID", // how to get session ID?
      vote: $(this).attr('id')
    }
    
    $.post("/voting", postParameters, responseJSON => {
      
      responseObject = JSON.parse(responseJSON);
      
      // Back-end waits for all the votes before sending response?
      
    });
    
  });
  
});

