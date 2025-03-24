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

public class HttpTaskManagerHistoryTest {

    TaskManager manager = Manager.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                6, Status.NEW, 1, LocalDateTime.now().plusHours(3), Duration.ofHours(1));
        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1",
                5, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 7, Status.NEW,
                LocalDateTime.now().plusHours(7), Duration.ofHours(1));
        manager.add(epic);
        manager.add(subtask1OfEpic1);
        manager.add(subtask2OfEpic1);
        manager.add(simpleTask1);

        manager.getSimpleTaskById(4);
        manager.getSubtaskById(2);
        manager.getSubtaskById(3);
        manager.getEpicById(1);

        List<Task> historyFromManager = manager.getHistory();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> historyFromTaskServer = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(historyFromTaskServer, "История не возвращаются");
        assertEquals(historyFromManager.size(), historyFromTaskServer.size(), "Некорректное количество");
    }
}