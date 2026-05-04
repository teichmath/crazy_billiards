'use strict'

//app to draw polymorphic shapes on canvas
var app;

function createApp(canvas) {
    var c = canvas.getContext("2d");

    var drawBall = function(x, y, radius, color) {
    }

    var clear = function() {
    }


    return {
        drawBall: drawBall,
        clear: clear
    }
}


window.onload = function() {
    app = createApp(document.querySelector("canvas"));
}

/**
 * load a paint at a location on the canvas
 */
function loadBall() {
    $.post("/load", { }, function (data, status) {
    }, "json");
}

function switchStrategy() {
    $.post("/switch", { }, function (data, status) {
    }, "json");
}

function updateBallWorld() {
    $.get("/update", function(data, status) {
    }, "json");
}
/**
 * Clear the canvas
 */
function clear() {
}