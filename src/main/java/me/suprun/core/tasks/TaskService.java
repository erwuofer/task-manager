package me.suprun.core.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TaskService {
    private final TaskRepository repository;
    private List<Task> tasks;
    private long nextId;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
        this.tasks = new ArrayList<>(repository.load());
        validateLoadedTasks(tasks);
        this.nextId = tasks.stream().mapToLong(Task::id).max().orElse(0L) + 1L;
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
        Task task = new Task(nextId, title, description, false);
        List<Task> updated = new ArrayList<>(tasks);
        updated.add(task);
        saveAndReplace(updated);
        nextId++;
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

    private void validateLoadedTasks(List<Task> loaded) {
        Set<Long> ids = new HashSet<>();
        for (Task task : loaded) {
            if (!ids.add(task.id())) {
                throw new IllegalStateException("У файлі збереження є повторюваний номер завдання: " + task.id());
            }
        }
    }
}
