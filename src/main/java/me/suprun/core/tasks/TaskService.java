package me.suprun.core.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class TaskService {
    private final TaskRepository repository;
    private List<Task> tasks;
    private long nextId;

    public TaskService(TaskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Сховище завдань не може бути null.");
        List<Task> loaded = Objects.requireNonNull(repository.load(), "Сховище повернуло null замість списку завдань.");
        this.tasks = new ArrayList<>(loaded);
        validateLoadedTasks(tasks);
        this.nextId = calculateNextId(tasks);
    }

    public List<Task> findAll() {
        return List.copyOf(tasks);
    }

    public Task findById(long id) {
        return tasks.stream()
                .filter(task -> task.id() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Завдання з номером %d не знайдено.".formatted(id)));
    }

    public Task create(String title, String description) {
        long id = nextId;
        long followingId = Math.incrementExact(id);
        Task task = new Task(id, title, description, false);
        List<Task> updated = new ArrayList<>(tasks);
        updated.add(task);
        saveAndReplace(updated);
        nextId = followingId;
        return task;
    }

    public Task complete(long id) {
        Task current = findById(id);
        if (current.completed()) {
            return current;
        }

        List<Task> updated = new ArrayList<>(tasks);
        int index = indexOf(id);
        Task completed = current.markCompleted();
        updated.set(index, completed);
        saveAndReplace(updated);
        return completed;
    }

    public void delete(long id) {
        int index = indexOf(id);
        List<Task> updated = new ArrayList<>(tasks);
        updated.remove(index);
        saveAndReplace(updated);
    }

    private int indexOf(long id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                return i;
            }
        }
        throw new IllegalArgumentException("Завдання з номером %d не знайдено.".formatted(id));
    }

    private void saveAndReplace(List<Task> updated) {
        repository.save(List.copyOf(updated));
        tasks = updated;
    }

    private static long calculateNextId(List<Task> loaded) {
        long maxId = loaded.stream().mapToLong(Task::id).max().orElse(0L);
        try {
            return Math.incrementExact(maxId);
        } catch (ArithmeticException exception) {
            throw new IllegalStateException("Не вдалося визначити наступний номер завдання.", exception);
        }
    }

    private void validateLoadedTasks(List<Task> loaded) {
        Set<Long> ids = new HashSet<>();
        for (Task task : loaded) {
            if (task == null) {
                throw new IllegalStateException("У файлі збереження є порожній запис завдання.");
            }
            if (!ids.add(task.id())) {
                throw new IllegalStateException("У файлі збереження є повторюваний номер завдання: " + task.id());
            }
        }
    }
}
