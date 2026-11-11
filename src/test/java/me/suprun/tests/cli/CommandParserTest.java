package me.suprun.tests.cli;

import me.suprun.cli.CommandParser;
import me.suprun.cli.ParsedCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandParserTest {
    private final CommandParser parser = new CommandParser();

    @Test
    void readsQuotedArguments() {
        ParsedCommand command = parser.parse("show 'long task name'");

        assertEquals("show", command.name());
        assertEquals("long task name", command.arguments().getFirst());
    }

    @Test
    void readsNamedOptions() {
        ParsedCommand command = parser.parse("list --status completed");

        assertEquals("completed", command.options().get("status"));
    }

    @Test
    void rejectsUnclosedQuotes() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("show \"unfinished"));
    }
}
