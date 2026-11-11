package me.suprun.core.tasks;

import java.util.List;

public interface TaskRepository {
    List<Task> load();

    void save(List<Task> tasks);
}
