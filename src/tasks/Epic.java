package tasks;

import enams.Status;
import enams.TypesOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String title, String description, int id, Status status) {
        super(title, description, id, status);
        subtaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id, Status status, LocalDateTime startTime, Duration taskDuration) {
        super(title, description, id, status, startTime, taskDuration);
        subtaskIds = new ArrayList<>();
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public TypesOfTasks getType() {
        return TypesOfTasks.EPIC;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
