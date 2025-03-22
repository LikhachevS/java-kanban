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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    enum Endpoint { GET_All, GET_BY_ID, POST, DELETE, UNKNOWN }

    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
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
                ArrayList<Task> allTasks = taskManager.getSimpleTasks();
                if (allTasks.isEmpty()) {
                    sendNotFound(exchange);
                    break;
                }
                String allTasksJsonString = gson.toJson(allTasks);
                sendText(exchange, allTasksJsonString);
                break;
            }
            case GET_BY_ID: {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                int taskId = Integer.parseInt(parts[2]);
                Task task = taskManager.getSimpleTaskById(taskId);
                if (task == null) {
                    sendNotFound(exchange);
                    break;
                }
                String taskJsonString = gson.toJson(task);
                sendText(exchange, taskJsonString);
                break;
            }
            case POST: {
                String taskJsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task taskDeserialized = gson.fromJson(taskJsonString, Task.class);
                List<Task> simpleTasks = taskManager.getSimpleTasks();

                Optional<Task> taskWithTargetId = simpleTasks.stream()
                        .filter(task -> task.getId() == taskDeserialized.getId())
                        .findFirst();
                try {
                    if (taskWithTargetId.isPresent()) {
                        taskManager.update(taskDeserialized);
                    } else {
                        taskManager.add(taskDeserialized);
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
                int taskId = Integer.parseInt(parts[2]);
                Task task = taskManager.getSimpleTaskById(taskId);
                if (task == null) {
                    sendNotFound(exchange);
                    break;
                }
                taskManager.deleteTaskById(taskId);
                sendStatusOnly(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestMethod.equals("GET")) {
            if (requestPath.equals("/tasks")) {
                return Endpoint.GET_All;
            } else if (requestPath.matches("/tasks/[0-9]+/?$")) {
                return Endpoint.GET_BY_ID;
            }
        } else if (requestMethod.equals("POST") && requestPath.equals("/tasks")) {
            return Endpoint.POST;
        } else if (requestMethod.equals("DELETE") && requestPath.matches("/tasks/[0-9]+/?$")) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }
}
