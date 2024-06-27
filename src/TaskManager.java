import java.util.HashMap;

public class TaskManager {
    int nextId = 1;
    HashMap<Integer, Task> simpleTasks;
    HashMap<Integer, Subtask> subtasks;
    HashMap<Integer, Epic> epics;

    public void add (Task simpleTask){
        simpleTask.id = nextId;
        nextId++;
        simpleTasks.put(simpleTask.id, simpleTask);
    }

    public void add (Epic epic){
        epic.id = nextId;
        nextId++;
        epics.put(epic.id, epic);
    }

    public void add (Subtask subtask){
        subtask.id = nextId;
        nextId++;
        subtasks.put(subtask.id, subtask);

        Epic epic = epics.get(subtask.epicId);
        epic.subtaskIds.add(subtask.id);
    }
}
