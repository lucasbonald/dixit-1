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
				  <img class="card-img-top" src="../img/blank.jpg">
				</div>
			</div>				
			
</#assign>
<#include "play.ftl">
