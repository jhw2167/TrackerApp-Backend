package com.jack.model.enums;

public enum TransactionStatusType {
    COMPLETE("COMPLETE"),
    COVERED("COVERED"),
    OWED("OWED"),
    PENDING("PENDING"),
    OWED_PARTIAL("OWED_PARTIAL"),
    CANCELLED("CANCELLED");

    private final String label;

    TransactionStatusType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}