import Interfaces.TaskManager;
import enams.Status;
import storage.InMemoryTaskManager;
import typesofTask.Epic;
import typesofTask.Subtask;
import typesofTask.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new InMemoryTaskManager();

        //Создание простых задач:
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        taskManager.add(simpleTask1);
        Task simpleTask2 = new Task("Простая задача 2", "Описание простой задачи 2", 1, Status.NEW);
        taskManager.add(simpleTask2);

        //Создание эпиков:
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        taskManager.add(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", 1, Status.NEW);
        taskManager.add(epic2);

        //Создание подзадач для эпиков:
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, epic1.getId());
        taskManager.add(subtask1OfEpic1);
        Subtask subtask2OfEpic1 = new Subtask("Подзадача 2, эпика 1", "Описание подзадачи 2, эпика 1",
                1, Status.NEW, epic1.getId());
        taskManager.add(subtask2OfEpic1);
        Subtask subtask1OfEpic2 = new Subtask("Подзадача 1, эпика 2", "Описание подзадачи 1, эпика 2",
                1, Status.NEW, epic2.getId());
        taskManager.add(subtask1OfEpic2);

        //Вывод списка простых задач:
        System.out.println(taskManager.getSimpleTasks());

        //Вывод списка эпиков:
        System.out.println(taskManager.getEpics());

        //Вывод списка подзадач:
        System.out.println(taskManager.getSubtasks());

        //Обновление статуса простых задач:
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.IN_PROGRESS);
        taskManager.update(simpleTask1);
        System.out.println(simpleTask1.title + " " + taskManager.getSimpleTaskById(1).getStatus());

        simpleTask2 = new Task("Простая задача 2", "Описание простой задачи 2", 2, Status.DONE);
        taskManager.update(simpleTask2);
        System.out.println(simpleTask2.title + " " + taskManager.getSimpleTaskById(2).getStatus());

        //Обновление статуса подзадач эпика 1:
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                5, Status.IN_PROGRESS, epic1.getId());
        taskManager.update(subtask1OfEpic1);
        System.out.println(subtask1OfEpic1.title + " " + taskManager.getSubtaskById(5).getStatus());

        subtask2OfEpic1 = new Subtask("Подзадача 2, эпика 1", "Описание подзадачи 2, эпика 1",
                6, Status.DONE, epic1.getId());
        taskManager.update(subtask2OfEpic1);
        System.out.println(subtask2OfEpic1.title + " " + taskManager.getSubtaskById(6).getStatus());

        //Проверка статуса эпика 1:
        System.out.println(taskManager.getEpicById(3).title + " " + taskManager.getEpicById(3).getStatus());

        //Обновление статуса подзадач эпика 2:
        subtask1OfEpic2 = new Subtask("Подзадача 1, эпика 2", "Описание подзадачи 1, эпика 2",
                7, Status.DONE, epic2.getId());
        taskManager.update(subtask1OfEpic2);
        System.out.println(subtask1OfEpic2.title + " " + taskManager.getSubtaskById(7).getStatus());

        //Проверка статуса эпика 2:
        System.out.println(taskManager.getEpicById(4).title + " " + taskManager.getEpicById(4).getStatus());


        //Удаление подзадачи 1 эпика 1:
        taskManager.deleteSubtaskById(5);
        //Вывод списка подзадач:
        System.out.println(taskManager.getSubtasks());

        //Удаление эпика 2:
        taskManager.deleteEpicById(4);
        //Вывод списка эпиков:
        System.out.println(taskManager.getEpics());
        //Вывод списка подзадач:
        System.out.println(taskManager.getSubtasks());

        //Получение истоии просмотров:
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
