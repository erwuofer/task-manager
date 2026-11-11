package me.suprun.cli.commands;

import me.suprun.cli.Command;
import me.suprun.cli.CommandSupport;
import me.suprun.cli.ParsedCommand;
import me.suprun.presenter.TaskPresenter;

public final class DeleteTaskCommand implements Command {
    private final TaskPresenter presenter;

    public DeleteTaskCommand(TaskPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String name() {
        return "delete";
    }

    @Override
    public String description() {
        return "видалити завдання: delete <id>";
    }

    @Override
    public void execute(ParsedCommand command) {
        presenter.deleteTask(CommandSupport.requireId(command, "Використання: delete <id>"));
    }
}
