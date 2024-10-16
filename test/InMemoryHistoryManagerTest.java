import Interfaces.HistoryManager;
import enams.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import serviceClass.Manager;
import storage.InMemoryHistoryManager;
import typesOfTask.Epic;
import typesOfTask.Subtask;
import typesOfTask.Task;

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
        epic1 = new Epic("Эпик 2", "Описание эпика 2", 2, Status.NEW);
        subtask1OfEpic1 = new Subtask("Подзадача 3, эпика 2", "Описание подзадачи 3, эпика 2",
                3, Status.NEW, epic1.getId());
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

        historyManager.add(epic1); // Добавление в истоприю задачи которая уже была в списке
        historyManager.add(simpleTask1); // Добавление в истоприю задачи которая уже была в списке первой
        historyManager.add(simpleTask1); // Добавление в историю задачи которая уже была в списке последней

        List<Task> history = historyManager.getHistory();
        if (history.get(0) instanceof Subtask // Первая в списке
                && history.get(1) instanceof Epic // Вторая в списке
                && history.get(2) instanceof Task // Третья в списке
                && history.size() == 3) { // Проверка размера списка
            correctly = true;
        }

        assertTrue(correctly);
    }

    @Test
    void testRemoveFirst() {
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(simpleTask1.getId()), "История пустая.");
    }

    @Test
    void testRemoveLast() {
        historyManager.add(simpleTask1);
        historyManager.add(epic1);
        historyManager.add(subtask1OfEpic1);

        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(subtask1OfEpic1.getId()), "История пустая.");
    }
}