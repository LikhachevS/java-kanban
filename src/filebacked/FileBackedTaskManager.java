package filebacked;

import enams.Status;
import enams.TypesOfTasks;
import service.Manager;
import storage.InMemoryTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

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
            Task task = fromString(line);
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
    }

    String toString(Task task) {

        return "";
    }

    Task fromString(String value) {

        return null;
    }
}
