let currState = "Voting";

$(document).ready(function(){
  
  // Voting
  $(".drawn").click(function() {
    console.log($(this).attr('id'));
    
    const postParameters = {
      player: "Session ID", // how to get session ID?
      vote: $(this).attr('id')
    }
    
    $.post("/vote", postParameters, responseJSON => {
      
      responseObject = JSON.parse(responseJSON);
      
      // Back-end waits for all the votes before sending response?
      
    });
    
  });
  
});

