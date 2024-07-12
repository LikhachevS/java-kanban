import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task simpleTask1;
    Epic epic1;
    Subtask subtask1OfEpic1;

    @BeforeEach
    void BeforeEach() {
        historyManager = Manager.getDefaultHistory();
        simpleTask1 = new Task("Простая задача 1", "Описание простой задачи 1", 1, Status.NEW);
        epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        subtask1OfEpic1 = new Subtask("Подзадача 1, эпика 1", "Описание подзадачи 1, эпика 1",
                1, Status.NEW, epic1.getId());
    }

    @Test
    void testAdd() {
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
    }

    @Test
    void testGetHistory() {
        boolean correctly = false;

        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);

        List<Task> history = historyManager.getHistory();
        if (history.get(0) instanceof Task
                && history.get(1) instanceof Epic
                && history.get(2) instanceof Subtask) {
            correctly = true;
        }

        assertTrue(correctly);
    }

    @Test
    void TestMaxSizeofHistory() {
        //Добавляем двенадцать задач:
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История не пустая.");
    }
}