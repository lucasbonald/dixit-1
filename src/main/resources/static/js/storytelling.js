currState = "Storytelling";

$(document).ready(function(){
  
  // VStorytelling
  
  $('#promptForm').on('submit', function(e){
  	console.log(documnet.cookie)
  	console.log("prompt in storytellingjs called");
  	
  	console.log("form is submitted bro!!")
      // validation code here
      e.preventDefault();
      submitPrompt($("#promptField").value, 12345);

    });
  
});

