package me.suprun.tests.cli;

import me.suprun.cli.CommandLoop;
import me.suprun.cli.CommandParser;
import me.suprun.cli.CommandRegistry;
import me.suprun.cli.commands.AddTaskCommand;
import me.suprun.cli.commands.CompleteTaskCommand;
import me.suprun.cli.commands.DeleteTaskCommand;
import me.suprun.cli.commands.HelpCommand;
import me.suprun.cli.commands.ListTasksCommand;
import me.suprun.cli.commands.ShowTaskCommand;
import me.suprun.core.tasks.TaskService;
import me.suprun.infra.data.JsonTaskRepository;
import me.suprun.infra.display.ConsoleDisplay;
import me.suprun.presenter.ConsolePresenter;
import me.suprun.presenter.TaskPresenter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandScenarioTest {
    @TempDir
    Path directory;

    @Test
    void runsTaskCommandsAndStoresChanges() {
        Path dataFile = directory.resolve("tasks.json");
        TaskService service = new TaskService(new JsonTaskRepository(dataFile));
        ByteArrayInputStream input = new ByteArrayInputStream((
                "help\nadd\nПідготувати звіт\nПеревірити висновки\nlist\ndone 1\nlist --status completed\nshow 1\ndelete 1\nexit\n"
        ).getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConsolePresenter console = new ConsolePresenter(new ConsoleDisplay(input, new PrintStream(output)));
        TaskPresenter taskPresenter = new TaskPresenter(service, console);
        CommandRegistry registry = new CommandRegistry();
        registry.register(new AddTaskCommand(taskPresenter));
        registry.register(new ListTasksCommand(taskPresenter));
        registry.register(new ShowTaskCommand(taskPresenter));
        registry.register(new CompleteTaskCommand(taskPresenter));
        registry.register(new DeleteTaskCommand(taskPresenter));
        registry.register(new HelpCommand(registry, console));

        new CommandLoop(new CommandParser(), registry, console).run();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Створено завдання #1"));
        assertTrue(result.contains("позначено виконаним"));
        assertTrue(result.contains("Статус: виконано"));
        assertTrue(result.contains("list [--status all|pending|completed]"));
        assertTrue(new JsonTaskRepository(dataFile).load().isEmpty());
    }
}
