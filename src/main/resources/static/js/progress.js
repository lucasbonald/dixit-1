currState = "Storytelling";
$(document).ready(function(){
  
  console.log(document.cookie)
  // VStorytelling
  
  $('#promptForm').on('submit', function(e){
  	console.log(documnet.cookie)
  	console.log("prompt in storytellingjs called");
  	
  	console.log("form is submitted!!")
      e.preventDefault();
      submitPrompt($("#promptField").value, 12345);
      
    });
  
  $('#exit').on('submit', function(e){
	  	console.log("exiting");
	    e.preventDefault();
	    close();
  });
});

