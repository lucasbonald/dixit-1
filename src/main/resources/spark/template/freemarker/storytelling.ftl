

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
				  <img class="card-img-top" src="../img/blank.jpg">
				</div>
			</div>		
			
</#assign>
<#include "play.ftl">
