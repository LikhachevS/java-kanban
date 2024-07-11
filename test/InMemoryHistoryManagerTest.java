import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    @Test
    void testAdd() {
        HistoryManager History = Manager.getDefaultHistory();
        boolean correctly = false;
        Task simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        Subtask subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, epic1.getId());

        History.add(simpleTask1);
        History.add(epic1);
        History.add(subtask1OfEpic1);

        List<Task> AllHistory = History.getHistory();
        if (AllHistory.get(0) instanceof Task
                && AllHistory.get(1) instanceof Epic
                && AllHistory.get(2) instanceof Subtask) {
            correctly = true;
        }

        assertTrue(correctly);
    }

    @Test
    void testGetHistory() {
    }
}