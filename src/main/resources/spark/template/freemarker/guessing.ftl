<#assign board>		
<script>
	$(document).ready(function() {
	  setup_update();
	});
</script>
 <div class = "pickedcard">
 				<form id="guessForm">
 					<input type="submit" class="formSubmit" id="guesser-button" value="Guess">
 				</form>
				<div class="card picked" >
				  <img class="card-img-top" src="https://s-media-cache-ak0.pinimg.com/736x/c4/f5/5b/c4f55b1202eaa088ca98dead0c88e378.jpg">
				</div>
			</div>				
			
</#assign>
<#include "play.ftl">
