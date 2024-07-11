import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    //проверьте, что объект Subtask нельзя сделать своим же эпиком:
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