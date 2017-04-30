<#assign board>		
<script>
	$(document).ready(function() {
	  setup_update();
	});
</script>
	<div id="guessFormWrapper">
		<form id="guessForm">
			<input type="submit" class="formSubmit" id="guesser-button" value="Guess">
		</form>
		<div class="picked-cards" >
			<div class="card picked">
	  			<img class="card-img-top" src="../img/blank.jpg">
  			</div>	  		
		</div>
	</div>				
			
</#assign>
<#include "play.ftl">
