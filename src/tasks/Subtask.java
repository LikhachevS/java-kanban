package tasks;

import enams.Status;
import enams.TypesOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int id, Status status, int epicId) {
        super(title, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int id, Status status, int epicId, LocalDateTime startTime, Duration taskDuration) {
        super(title, description, id, status, startTime, taskDuration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypesOfTasks getType() {
        return TypesOfTasks.SUBTASK;
    }

}