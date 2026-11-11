package me.suprun.presenter;

import me.suprun.infra.display.Display;

public final class ConsolePresenter {
    private final Display display;

    public ConsolePresenter(Display display) {
        this.display = display;
    }

    public String ask(String prompt) {
        display.write(prompt);
        return display.readLine();
    }

    public void println(String value) {
        display.writeLine(value);
    }

    public void error(String message) {
        println("Помилка: " + message);
    }
}
