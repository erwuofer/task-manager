package me.suprun.tests.cli;

import me.suprun.cli.ModeSelector;
import me.suprun.cli.TaskMode;
import me.suprun.infra.display.ConsoleDisplay;
import me.suprun.presenter.ConsolePresenter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModeSelectorTest {
    @Test
    void selectsModeFromArguments() {
        ModeSelector selector = new ModeSelector();
        ConsolePresenter console = presenter("");

        assertEquals(TaskMode.VIEW, selector.select(new String[]{"--view"}, console));
        assertEquals(TaskMode.MANAGE, selector.select(new String[]{"--manage"}, console));
    }

    @Test
    void selectsModeThroughDialog() {
        ModeSelector selector = new ModeSelector();

        assertEquals(TaskMode.VIEW, selector.select(new String[0], presenter("1\n")));
        assertEquals(TaskMode.MANAGE, selector.select(new String[0], presenter("2\n")));
    }

    @Test
    void rejectsUnknownMode() {
        ModeSelector selector = new ModeSelector();

        assertThrows(IllegalArgumentException.class,
                () -> selector.select(new String[]{"--unknown"}, presenter("")));
        assertThrows(IllegalArgumentException.class,
                () -> selector.select(new String[]{"--view", "--manage"}, presenter("")));
    }

    private static ConsolePresenter presenter(String input) {
        ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        return new ConsolePresenter(new ConsoleDisplay(stream, new PrintStream(output)));
    }
}
