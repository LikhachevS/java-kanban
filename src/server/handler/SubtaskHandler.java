package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    enum Endpoint {GET_All, GET_BY_ID, POST, DELETE, UNKNOWN}

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
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
            case GET_All: {
                ArrayList<Subtask> AllSubtask = taskManager.getSubtasks();
                if (AllSubtask.isEmpty()) {
                    sendNotFound(exchange);
                    break;
                }
                String AllSubtasksJsonString = gson.toJson(AllSubtask);
                sendText(exchange, AllSubtasksJsonString);
                break;
            }
            case GET_BY_ID: {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                int subtaskId = Integer.parseInt(parts[2]);
                Subtask subtask = taskManager.getSubtaskById(subtaskId);
                if (subtask == null) {
                    sendNotFound(exchange);
                    break;
                }
                String taskJsonString = gson.toJson(subtask);
                sendText(exchange, taskJsonString);
                break;
            }
            case POST: {
                String subtaskJsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtaskDeserialized = gson.fromJson(subtaskJsonString, Subtask.class);
                List<Subtask> subtasks = taskManager.getSubtasks();

                Optional<Subtask> subtaskWithTargetId = subtasks.stream()
                        .filter(subtask -> subtask.getId() == subtaskDeserialized.getId())
                        .findFirst();
                try {
                    if (subtaskWithTargetId.isPresent()) {
                        taskManager.update(subtaskDeserialized);
                    } else {
                        taskManager.add(subtaskDeserialized);
                    }
                } catch (RuntimeException e) {
                    sendHasInteractions(exchange);
                    break;
                }
                sendStatusOnly(exchange);
                break;
            }
            case DELETE: {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                int subtaskId = Integer.parseInt(parts[2]);
                Subtask subtask = taskManager.getSubtaskById(subtaskId);
                if (subtask == null) {
                    sendNotFound(exchange);
                    break;
                }
                taskManager.deleteSubtaskById(subtaskId);
                sendStatusOnly(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestMethod.equals("GET")) {
            if (requestPath.equals("/subtasks")) {
                return Endpoint.GET_All;
            } else if (requestPath.matches("/subtasks/[0-9]+/?$")) {
                return Endpoint.GET_BY_ID;
            }
        } else if (requestMethod.equals("POST") && requestPath.equals("/subtasks")) {
            return Endpoint.POST;
        } else if (requestMethod.equals("DELETE") && requestPath.matches("/subtasks/[0-9]+/?$")) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }
}
