import enams.Status;
import filebacked.FileBackedTaskManager;
import storage.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InMemoryTaskManagerTest{
    @Override
    void BeforeEach() {
        taskManager = new FileBackedTaskManager();
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 2);
    }
}