package me.suprun.tests.core;

import me.suprun.core.tasks.Task;
import me.suprun.core.tasks.TaskRepository;
import me.suprun.core.tasks.TaskService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskServiceTest {
    @Test
    void createsCompletesAndDeletesTask() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);

        Task task = service.create("Підготувати звіт", "Перевірити висновки");

        assertEquals(1, task.id());
        assertFalse(task.completed());
        assertEquals(1, repository.tasks.size());

        Task completed = service.complete(task.id());
        assertTrue(completed.completed());
        assertTrue(service.findById(task.id()).completed());

        service.delete(task.id());
        assertTrue(service.findAll().isEmpty());
        assertTrue(repository.tasks.isEmpty());
    }

    @Test
    void rejectsEmptyTitleAndUnknownTask() {
        TaskService service = new TaskService(new InMemoryTaskRepository());

        assertThrows(IllegalArgumentException.class, () -> service.create("   ", ""));
        assertThrows(IllegalArgumentException.class, () -> service.findById(10));
    }

    private static final class InMemoryTaskRepository implements TaskRepository {
        private List<Task> tasks = new ArrayList<>();

        @Override
        public List<Task> load() {
            return List.copyOf(tasks);
        }

        @Override
        public void save(List<Task> tasks) {
            this.tasks = new ArrayList<>(tasks);
        }
    }
}
