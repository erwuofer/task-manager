package me.suprun.cli;

import me.suprun.presenter.ConsolePresenter;

import java.util.Locale;

public final class ModeSelector {
    public TaskMode select(String[] args, ConsolePresenter presenter) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Використання: --view або --manage");
        }

        if (args.length == 1) {
            return switch (args[0].toLowerCase(Locale.ROOT)) {
                case "--view" -> TaskMode.VIEW;
                case "--manage" -> TaskMode.MANAGE;
                default -> throw new IllegalArgumentException("Невідомий режим. Використовуйте --view або --manage.");
            };
        }

        presenter.println("Оберіть режим роботи:");
        presenter.println("1. Перегляд завдань");
        presenter.println("2. Керування завданнями");

        String choice = presenter.ask("> ");
        if (choice == null) {
            throw new IllegalArgumentException("Режим роботи не вибрано.");
        }

        return switch (choice.trim()) {
            case "1" -> TaskMode.VIEW;
            case "2" -> TaskMode.MANAGE;
            default -> throw new IllegalArgumentException("Невірний вибір режиму.");
        };
    }
}
