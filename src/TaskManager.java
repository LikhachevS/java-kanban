import java.util.HashMap;

public class TaskManager {
    private int nextId;
    private HashMap<Integer, Task> simpleTasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        simpleTasks = new HashMap<>();
        subtasks  = new HashMap<>();
        epics = new HashMap<>();
        nextId = 1;
    }

    // Создание задач
    public void add(Task simpleTask) {
        simpleTask.setId(nextId);
        nextId++;
        simpleTasks.put(simpleTask.getId(), simpleTask);
        simpleTask.setStatus(Status.NEW);
    }

    public void add(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        epic.setStatus(Status.NEW);
    }

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
    public void update(Task simpleTask) {
        simpleTasks.put(simpleTask.getId(), simpleTask);
    }

    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        updateStatusOfEpic(epic);
    }

    public void update(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateStatusOfEpic(epic);
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
    public HashMap<Integer, Task> getSimpleTasks() {
        return simpleTasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    //Удаление всех задач:
    public void deleteAllTasks() {
        simpleTasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic: epics.values()) {
            deleteSubtasksOfEpic(epic.getId());
            updateStatusOfEpic(epic);
        }
    }

    public void deleteAllEpics() {
        deleteAllSubtasks();
        epics.clear();
    }

    //Получение по идентификатору:
    public Task getSimpleTaskById(int id){
        return simpleTasks.get(id);
    }

    public Subtask getSubtaskById(int id){
        return subtasks.get(id);
    }

    public Epic getEpicById(int id){
        return epics.get(id);
    }

    //Удаление по идентификатору:
    public void deleteTaskById(int id) {
        simpleTasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Epic epic = epics.get(getSubtaskById(id).getEpicId());
        epic.getSubtaskIds().remove((Integer) id);
        subtasks.remove(id);
        updateStatusOfEpic(epic);
    }

    public void deleteEpicById(int id) {
        deleteSubtasksOfEpic(id);
        epics.remove(id);
    }

    //Получение подзадач эпика по его id:
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
}
