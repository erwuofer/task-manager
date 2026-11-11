package me.suprun.cli.commands;

import me.suprun.cli.Command;
import me.suprun.cli.CommandSupport;
import me.suprun.cli.ParsedCommand;
import me.suprun.presenter.TaskPresenter;

public final class CompleteTaskCommand implements Command {
    private final TaskPresenter presenter;

    public CompleteTaskCommand(TaskPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String name() {
        return "done";
    }

    @Override
    public String description() {
        return "позначити завдання виконаним: done <id>";
    }

    @Override
    public void execute(ParsedCommand command) {
        presenter.completeTask(CommandSupport.requireId(command, "Використання: done <id>"));
    }
}
