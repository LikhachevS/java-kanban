package serviceClass;

import Interfaces.HistoryManager;
import Interfaces.TaskManager;
import storage.InMemoryHistoryManager;
import storage.InMemoryTaskManager;

public class Manager {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}