package de.dervonnebe.aps.utils;

import lombok.Getter;

@Getter
public class CustomCommand {

    private final String name;
    private final String actionType;
    private final String action;
    private final String message;

    public CustomCommand(String name, String actionType, String action, String message) {
        this.name = name;
        this.actionType = actionType;
        this.action = action;
        this.message = message;
    }

}
