<script>
$(document).ready(function() {
  $('#submitmsg').click(function() {

    var message = $('#usermsg').val();

    $('#chatboxborder').append('<p id="username">' + ' says: ' + message + '</p id="username">' + '<br>');
    $('#usermsg').val('');
  });
});
</script>