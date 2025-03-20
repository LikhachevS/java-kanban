import enams.Status;
import interfaces.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task simpleTask1;
    protected Epic epic1;
    protected Subtask subtask1OfEpic1;

    protected abstract T createTaskManager();

    @BeforeEach
    void BeforeEach() {
        taskManager = createTaskManager();
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofHours(1));
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW, LocalDateTime.now().plusHours(3),
                Duration.ofHours(1));
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 2, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
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

        Task simpleTask1New = new Task("Простая задача 1 обновлённая", "Описание простой задачи 1", 1,
                Status.DONE, LocalDateTime.now().plusHours(1), Duration.ofHours(1));

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
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
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
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
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
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
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
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
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
    void updateStatusOfEpic() {
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(7), Duration.ofHours(1));
        Subtask subtask3OfEpic1 = new Subtask("Подзадача 3 эпика 1", "Описание подзадачи 3 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(9), Duration.ofHours(1));

        taskManager.add(epic1); //id 1
        taskManager.add(subtask1OfEpic1); //id 2
        taskManager.add(subtask2OfEpic1); //id 3
        taskManager.add(subtask3OfEpic1); //id 4

        //Проверка статуса эпика если все подзадачи со статусом NEW
        assertTrue(taskManager.getEpicById(1).getStatus().equals(Status.NEW));

        taskManager.update(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                2, Status.DONE, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1)));

        //Проверка статуса эпика если есть подзадачи со статусом NEW и DONE
        assertTrue(taskManager.getEpicById(1).getStatus().equals(Status.IN_PROGRESS));

        taskManager.update(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                3, Status.DONE, 1, LocalDateTime.now().plusHours(7), Duration.ofHours(1)));
        taskManager.update(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                4, Status.DONE, 1, LocalDateTime.now().plusHours(9), Duration.ofHours(1)));

        //Проверка статуса эпика если все подзадачи со статусом DONE
        assertTrue(taskManager.getEpicById(1).getStatus().equals(Status.DONE));

        taskManager.update(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                2, Status.IN_PROGRESS, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1)));
        taskManager.update(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                3, Status.IN_PROGRESS, 1, LocalDateTime.now().plusHours(7), Duration.ofHours(1)));
        taskManager.update(new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                4, Status.IN_PROGRESS, 1, LocalDateTime.now().plusHours(9), Duration.ofHours(1)));

        //Проверка статуса эпика если все подзадачи со статусом IN_PROGRESS
        assertTrue(taskManager.getEpicById(1).getStatus().equals(Status.IN_PROGRESS));
    }

    @Test
    void isOverlappingTest() {
        subtask1OfEpic1 = new Subtask("Подзадача 1 эпика 1", "Описание подзадачи 1 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2 эпика 1", "Описание подзадачи 2 эпика 1",
                1, Status.NEW, 1, LocalDateTime.now().plusHours(5), Duration.ofHours(1));
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW,
                LocalDateTime.now().plusHours(5), Duration.ofHours(1));

        //Добавляем в менеджер эпик и его подзадачу
        taskManager.add(epic1); //id 1
        taskManager.add(subtask1OfEpic1); //id 2

        //Проверяем будет ли ошибка при добавляем в менеджер подзадачи и простой задачи, которые имеют пересечения по времени
        Assertions.assertThrows(RuntimeException.class, () -> taskManager.add(subtask2OfEpic1));
        Assertions.assertThrows(RuntimeException.class, () -> taskManager.add(simpleTask1));
    }

}