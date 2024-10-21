import interfaces.HistoryManager;
import interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import service.Manager;
import storage.InMemoryHistoryManager;
import storage.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ManagerTest {

    //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров:
    @Test
    void testGetDefault() {
        Object taskManagerCreateByManager = Manager.getDefault();

        assertTrue(taskManagerCreateByManager instanceof InMemoryTaskManager
                && taskManagerCreateByManager instanceof TaskManager);
    }

    @Test
    void testGetDefaultHistory() {
        Object History = Manager.getDefaultHistory();
        assertTrue(History instanceof InMemoryHistoryManager
                && History instanceof HistoryManager);
    }
}