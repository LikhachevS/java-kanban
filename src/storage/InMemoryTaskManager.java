package storage;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import enams.Status;
import service.Manager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId;
    protected HashMap<Integer, Task> simpleTasks;
    protected HashMap<Integer, Subtask> subtasks;
    protected HashMap<Integer, Epic> epics;
    protected HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTasks;
    Comparator<Task> comparator;

    public InMemoryTaskManager() {
        simpleTasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        nextId = 1;
        historyManager = Manager.getDefaultHistory();
        comparator = Comparator.comparing(Task::getStartTime);
        prioritizedTasks = new TreeSet<>(comparator);
    }

    // Создание задач
    @Override
    public void add(Task simpleTask) {
        if (prioritizedTasks.stream()
                .anyMatch(existingTask -> isOverlapping(existingTask, simpleTask))) {
            throw new RuntimeException("Новая задача пересекается с существующей задачей по времени выполнения.");
        }

        simpleTask.setId(nextId);
        nextId++;
        simpleTasks.put(simpleTask.getId(), simpleTask);
        simpleTask.setStatus(Status.NEW);
        prioritizedTasks.add(simpleTask);
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
            if (prioritizedTasks.stream()
                    .anyMatch(existingTask -> isOverlapping(existingTask, subtask))) {
                throw new RuntimeException("Новая задача пересекается с существующей задачей по времени выполнения.");
            }

            subtask.setId(nextId);
            nextId++;
            subtasks.put(subtask.getId(), subtask);
            subtask.setStatus(Status.NEW);
            prioritizedTasks.add(subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            updateStatusOfEpic(epic);
            updateTimeOfEpic(epic);
        }
    }

    //Обновление задач
    @Override
    public void update(Task simpleTask) {
        if (!subtasks.containsKey(simpleTask.getId()) && !epics.containsKey(simpleTask.getId())
                && simpleTasks.containsKey(simpleTask.getId())) {
            //Уберём старую задачу из приоритезированного списка, чтобы проверить, пересекается ли её новый экземпляр
            //по времени с другими задачами
            prioritizedTasks.remove(simpleTasks.get(simpleTask.getId()));
            if (prioritizedTasks.stream()
                    .anyMatch(existingTask -> isOverlapping(existingTask, simpleTask))) {
                //Вернём старую задачу обратно в приоритезированный список если новый экземпляр пересекается по времени
                prioritizedTasks.add(simpleTasks.get(simpleTask.getId()));
                //И выкинем ошибку
                throw new RuntimeException("Новая задача пересекается с существующей задачей по времени выполнения.");
            }
            prioritizedTasks.add(simpleTask);
            simpleTasks.put(simpleTask.getId(), simpleTask);

        }
    }

    @Override
    public void update(Epic epic) {
        if (!subtasks.containsKey(epic.getId()) && !simpleTasks.containsKey(epic.getId())
                && epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateStatusOfEpic(epic);
            updateTimeOfEpic(epic);
        }
    }

    @Override
    public void update(Subtask subtask) {
        if (!epics.containsKey(subtask.getId()) && !simpleTasks.containsKey(subtask.getId())
                && subtasks.containsKey(subtask.getId())) {
            //Уберём старую задачу из приоритезированного списка, чтобы проверить, пересекается ли её новый экземпляр
            //по времени с другими задачами
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            if (prioritizedTasks.stream()
                    .anyMatch(existingTask -> isOverlapping(existingTask, subtask))) {
                //Вернём старую задачу обратно в приоритезированный список если новый экземпляр пересекается по времени
                prioritizedTasks.add(subtasks.get(subtask.getId()));
                //И выкинем ошибку
                throw new RuntimeException("Новая задача пересекается с существующей задачей по времени выполнения.");
            }
            prioritizedTasks.add(subtask);
            subtasks.put(subtask.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            updateStatusOfEpic(epic);
            updateTimeOfEpic(epic);
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

    private void updateTimeOfEpic(Epic epic) {
        ArrayList<Integer> subtaskIdsOfEpic = epic.getSubtaskIds();
        Duration epicDuration;
        LocalDateTime epicStartTime = subtasks.get(subtaskIdsOfEpic.getFirst()).getStartTime();
        LocalDateTime epicEndTime = subtasks.get(subtaskIdsOfEpic.getFirst()).getEndTime();
        ;

        for (Integer subtaskId : subtaskIdsOfEpic) {
            if (epicStartTime.isAfter(subtasks.get(subtaskId).getStartTime())) {
                epicStartTime = subtasks.get(subtaskId).getStartTime();
            }
            if (epicEndTime.isBefore(subtasks.get(subtaskId).getEndTime())) {
                epicStartTime = subtasks.get(subtaskId).getStartTime();
            }
        }

        epicDuration = Duration.between(epicStartTime, epicEndTime);

        epic.setStartTime(epicStartTime);
        epic.setTaskDuration(epicDuration);
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
        prioritizedTasks.removeAll(simpleTasks.values());
        simpleTasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        prioritizedTasks.removeAll(subtasks.values());
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
        prioritizedTasks.remove(simpleTasks.get(id));
        simpleTasks.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        prioritizedTasks.remove(subtasks.get(id));
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
        return (HashMap<Integer, Subtask>) epics.get(id).getSubtaskIds().stream()
                .collect(Collectors.toMap(Function.identity(), subtasks::get));

    }

    //Удаление задач Эпика по его id:
    private void deleteSubtasksOfEpic(int id) {
        for (int subtaskId : epics.get(id).getSubtaskIds()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            subtasks.remove(subtaskId);
        }
        epics.get(id).getSubtaskIds().clear();
    }

    //Получение истоии просмотров:
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //@Override
    public int getNextId() {
        return nextId;
    }

    private boolean isOverlapping(Task task1, Task task2) {
        // Если конец первой задачи раньше начала второй или конец второй задачи раньше начала первой, то пересечения нет
        return !(task1.getEndTime().isBefore(task2.startTime) || task2.getEndTime().isBefore(task1.startTime));
    }

}
