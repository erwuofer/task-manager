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
    void continuesIdsAfterLoadingExistingTasks() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        repository.tasks.add(new Task(4, "Старе завдання", "", false));
        TaskService service = new TaskService(repository);

        Task created = service.create("Нове завдання", "");

        assertEquals(5, created.id());
    }

    @Test
    void completingTaskTwiceDoesNotChangeItAgain() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        Task task = service.create("Завдання", "");
        Task completed = service.complete(task.id());

        Task repeated = service.complete(task.id());

        assertEquals(completed, repeated);
        assertEquals(2, repository.saveCount);
    }

    @Test
    void rejectsEmptyTitleAndUnknownTask() {
        TaskService service = new TaskService(new InMemoryTaskRepository());

        assertThrows(IllegalArgumentException.class, () -> service.create("   ", ""));
        assertThrows(IllegalArgumentException.class, () -> service.findById(10));
        assertThrows(IllegalArgumentException.class, () -> service.complete(10));
        assertThrows(IllegalArgumentException.class, () -> service.delete(10));
    }

    @Test
    void failedSaveDoesNotChangeCurrentTasksOrConsumeId() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        TaskService service = new TaskService(repository);
        repository.failSave = true;

        assertThrows(IllegalStateException.class, () -> service.create("Не збережеться", ""));
        assertTrue(service.findAll().isEmpty());

        repository.failSave = false;
        Task created = service.create("Збережеться", "");

        assertEquals(1, created.id());
        assertEquals(List.of(created), service.findAll());
    }

    @Test
    void returnedTaskListCannotBeChanged() {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        service.create("Завдання", "");

        assertThrows(UnsupportedOperationException.class, () -> service.findAll().clear());
    }

    @Test
    void rejectsDuplicateIdsInLoadedTasks() {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();
        repository.tasks.add(new Task(1, "Перше", "", false));
        repository.tasks.add(new Task(1, "Друге", "", false));

        assertThrows(IllegalStateException.class, () -> new TaskService(repository));
    }

    private static final class InMemoryTaskRepository implements TaskRepository {
        private List<Task> tasks = new ArrayList<>();
        private boolean failSave;
        private int saveCount;

        @Override
        public List<Task> load() {
            return List.copyOf(tasks);
        }

        @Override
        public void save(List<Task> tasks) {
            if (failSave) {
                throw new IllegalStateException("Тестова помилка збереження.");
            }
            this.tasks = new ArrayList<>(tasks);
            saveCount++;
        }
    }
}
