let currState = "Guessing";

$(document).ready(function(){
  
  // submitting a card
  $(".hand-card").click(function(event) {
    
    console.log("clicked card div: " + event.target.id);
    const card = $(this).find("img");
    
    $(".picked").empty();
    $(".picked").append("<img id=\"" + card.attr('id') + "\" src=\"" + card.attr('src') + "\"></img>");
    // if (currState == "Storytelling" && myId == storyteller) {
      
    //   $(".picked").empty();
    //   $(".picked").append("<img id=\"" + card.attr('id') + "\" src=\"" + card.attr('src') + "\"></img>");
      
    //   console.log("storytelling?");
      
    // } else if (currState == "Guessing" && myId != storyteller) {
      
    //   console.log("guessing?");
    
    //   // send guess message
    //   sendCard(myId, card.attr('id'), MESSAGE_TYPE.GS_SUBMIT);
    // }
    
  });
  
  
  
  
  
  
});

