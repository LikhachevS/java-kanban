package interfaces;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    // Создание задач
    void add(Task simpleTask);

    void add(Epic epic);

    void add(Subtask subtask);

    //Обновление задач
    void update(Task simpleTask);

    void update(Epic epic);

    void update(Subtask subtask);

    //Получение списков задач:
    ArrayList<Task> getSimpleTasks();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Epic> getEpics();

    //Удаление всех задач:
    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    //Получение по идентификатору:
    Task getSimpleTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    //Удаление по идентификатору:
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    //Получение подзадач эпика по его id:
    HashMap<Integer, Subtask> getSubtasksOfEpic(int id);

    //Получение истоии просмотров:
    List<Task> getHistory();
}
