package io.github.sepgh.sbdiscord.exceptions;

public class CommandParseException extends Exception {
    public CommandParseException() {
        super("Failed to parse the command inputs");
    }

    public CommandParseException(String s) {
        super(s);
    }
}
