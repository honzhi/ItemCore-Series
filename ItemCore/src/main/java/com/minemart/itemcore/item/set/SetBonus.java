package com.minemart.itemcore.item.set;

import com.minemart.itemcore.item.attribute.AttributeContainer;
import com.minemart.itemcore.item.skill.ItemSkill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetBonus {

    private final int requiredPieces;
    private final AttributeContainer attributes;
    private final List<ItemSkill> skills;

    public SetBonus(int requiredPieces, AttributeContainer attributes, List<ItemSkill> skills) {
        this.requiredPieces = requiredPieces;
        this.attributes = attributes != null ? attributes : new AttributeContainer();
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
    }

    public int getRequiredPieces() {
        return requiredPieces;
    }

    public AttributeContainer getAttributes() {
        return attributes;
    }

    public List<ItemSkill> getSkills() {
        return Collections.unmodifiableList(skills);
    }
}
