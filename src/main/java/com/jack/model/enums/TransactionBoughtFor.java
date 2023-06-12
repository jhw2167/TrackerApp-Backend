package com.jack.model.enums;

public enum TransactionBoughtFor{
    PERSONAL("PERSONAL"),
    FRIEND("FRIEND"),
    FAMILY("FAMILY"),
    DATE("DATE"),
    GROUP("GROUP");

    private final String label;

    TransactionBoughtFor(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
