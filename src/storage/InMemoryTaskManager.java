package storage;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import enams.Status;
import service.Manager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId;
    protected HashMap<Integer, Task> simpleTasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;
    protected HistoryManager historyManager;

    public InMemoryTaskManager() {
        simpleTasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        nextId = 1;
        historyManager = Manager.getDefaultHistory();
    }

    // Создание задач
    @Override
    public void add(Task simpleTask) {
        simpleTask.setId(nextId);
        nextId++;
        simpleTasks.put(simpleTask.getId(), simpleTask);
        simpleTask.setStatus(Status.NEW);
    }

    @Override
    public void add(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
    }

    @Override
    public void add(Subtask subtask) {

        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(nextId);
            nextId++;
            subtasks.put(subtask.getId(), subtask);
            subtask.setStatus(Status.NEW);

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            updateStatusOfEpic(epic);
        }
    }

    //Обновление задач
    @Override
    public void update(Task simpleTask) {
        if (!subtasks.containsKey(simpleTask.getId()) && !epics.containsKey(simpleTask.getId())
                && simpleTasks.containsKey(simpleTask.getId())) {
            simpleTasks.put(simpleTask.getId(), simpleTask);
        }
    }

    @Override
    public void update(Epic epic) {
        if (!subtasks.containsKey(epic.getId()) && !simpleTasks.containsKey(epic.getId())
                && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusOfEpic(epic);
        }
    }

    @Override
    public void update(Subtask subtask) {
        if (!epics.containsKey(subtask.getId()) && !simpleTasks.containsKey(subtask.getId())
                && subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            updateStatusOfEpic(epic);
        }
    }

    // Обновление статуса Эпика
    private void updateStatusOfEpic(Epic epic) {
        if (epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            int amountOfDONE = 0;
            int amountOfNEW = 0;

            for (Integer subtaskId : epic.getSubtaskIds()) {
                if (subtasks.get(subtaskId).getStatus() == Status.DONE) {
                    amountOfDONE++;
                }
                if (subtasks.get(subtaskId).getStatus() == Status.NEW) {
                    amountOfNEW++;
                }
            }
            if (epic.getSubtaskIds().size() == amountOfDONE) {
                epic.setStatus(Status.DONE);
            } else if (epic.getSubtaskIds().size() == amountOfNEW) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    //Получение списков задач:
    @Override
    public ArrayList<Task> getSimpleTasks() {
        return new ArrayList<>(simpleTasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    //Удаление всех задач:
    @Override
    public void deleteAllTasks() {
        simpleTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            deleteSubtasksOfEpic(epic.getId());
            updateStatusOfEpic(epic);
        }
    }

    @Override
    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
    }

    //Получение по идентификатору:
    @Override
    public Task getSimpleTaskById(int id) {
        historyManager.add(simpleTasks.get(id));
        return simpleTasks.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    //Удаление по идентификатору:
    @Override
    public void deleteTaskById(int id) {
        simpleTasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Epic epic = epics.get(getSubtaskById(id).getEpicId());
        epic.getSubtaskIds().remove((Integer) id);
        subtasks.remove(id);
        updateStatusOfEpic(epic);
    }

    @Override
    public void deleteEpicById(int id) {
        deleteSubtasksOfEpic(id);
        epics.remove(id);
    }

    //Получение подзадач эпика по его id:
    @Override
    public HashMap<Integer, Subtask> getSubtasksOfEpic(int id) {
        HashMap<Integer, Subtask> subtasksOfEpic = new HashMap<>();
        for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
            subtasksOfEpic.put(subtaskId, subtasks.get(subtaskId));
        }
        return subtasksOfEpic;
    }

    //Удаление задач Эпика по его id:
    private void deleteSubtasksOfEpic(int id) {
        for (int subtaskId : epics.get(id).getSubtaskIds()) {
            subtasks.remove(subtaskId);
        }
        epics.get(id).getSubtaskIds().clear();
    }

    //Получение истоии просмотров:
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
