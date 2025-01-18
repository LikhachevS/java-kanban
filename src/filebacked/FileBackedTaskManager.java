package filebacked;

import enams.Status;
import enams.TypesOfTasks;
import storage.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    Path path;

    public FileBackedTaskManager(Path path) {
        super();
        this.path = path;
    }

    public void loadFromFile() {
        List<String> lines = new ArrayList<>();
        try {
            String fileContent = Files.readString(path, StandardCharsets.UTF_8);
            String[] words = fileContent.split("\n");
            lines.addAll(Arrays.asList(words));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String line : lines) {
            Task task = taskFromString(line);
            if (task.getType() == TypesOfTasks.TASK) {
                simpleTasks.put(task.getId(), task);
            } else if (task.getType() == TypesOfTasks.EPIC) {
                epics.put(task.getId(), (Epic) task);
            } else if (task.getType() == TypesOfTasks.SUBTASK) {
                subtasks.put(task.getId(), (Subtask) task);
            }
        }
    }

    @Override
    public void add(Task simpleTask) {
        super.add(simpleTask);
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
            for (Task task : result) {
                writer.write(taskToString(task));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String taskToString(Task task) {
        String taskAsString = switch (task.getType()) {
            case TASK, EPIC -> task.getId() + "," +
                    task.getType() + "," +
                    task.getTitle() + "," +
                    task.getStatus() + "," +
                    task.getDescription();
            case SUBTASK -> task.getId() + "," +
                    task.getType() + "," +
                    task.getTitle() + "," +
                    task.getStatus() + "," +
                    task.getDescription() + "," +
                    ((Subtask) task).getEpicId();
        };

        return taskAsString;
    }

    Task taskFromString(String taskAsString) {
        Task task;
        int id;
        String title;
        Status status;
        String description;
        int epicId;
        String[] parts = taskAsString.split(",");
            switch (parts[1]) {
                case "TASK":
                    id = Integer.parseInt(parts[0]);
                    title = parts[2];
                    status = Status.valueOf(parts[3]);
                    description = parts[4];
                    task = new Task(title, description, id, status);
                    break;
                case "EPIC":
                    id = Integer.parseInt(parts[0]);
                    title = parts[2];
                    status = Status.valueOf(parts[3]);
                    description = parts[4];
                    task = new Epic(title, description, id, status);
                    break;
                case "SUBTASK":
                    id = Integer.parseInt(parts[0]);
                    title = parts[2];
                    status = Status.valueOf(parts[3]);
                    description = parts[4];
                    epicId = Integer.parseInt(parts[5]);
                    task = new Subtask(title, description, id, status, epicId);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + parts[1]);
            }
            
        return task;
    }

}


