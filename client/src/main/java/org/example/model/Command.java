package org.example.model;

public enum Command {
    PUT("PUT"), GET("GET"), DELETE("DELETE"), AUTH("AUTH"), AUTH_OK("AUTH_OK");

    private final String command;


    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}