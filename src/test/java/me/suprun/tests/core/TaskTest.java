package me.suprun.tests.core;

import me.suprun.core.tasks.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {
    @Test
    void normalizesTitleAndDescription() {
        Task task = new Task(1, "  Підготувати звіт  ", "  Перевірити висновки  ", false);

        assertEquals("Підготувати звіт", task.title());
        assertEquals("Перевірити висновки", task.description());
    }

    @Test
    void allowsMissingDescription() {
        Task task = new Task(1, "Підготувати звіт", null, false);

        assertEquals("", task.description());
    }

    @Test
    void rejectsInvalidIdAndEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> new Task(0, "Завдання", "", false));
        assertThrows(IllegalArgumentException.class, () -> new Task(-1, "Завдання", "", false));
        assertThrows(IllegalArgumentException.class, () -> new Task(1, "   ", "", false));
        assertThrows(NullPointerException.class, () -> new Task(1, null, "", false));
    }

    @Test
    void completingTaskReturnsCompletedCopy() {
        Task pending = new Task(1, "Завдання", "", false);
        Task completed = pending.markCompleted();

        assertNotSame(pending, completed);
        assertFalse(pending.completed());
        assertTrue(completed.completed());
        assertSame(completed, completed.markCompleted());
    }
}
