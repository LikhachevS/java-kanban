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
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    enum Endpoint { GET_PRIORITIZED, UNKNOWN }

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager) {
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
            case GET_PRIORITIZED: {
                Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String prioritizedTasksJsonString = gson.toJson(prioritizedTasks);
                sendText(exchange, prioritizedTasksJsonString);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestMethod.equals("GET")) {
            if (requestPath.equals("/prioritized")) {
                return Endpoint.GET_PRIORITIZED;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
