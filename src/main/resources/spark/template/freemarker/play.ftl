<#assign content>

<div class = "wrapper">
	<div class = "row screen">
		<div class = "col-xs-9 board" style="background-color: #f7f7f7;">
			<div class = "status-indicator" style="float:left;">
				  <div id = "status-indicator-text">
				  </div>
			</div>
			<div id="board-error-message" style="float:left;">
		  	</div>
			<div class = "prompt" >
			<i id = "promptvalue"></i>
			</div>
			
			${board}
			
			<div class="hand" style="text-align: center">
				<div class="card hand-card" id="card0">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
				<div class="card hand-card" id="card1">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
				<div class="card hand-card" id="card2">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
				<div class="card hand-card" id="card3">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
				<div class="card hand-card" id="card4">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
				<div class="card hand-card" id="card5">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
			</div>

		</div>
		<div class = "col-xs-3 statusbar" style= "border-color:  black; border-width: 5px; background-color: #f5e4e4;">
			<hr>
			<div class = "statuselement" id = "stopwatch">
			<div id = "stopwatchvalue">
			     <span style = "color: transparent;"> -sfwe </white>
			</div>
			</div>
			<hr>
			<div class = "statuselement">
			<span class="statuselement-header">Storyteller</span>
				<div>
				<span id="st-identity">Player</span>
				</div>
			</div>
			<hr>
			<div class="statuselement">
			<span class="statuselement-header">Player Feed</span>
				<br>
				<table id="player-feed">
					<thead>	
						<tr>
							<th>Player</th>
							<th>Status</th>
							<th>Points</th>
						</tr>
					</thead>
					<tbody id="scoreboard">
					</tbody>
				</table>
			</div>
			
			
			<hr>
			<div class = "statuselement" id = "chatroom">
			<span class="statuselement-header">Chat</span>
			<div class = "chatbox">
				<ul class = "chatList">
				</ul>
			</div>
		    <div id = "formWrapper">
		      <form method="POST" action="/whatever" id="messageForm">
		          <input type="text" name="message"   id="messageField">
		          <input type="hidden" name="nickname" id="nicknameField" value="">
		          <input type="hidden" name="time" id="timeField" value="">
		          <input id="chat-submit" type="submit" value="Send">
		      </form>
		    </div>
		    </div>
		</div>
	</div>
	<div class="results-overlay hidden">
		<span id="results-message">You've received <span id="received-points"></span> points.</span>
	</div>
</div>


</#assign>
<#include "main.ftl">