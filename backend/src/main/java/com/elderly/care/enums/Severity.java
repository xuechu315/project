package com.elderly.care.enums;

public enum Severity {
    critical("紧急"),
    warning("警告"),
    notice("注意"),
    normal("正常");

    private final String description;

    Severity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}