package me.suprun.core.tasks;

import java.util.Objects;

public record Task(long id, String title, String description, boolean completed) {
    public Task {
        if (id <= 0) {
            throw new IllegalArgumentException("Ідентифікатор завдання має бути додатним.");
        }

        title = Objects.requireNonNull(title, "Назва завдання не може бути null.").trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Назва завдання не може бути порожньою.");
        }

        description = description == null ? "" : description.trim();
    }

    public Task markCompleted() {
        return completed ? this : new Task(id, title, description, true);
    }
}
