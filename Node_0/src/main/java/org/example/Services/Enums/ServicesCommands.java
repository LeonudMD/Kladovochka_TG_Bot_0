package org.example.Services.Enums;

public enum ServicesCommands {

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String cmd;

    ServicesCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public  String toString() {
        return cmd;
    }

    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }

}
