import enams.Status;
import org.junit.jupiter.api.Test;
import typesOfTask.Epic;
import typesOfTask.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testGetId() {
        int taskId = 1;
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", taskId, Status.NEW);
        assertEquals(taskId, simpleTask1.getId(), "Возвращает правильное значение");
    }

    @Test
    void testSetId() {
        int taskId = 5;
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        simpleTask1.setId(taskId);
        assertEquals(taskId, simpleTask1.getId(), "Возвращает правильное значение");
    }

    @Test
    void testGetStatus() {
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        assertEquals(Status.NEW, simpleTask1.getStatus(), "Возвращает правильное значение");
    }

    @Test
    void testSetStatus() {
        Status status = Status.DONE;
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        simpleTask1.setStatus(status);
        assertEquals(status, simpleTask1.getStatus(), "Возвращает правильное значение");
    }

    @Test
    void testTaskEqualsIfIdsAreEqual() {
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        Task simpleTask2 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        assertEquals(simpleTask1, simpleTask2);
    }

    @Test
    void testTaskExtendsEqualsIfIdsAreEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 2, Status.NEW);
        Epic epic2 = new Epic("Эпик 1", "Описание эпика 1", 2, Status.NEW);
        assertEquals(epic1, epic2);
    }
}