let prevSelected;

$(document).ready(function(){
  
    $('table.table-hover tbody tr').on('click', function () {
      $(this).find('td').toggleClass('selected-row');
      if (prevSelected != undefined) {
        prevSelected.toggleClass('selected-row');
      }
      prevSelected = $(this).find('td');
    });
  
});