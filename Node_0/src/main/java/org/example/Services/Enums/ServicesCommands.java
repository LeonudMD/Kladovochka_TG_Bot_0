package org.example.Services.Enums;

public enum ServicesCommands {

    HELP("/help"),

    REGISTRATION("/registration"),

    CANCEL("/cancel"),

    START("/start");

    private final String value;

    ServicesCommands(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServicesCommands fromValue(String v) {
        for (ServicesCommands c : ServicesCommands.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}