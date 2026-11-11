package me.suprun;

import me.suprun.cli.CommandRegistry;
import me.suprun.cli.TaskMode;
import me.suprun.core.tasks.Task;
import me.suprun.core.tasks.TaskRepository;
import me.suprun.core.tasks.TaskService;
import me.suprun.infra.display.ConsoleDisplay;
import me.suprun.presenter.ConsolePresenter;
import me.suprun.presenter.TaskPresenter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class MainTest {
    @Test
    void viewModeRegistersOnlyReadCommands() {
        CommandRegistry registry = createRegistry(TaskMode.VIEW);

        assertNotNull(registry.find("list"));
        assertNotNull(registry.find("show"));
        assertNotNull(registry.find("help"));
        assertNull(registry.find("add"));
        assertNull(registry.find("done"));
        assertNull(registry.find("delete"));
    }

    @Test
    void manageModeRegistersAllCommands() {
        CommandRegistry registry = createRegistry(TaskMode.MANAGE);

        assertNotNull(registry.find("list"));
        assertNotNull(registry.find("show"));
        assertNotNull(registry.find("add"));
        assertNotNull(registry.find("done"));
        assertNotNull(registry.find("delete"));
        assertNotNull(registry.find("help"));
    }

    private static CommandRegistry createRegistry(TaskMode mode) {
        TaskRepository repository = new TaskRepository() {
            @Override
            public List<Task> load() {
                return List.of();
            }

            @Override
            public void save(List<Task> tasks) {
            }
        };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsolePresenter console = new ConsolePresenter(new ConsoleDisplay(
                new ByteArrayInputStream(new byte[0]), new PrintStream(output)));
        TaskPresenter tasks = new TaskPresenter(new TaskService(repository), console);
        return Main.createCommands(tasks, console, mode);
    }
}
