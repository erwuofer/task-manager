package me.suprun.cli.commands;

import me.suprun.cli.Command;
import me.suprun.cli.CommandRegistry;
import me.suprun.cli.CommandSupport;
import me.suprun.cli.ParsedCommand;
import me.suprun.presenter.ConsolePresenter;

public final class HelpCommand implements Command {
    private final CommandRegistry registry;
    private final ConsolePresenter presenter;

    public HelpCommand(CommandRegistry registry, ConsolePresenter presenter) {
        this.registry = registry;
        this.presenter = presenter;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "показати список команд";
    }

    @Override
    public void execute(ParsedCommand command) {
        CommandSupport.requireNoArguments(command, "Використання: help");
        registry.all().forEach(item -> presenter.println("%s — %s".formatted(item.name(), item.description())));
        presenter.println("exit — завершити роботу");
    }
}
