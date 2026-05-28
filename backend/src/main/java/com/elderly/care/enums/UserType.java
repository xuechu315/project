package com.elderly.care.enums;

public enum UserType {
    elder("老人"),
    family("家属"),
    doctor("医生"),
    admin("管理员");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}