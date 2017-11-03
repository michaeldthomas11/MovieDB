$("#movieDetails").hide();

$(".title").mouseover(function (event) {
    $("#movieDetails").show();
    $("#movieDetails").css({
        top: event.pageY,
        left: event.pageX
    });
    $(this).hide();
});

$(".title").mouseout(function () {
    $("#movieDetails").hide();
    $(this).show();
});