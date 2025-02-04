import enams.Status;
import filebacked.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    @Override
    @BeforeEach
    void BeforeEach() {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(null, ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempFile.toFile().deleteOnExit();
        taskManager = new FileBackedTaskManager(tempFile);
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 2);
    }

    @Test
    @Override
    void testAdd() {
        super.testAdd();
    }

    @Test
    @Override
    void testUpdate() {
        super.testUpdate();
    }

    @Test
    @Override
    void testGetSimpleTasks() {
        super.testGetSimpleTasks();
    }

    @Test
    @Override
    void testGetSubtasks() {
        super.testGetSubtasks();
    }

    @Test
    @Override
    void testGetEpics() {
        super.testGetEpics();
    }

    @Test
    @Override
    void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Test
    @Override
    void deleteAllSubtasks() {
        super.deleteAllSubtasks();
    }

    @Test
    @Override
    void deleteAllEpics() {
        super.deleteAllEpics();
    }

    @Test
    @Override
    void getSimpleTaskById() {
        super.getSimpleTaskById();
    }

    @Test
    @Override
    void getSubtaskById() {
        super.getSubtaskById();
    }

    @Test
    @Override
    void getEpicById() {
        super.getEpicById();
    }

    @Test
    @Override
    void deleteTaskById() {
        super.deleteTaskById();
    }

    @Test
    @Override
    void deleteSubtaskById() {
        super.deleteSubtaskById();
    }

    @Test
    @Override
    void deleteEpicById() {
        super.deleteEpicById();
    }

    @Test
    @Override
    void getSubtasksOfEpic() {
        super.getSubtasksOfEpic();
    }

    @Test
    @Override
    void getHistory() {
        super.getHistory();
    }
}