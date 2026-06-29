package com.minemart.itemcore.item.attribute;

public class ElementType {

    private final String id;
    private final String displayName;

    public ElementType(String id, String displayName) {
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
        if (!(o instanceof ElementType)) return false;
        return id.equals(((ElementType) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ElementType{id='" + id + "', display='" + displayName + "'}";
    }

    public static final ElementType NONE = new ElementType("NONE", "无");
    public static final ElementType LIUHUO = new ElementType("LIUHUO", "流火");
    public static final ElementType HANSHUANG = new ElementType("HANSHUANG", "寒霜");
    public static final ElementType LEIZHE = new ElementType("LEIZHE", "雷蛰");
    
    public static final ElementType FIRE = LIUHUO;
    public static final ElementType ICE = HANSHUANG;
    public static final ElementType THUNDER = LEIZHE;
}
