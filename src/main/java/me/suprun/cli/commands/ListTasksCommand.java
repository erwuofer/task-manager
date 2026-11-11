package me.suprun.cli.commands;

import me.suprun.cli.Command;
import me.suprun.cli.ParsedCommand;
import me.suprun.presenter.TaskPresenter;

public final class ListTasksCommand implements Command {
    private final TaskPresenter presenter;

    public ListTasksCommand(TaskPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return "показати завдання: list [--status all|pending|completed]";
    }

    @Override
    public void execute(ParsedCommand command) {
        if (!command.arguments().isEmpty()) {
            throw new IllegalArgumentException("Використання: list [--status all|pending|completed]");
        }
        if (command.options().isEmpty()) {
            presenter.listTasks("all");
            return;
        }
        if (command.options().size() != 1 || !command.options().containsKey("status")) {
            throw new IllegalArgumentException("Доступна опція: --status all|pending|completed");
        }

        String status = command.options().get("status");
        if (status == null) {
            throw new IllegalArgumentException("Для опції --status потрібно вказати значення.");
        }
        presenter.listTasks(status);
    }
}
