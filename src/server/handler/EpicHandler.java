package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    enum Endpoint {GET_All, GET_BY_ID, GET_SUBTASKS, POST, DELETE, UNKNOWN}

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
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
                ArrayList<Epic> AllEpic = taskManager.getEpics();
                if (AllEpic.isEmpty()) {
                    sendNotFound(exchange);
                    break;
                }
                String AllEpicsJsonString = gson.toJson(AllEpic);
                sendText(exchange, AllEpicsJsonString);
                break;
            }
            case GET_BY_ID: {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                int epicId = Integer.parseInt(parts[2]);
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    sendNotFound(exchange);
                    break;
                }
                String taskJsonString = gson.toJson(epic);
                sendText(exchange, taskJsonString);
                break;
            }
            case GET_SUBTASKS: {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                int epicId = Integer.parseInt(parts[2]);
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    sendNotFound(exchange);
                    break;
                } else if (taskManager.getSubtasksOfEpic(epic.getId()) == null) {
                    sendNotFound(exchange);
                    break;
                }
                List<Subtask> SubtasksOfEpic = new ArrayList<>(taskManager.getSubtasksOfEpic(epic.getId()).values());

                String SubtasksOfEpicJsonString = gson.toJson(SubtasksOfEpic);
                sendText(exchange, SubtasksOfEpicJsonString);
                break;
            }
            case POST: {
                String epicJsonString = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epicDeserialized = gson.fromJson(epicJsonString, Epic.class);
                taskManager.add(epicDeserialized);
                sendStatusOnly(exchange);
                break;
            }
            case DELETE: {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                int epicId = Integer.parseInt(parts[2]);
                Epic epic = taskManager.getEpicById(epicId);
                if (epic == null) {
                    sendNotFound(exchange);
                    break;
                }
                taskManager.deleteEpicById(epicId);
                sendStatusOnly(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        if (requestMethod.equals("GET")) {
            if (requestPath.equals("/epics")) {
                return Endpoint.GET_All;
            } else if (requestPath.matches("/epics/[0-9]+/?$")) {
                return Endpoint.GET_BY_ID;
            } else if (requestPath.matches("/epics/[0-9]+/subtasks/?$")) {
                return Endpoint.GET_SUBTASKS; // Новый эндпоинт
            }
        } else if (requestMethod.equals("POST") && requestPath.equals("/epics")) {
            return Endpoint.POST;
        } else if (requestMethod.equals("DELETE") && requestPath.matches("/epics/[0-9]+/?$")) {
            return Endpoint.DELETE;
        }
        return Endpoint.UNKNOWN;
    }
}
