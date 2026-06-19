package edu.rice.comp504.controller;

import edu.rice.comp504.model.DispatchAdapter;
import com.google.gson.Gson;
import edu.rice.comp504.model.paint.Ball;
import spark.Request;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static spark.Spark.*;

/**
 * The controller that creates the dispatch adapter and defines the REST end points
 */
public class BallWorldController {

    private static final long SESSION_TIMEOUT_MS = 30_000;

    private static final ConcurrentHashMap<String, DispatchAdapter> worlds = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastSeen = new ConcurrentHashMap<>();

    private static DispatchAdapter getWorld(Request request) {
        String sid = request.queryParams("sid");
        if (sid == null || sid.isEmpty()) sid = "default";
        lastSeen.put(sid, System.currentTimeMillis());
        return worlds.computeIfAbsent(sid, k -> new DispatchAdapter());
    }

    private static void startSessionReaper() {
        ScheduledExecutorService reaper = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "session-reaper");
            t.setDaemon(true);
            return t;
        });
        reaper.scheduleAtFixedRate(() -> {
            long cutoff = System.currentTimeMillis() - SESSION_TIMEOUT_MS;
            lastSeen.forEach((sid, ts) -> {
                if (ts < cutoff) {
                    worlds.remove(sid);
                    lastSeen.remove(sid);
                }
            });
        }, 10, 10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        port(Integer.parseInt(System.getenv().getOrDefault("PORT", "4567")));
        startSessionReaper();

        staticFiles.location("/public");

        String allowedOrigin = System.getenv().getOrDefault("ALLOWED_ORIGIN", "http://localhost:4567");

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", allowedOrigin);
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type");
            response.header("Cache-Control", "no-store");
        });

        options("/*", (request, response) -> {
            response.status(200);
            return "OK";
        });

        Gson gson = new Gson();

        post("/load", (request, response) -> {
            String body = request.body();
            DispatchAdapter dis = getWorld(request);
            System.out.println("trying to load");
            Ball b = dis.loadBall(body);
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

        post("/impulse", (request, response) -> {
            String body = request.body();
            DispatchAdapter dis = getWorld(request);
            double x = 0, y = 0, angle = 0, strength = 5, spin = 0;
            for (String pair : body.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length != 2) continue;
                switch (kv[0]) {
                    case "x":        x        = Double.parseDouble(kv[1]); break;
                    case "y":        y        = Double.parseDouble(kv[1]); break;
                    case "angle":    angle    = Double.parseDouble(kv[1]); break;
                    case "strength": strength = Double.parseDouble(kv[1]); break;
                    case "spin":     spin     = Double.parseDouble(kv[1]); break;
                }
            }
            dis.applyImpulse(x, y, angle, strength, spin);
            return "{}";
        });

        post("/pocket", (request, response) -> {
            String body = request.body();
            DispatchAdapter dis = getWorld(request);
            double x = 0, y = 0;
            int radius = 20;
            for (String pair : body.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length != 2) continue;
                switch (kv[0]) {
                    case "x":      x      = Double.parseDouble(kv[1]); break;
                    case "y":      y      = Double.parseDouble(kv[1]); break;
                    case "radius": radius = Integer.parseInt(kv[1]);   break;
                }
            }
            boolean added = dis.addPocket(x, y, radius);
            return "{\"added\":" + added + "}";
        });

        get("/clearpockets", (request, response) -> {
            DispatchAdapter dis = getWorld(request);
            dis.clearPockets();
            return "{}";
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
