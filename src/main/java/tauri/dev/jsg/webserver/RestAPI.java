package tauri.dev.jsg.webserver;


import com.sun.net.httpserver.HttpServer;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.webserver.sites.IHttpSite;
import tauri.dev.jsg.webserver.sites.Network;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestAPI {
    protected static HttpServer server = null;

    public static final IHttpSite[] sitesArray = {
            new Network()
    };

    public static void start() {
        if(!JSGConfig.WebServer.general.enabled) return;
        if (server != null) server.stop(0);
        try {
            int port = JSGConfig.WebServer.general.port;
            server = HttpServer.create(new InetSocketAddress(port), 0);
            for (IHttpSite site : sitesArray) {
                server.createContext(site.getPath(), site::handle);
            }
            server.setExecutor(null);
            server.start();
            JSG.logger.info("HTTP Server Listening on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (server == null) return;
        server.stop(0);
        server = null;
        JSG.logger.info("Stopping HTTP Server");
    }
}
