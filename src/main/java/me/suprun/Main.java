package me.suprun;

import me.suprun.cli.CommandLoop;
import me.suprun.cli.CommandParser;
import me.suprun.cli.CommandRegistry;
import me.suprun.cli.ModeSelector;
import me.suprun.cli.TaskMode;
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

import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        ConsolePresenter console = new ConsolePresenter(new ConsoleDisplay());
        try {
            TaskMode mode = new ModeSelector().select(args, console);
            TaskService tasks = new TaskService(new JsonTaskRepository(Path.of("data", "tasks.json")));
            TaskPresenter taskPresenter = new TaskPresenter(tasks, console);
            CommandRegistry registry = createCommands(taskPresenter, console, mode);
            new CommandLoop(new CommandParser(), registry, console).run();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            console.error(message == null || message.isBlank() ? "Не вдалося запустити програму." : message);
        }
    }

    static CommandRegistry createCommands(
            TaskPresenter tasks,
            ConsolePresenter console,
            TaskMode mode) {
        CommandRegistry registry = new CommandRegistry();
        registry.register(new ListTasksCommand(tasks));
        registry.register(new ShowTaskCommand(tasks));

        if (mode == TaskMode.MANAGE) {
            registry.register(new AddTaskCommand(tasks));
            registry.register(new CompleteTaskCommand(tasks));
            registry.register(new DeleteTaskCommand(tasks));
        }

        registry.register(new HelpCommand(registry, console));
        return registry;
    }
}
