package me.suprun.presenter;

import me.suprun.core.tasks.Task;
import me.suprun.core.tasks.TaskService;

import java.util.List;
import java.util.Locale;

public final class TaskPresenter {
    private final TaskService tasks;
    private final ConsolePresenter console;

    public TaskPresenter(TaskService tasks, ConsolePresenter console) {
        this.tasks = tasks;
        this.console = console;
    }

    public void listTasks(String statusFilter) {
        List<Task> all = tasks.findAll();
        List<Task> selected = switch (statusFilter.toLowerCase(Locale.ROOT)) {
            case "all" -> all;
            case "pending" -> all.stream().filter(task -> !task.completed()).toList();
            case "completed" -> all.stream().filter(Task::completed).toList();
            default -> throw new IllegalArgumentException(
                    "Невідомий статус. Використовуйте all, pending або completed.");
        };

        if (selected.isEmpty()) {
            console.println("Завдань за вибраним фільтром немає.");
            return;
        }

        console.println("Список завдань:");
        for (Task task : selected) {
            String status = task.completed() ? "виконано" : "не виконано";
            console.println("#%d [%s] %s".formatted(task.id(), status, task.title()));
        }
    }

    public void showTask(long id) {
        Task task = tasks.findById(id);
        console.println("Завдання #%d".formatted(task.id()));
        console.println("Назва: " + task.title());
        console.println("Опис: " + (task.description().isBlank() ? "не вказано" : task.description()));
        console.println("Статус: " + (task.completed() ? "виконано" : "не виконано"));
    }

    public void createTask() {
        String title = console.ask("Назва: ");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Назва завдання не може бути порожньою.");
        }

        String description = console.ask("Опис (можна залишити порожнім): ");
        Task task = tasks.create(title, description == null ? "" : description);
        console.println("Створено завдання #%d.".formatted(task.id()));
    }

    public void completeTask(long id) {
        Task task = tasks.complete(id);
        console.println("Завдання #%d позначено виконаним.".formatted(task.id()));
    }

    public void deleteTask(long id) {
        tasks.delete(id);
        console.println("Завдання #%d видалено.".formatted(id));
    }
}
