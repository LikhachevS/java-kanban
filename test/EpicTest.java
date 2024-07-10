import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testTaskEqualsIfIdsAreEqual() {
        TaskManager manager = Manager.getDefault();

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, Status.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", 1, Status.NEW);
        epic1.addSubtaskId(1);
        assertNull(epic1.getSubtaskIds());

    }
}