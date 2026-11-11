package me.suprun.cli.commands;

import me.suprun.cli.Command;
import me.suprun.cli.CommandSupport;
import me.suprun.cli.ParsedCommand;
import me.suprun.presenter.TaskPresenter;

public final class ShowTaskCommand implements Command {
    private final TaskPresenter presenter;

    public ShowTaskCommand(TaskPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String name() {
        return "show";
    }

    @Override
    public String description() {
        return "показати інформацію про завдання: show <id>";
    }

    @Override
    public void execute(ParsedCommand command) {
        presenter.showTask(CommandSupport.requireId(command, "Використання: show <id>"));
    }
}
