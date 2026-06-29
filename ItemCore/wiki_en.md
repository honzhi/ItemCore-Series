# ItemCore Wiki

> | Paper 1.21.x |  This wiki is translated by AI. Please excuse any errors.

---

## 📑 Table of Contents

- [1. Introduction](#1-introduction)
- [2. Installation and Configuration](#2-installation-and-configuration)
- [3. Command System](#3-command-system)
- [4. Permission System](#4-permission-system)
- [5. Item Creation](#5-item-creation)
- [6. Attribute System](#6-attribute-system)
- [7. Element System](#7-element-system)
- [8. Damage System](#8-damage-system)
- [Expansion 1 Skill System (ItemCoreMythic)](#expansion-1-skill-system-itemcoremythic)
- [Appendix 1 PlaceholderAPI Placeholders](#appendix-1-placeholderapi-placeholders)
- [Appendix 2 Developer API](#appendix-2-developer-api)
- [FAQ](#faq)

---

## 1. Introduction

ItemCore is a concise and focused item library plugin, featuring the ability to freely add expansion plugins to enrich plugin functionality.

Below are expansion plugins that are already or planned to be developed
| Plugin | Purpose |
|--------|---------|
| **ItemCoreMythic** | MythicMobs expansion |
| **ItemCoreForge** | RPG expansion |
| **ItemCoreRPG** | Forging expansion |
| **ItemCoreTrinkets** | Trinket expansion |


**Core Features:**
  
- Full configurability
- Unique element system (element accumulation/ailment mechanism)    
- Real-time update of player item lore and attributes

---

## 2. Installation and Configuration

### 2.1 Installation Steps

1. Place `ItemCore.jar` into the `plugins/` directory
2. Restart the server
3. The plugin will automatically generate the configuration file structure on first startup:

```
plugins/ItemCore/
├── config.yml              # Main configuration
├── attributes.yml          # Attribute global parameters
├── messages.yml            # Message configuration
├── categories.yml          # Category configuration
├── elements.yml            # Element configuration
├── ailments.yml            # Ailment configuration
├── items/                  # Item configuration directory
│   ├── weapons.yml
│   ├── armors.yml
│   └── ...
├── tooltip/
│   ├── lore.yml            # Lore layout
│   └── stats.yml           # Attribute display format
```

---

## 3. Command System

### 3.1 Main Command `/ic`

| Subcommand | Purpose | Permission |
|------------|---------|------------|
| `open` | Open item library GUI | `itemcore.command.gui` |
| `give <player> <item> [amount]` | Give item | `itemcore.command.give` |
| `reload` | Hot reload all configurations | `itemcore.command.reload` |
| `help` | Show help | `itemcore.command.help` |

**Examples:**

```
/ic open                       # Open item library
/ic give Is_Lianhua iron_sword # Give item
/ic reload                     # Reload configuration
```
---

## 4. Permission System

| Permission Node | Default | Description |
|-----------------|---------|-------------|
| `itemcore.admin` | OP | Admin permission (includes all below) |
| `itemcore.command.give` | OP | Give item |
| `itemcore.command.reload` | OP | Reload configuration |
| `itemcore.gui.obtain` | OP | Obtain item from GUI |
| `itemcore.gui.gui` | OP | Open item GUI |
| `itemcore.command.help` | Everyone | View help |

---

## 5. Item Creation

Create items in category files under the items/ folder, such as weapons.yml.

**Complete configurable items:**

| Configuration | Type | Default | Description |
|---------------|------|---------|-------------|
| `material` | Material name | **Required** | Item material |
| `type` | String | - | Temporarily unused |
| `display-name` | String | — | Display name (supports `&` color codes) |
| `lore` | List | — | Item description |
| `attributes` | Map | — | Attribute configuration |
| `enchantments` | Map | — | Enchantments |
| `item-flags` | List | — | Item flags |
| `unbreakable` | Boolean | false | Whether unbreakable |
| `max-stack` | Integer | Vanilla rules | Maximum stack size |
| `custom-model-data` | Integer | — | Custom model data |
| `active-slots` | List | any | Active equipment slots |
| `effects` | List | — | Potion effects |
| `keep-on-death` | Boolean | false | Whether to keep on death |
| `skills` | List | — | Skill configuration (requires ItemCoreMythic) |

**Configuration Supplement**
```yaml
active-slots:
  - main-hand  # Main hand
  - off-hand   # Off hand
  - head       # Head
  - chest      # Chest
  - legs       # Legs
  - feet       # Feet
  - any        # All
item-flags:
  - HIDE_ATTRIBUTES   # Hide attributes (will directly remove vanilla attributes)
  - HIDE_ENCHANTS    # Hide enchantments
  - HIDE_UNBREAKABLE  # Hide unbreakable
  - HIDE_POTION_EFFECTS  # Hide potion effects

```

**Example - Complete Item:**

```yaml
fire_sword:
  material: DIAMOND_SWORD
  type: weapons
  display-name: "&cFlame Sword"
  lore:
    - "&7Flame sword that burns everything"
  enchantments:
    sharpness: 3
    fire_aspect: 2
  item-flags:
    - HIDE_ATTRIBUTES
  unbreakable: true
  active-slots:
    - main-hand
  attributes:
    ATTACK_DAMAGE: 12
    ATTACK_SPEED: 1.6
    CRIT_CHANCE: 15
    CRIT_DAMAGE: 30
    PHYSICAL_PENETRATION: 5
  skills:
    Right_Click:
      skill: fireball
      provider: mythicmobs
```


---

## 6. Attribute System

### 6.1 Complete Attribute List

**Attack Class:**

| Config Key | Display Name | Percentage | Description |
|------------|--------------|------------|-------------|
| `ATTACK_DAMAGE` | Attack Damage | — | Base attack power, directly added to damage |
| `ATTACK_SPEED` | Attack Speed | — | Final attack speed value (e.g., `1.6` = iron sword speed) |
| `ATTACK_RANGE` | Attack Range | — | Attack distance bonus |
| `KNOCKBACK` | Knockback | — | Knockback bonus |
| `CRIT_CHANCE` | Crit Chance | % | Critical strike probability |
| `CRIT_DAMAGE` | Crit Damage | % | Extra crit multiplier (total crit = default 150% + this value) |

**Spell/Damage Type:**

| Config Key | Display Name | Percentage | Description |
|------------|--------------|------------|-------------|
| `SPELL_POWER` | Spell Power | — | Spell damage base value |
| `PHYSICAL_DAMAGE` | Physical Bonus | % | Physical damage percentage bonus |
| `SPELL_DAMAGE` | Spell Bonus | % | Spell damage percentage bonus |
| `PROJECTILE_DAMAGE` | Projectile Bonus | % | Projectile damage percentage bonus |
| `ADAPTIVE_FORCE` | Adaptive Force | — | Automatically converts to attack or spell power (whichever is higher) |

**Defense Class:**

| Config Key | Display Name | Percentage | Description |
|------------|--------------|------------|-------------|
| `PHYSICAL_RESIST` | Physical Resistance | — | Physical damage reduction (configurable formula) |
| `SPELL_RESIST` | Spell Resistance | — | Spell damage reduction (configurable formula) |
| `ARMOR` | Armor | — | Vanilla armor value |
| `DAMAGE_REDUCTION` | Damage Reduction | % | Final percentage damage reduction (can be negative) |

**Penetration Class:**

| Config Key | Display Name | Percentage | Description |
|------------|--------------|------------|-------------|
| `PHYSICAL_PENETRATION` | Physical Penetration | — | Fixed value physical penetration |
| `PHYSICAL_PENETRATION_PERCENT` | Physical Penetration | % | Percentage physical penetration |
| `SPELL_PENETRATION` | Spell Penetration | — | Fixed value spell penetration |
| `SPELL_PENETRATION_PERCENT` | Spell Penetration | % | Percentage spell penetration |

**Survival Class:**

| Config Key | Display Name | Percentage | Description |
|------------|--------------|------------|-------------|
| `HEALTH` | Health | — | Extra health (base 20 points) |
| `MOVEMENT_SPEED` | Movement Speed | — | Extra movement speed (base 0.1) |
| `REGENERATION` | Life Regeneration | — | Health regeneration per second |
| `LUCK` | Luck | — | Luck bonus |

All percentage attributes are filled directly as integers:

```yaml
CRIT_CHANCE: 30    # ✅ Correct = 30%
CRIT_DAMAGE: 50    # ✅ Correct = 50%
PHYSICAL_DAMAGE: 10  # ✅ Correct = 10%
```

---

## 7. Element System

### 7.1 Default Elements

| Element ID | Display Name | Icon | Color | Ailment Effect |
|------------|--------------|------|-------|----------------|
| `LIUHUO` | Fire | `🔥` | `&c` | Burning: Lasts 8 seconds, 2% current health damage per second |
| `HANSHUANG` | Frost | `❄️` | `&b` | Weakness: Lasts 4 seconds, physical/spell resistance -30% |
| `LEIZHE` | Thunder | `⚡` | `&e` | Vulnerability: Lasts 6 seconds, element resistance -25% |

### 7.2 Element Accumulation

```yaml
# elements.yml
LIUHUO:
  display: "&cFire"
  icon: '🔥'
  color: '&c'
  threshold: 30           # Accumulation threshold, triggers ailment when reached
  decay-per-second: 1     # Decay per second when no elemental damage
  accumulation:
    mode: DAMAGE_PERCENT  # DAMAGE_PERCENT / FIXED / ATTRIBUTE
    value: 0.5            # Each damage = damage value × 50% accumulation
    allow-sources:
      - ATTACK
      - SKILL
  ailment: LIUHUO_DOT     # Associated ailment ID (see ailments.yml)
```

**Accumulation Modes:**
- `DAMAGE_PERCENT`: Accumulation = damage × `value`
- `FIXED`: Fixed accumulation of `value` each time
- `ATTRIBUTE`: Based on attribute value × `multiplier`

### 7.3 Ailment Configuration

```yaml
# ailments.yml
LIUHUO_DOT:
  display: "&cFire"
  duration: 160           # 160 ticks = 8 seconds
  refresh-policy: RESET   # RESET / STACK / IGNORE / REPLACE
  triggers:
    - type: DAMAGE_PERCENT
      value: 0.02         # 2% current health
      interval: 20        # Triggers once per second

HANSHUANG_WEAKEN:
  display: "&bFrost"
  duration: 80
  refresh-policy: RESET
  triggers:
    - type: ATTRIBUTE_MOD
      attribute: PHYSICAL_RESIST
      value: -0.3         # Physical resistance -30%
    - type: ATTRIBUTE_MOD
      attribute: SPELL_RESIST
      value: -0.3         # Spell resistance -30%

LEIZHE_BREAK:
  display: "&eThunder"
  duration: 120
  refresh-policy: RESET
  triggers:
    - type: RESISTANCE_REDUCTION
      value: -0.25        # All element resistance -25%
```

**Trigger Types:**
- `DAMAGE_PERCENT` — Deal percentage damage of that element
- `DAMAGE_FIXED` — Fixed value damage of that element
- `ATTRIBUTE_MOD` — Temporarily modify target attribute
- `RESISTANCE_REDUCTION` — Reduce element resistance
- `POTION_EFFECT` — Apply potion effect

### 7.4 Expanding Custom Elements

Register in code:

```java
ItemCore.getInstance().getElementRegistry()
    .register(new ElementType("ARCANE", "Arcane"));
```

Configure corresponding accumulation/ailment rules in `elements.yml` and `ailments.yml`.

---

## 8. Damage System

### 8.1 Damage Types

| Type | Tag | Description |
|------|-----|-------------|
| **Physical** | `PHYSICAL` | Default attack type, affected by physical bonus/physical resistance |
| **Spell** | `SPELL` | Default skill type, affected by spell bonus/spell resistance |
| **Projectile** | `PROJECTILE` | Projectile damage, affected by projectile bonus |

### 8.2 Damage Calculation Process

```
Original damage
  → Attack type tag (PHYSICAL/SPELL/PROJECTILE)
  → Crit check (CRIT_CHANCE → CRIT_DAMAGE)
  → Damage type percentage bonus (PHYSICAL_DAMAGE%, etc.)
  → Penetration calculation (percentage first or fixed first)
  → Resistance reduction (only non-elemental damage is affected by physical/spell resistance)
  → Element resistance reduction (only elemental damage is affected)
  → Damage reduction DAMAGE_REDUCTION% (final damage reduction)
  → Element accumulation
  → Final damage
```

### 8.3 Elemental Damage Rules

- Damage with element type **skips** physical/spell resistance calculation
- Only affected by **element resistance** and **DAMAGE_REDUCTION**
- When element resistance is negative, it becomes vulnerability bonus

### 8.4 Damage Reduction Priority

`DAMAGE_REDUCTION` is final damage reduction, affecting all damage (including elemental damage), calculated叠加 with physical resistance/spell resistance/element resistance.

---

## Expansion 1 Skill System (ItemCoreMythic)

### 1.1 Skill Configuration

```yaml
# In item configuration
skills:
  Right_Click:
    skill: fireball
    provider: mythicmobs
  Left_Click:
    skill: slash
    provider: mythicmobs
  Timer:
    skill: regen
    provider: mythicmobs
    duration: 20
```

### 1.2 Skill Trigger Types

| Trigger Type | Description |
|--------------|-------------|
| `Right_Click` | Right-click trigger |
| `Left_Click` | Left-click trigger |
| `Timer` | Timer, `duration` = interval ticks |

### 1.3 ICDamageMechanic

Custom damage can be used in MythicMobs skills:

```yaml
Skills:
- icdamage{amount="<ic.attack_damage> * 2",type=physical,element=LIUHUO,penetration=5,crit=true} @EIR{r=5}
```

**Parameters:**

| Parameter | Alias | Default | Description |
|-----------|-------|---------|-------------|
| `amount` | `a` | `1` | Damage formula, supports `<ic.xxx>` placeholders and mathematical operations |
| `type` | `t` | `physical` | Damage type: `physical` / `spell` / `projectile` |
| `element` | `e` | `none` | Element type: `LIUHUO` / `HANSHUANG` / `LEIZHE` |
| `crit` | `c` | `true` | Whether can crit |
| `attacktype` | `at` | `skill` | Attack type: `skill` / `attack` |
| `penetration` | `p` | `0` | Extra penetration value |
| `lifesteal` | `ls` | `0` | Lifesteal ratio |

**Mathematical Operation Support:**
```
<ic.spell_power> * 0.5        # 50% spell power
<ic.attack_damage> * 2 + 10  # Attack power × 2 + 10
<ic.attack_damage> * 50%     # 50% attack power
```

### 1.4 active-slots Scope of Influence

`active-slots` configuration controls:
- **Attributes**: Equipment slot determines whether active
- **Skills**: Main hand/off hand configuration determines skill trigger
- **Potion Effects**: Equipment slot determines whether to grant potion effects

---

## Appendix 1 PlaceholderAPI Placeholders

| Placeholder | Description |
|-------------|-------------|
| `%itemcore_attack_damage%` | Attack damage |
| `%itemcore_attack_speed%` | Attack speed |
| `%itemcore_attack_range%` | Attack range |
| `%itemcore_health%` | Current health |
| `%itemcore_max_health%` | Maximum health |
| `%itemcore_movement_speed%` | Total movement speed |
| `%itemcore_regeneration%` | Life regeneration |
| `%itemcore_knockback%` | Knockback |
| `%itemcore_luck%` | Luck |
| `%itemcore_spell_damage%` | Spell bonus |
| `%itemcore_physical_damage%` | Physical bonus |
| `%itemcore_projectile_damage%` | Projectile bonus |
| `%itemcore_spell_power%` | Spell power |
| `%itemcore_adaptive_force%` | Adaptive force |
| `%itemcore_crit_chance%` | Crit chance |
| `%itemcore_crit_damage%` | Crit damage |
| `%itemcore_physical_resist%` | Physical resistance |
| `%itemcore_spell_resist%` | Spell resistance |
| `%itemcore_physical_penetration%` | Physical penetration |
| `%itemcore_physical_penetration_percent%` | Percentage physical penetration |
| `%itemcore_spell_penetration%` | Spell penetration |
| `%itemcore_spell_penetration_percent%` | Percentage spell penetration |
| `%itemcore_damage_reduction%` | Damage reduction |


---

## Appendix 2 Developer API

### 1. Get API Instance

```java
import com.minemart.itemcore.api.ItemCoreAPI;

// Get player attributes
AttributeContainer attrs = ItemCoreAPI.getPlayerAttributes(player);
double attackDamage = attrs.getAttribute(CustomAttribute.ATTACK_DAMAGE);

// Get item
CustomItem item = ItemCoreAPI.getCustomItem("legendary_blade");

// Get categories
Collection<ItemCategory> categories = ItemCoreAPI.getCategories();

// Process custom damage
DamageRequest request = DamageRequest.builder()
    .attacker(attacker)
    .victim(victim)
    .baseDamage(100)
    .damageType(DamageTag.SPELL)
    .element(ElementType.LIUHUO)
    .canCrit(true)
    .attackType(AttackType.SKILL)
    .build();
ItemCoreAPI.processDamage(request);
```

### 2. Register Custom Element

```java
ItemCore.getInstance().getElementRegistry()
    .register(new ElementType("ARCANE", "Arcane"));
```

---

## FAQ

**Q: Do I need to restart the server after modifying the configuration?**
A: `/ic reload` can hot reload, no need to restart.

**Q: Why aren't attributes working?**
A: Check `active-slots` configuration. Items must be in the specified equipment slot to work.

**Q: How are attributes from multiple equipment calculated?**
A: Attributes with the same name are automatically accumulated. If two pieces of equipment both have `ATTACK_DAMAGE: 10`, the final result is +20.

**Q: Why does crit damage show 200%?**
A: `CRIT_DAMAGE` is an extra bonus. Total crit = default value (150%) + `CRIT_DAMAGE`. The default value can be modified in `attributes.yml`.

**Q: How to configure attack speed?**
A: Write the final value directly. `ATTACK_SPEED: 1.6` = iron sword speed. `ATTACK_SPEED: 4.0` = extremely fast.

**Q: How to write percentage attributes?**
A: Fill in integers directly. `CRIT_CHANCE: 30` = 30%, not 0.3.

**Q: What is adaptive force used for?**
A: Automatically converts to corresponding bonus based on whichever is higher between attack/spell power. Conversion rate is configured in `attributes.yml`.

**Q: How is elemental damage calculated?**
A: Damage with element skips physical/spell resistance, only affected by element resistance and damage reduction.

**Q: How to make item skills trigger?**
A: Install ItemCoreMythic, configure skills in the `skills` node of item configuration, ensure `active-slots` includes `main-hand`.

**Q: Why is attack speed incorrect for items taken in creative mode?**
A: This is a mechanism issue with Minecraft creative mode inventory, normal in survival mode or obtained via command.

---
