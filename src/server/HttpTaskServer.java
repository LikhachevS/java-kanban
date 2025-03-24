package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import interfaces.TaskManager;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;
import server.handler.*;
import service.Manager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final HttpServer server;
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .setPrettyPrinting()
            .create();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Привязка обработчиков к эндпоинтам
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void main(String[] args) throws IOException {

        // Создаем и запускаем сервер
        TaskManager manager = Manager.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(manager);
        taskServer.start();

        System.out.println("HTTP-сервер запущен на порту 8080");
    }
}
