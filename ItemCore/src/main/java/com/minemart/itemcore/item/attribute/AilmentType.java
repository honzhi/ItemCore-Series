package com.minemart.itemcore.item.attribute;

public class AilmentType {

    private final String id;
    private final String displayName;

    public AilmentType(String id, String displayName) {
        this.id = id.toUpperCase();
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AilmentType)) return false;
        return id.equals(((AilmentType) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "AilmentType{id='" + id + "', display='" + displayName + "'}";
    }
}
