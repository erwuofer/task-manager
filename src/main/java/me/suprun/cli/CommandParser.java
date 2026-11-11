package me.suprun.cli;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CommandParser {
    public ParsedCommand parse(String line) {
        List<String> tokens = tokenize(line);
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Порожню команду неможливо виконати.");
        }

        String name = tokens.getFirst();
        List<String> arguments = new ArrayList<>();
        Map<String, String> options = new LinkedHashMap<>();

        for (int i = 1; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (!token.startsWith("--")) {
                arguments.add(token);
                continue;
            }

            String key = token.substring(2);
            if (key.isBlank()) {
                throw new IllegalArgumentException("Некоректна опція: " + token);
            }
            if (options.containsKey(key)) {
                throw new IllegalArgumentException("Опцію --%s вказано декілька разів.".formatted(key));
            }

            String value = null;
            if (i + 1 < tokens.size() && !tokens.get(i + 1).startsWith("--")) {
                value = tokens.get(++i);
            }
            options.put(key, value);
        }

        return new ParsedCommand(name, arguments, options);
    }

    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        char quote = 0;

        for (char character : line.toCharArray()) {
            if (quote != 0) {
                if (character == quote) {
                    quote = 0;
                } else {
                    current.append(character);
                }
                continue;
            }

            if (character == '\'' || character == '"') {
                quote = character;
            } else if (Character.isWhitespace(character)) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(character);
            }
        }

        if (quote != 0) {
            throw new IllegalArgumentException("Не закрито лапки в команді.");
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
