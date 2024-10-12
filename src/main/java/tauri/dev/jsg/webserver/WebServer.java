package tauri.dev.jsg.webserver;

import io.javalin.Javalin;
import io.javalin.http.Context;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;

import java.util.Objects;

public class WebServer {
    public static boolean isEnabled() {
        return JSGConfig.WebServer.general.enabled;
    }

    public static boolean running = false;

    private static Javalin app = null;

    public static void start() {
        if (!isEnabled()) return;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(JSG.instance.getClass().getClassLoader());
        running = true;
        app = Javalin.create()
                .start(JSGConfig.WebServer.general.port);
        Thread.currentThread().setContextClassLoader(classLoader);

        registerNetworkAPI();
    }

    public static void stop() {
        if (!running) return;
        if (app == null) return;
        app.stop();
        app = null;
        running = false;
    }

    public static boolean authenticate(Context ctx) {
        String token = ctx.header("Auth");
        return Objects.equals(JSGConfig.WebServer.general.token, "") || (token != null && token.equals(JSGConfig.WebServer.general.token));
    }

    public static void registerNetworkAPI() {
        app.get("/sgnetwork", (ctx) -> {
            if (!authenticate(ctx)) {
                ctx.result("Not authenticated!");
                ctx.status(403);
                return;
            }
            ctx.result("test!");
        });
    }
}
