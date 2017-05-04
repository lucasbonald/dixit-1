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
	</div>
	<div class="picked-cards" >
		<div class="card picked">
			<div class = "image bigimg" style ="background-image: url(../img/blank.jpg)"></div>
		</div>	  		
	</div>			
</#assign>
<#include "play.ftl">
