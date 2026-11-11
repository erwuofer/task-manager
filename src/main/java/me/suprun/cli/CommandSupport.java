package me.suprun.cli;

public final class CommandSupport {
    private CommandSupport() {
    }

    public static void requireNoArguments(ParsedCommand command, String usage) {
        if (!command.arguments().isEmpty() || !command.options().isEmpty()) {
            throw new IllegalArgumentException(usage);
        }
    }

    public static long requireId(ParsedCommand command, String usage) {
        if (command.arguments().size() != 1 || !command.options().isEmpty()) {
            throw new IllegalArgumentException(usage);
        }

        try {
            long id = Long.parseLong(command.arguments().getFirst());
            if (id <= 0) {
                throw new IllegalArgumentException("Ідентифікатор має бути додатним числом.");
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Ідентифікатор має бути цілим числом.");
        }
    }
}
