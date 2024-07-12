import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager;

    Task simpleTask1;
    Epic epic1;
    Subtask subtask1OfEpic1;

    @BeforeEach
    void BeforeEach() {
        taskManager = new InMemoryTaskManager();
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 2);
    }

    @Test
    void testAdd() {
        taskManager.add(simpleTask1); //id 1
        taskManager.add(epic1); //id 2
        taskManager.add(subtask1OfEpic1); //id 3

        assertEquals(simpleTask1, taskManager.getSimpleTaskById(1));
        assertEquals(epic1, taskManager.getEpicById(2));
        assertEquals(subtask1OfEpic1, taskManager.getSubtaskById(3));
    }

    @Test
    void testUpdate() {
        taskManager.add(simpleTask1);

        Task simpleTask1New = new Task("Простая задача 1 обновлённая", "Описание простой задачи 1", 1, Status.DONE);

        taskManager.update(simpleTask1New);

        assertFalse(taskManager.getSimpleTasks().contains(simpleTask1), "Не содержит версии задачи до обновления");
        assertTrue(taskManager.getSimpleTasks().contains(simpleTask1New), "Cодержит версию обновлённой задачи");
    }

    @Test
    void testGetSimpleTasks() {
        taskManager.add(simpleTask1);
        assertTrue(taskManager.getSimpleTasks().size() == 1);
        assertTrue(taskManager.getSimpleTasks().contains(simpleTask1));
    }

    @Test
    void testGetSubtasks() {
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 1);
        taskManager.add(epic1);
        taskManager.add(subtask1OfEpic1);
        assertTrue(taskManager.getSubtasks().size() == 1);
        assertTrue(taskManager.getSubtasks().contains(subtask1OfEpic1));
    }

    @Test
    void testGetEpics() {
        taskManager.add(epic1);
        assertTrue(taskManager.getEpics().size() == 1);
        assertTrue(taskManager.getEpics().contains(epic1));
    }

    @Test
    void deleteAllTasks() {
        taskManager.add(simpleTask1);
        taskManager.add(simpleTask1);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getSimpleTasks().isEmpty());
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.add(subtask1OfEpic1);

        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteAllEpics() {
        taskManager.add(epic1);

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void getSimpleTaskById() {
        taskManager.add(simpleTask1);

        assertEquals(taskManager.getSimpleTaskById(1), simpleTask1);
    }

    @Test
    void getSubtaskById() {
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 1);
        taskManager.add(epic1);
        taskManager.add(subtask1OfEpic1);
        assertEquals(taskManager.getSubtaskById(2), subtask1OfEpic1);
    }

    @Test
    void getEpicById() {
        taskManager.add(epic1);

        assertEquals(taskManager.getEpicById(1), epic1);
    }

    @Test
    void deleteTaskById() {
        taskManager.add(simpleTask1);

        taskManager.deleteTaskById(1);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void deleteSubtaskById() {
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 1);
        taskManager.add(epic1);
        taskManager.add(subtask1OfEpic1);

        taskManager.deleteSubtaskById(2);
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteEpicById() {
        taskManager.add(epic1);

        taskManager.deleteEpicById(1);
        assertTrue(taskManager.getEpics().isEmpty());
    }

    @Test
    void getSubtasksOfEpic() {
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 1);
        taskManager.add(epic1);
        taskManager.add(subtask1OfEpic1);

        assertTrue(taskManager.getSubtasksOfEpic(1).containsKey(subtask1OfEpic1.getId()));
    }

    @Test
    void getHistory() {
        taskManager.add(simpleTask1); //id 1
        taskManager.add(epic1); //id 2
        taskManager.add(subtask1OfEpic1);

        taskManager.getSimpleTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);

        assertTrue(taskManager.getHistory().size() == 3);
        assertTrue(taskManager.getHistory().contains(simpleTask1));
        assertTrue(taskManager.getHistory().contains(epic1));
        assertTrue(taskManager.getHistory().contains(subtask1OfEpic1));
    }
    @Test
    void getHistoryReturnOldVersionOfTask() {
        taskManager.add(simpleTask1); //id 1
        taskManager.add(epic1); //id 2
        taskManager.add(subtask1OfEpic1);

        taskManager.getSimpleTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);

        assertTrue(taskManager.getHistory().size() == 3);
        assertEquals((taskManager.getSimpleTaskById(1)), taskManager.getHistory().get(0));
        assertEquals((taskManager.getEpicById(2)), taskManager.getHistory().get(1));
        assertEquals((taskManager.getSubtaskById(3)), taskManager.getHistory().get(2));

        Task simpleTask1New = new Task("Простая задача 1 обновлённая", "Описание простой задачи 1", 1, Status.DONE);
        Epic epic1New = new Epic("Эпик 1 обновлённый", "Описание эпика 1", 2, Status.NEW);
        Subtask subtask1OfEpic1New = new Subtask("Подзадача 1, эпика 1 обновлённая", "Описание подзадачи 1, эпика 1",
                3, Status.NEW, 2);

        taskManager.update(simpleTask1New);
        taskManager.update(epic1New);
        taskManager.update(subtask1OfEpic1New);

        assertNotEquals((taskManager.getSimpleTaskById(1)), taskManager.getHistory().get(0));
        assertNotEquals((taskManager.getEpicById(2)), taskManager.getHistory().get(1));
        assertNotEquals((taskManager.getSubtaskById(3)), taskManager.getHistory().get(2));
    }
}