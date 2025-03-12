package tasks;

import enams.Status;
import enams.TypesOfTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    public String title;
    protected String description;
    public int id;
    protected Status status;
    public Duration taskDuration;
    public LocalDateTime startTime;

    public Task(String title, String description, int id, Status status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String title, String description, int id, Status status, LocalDateTime startTime, Duration taskDuration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.taskDuration = taskDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(taskDuration, task.taskDuration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, taskDuration, startTime);
    }

    @Override
    public String toString() {
        return "typesOfTask.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TypesOfTasks getType() {
        return TypesOfTasks.TASK;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(taskDuration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration gettaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(Duration taskDuration) {
        this.taskDuration = taskDuration;
    }
}
