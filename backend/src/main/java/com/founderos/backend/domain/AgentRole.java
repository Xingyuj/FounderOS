package com.founderos.backend.domain;

public enum AgentRole {
    CHIEF_OF_STAFF("Chief of Staff"),
    PRODUCT_LEAD("Product Lead"),
    RESEARCH_ANALYST("Research Analyst"),
    ENGINEERING_LEAD("Engineering Lead"),
    GROWTH_LEAD("Growth Lead");

    private final String displayName;

    AgentRole(String displayName) { this.displayName = displayName; }
    public String displayName() { return displayName; }
}
