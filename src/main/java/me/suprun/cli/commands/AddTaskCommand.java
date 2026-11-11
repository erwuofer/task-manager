package me.suprun.cli.commands;

import me.suprun.cli.Command;
import me.suprun.cli.CommandSupport;
import me.suprun.cli.ParsedCommand;
import me.suprun.presenter.TaskPresenter;

public final class AddTaskCommand implements Command {
    private final TaskPresenter presenter;

    public AddTaskCommand(TaskPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String name() {
        return "add";
    }

    @Override
    public String description() {
        return "додати завдання через діалог";
    }

    @Override
    public void execute(ParsedCommand command) {
        CommandSupport.requireNoArguments(command, "Використання: add");
        presenter.createTask();
    }
}
