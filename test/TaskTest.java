import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

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