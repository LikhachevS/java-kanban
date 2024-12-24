package tasks;

import enams.Status;
import enams.TypesOfTasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypesOfTasks getType() {return TypesOfTasks.SUBTASK;}

}
