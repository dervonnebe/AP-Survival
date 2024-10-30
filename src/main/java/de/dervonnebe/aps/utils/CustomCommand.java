package de.dervonnebe.aps.utils;

import lombok.Getter;

@Getter
public class CustomCommand {

    private final String name;
    private final String actionType;
    private final String action;
    private final String message;
    private final String permission;

    public CustomCommand(String name, String actionType, String action, String message, String permission) {
        this.name = name;
        this.actionType = actionType;
        this.action = action;
        this.message = message;
        this.permission = permission;
    }

}
