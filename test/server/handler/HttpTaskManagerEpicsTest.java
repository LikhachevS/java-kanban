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

public class HttpTaskManagerEpicsTest {

    TaskManager manager = Manager.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Эпик 1", epicsFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(epic);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        List<Epic> epicsFromTaskServer = gson.fromJson(response2.body(), new EpicListTypeToken().getType());

        assertNotNull(epicsFromTaskServer, "Задачи не возвращаются");
        assertEquals(1, epicsFromTaskServer.size(), "Некорректное количество задач");
        assertEquals(epicsFromTaskServer.get(0), manager.getEpicById(1), "Некорректная дисериализация");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(epic);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());

        Epic epicFromTaskServer = gson.fromJson(response2.body(), Epic.class);
        assertNotNull(epicFromTaskServer, "Задачи не возвращаются");
        assertEquals(epicFromTaskServer, manager.getEpicById(1), "Некорректная дисериализация");
    }

    @Test
    public void testGetSubtasksOfEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                6, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1",
                5, Status.NEW, 1, LocalDateTime.now().plusHours(7), Duration.ofHours(1));
        manager.add(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
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
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        manager.add(epic);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode());

        assertTrue(manager.getEpics().isEmpty(), "Задача не удалена");
    }
}