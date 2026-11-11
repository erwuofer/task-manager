package me.suprun.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ParsedCommand(
        String name,
        List<String> arguments,
        Map<String, String> options) {

    public ParsedCommand {
        arguments = List.copyOf(arguments);
        options = Collections.unmodifiableMap(new LinkedHashMap<>(options));
    }
}
