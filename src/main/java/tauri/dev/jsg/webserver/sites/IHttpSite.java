package tauri.dev.jsg.webserver.sites;

import com.sun.net.httpserver.HttpExchange;

public interface IHttpSite {
    String getPath();
    void handle(HttpExchange exchange);
}
