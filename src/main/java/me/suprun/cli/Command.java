package me.suprun.cli;

public interface Command {
    String name();

    String description();

    void execute(ParsedCommand command);
}
