import interfaces.TaskManager;
import enams.Status;
import org.junit.jupiter.api.Test;
import serviceClasses.Manager;
import typesTasks.Epic;
import typesTasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testGetId() {
        int taskId = 2;
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                taskId, Status.NEW, 1);
        assertEquals(taskId, subtask1.getId(), "Возвращает правильное значение");
    }

    @Test
    void testSetId() {
        int taskId = 5;
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                2, Status.NEW, 1);
        subtask1.setId(taskId);
        assertEquals(taskId, subtask1.getId(), "Возвращает правильное значение");
    }

    @Test
    void testGetStatus() {
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                2, Status.NEW, 1);
        assertEquals(Status.NEW, subtask1.getStatus(), "Возвращает правильное значение");
    }

    @Test
    void testSetStatus() {
        Status status = Status.DONE;
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                2, Status.NEW, 1);
        subtask1.setStatus(status);
        assertEquals(status, subtask1.getStatus(), "Возвращает правильное значение");
    }

    @Test
    void testGetEpicId() {
        int epicId = 1;
        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                2, Status.NEW, epicId);

        assertEquals(epicId, subtask1.getEpicId(), "Возвращает правильное значение");
    }

    //проверьте, что объект typesOfTask.Subtask нельзя сделать своим же эпиком:
    @Test
    void testSubtaskAddIntoTheSameSubtask() {
        TaskManager taskManager = Manager.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        taskManager.add(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 1);
        taskManager.add(subtask1);

        //Создаём ещё одну подзадачу, с такими же полями. Полю epicid присваеваем значение id subtask1.
        Subtask subtask2 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, 2);
        taskManager.add(subtask2);
        assertFalse(taskManager.getSubtasks().contains(subtask2));
    }
}