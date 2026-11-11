package me.suprun.infra.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.suprun.core.tasks.Task;
import me.suprun.core.tasks.TaskRepository;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class JsonTaskRepository implements TaskRepository {
    private static final Type TASK_LIST_TYPE = new TypeToken<List<Task>>() { }.getType();

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonTaskRepository(Path file) {
        this.file = file;
    }

    @Override
    public List<Task> load() {
        if (Files.notExists(file)) {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            List<Task> tasks = gson.fromJson(reader, TASK_LIST_TYPE);
            return tasks == null ? List.of() : List.copyOf(tasks);
        } catch (IOException exception) {
            throw new UncheckedIOException("Не вдалося прочитати файл даних: " + file, exception);
        }
    }

    @Override
    public void save(List<Task> tasks) {
        Path parent = file.getParent();

        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(tasks, TASK_LIST_TYPE, writer);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Не вдалося зберегти файл даних: " + file, exception);
        }
    }
}
