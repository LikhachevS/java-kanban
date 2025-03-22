package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;
import tasks.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    enum Endpoint {GET_HISTORY, UNKNOWN}

    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY: {
                List<Task> history = taskManager.getHistory();
                String histotyJsonString = gson.toJson(history);
                sendText(exchange, histotyJsonString);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestMethod.equals("GET")) {
            if (requestPath.equals("/history")) {
                return Endpoint.GET_HISTORY;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
