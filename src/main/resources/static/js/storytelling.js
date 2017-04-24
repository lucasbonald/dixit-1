currState = "Storytelling";

$(document).ready(function(){
  
  // VStorytelling
  $(".drawn").click(function() {
    console.log($(this).attr('id'));
	console.log("storytelling page should work!")

    const postParameters = {
      player: "Session ID", // how to get session ID?
      vote: $(this).attr('id')
    }
    
    
    $('#promptForm').on('submit', function(e){
    	console.log("form is submitted bro!!")
        // validation code here
        e.preventDefault();
        submitPrompt($("#promptField").value(), 12345);

      });
    
  });
  
});

