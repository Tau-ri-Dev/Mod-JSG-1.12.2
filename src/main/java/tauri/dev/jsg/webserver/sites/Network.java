package tauri.dev.jsg.webserver.sites;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.StargateNetwork;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;

import java.io.OutputStream;
import java.util.Map;

public class Network implements IHttpSite {
    @Override
    public String getPath() {
        return "/sgnetwork";
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            Map<SymbolTypeEnum, Map<StargateAddress, StargatePos>> network = StargateNetwork.get(JSG.currentServer.getWorld(0)).getMap();
            String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(network);

            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (Exception ignored) {
        }
    }
}
