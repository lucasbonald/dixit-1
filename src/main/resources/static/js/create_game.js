let prevSelected;
let currSelected;
let gameCount = 1;
let newGameId = 1;

$(document).ready(function(){
  
    $("#create-button").click(function() {
      
      console.log("clicked");
      
      $(".create-error-message").empty();
      if($(".lobby-name").val() == "") {
        $(".create-error-message").append("<p style=\"color:red;margin-top:30px;margin-left:30px;\">Please provide a lobby name.</p>")
      } else {
        
        let gameInit = {
          type: MESSAGE_TYPE.CREATE,
          payload: {
            game_id: newGameId,
            name: $(".lobby-name").val(),
            num_players: Number($(".num-players").val()),
            victory_pts: $(".victory-points").val(),
            cards: $(".configure-cards.active").text().trim(),
            story_types: {
              text: $("#story-text").attr("class").includes("active"),
              audio: $("#story-audio").attr("class").includes("active"),
              video: $("#story-video").attr("class").includes("active")  
            }
          }
        }
        
        // send new game information to backend
        new_game(gameInit);
        newGameId++;
        
        // display new available game to allow joining
        $('table.table-hover tbody').append("<tr><td id=\"" + gameInit.game_id + "\">" + gameInit.name + "</td><td id=\"" + gameInit.game_id + "\">1/4</td></tr");
        
      }
      
    });
    
    $('table.table-hover tbody tr').on('click', function () {
      currSelected = $(this).find('td');
      currSelected.toggleClass('selected-row');
      if (prevSelected != undefined) {
        prevSelected.toggleClass('selected-row');
      }
      prevSelected = $(this).find('td');
    });
  
    $("#join-button").click(function() {
      $(".join-error-message").empty();
      if(currSelected == undefined) {
        $(".join-error-message").append("<p style=\"color:red;margin-top:30px;margin-left:30px;\">Please select an available lobby.</p>");
        
      } else {
        console.log(currSelected.attr('id'));
        join_game(currSelected.attr('id'));
      }
 
    });
  
});