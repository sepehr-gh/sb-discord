package com.github.sepehrgh.sbdiscord.exceptions;

public class CommandParseException extends Exception {
    public CommandParseException() {
        super("Failed to parse the command inputs");
    }

    public CommandParseException(String s) {
        super(s);
    }

    public CommandParseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CommandParseException(Throwable throwable) {
        super(throwable);
    }

    public CommandParseException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
