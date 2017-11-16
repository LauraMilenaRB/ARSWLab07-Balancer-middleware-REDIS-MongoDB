var app = (function () {

    var nombreJugador = "NN";

    var stompClient = null;
    var gameid = 0;

    return {
        loadWord: function () {

            gameid = $("#gameid").val();

            $.get("/hangmangames/" + gameid + "/currentword",
                    function (data) {
                        $("#palabra").html("<h1>" + data + "</h1>");
                        app.wsconnect();
                    }
            ).fail(
                    function (data) {
                        alert(data["responseText"]);
                    }

            );


        }
        ,
        wsconnect: function () {

            var socket = new SockJS('/stompendpoint');
            stompClient = Stomp.over(socket);
            /*Configuration normal
            stompClient.connect({}, function (frame) {

                console.log('Connected: ' + frame);
                stompClient.subscribe('/topic/wupdate.' + gameid, function (eventbody) {
                    var new_word = eventbody.body;
                    $("#palabra").html("<h1>" + new_word + "</h1>");
                });
                stompClient.subscribe('/topic/winner.' + gameid, function (eventbody) {
                    var winner = eventbody.body;
                    $("#estado").text("Estado: Finalizado.");
                    $("#ganador").text("Ganador: " + winner);
                    alert("El jugador " + winner + " ha ganado.");
                 });

            });*/
            //Configuration rabbitMQ
            stompClient.connect("dwmvuoia","sNnf7ADSvIHcU3CgfUlDK7OOBlIQrk_9", 

                function(frame){
                    console.log('Connected: ' + frame);
                    //subscriptions
                    stompClient.subscribe('/topic/wupdate.' + gameid, function (eventbody) {
                        var new_word = eventbody.body;
                        $("#palabra").html("<h1>" + new_word + "</h1>");
                    });

                    stompClient.subscribe('/topic/winner.' + gameid, function (eventbody) {
                        var winner = eventbody.body;
                        $("#estado").text("Estado: Finalizado.");
                        $("#ganador").text("Ganador: " + winner);
                        alert("El jugador " + winner + " ha ganado.");
                    });

                }
                , 
                function(error){
                    console.info("error"+error);
                }

            , "dwmvuoia");
            

        },
        sendLetter: function () {

            var id = gameid;

            var hangmanLetterAttempt = {letter: $("#caracter").val(), username: nombreJugador};

            console.info("Gameid:" + gameid + ",Sending v2:" + JSON.stringify(hangmanLetterAttempt));
            jQuery.ajax({
                url: "/hangmangames/" + id + "/letterattempts",
                type: "POST",
                data: JSON.stringify(hangmanLetterAttempt),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function () {
                    //
                }
            });


        },
        loadUser: function () {
            var userid = $("#playerid").val();
            $.get("/users/" + userid, function (data) {
                nombreJugador = data.name;
                $("#userimg").attr("src", data.photoUrl);
                $("#username").text("Nombre: " + nombreJugador);
            }).fail(function () {
                alert("User " + userid + " doesn't exist");
            }
            );
        },
        sendWord: function () {

            var hangmanWordAttempt = {word: $("#adivina").val(), username: nombreJugador};

            var id = gameid;

            jQuery.ajax({
                url: "/hangmangames/" + id + "/wordattempts",
                type: "POST",
                data: JSON.stringify(hangmanWordAttempt),
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                success: function () {
                    //
                }
            });


        }

    };

})();

