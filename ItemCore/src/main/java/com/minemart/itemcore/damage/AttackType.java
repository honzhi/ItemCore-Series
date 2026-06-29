package com.minemart.itemcore.damage;

public enum AttackType {
    ATTACK,  // 普攻触发 — 使用玩家暴击/吸血属性，强制 PHYSICAL
    SKILL    // 技能释放 — 使用传入的伤害类型和暴击参数
}