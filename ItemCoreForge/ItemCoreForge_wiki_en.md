# ItemCoreForge Wiki

***

## Overview

ItemCoreForge is an advanced crafting system plugin that provides custom forge functionality for Minecraft servers.

**Core Features**:

- Support for multiple independently configured forges
- Multi-queue parallel crafting
- Rich trigger system (commands, sounds, messages)
- Customizable GUI interface
- Support for ItemCore and MythicMobs items

**Dependencies**:

- **Optional**: ItemCore v1.0.0+
- **Optional**: MythicMobs v5.0+

***

## Installation

1. Place `ItemCoreForge-1.0.0.jar` in your server's `plugins/` directory
2. Start the server, the plugin will automatically generate configuration files
3. Configure forges as needed

***

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/icf` | Show help information | None |
| `/icf gui <forge-id>` | Open specified forge GUI | `itemcoreforge.gui` |
| `/icf list` | List all forges | `itemcoreforge.list` |
| `/icf reload` | Reload configuration files | `itemcoreforge.reload` |

***

## Permissions

| Permission Node | Description | Default |
|-----------------|-------------|---------|
| `itemcoreforge.*` | All permissions | OP |
| `itemcoreforge.gui` | Open forge GUI | Everyone |
| `itemcoreforge.list` | List forges | OP |
| `itemcoreforge.reload` | Reload config | OP |
| `itemcoreforge.admin` | Admin access | OP |

***

## Configuration Files

### forge.yml

Define forges and their recipes.

```yaml
forge-id: "example_template"
# Display name (supports color codes)
# Use & symbol for color codes
display-name: "&6Example Forge"
# Forge settings
settings:
  # Maximum queue size (number of items that can be queued simultaneously)
  # Recommended: 3-10
  max-queue-size: 6
  # Forge type
  # custom: Use custom layout file (requires GUI interface)
  # crafting_table: Vanilla crafting table recipe (use directly in vanilla crafting table)
  # furnace: Furnace type
  type: custom
  # Layout file to use (only applies when type is custom)
  # Layout files are located in the layouts/ directory
  layout-file: "Default.yml"
# Recipe list
# Multiple recipes can be defined
recipes:
  # ====================
  # Example 1: Vanilla Item Recipe
  # ====================
  iron_sword:
    # Output item
    output:
      # Item source
      # vanilla: Minecraft vanilla item
      # itemcore: ItemCore plugin custom item
      # mythicmobs: MythicMobs plugin custom item
      source: "vanilla"
      # Item ID
      # Vanilla items use item names (e.g., IRON_SWORD)
      # ItemCore items use item ID
      id: "IRON_SWORD"
      # Output amount
      amount: 1
      # Check durability (only for repairable items)
      check-durability: false
    # Required materials (list format)
    materials:
      - source: "vanilla"
        id: "IRON_INGOT"
        amount: 2
        check-durability: false
      - source: "vanilla"
        id: "STICK"
        amount: 1
        check-durability: false
    # Crafting conditions (optional)
    # Multiple conditions can be defined, all must be satisfied to craft
    conditions:
      # Level condition
      - type: "level"
        value: 10              # Player must be level 10
      # Permission condition (optional)
      # - type: "permission"
      #   value: "myplugin.forge.use"
      # Economy condition (optional, requires Vault plugin)
      # - type: "money"
      #   value: 500            # Requires 500 coins
    # Crafting time (seconds)
    # Set to 0 or negative for instant completion (not added to queue)
    craft-time: 5.0
    # Success rate (percentage), 100 means guaranteed success, 50 means 50% success rate
    # When success rate is less than 100, crafting may fail
    success-rate: 100 
    # Consume materials on failure (only applies when success-rate < 100)
    # true: Materials are consumed on failure
    # false: Materials are returned to player on failure
    consume-on-fail: true
    # Triggers (optional)
    # Actions executed at specific times
    triggers:
      # Triggered when crafting starts
      on-start:
        messages:
          - "&6Starting to forge Iron Sword..."
        sounds:
          - "BLOCK_ANVIL_USE 1 1"   # Format: sound_name volume pitch
        commands:
          - "say {player} is forging an Iron Sword"   # Executed by console
        # player-commands:            # Executed by player
        #   - "heal"
      # Triggered when claiming item  
      on-claim:
        messages:
          - "&aSuccessfully forged Iron Sword!"
        sounds:
          - "ENTITY_PLAYER_LEVELUP 1 1"
      # Triggered when crafting is cancelled
      on-cancel:
        messages:
          - "&cIron Sword crafting cancelled"
        sounds:
          - "ENTITY_VILLAGER_NO 1 1"
```

### layout.yml

Define GUI layouts.

```yaml
# Layout settings
settings:
  # GUI rows (1-6)
  rows: 6

# Layout definition
# Use characters to represent different functional positions
# Each row must be 9 characters (GUI width is fixed at 9 slots)
layout:
  - 'AAAAAAAAA'  
  - 'AAA000AAA'  
  - 'AZA000AEA'  
  - 'AAA000AAA'  
  - 'AAAAAAAAA'  
  - '←AXXXXXAA'  

# Slot definitions
# Each character represents a functional position
slots:
  # A - Border position
  A:
    material: BLACK_STAINED_GLASS_PANE  # Border item
    display-name: ""                      # Display name (empty means no display)
    function: border                      # Function type: border

  # 0 - Material slot position
  0:
    material: AIR                         # Display as air (actually shows player items)
    function: material_slot               # Function type: material_slot
    show-item: true                       # Show item

  # E - Output slot position
  E:
    material: AIR                         # Display as air (actually shows output item)
    function: output_slot                 # Function type: output_slot

  # X - Queue display position
  X:
    material: GREEN_STAINED_GLASS_PANE    # Queue background item
    display-name: "&aCrafting Queue"      # Display name
    function: queue_display               # Function type: queue_display

  # Z - Confirm button position
  Z:
    material: LIME_WOOL                  # Button item
    display-name: "&aStart Crafting"      # Display name
    function: confirm_button              # Function type: confirm_button


  ←:
    material: ARROW
    display-name: "&aBack"            
    function: back_button   
    lore:                                # Description
      - "&7Back to recipe list"        

# ================================================
# Function Type Explanation
# ================================================
# border:           Border decoration, no interaction
# material_slot:    Material display slot, shows required materials
# output_slot:      Output display slot, shows crafting result
# queue_display:    Queue display area, shows crafting and waiting items
# confirm_button:   Confirm button, click to start crafting
# back_button:      Return to recipe list
# ================================================
```

### config.yml

Main plugin configuration.

```yaml

settings:
  # Directory where forge configuration files are located (relative to plugin directory)
  forges-directory: "forges"
  # Directory where GUI layout files are located (relative to plugin directory)
  layouts-directory: "layouts"
  # Debug mode
  debug: true
# GUI settings
gui:
  # Number of recipes displayed per page in recipe selection interface
  recipes-per-page: 28
  # Interface border item
  border-item: GRAY_STAINED_GLASS_PANE
  # Previous page button item
  previous-item: ARROW
  # Next page button item
  next-item: ARROW
  # Back button item
  back-item: BARRIER
  # Craft button item (when materials are sufficient)
  craft-item: GREEN_WOOL
  # Craft button display name
  craft-item-name: "&aStart Crafting"
  # Craft button description
  craft-item-lore:
    - "&7Click to start crafting"
    - "&7Crafting time: {craft_time}s"
  # Craft button item (when materials are insufficient)
  craft-disabled-item: RED_WOOL
  # Button display name when materials are insufficient
  craft-disabled-name: "&cInsufficient Materials"
  # Button description when materials are insufficient
  craft-disabled-lore:
    - "&7Insufficient materials, cannot craft"
    - "&7Please collect materials first"
  # Click cooldown (milliseconds), prevents rapid clicking
  craft-click-cooldown: 150
```

<br />