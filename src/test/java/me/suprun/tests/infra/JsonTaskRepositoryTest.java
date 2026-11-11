package me.suprun.tests.infra;

import me.suprun.core.tasks.Task;
import me.suprun.infra.data.JsonTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonTaskRepositoryTest {
    @TempDir
    Path directory;

    @Test
    void savesAndLoadsTasks() {
        Path file = directory.resolve("data").resolve("tasks.json");
        JsonTaskRepository repository = new JsonTaskRepository(file);
        List<Task> tasks = List.of(
                new Task(1, "Підготувати звіт", "Перевірити розрахунки", false),
                new Task(2, "Надіслати лист", "", true)
        );

        repository.save(tasks);
        List<Task> loaded = repository.load();

        assertEquals(tasks, loaded);
        assertTrue(file.toFile().exists());
    }

    @Test
    void returnsEmptyListWhenFileDoesNotExist() {
        JsonTaskRepository repository = new JsonTaskRepository(directory.resolve("missing.json"));

        assertTrue(repository.load().isEmpty());
    }
}
