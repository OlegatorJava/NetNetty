package org.example.model;

public enum Command {
    AUTH_OK("AUTH_OK");

    private final String command;


    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
