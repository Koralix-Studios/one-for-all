package com.koralix.oneforall.input.event;

public record KeyboardKeyEvent(int button, int action) implements ButtonEvent {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeyboardKeyEvent that = (KeyboardKeyEvent) obj;
        return button == that.button && action == that.action;
    }
}
