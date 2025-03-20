import filebacked.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import storage.InMemoryTaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    Path tempFile;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        try {
            tempFile = Files.createTempFile(null, ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tempFile.toFile().deleteOnExit();
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoad() {
        taskManager.add(simpleTask1); //id 1
        taskManager.add(epic1); //id 2
        taskManager.add(subtask1OfEpic1); //id 3

        FileBackedTaskManager taskManager2 = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(taskManager.getSimpleTaskById(1), taskManager2.getSimpleTaskById(1));
        assertEquals(taskManager.getEpicById(2), taskManager2.getEpicById(2));
        assertEquals(taskManager.getSubtaskById(3), taskManager2.getSubtaskById(3));
        assertEquals(taskManager.getNextId(), taskManager2.getNextId());
    }

}