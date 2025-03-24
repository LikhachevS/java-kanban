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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTest {

    TaskManager manager = Manager.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioritizedTest() throws IOException {
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
    public void testGetPrioritized() throws IOException, InterruptedException {
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

        Set<Task> PrioritizedFromManager = manager.getPrioritizedTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Set<Task> PrioritizedFromTaskServer = gson.fromJson(response.body(), new TaskSetTypeToken().getType());

        assertNotNull(PrioritizedFromTaskServer, "История не возвращаются");
        assertEquals(PrioritizedFromManager.size(), PrioritizedFromTaskServer.size(), "Некорректное количество");
    }
}