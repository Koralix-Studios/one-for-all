package com.koralix.oneforall.input.event;

public record MouseButtonEvent(int button, int action) implements ButtonEvent {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MouseButtonEvent that = (MouseButtonEvent) obj;
        return button == that.button && action == that.action;
    }
}
