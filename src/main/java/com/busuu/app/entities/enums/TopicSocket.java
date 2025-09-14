package com.busuu.app.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TopicSocket {
    PRESENCE("presence"),
    CHAT("chat");

    private final String value;

    TopicSocket(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidDestination(String destination) {
        for (TopicSocket topic : values()) {
            if (destination.startsWith("/topic/" + topic.value + ".")) {
                return true;
            }
        }
        return false;
    }
}
