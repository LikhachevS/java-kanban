import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {


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