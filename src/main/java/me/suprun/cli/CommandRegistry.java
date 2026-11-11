package me.suprun.cli;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class CommandRegistry {
    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        String name = normalize(command.name());
        if (commands.putIfAbsent(name, command) != null) {
            throw new IllegalArgumentException("Команду вже зареєстровано: " + command.name());
        }
    }

    public Command find(String name) {
        return commands.get(normalize(name));
    }

    public Collection<Command> all() {
        return commands.values();
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
