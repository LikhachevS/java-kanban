package filebacked;

import enams.Status;
import enams.TypesOfTasks;
import exception.ManagerSaveException;
import storage.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    Path path;

    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    //Данный метод возвращает менеджер с загруженной информацией о задачах в оперативную память из ссылки
    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager manager = new FileBackedTaskManager(path);
        List<String> lines = new ArrayList<>();
        try {
            String fileContent = Files.readString(path, StandardCharsets.UTF_8);
            String[] words = fileContent.split("\n");
            lines.addAll(Arrays.asList(words));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 1; i < lines.size(); i++) {
            Task task = taskFromString(lines.get(i));
            if (task.getId() >= manager.nextId) {
                manager.nextId++;
            }
            if (task.getType() == TypesOfTasks.TASK) {
                manager.simpleTasks.put(task.getId(), task);
            } else if (task.getType() == TypesOfTasks.EPIC) {
                manager.epics.put(task.getId(), (Epic) task);
            } else if (task.getType() == TypesOfTasks.SUBTASK) {
                manager.subtasks.put(task.getId(), (Subtask) task);
            }
            for (Subtask subtask : manager.subtasks.values()) {
                manager.epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            }
        }
        return manager;
    }

    @Override
    public void add(Task simpleTask) {
        super.add(simpleTask);
        save();
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void add(Subtask subtask) {
        super.add(subtask);
        save();
    }

    @Override
    public void update(Task simpleTask) {
        super.update(simpleTask);
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    private void save() {
        // Формируем общий список ключей (идентификаторов)
        List<Integer> keys = new ArrayList<>();
        keys.addAll(simpleTasks.keySet());
        keys.addAll(subtasks.keySet());
        keys.addAll(epics.keySet());

        // Сортируем ключи по числовому значению
        Collections.sort(keys);

        // Проходим по каждому ключу и собираем значения из всех трех HashMap
        List<Task> result = new ArrayList<>();
        for (int key : keys) {
            if (simpleTasks.containsKey(key)) {
                result.add(simpleTasks.get(key));
            } else if (subtasks.containsKey(key)) {
                result.add(subtasks.get(key));
            } else if (epics.containsKey(key)) {
                result.add(epics.get(key));
            }
        }

        // Выводим результат
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : result) {
                writer.write(taskToString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("По данному пути нет файла для записи");
        }
    }

    private static String taskToString(Task task) {
        String taskAsString = switch (task.getType()) {
            case TASK, EPIC -> task.getId() + "," +
                    task.getType() + "," +
                    task.getTitle() + "," +
                    task.getStatus() + "," +
                    task.getDescription() + "," +
                    task.getStartTime() + "," +
                    task.gettaskDuration();
            case SUBTASK -> task.getId() + "," +
                    task.getType() + "," +
                    task.getTitle() + "," +
                    task.getStatus() + "," +
                    task.getDescription() + "," +
                    ((Subtask) task).getEpicId() + "," +
                    task.getStartTime() + "," +
                    task.gettaskDuration();
        };

        return taskAsString;
    }

    private static Task taskFromString(String taskAsString) {
        Task task;
        String[] parts = taskAsString.split(",");
        switch (parts[1]) {
            case "TASK":
                int id = Integer.parseInt(parts[0]);
                String title = parts[2];
                Status status = Status.valueOf(parts[3]);
                String description = parts[4].trim();
                LocalDateTime startTime = LocalDateTime.parse(parts[5]);
                Duration duration = Duration.parse(parts[6].trim());
                task = new Task(title, description, id, status, startTime, duration);
                break;
            case "EPIC":
                id = Integer.parseInt(parts[0]);
                title = parts[2];
                status = Status.valueOf(parts[3]);
                description = parts[4].trim();
                startTime = LocalDateTime.parse(parts[5]);
                duration = Duration.parse(parts[6].trim());
                task = new Epic(title, description, id, status, startTime, duration);
                break;
            case "SUBTASK":
                id = Integer.parseInt(parts[0]);
                title = parts[2];
                status = Status.valueOf(parts[3]);
                description = parts[4].trim();
                int epicId = Integer.parseInt(parts[5]);
                startTime = LocalDateTime.parse(parts[6]);
                duration = Duration.parse(parts[7].trim());
                task = new Subtask(title, description, id, status, epicId, startTime, duration);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + parts[1]);
        }

        return task;
    }
}


