package com.jack.model.enums;

public enum TransactionInstitutionType {
    SIMPLE("SIMPLE"),
    CREDIT("CREDIT"),
    BANK("BANK"),
    APP("APP");

    private final String label;

    TransactionInstitutionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
