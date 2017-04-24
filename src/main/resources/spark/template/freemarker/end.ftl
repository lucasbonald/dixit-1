<#assign content>
	<div class = "wrapper" style="background-color: #f7f7f7">
		<div class = "statuscard" id = "endstatus">
			<div id = "statuscardelement">
			    End
			</div>
		</div>
		<div class = "message endmessage">
			END OF GAME
		</div>
		<div class = "thumbswrapper">
			<i class="glyphicon glyphicon-thumbs-up"></i>
		</div>
		<div class = "message winnermessage">
			Player2 Won
		</div>
		<div class = "message statsmessage">
			with 999 Points
		</div>
	 	<div id = "buttonwrapper">
		    <form class = "endform" action="/whatever" id="playagain">
	          	<input class = "endbutton" type="submit" id = "againButton" value="Play Again">
	      	</form>
	      	<form class = "endform" action="/whatever" id="exit">
	          	<input class = "endbutton" type="submit" id = "exitButton" value="Exit Game">
	      	</form>
	    </div>
	</div>
</#assign>
<#include "main.ftl">