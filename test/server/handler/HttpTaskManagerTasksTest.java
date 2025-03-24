package server.handler;

import com.google.gson.Gson;
import enams.Status;
import interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import server.HttpTaskServer;
import service.Manager;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    TaskManager manager = Manager.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSimpleTask() throws IOException, InterruptedException {
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(5));
        String taskJson = gson.toJson(simpleTask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getSimpleTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Простая задача 1", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");

        Task simpleTask2 = new Task("Простая задача 2", "Описание простой задачи 2", 2, Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        String task2Json = gson.toJson(simpleTask2);

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testGetSimpleTasks() throws IOException, InterruptedException {
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        Task simpleTask2 = new Task("Простая задача 2", "Описание простой задачи 2", 2, Status.NEW,
                LocalDateTime.now().plusHours(3), Duration.ofHours(1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(simpleTask1);
        manager.add(simpleTask2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Task> tasksFromTaskServer = gson.fromJson(response2.body(), new TaskListTypeToken().getType());

        assertNotNull(tasksFromTaskServer, "Задачи не возвращаются");
        assertEquals(2, tasksFromTaskServer.size(), "Некорректное количество задач");
        assertEquals(tasksFromTaskServer.get(0), manager.getSimpleTaskById(1), "Некорректная дисериализация");
        assertEquals(tasksFromTaskServer.get(1), manager.getSimpleTaskById(2), "Некорректная дисериализация");
    }

    @Test
    public void testGetSimpleTaskById() throws IOException, InterruptedException {
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(simpleTask1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        Task taskFromTaskServer = gson.fromJson(response2.body(), Task.class);
        assertNotNull(taskFromTaskServer, "Задачи не возвращаются");
        assertEquals(taskFromTaskServer, manager.getSimpleTaskById(1), "Некорректная дисериализация");
    }

    @Test
    public void testDeleteSimpleTaskById() throws IOException, InterruptedException {
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(simpleTask1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        assertTrue(manager.getSimpleTasks().isEmpty(), "Задача не удалена");
    }
}