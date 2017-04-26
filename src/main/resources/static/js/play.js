let currState = "Guessing";

$(document).ready(function(){
  
  
  $(".hand-card").click(function(event) {
    
    if (currState == "Storytelling" && myId == /* storyteller's id */) {
        
    } else if (currState == "Guessing" && myId != /* storyteller's id */) {
      
      console.log("clicked card div: " + event.target.id);
      const card_id = $(this).find("img").attr("id");
      // send guess message
      sendGuess(myId, card_id);
      
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

