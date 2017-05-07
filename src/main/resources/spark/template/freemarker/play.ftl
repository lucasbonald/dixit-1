<#assign content>
<script>
	$(document).ready(function() {
	  setup_update();
	});
</script>

<div class = "wrapper">
	<div class = "row screen">
		<div class = "col-xs-9 board" style="background-color:#50B5D6;">
			<div class = "status-indicator" style="float:left;">
				  <div id = "status-indicator-text">
				  </div>
			</div>
			<div id="board-error-message" style="float:left;">
		  	</div>
			<div class = "prompt" >
				<i id = "promptValue"></i>
			</div>
			
			<div id="board">
				<form id="playerInput">
			      <input type="submit" class="formSubmit" value="Submit">
			  </form>
			  <div class="picked-cards">
					<div class="card picked">
					</div>
				</div>
			</div>
			
			<div class="hand" style="text-align: center">
				<div class="card hand-card" id="card0" draggable = "true" ondragstart="drag(event)" data-toggle="modal" data-target="#myModal">
					<div class="image" style="background-image: url(http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg)"></div>
				</div>
				<div class="card hand-card" id="card1" draggable = "true" ondragstart="drag(event)" data-toggle="modal" data-target="#myModal">
					<div class="image" style="background-image: url(http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg)"></div>
				</div>
				<div class="card hand-card" id="card2" draggable = "true" ondragstart="drag(event)" data-toggle="modal" data-target="#myModal">
					<div class="image" style="background-image: url(http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg)"></div>				
				</div>
				<div class="card hand-card" id="card3" draggable = "true" ondragstart="drag(event)" data-toggle="modal" data-target="#myModal">
					<div class="image" style="background-image: url(http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg)"></div>				
				</div>
				<div class="card hand-card" id="card4" draggable = "true" ondragstart="drag(event)" data-toggle="modal" data-target="#myModal">
					<div class="image" style="background-image: url(http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg)"></div>				
				</div>
				<div class="card hand-card" id="card5" draggable = "true" ondragstart="drag(event)" data-toggle="modal" data-target="#myModal">
					<div class="image" style="background-image: url(http://www.dungeoncrawl.com.au/assets/thumbL/18041.jpg)"></div>
				</div>
			</div>
		</div>
		<div class = "col-xs-3 statusbar">
      <ul class="nav nav-tabs">
        <li class="active"><a data-toggle="tab" href="#player-feed">Player</a></li>
        <li><a data-toggle="tab" href="#chatroom">Chat</a></li>
      </ul>
      <div class="tab-content">
        <div id="player-feed" class="tab-pane fade in active">
          <div class="statuselement" id="user-name">
        </div>
        <div class = "statuselement" id = "stopwatch">
        <div id = "stopwatchvalue">
             <span style = "color: transparent;"> -sfwe </white>
        </div>
        </div>
        <div class = "statuselement">
        <span class="statuselement-header">Storyteller</span>
          <div>
          <span id="st-identity">Player</span>
          </div>
        </div>
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
        </div>
        <div id="chatroom" class="tab-pane fade">
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
		</div>
	</div>
	<div class="results-overlay hidden">
		<span id="results-message"></span>
		<button type="button" class="btn btn-primary hidden" id="play-again-button">Play again</button>
	</div>
</div>

<div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <div class="modal-title">Detailed Card View</div>
      </div>
      <div class="modal-body">
      </div>
      <div class="modal-footer">
      </div>
    </div>

  </div>
</div>

</#assign>
<#include "main.ftl">