package me.suprun.infra.display;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public final class ConsoleDisplay implements Display {
    private final Scanner scanner;
    private final PrintStream output;

    public ConsoleDisplay() {
        this(System.in, System.out);
    }

    public ConsoleDisplay(InputStream input, PrintStream output) {
        this.scanner = new Scanner(input);
        this.output = output;
    }

    @Override
    public String readLine() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    @Override
    public void write(String value) {
        output.print(value);
    }

    @Override
    public void writeLine(String value) {
        output.println(value);
    }
}
