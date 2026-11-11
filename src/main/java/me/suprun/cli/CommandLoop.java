package me.suprun.cli;

import me.suprun.presenter.ConsolePresenter;

public final class CommandLoop {
    private final CommandParser parser;
    private final CommandRegistry registry;
    private final ConsolePresenter presenter;

    public CommandLoop(CommandParser parser, CommandRegistry registry, ConsolePresenter presenter) {
        this.parser = parser;
        this.registry = registry;
        this.presenter = presenter;
    }

    public void run() {
        presenter.println("Менеджер завдань. Введіть help, щоб переглянути команди.");

        while (true) {
            String line = presenter.ask("> ");
            if (line == null) {
                return;
            }
            if (line.isBlank()) {
                continue;
            }

            try {
                ParsedCommand parsed = parser.parse(line);
                if (parsed.name().equalsIgnoreCase("exit")) {
                    requireNoArguments(parsed, "Використання: exit");
                    return;
                }

                Command command = registry.find(parsed.name());
                if (command == null) {
                    throw new IllegalArgumentException("Невідома команда: " + parsed.name());
                }
                command.execute(parsed);
            } catch (RuntimeException exception) {
                String message = exception.getMessage();
                presenter.error(message == null || message.isBlank() ? "Не вдалося виконати команду." : message);
            }
        }
    }

    private static void requireNoArguments(ParsedCommand command, String usage) {
        if (!command.arguments().isEmpty() || !command.options().isEmpty()) {
            throw new IllegalArgumentException(usage);
        }
    }
}
