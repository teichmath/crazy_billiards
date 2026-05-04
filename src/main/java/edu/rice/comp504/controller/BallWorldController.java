package edu.rice.comp504.controller;

import edu.rice.comp504.model.DispatchAdapter;
import com.google.gson.Gson;
import edu.rice.comp504.model.paint.Ball;
import spark.Request;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

/**
 * The controller that creates the dispatch adapter and defines the REST end points
 */
public class BallWorldController {

    private static final ConcurrentHashMap<String, DispatchAdapter> worlds = new ConcurrentHashMap<>();

    private static DispatchAdapter getWorld(Request request) {
        String sid = request.queryParams("sid");
        if (sid == null || sid.isEmpty()) sid = "default";
        return worlds.computeIfAbsent(sid, k -> new DispatchAdapter());
    }

    public static void main(String[] args) {
        port(Integer.parseInt(System.getenv().getOrDefault("PORT", "4567")));

        staticFiles.location("/public");

        String allowedOrigin = System.getenv().getOrDefault("ALLOWED_ORIGIN", "http://localhost:4567");

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", allowedOrigin);
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });

        Gson gson = new Gson();

        post("/load", (request, response) -> {
            DispatchAdapter dis = getWorld(request);
            System.out.println("trying to load");
            Ball b = dis.loadBall(request.body());
            return gson.toJson(b);
        });

        post("/switch", (request, response) -> {
            DispatchAdapter dis = getWorld(request);
            dis.switchStrategy(request.body());
            return gson.toJson(dis);
        });

        get("/update", (request, response) -> {
            DispatchAdapter dis = getWorld(request);
            dis.updateBallWorld();
            return gson.toJson(dis);
        });

        get("/clear", (request, response) -> {
            DispatchAdapter dis = getWorld(request);
            dis.deleteObservers();
            return gson.toJson(dis);
        });

        get("/canvas/:width/:height", (request, response) -> {
            DispatchAdapter dis = getWorld(request);
            int width = Integer.valueOf(request.params(":width"));
            int height = Integer.valueOf(request.params(":height"));
            dis.setCanvasDims(new Point(width, height));
            return gson.toJson(dis);
        });

    }
}
