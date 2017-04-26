

<#assign board>		
  <script>
	$(document).ready(function() {
	  setup_update();
	});
</script>
  <div id = "storyFormWrapper">
		      <form id="promptForm">
		          <input type="text" name="prompt" id="promptField" placeholder = "Please enter your interesting story here" value = "">
		          <input type="hidden" name="nickname" id="nicknameField" value="">
		          <input type="hidden" name="time" id="timeField" value="">
		          <input type="submit" class = "formSubmit" value="Done">
		      </form>
		    </div>
		    <div class = "pickedcard">
				<div class="card deckcard picked" >
				  <img class="card-img-top" src="https://s-media-cache-ak0.pinimg.com/736x/c4/f5/5b/c4f55b1202eaa088ca98dead0c88e378.jpg">
				</div>
			</div>		
			
</#assign>
<#include "play.ftl">
