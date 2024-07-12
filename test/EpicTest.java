import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EpicTest {

    @Test
    void testGetId() {
        int taskId = 2;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", taskId, Status.NEW);
        assertEquals(taskId, epic1.getId(), "Возвращает правильное значение");
    }

    @Test
    void testSetId() {
        int taskId = 5;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        epic1.setId(taskId);
        assertEquals(taskId, epic1.getId(), "Возвращает правильное значение");
    }

    @Test
    void testGetStatus() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        assertEquals(Status.NEW, epic1.getStatus(), "Возвращает правильное значение");
    }

    @Test
    void testSetStatus() {
        Status status = Status.DONE;
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        epic1.setStatus(status);
        assertEquals(status, epic1.getStatus(), "Возвращает правильное значение");
    }

    @Test
    void testAddSubtaskId() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                2, Status.NEW, epic1.getId());
        epic1.addSubtaskId(subtask1.getId());

        assertNotNull(epic1.getSubtaskIds(), "Эпик содержит id подзадачи.");
    }

    @Test
    void testGetSubtaskIds() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                2, Status.NEW, epic1.getId());

        epic1.addSubtaskId(subtask1.getId());

        int subtaskId = epic1.getSubtaskIds().get(0);

        assertEquals(subtaskId, subtask1.getId(), "Возвращает правильное значение");
    }

    //проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи:
    @Test
    void testEpicAddIntoTheSameEpic1() {
        TaskManager taskManager = Manager.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        taskManager.add(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, epic1.getId());
        taskManager.add(subtask1);

        //Создаём Эпик 2, который имеет поля идентичные эпику 1, но id 2. Добавляем его через метод update в менеджер:
        Epic epic2 = new Epic("Эпик 1", "Описание эпика 1", 2, Status.NEW);
        taskManager.update(epic2);
        assertFalse(taskManager.getEpics().contains(epic2));

    }

    @Test
    void testEpicAddIntoTheSameEpic2() {
        TaskManager taskManager = Manager.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        taskManager.add(epic1);

        //Создаём подзадачу 1, которая имеет поля идентичные эпику 1. Добавляем её через метод update в менеджер задач:
        Subtask subtask1 = new Subtask("Эпик 1", "Описание эпика 1",
                1, Status.NEW, epic1.getId());
        taskManager.update(subtask1);
        assertFalse(taskManager.getSubtasks().contains(subtask1));
    }
}