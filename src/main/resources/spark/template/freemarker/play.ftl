<#assign content>

<div class = "wrapper">
	<div class = "row screen">
		<div class = "col-xs-9 board" style="background-color: #f7f7f7;">
			<div class = "status-indicator">
				  <div id = "status-indicator-text">
				    VOTING
				  </div>
			</div>
			<div class = "prompt" >
			<i id = "promptvalue">"Interesting prompt comes here"</i>
			</div>
			
			${board}
			
			<div class="hand" style="text-align: center">
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
				<div class="card hand-card" id="card6">
				  <img src="http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg">
				</div>
			</div>

		</div>
		<ul class = "col-xs-3 statusbar" style= "border-color:  black; border-width: 5px; background-color: #f5e4e4;">
			<hr>
			<div class = "statuselement" id = "stopwatch">
			<div id = "stopwatchvalue">
			     <span style = "color: transparent;"> -sfwe </white>
			</div>
			</div>
			<hr>
			<div class = "statuselement">
			STORYTELLER
				<div>
				<span id="st-identity">Player</span>
				</div>
			</div>
			<hr>
			<div class = "statuselement" id = "statusfeed">
			STATUS FEED
			<div class = "feed">
				<ul class = "feedList">
				sdfwef
				</ul>
			</div>
			</div>
			<hr>
			<div class = "statuselement" id = "chatroom">
			CHAT
			<div class = "chatbox">
				<ul class = "chatList">
					<li>  <span style="color: grey">Player 1</span> : yoyo whatup </li>
					<li>  <span style="color: grey">Player 2</span> : not much you? </li>
					<li>  <span style="color: grey">Player 3</span> : playing dixit</li>
					<li>  <span style="color: grey">Player 2</span> : cool how is it </li>
					<li>  <span style="color: grey">Player 3</span> : not bad not bad </li>
					<li>  <span style="color: #fb8383">Player 4</span> : alright cool </li>
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
		</ul>
	</div>

</div>

</#assign>
<#include "main.ftl">