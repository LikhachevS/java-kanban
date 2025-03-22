package server.handler;

import com.google.gson.Gson;
import enams.Status;
import interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import service.Manager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {

    TaskManager manager = Manager.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        manager.add(epic1);

        String subtaskJson = gson.toJson(subtask1OfEpic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Подзадача 1 эпика 1", subtasksFromManager.get(0).getTitle(), "Некорректное имя задачи");

        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1",
                5, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        String subtask2Json = gson.toJson(subtask2OfEpic1);

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response2.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                6, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1",
                5, Status.NEW, 1, LocalDateTime.now().plusHours(7), Duration.ofHours(1));
        manager.add(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(subtask1OfEpic1);
        manager.add(subtask2OfEpic1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Subtask> subtasksFromTaskServer = gson.fromJson(response2.body(), new SubtaskListTypeToken().getType());

        assertNotNull(subtasksFromTaskServer, "Задачи не возвращаются");
        assertEquals(2, subtasksFromTaskServer.size(), "Некорректное количество задач");
        assertEquals(subtasksFromTaskServer.get(0), manager.getSubtaskById(2), "Некорректная дисериализация");
        assertEquals(subtasksFromTaskServer.get(1), manager.getSubtaskById(3), "Некорректная дисериализация");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                6, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        manager.add(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(subtask1OfEpic1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        Subtask subtaskFromTaskServer = gson.fromJson(response2.body(), Subtask.class);
        assertNotNull(subtaskFromTaskServer, "Задачи не возвращаются");
        assertEquals(subtaskFromTaskServer, manager.getSubtaskById(2), "Некорректная дисериализация");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                6, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        manager.add(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(subtask1OfEpic1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        assertTrue(manager.getSubtasks().isEmpty(), "Задача не удалена");
    }
}