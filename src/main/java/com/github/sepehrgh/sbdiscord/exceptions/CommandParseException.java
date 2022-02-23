package com.github.sepehrgh.sbdiscord.exceptions;

public class CommandParseException extends Exception {
    public CommandParseException() {
        super("Failed to parse the command inputs");
    }

    public CommandParseException(String s) {
        super(s);
    }
}
