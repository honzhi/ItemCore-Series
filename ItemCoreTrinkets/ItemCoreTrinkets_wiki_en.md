# ItemCoreTrinkets Documentation

> A trinket slot system based on ItemCore

---

## 1. Overview

ItemCoreTrinkets is a **trinket slot system** plugin that depends on ItemCore. Players can equip ItemCore items into various trinket slots through a GUI. When equipped, the item's attributes are automatically applied to the player.

### Core Features

- Visual GUI management interface with customizable layout
- Unlimited trinket slots, any type
- Slot matching based on item type (e.g., ring → ring slot)
- Slot condition system (permission/level requirements)
- Automatic attribute calculation (via ItemCore's AttributeProvider mechanism)

---

## 📖 Table of Contents

1. [1. Overview](#1-overview)
   - [Core Features](#core-features)
2. [2. Quick Start](#2-quick-start)
   - [Basic Flow](#basic-flow)
   - [Permissions](#permissions)
3. [3. Configuration Files](#3-configuration-files)
   - [3.1 config.yml — Plugin Settings](#31-configyml--plugin-settings)
   - [3.2 slots.yml — Slot Definitions](#32-slotsyml--slot-definitions)
     - [Format](#format)
     - [Example](#example)
   - [3.3 gui.yml — GUI Layout](#33-guiyml--gui-layout)
     - [settings — Basic Settings](#settings--basic-settings)
     - [layout — Character Map Layout](#layout--character-map-layout)
     - [slots — Slot Definitions](#slots--slot-definitions)
     - [All function Types](#all-function-types)
     - [Locked State](#locked-state)
   - [3.4 messages.yml — Message Text](#34-messagesyml--message-text)
4. [4. Tutorial: Configuring a Trinket](#4-tutorial-configuring-a-trinket)
   - [Goal](#goal)
   - [Step 1: Create an Item Type in ItemCore](#step-1-create-an-item-type-in-itemcore)
   - [Step 2: Create an Item in ItemCore](#step-2-create-an-item-in-itemcore)
   - [Step 3: Add a Slot in ItemCoreTrinkets/slots.yml](#step-3-add-a-slot-in-itemcoretrinketsslotsyml)
   - [Step 4: Add the Slot in gui.yml](#step-4-add-the-slot-in-guiyml)

---

## 2. Quick Start

### Basic Flow

1. Create an item using ItemCore
2. Set the item's `type` field to the trinket type (e.g., `ring`)
3. Open the trinket GUI with `/ict`
4. Drag the item onto the corresponding slot

### Permissions

| Permission Node | Default | Description |
|---|---|---|
| `itemcoretrinkets.gui` | Everyone | Open the trinket GUI |
| `itemcoretrinkets.admin` | OP | Admin permission (includes reload) |
| `itemcoretrinkets.admin.reload` | OP | Reload configuration |

---

## 3. Configuration Files

The plugin's data files are located in `plugins/ItemCoreTrinkets/`. There are 4 configuration files in total.

---

### 3.1 config.yml — Plugin Settings

```yaml
# General settings
general:
  # Auto-save interval (seconds)
  # The plugin periodically writes player data to disk
  # Set to 0 to disable auto-save
  auto-save-interval: 300

  # Debug mode
  # Enables detailed console logs for troubleshooting
  debug-mode: false
```

| Key | Type | Default | Description |
|---|---|---|---|
| `general.auto-save-interval` | Integer | 300 | Auto-save interval (seconds), 0=disabled |
| `general.debug-mode` | Boolean | false | Debug logging toggle |

---

### 3.2 slots.yml — Slot Definitions

Defines the available trinket slots on the server, along with matching rules and unlock conditions for each slot.

#### Format

```yaml
slots:
  <slotId>:                        # Unique identifier
    type: <itemType>               # Matching ItemCore item type
    require:                       # (Optional) unlock conditions
      permission: "<permission>"   # Required permission
      level: <level>               # Required vanilla level
```

#### Example

```yaml
slots:
  ring_left:
    type: ring                     # Only items with type=ring can be equipped

  ring_right:
    type: ring

  amulet:
    type: amulet
    require:
      permission: "trinkets.amulet"  # Requires trinkets.amulet permission

  bracelet:
    type: bracelet
    require:
      level: 10                      # Requires level 10

  belt:
    type: belt
    require:
      permission: "trinkets.belt"    # Both conditions must be met
      level: 20
```

---

### 3.3 gui.yml — GUI Layout

Defines the appearance, layout, and display of each slot in the trinket management GUI.

#### settings — Basic Settings

```yaml
settings:
  rows: 6                          # GUI rows (1-6)
  title: "&6Trinket Manager"       # GUI title, supports & color codes
```

#### layout — Character Map Layout

Each row has 9 characters, with different letters representing different functional areas:

```yaml
layout:
  - 'AAAAAAAAA'     
  - 'AAAABAAXA'     
  - 'AAAFCGAAA'     
  - 'AAAADAAAA'     
  - 'AAAAEAAAA'     
  - 'AAAAAAAAA'     
```

Each character corresponds to a definition in the `slots` section below. Characters not defined in `slots` will not show any item.

#### slots — Slot Definitions

**Border decoration:**
```yaml
A:
  material: BLACK_STAINED_GLASS_PANE
  display-name: " "
  function: border
```

**Vanilla equipment display slot (read-only):**
```yaml
B:
  material: AIR
  display-name: "&7Helmet"
  function: equipment_slot
  slot-type: helmet
```

| Field | Required | Description |
|---|---|---|
| `material` | Yes | Material to display when empty (English name) |
| `display-name` | Yes | Display name, supports & color codes |
| `function` | Yes | Function type |
| `slot-type` | Yes | Vanilla slot type: `helmet`/`chestplate`/`leggings`/`boots`/`offhand` |

**Trinket slot (interactive):**
```yaml
F:
  material: glass_pane
  display-name: "&6Left Ring"
  custom-model-data: 0
  function: trinket_slot
  trinket-slot-id: ring_left
  lore:
    - "&7Type: Ring"
    - ""
    - "&eClick to equip a trinket"
  locked-material: BARRIER
  locked-lore:
    - "&cLocked"
    - "&7You need to meet the requirements to use this slot"
```

| Field | Required | Default | Description |
|---|---|---|---|
| `material` | Yes | — | Material for empty slot (English name) |
| `display-name` | Yes | — | Display name, supports & colors |
| `custom-model-data` | No | 0 | Custom model data (for resource packs) |
| `function` | Yes | — | Must be `trinket_slot` |
| `trinket-slot-id` | Yes | — | Corresponding slot ID in slots.yml |
| `lore` | No | None | Lore text for empty slot, supports & colors |
| `locked-material` | No | `RED_STAINED_GLASS_PANE` | Material when conditions are not met |
| `locked-lore` | No | Auto-generated | Lore text when conditions are not met |

**Close button:**
```yaml
→:
  material: ARROW
  display-name: "&aClose"
  custom-model-data: 0
  function: close
```

#### All function Types

| Value | Description | Required Fields |
|---|---|---|
| `border` | Non-interactive decorative border | material, display-name |
| `trinket_slot` | Interactive trinket slot | material, display-name, trinket-slot-id |
| `equipment_slot` | Read-only vanilla equipment display | material, display-name, slot-type |
| `close` | Button to close the GUI | material, display-name |

#### Locked State

When a player does not meet the `require` conditions for a slot in `slots.yml`:

- **When empty:** Displays the material specified by `locked-material` + lore from `locked-lore`
- **When equipped:** Attributes are not calculated (the trinket is effectively disabled), and the GUI shows the locked state
- **On equip attempt:** Shows the "Slot locked, cannot use" message

---

### 3.4 messages.yml — Message Text

```yaml
messages:
  prefix: "&6[Trinkets] "
  no-permission: "&cYou don't have permission to use this command"
  type-mismatch: "&cItem type does not match slot requirement"
  slot-locked: "&cSlot is locked and cannot be used"
  config-reloaded: "&aConfiguration reloaded"
```

---

## 4. Tutorial: Configuring a Trinket

### Goal

Add a **necklace** trinket slot to your server that only VIP players can use and grants extra health when equipped.

### Step 1: Create an Item Type in ItemCore

Create a new type using `ItemCore/categories.yml` (or add it to your item configuration):

```yaml
categories:
  ring:
    name: Ring
    icon: emerald
    slot: 23
    display-name: "&dRings"
    lore:
      - "&7Other items"
      - "&7Click to view all special items"
    items-file: ring.yml
```

> The item's `type` field must match the `type` in `slots.yml` (case-insensitive).

### Step 2: Create an Item in ItemCore

Manually create a `ring.yml` file in the items folder, then create an item inside. Set `active-slots` to `trinkets`.

```yaml
TestRing:
  material: emerald
  type: ring_left
  display-name: "&bTest Ring"
  lore:
    - "&7A test ring"
  unbreakable: true
  attributes:
    HEALTH: 20
  active-slots:
    - trinkets 
  item-flags:
    - HIDE_ENCHANTS
    - HIDE_ATTRIBUTES
    - HIDE_UNBREAKABLE
```

### Step 3: Add a Slot in ItemCoreTrinkets/slots.yml

The `type` here should match the item type defined in ItemCore.

```yaml
slots:
  ring_left:
    type: ring
    require:
      permission: "player.vip"
```

### Step 4: Add the Slot in gui.yml

First, modify the layout by adding a new character (e.g., `N`) in an empty spot:

```yaml
layout:
  - 'AAAAAAAAA'
  - 'AAAABAAXA'
  - 'AAANCGAAA'
  - 'AAAADAAAA'
  - 'AAAAEAAAA'
  - 'AAAAAAAAA'
```

Then add the N definition in the `slots` section:

```yaml
  N:
    material: IRON_NUGGET
    display-name: "&bRing Slot"
    custom-model-data: 0
    function: trinket_slot
    trinket-slot-id: ring_left
    lore:
      - "&7Type: Ring"
      - ""
      - "&eClick to equip a ring"
    locked-material: BARRIER
    locked-lore:
      - "&cLocked"
      - "&7Requires VIP permission"
```

