# ArmsorPlus

> **Minecraft 武器装备强化插件 — A Minecraft Weapon & Armor Enhancement Plugin**
>
> Paper 1.21+ (向下兼容至 1.13) | Backward compatible to 1.13
>
> 作者 Author: **Dim_LJR** · 版本 Version: **0.3-H+**

---

## 概述 | Overview

ArmsorPlus 为 Minecraft Paper/Purpur 服务器带来 RPG 化的装备、附魔、BOSS 战斗与饮食系统。本版本为原版本的重制版，解决了原有的大量遗留 Bug。

ArmsorPlus brings RPG-style gear, enchants, boss fights, and food systems to Paper/Purpur servers. This is a full rewrite of the original, fixing many legacy bugs.

- **20 种自定义附魔** — 基于 PDC 独立存储，不与原版附魔冲突 | 20 custom enchants (PDC-based, no vanilla conflict)
- **强化石系统** — 拖动强化石到装备上提升属性 | Enhancement stone system (drag onto gear)
- **魔法球抽奖** — 经验值兑换，右键随机抽取附魔书 | Magic ball lottery (XP → random enchants)
- **10 件魔法武器** — 每件拥有独特的右键技能 | 10 magic weapons with unique right-click abilities
- **食物与药品** — 回春散、止血绷带、压缩饼干等 | Custom food & medicine
- **5 个 BOSS** — 含元素反应机制 | 5 bosses with elemental reaction mechanics
- **公海世界** — 自定义地图 + 宝藏抽奖 | OpenSea world — custom map & treasure lottery
- **GUI 菜单** — 向导书一键打开 | GUI menu via guide book
- **玩家设置** — 附魔通知开关、管理员模式 | Player settings (enchant notifications, admin mode)

---

## 环境要求 | Requirements

| 项目 Item | 要求 Requirement |
|-----------|------------------|
| 服务端 Server | Paper / Purpur 1.21+ |
| Java | 21+ |
| 前置插件 Dependencies | 无 None |

## 快速开始 | Quick Start

1. 下载 `ArmsorPlus.jar` 放入 `plugins/` 目录 | Drop `ArmsorPlus.jar` into `plugins/`
2. 重启服务端 | Restart the server
3. 用圆石合成**高级附魔向导书**，右键打开主菜单 | Craft a **Guide Book** with 1 cobblestone, right-click to open the menu
4. OP 玩家可使用 `/ArmsorPlus help` 查看管理命令 | OP players use `/ArmsorPlus help`

---

## 自定义附魔 | Custom Enchantments

所有附魔通过 PDC 存储，将附魔书**拖动到装备上**即可应用，不占用原版附魔槽位。\
All enchants are PDC-based — drag the book onto your gear to apply, no vanilla enchant slot used.

### 装备附魔 | Armor Enchants

| 附魔 Enchant | 槽位 Slot | 效果 Effect |
|-------------|-----------|-------------|
| 闪避 Dodge | 靴子 Boots | 概率闪避攻击（PvP 15%/级，PvE 20%/级）Chance to dodge attacks |
| 影避 Shadow Dodge | 靴子 Boots | 概率闪避**所有类型**伤害（6%/级）Chance to dodge **all** damage types |
| 涟漪 Ripples | 靴子 Boots | 受击回复生命值（每级 +1 HP）Heal on hit |
| 格挡 Blocking | 头盔 Helmet | 按伤害比例减免 Damage reduction by threshold |
| 生命提升 Health Boost | 胸甲 Chestplate | 每级 +5 最大生命值 +5 max HP per level |
| 涤魂 Effect Clear | 胸甲 Chestplate | 周期性免疫负面魔法伤害 Periodic magic immunity |
| 复仇 Revenge | 胸甲 Chestplate | 20%/级概率反弹 50% 伤害 Chance to reflect 50% damage |
| 幸存 Survivor | 护腿 Leggings | 致命伤时 10%/级概率复活 Chance to revive on fatal hit |
| 不灭 Indestructible | 任意 Any | **仅指令获取**，受到致命伤时免疫死亡并回满状态 Command-only, prevents death |

### 武器附魔 | Weapon Enchants

| 附魔 Enchant | 效果 Effect |
|-------------|-------------|
| 血祭 Blood Sacrifice | 20%/级概率消耗 15 HP 造成 2~(等级+1) 倍伤害 Consume 15 HP for 2x~(level+1)x damage |
| 双重打击 Double Hit | 20%/级概率双倍伤害 Chance for double damage |
| 吸血 Feeding | 攻击吸取 1.5×等级 HP Life steal 1.5× per level |
| 凋零 Withering | 攻击附加凋零效果 Applies wither (5s per level, max III) |
| 寒冻 Freeze | 攻击附加缓慢效果 Applies slowness |
| 饥荒 Famine | 攻击附加饥饿效果 Applies hunger |
| 疾刺 Quick Thrust | 三叉戟/长矛右键获得速度提升 Right-click for speed boost (+10%/level) |
| 失明 Blindness | 攻击施加失明效果 Applies blindness (2s per level) |

### 工具附魔 | Tool Enchants

| 附魔 Enchant | 效果 Effect |
|-------------|-------------|
| 金刚钻 Diamond Drill | 镐子挖黑曜石时瞬间挖掉 Instant obsidian mining (20%/level, 100% at max) |

### 远程附魔 | Ranged Enchants

| 附魔 Enchant | 效果 Effect |
|-------------|-------------|
| 蓄爆 Explosive Arrow | 箭矢替换为烟花火箭，命中后范围爆炸（30×等级伤害）Replaces arrow with fireworks, AoE explosion |
| 弹道 Arrow Speed | 箭速每级翻倍 Arrow speed doubles per level |
| 狙击 Sniping | 箭速每级 ×5（**需要弹道作为前置**）5x arrow speed per level (**requires Arrow Speed**) |

> 蓄爆与弹道/狙击互斥，不可共存。Explosive Arrow is mutually exclusive with Arrow Speed / Sniping.

---

## 强化石 | Enhancement Stones

| 物品 Item | 合成/来源 Recipe/Source | 效果 Effect |
|-----------|----------------------|-------------|
| 基础强化石 Basic Stone | 4×钻石块 4 Diamond Blocks | 右键随机获得一种强化石 Right-click for a random stone |
| 武器强化石 I Weapon Stone I | 魔法球抽取 Magic Ball | 锋利 +1 Sharpness +1 |
| 武器强化石 II Weapon Stone II | 魔法球抽取 Magic Ball | 攻击伤害 +1 Attack Damage +1 |
| 护甲强化石 I Armor Stone I | 魔法球抽取 Magic Ball | 保护 +1 Protection +1 |
| 护甲强化石 II Armor Stone II | 魔法球抽取 Magic Ball | 护甲值/韧性/击退抗性各 +1 Armor/Toughness/KB Resist +1 |
| 弓箭强化石 Bow Stone | 魔法球抽取 Magic Ball | 力量 +1 Power +1 |
| 精炼金刚石 Refined Diamond | 9×钻石块 9 Diamond Blocks | 无限耐久 Infinite Durability |

---

## 魔法武器 | Magic Weapons

| 武器 Weapon | 合成 Recipe | 特性 Trait |
|------------|-------------|-----------|
| 血祭之剑 Blood Sword | 红石围骷髅头 Redstone around Skull | 自带血祭 V Blood Sacrifice V |
| 重剑 Iron Epee | 铁块 + 木棍 Iron Block + Stick | 基础攻击 +5 Base Attack +5 |
| 匕首 Dagger | 绿宝石 + 木棍 Emerald + Stick | 额外 5 点真实伤害 +5 True Damage |
| 飞斧 Throwing Axe | 下界合金锭 + 绿宝石块 + 木棍 Netherite+Emerald Block+Stick | 右键蓄力 3 秒掷出，20 点伤害 Charged throw, 20 dmg |
| 骷髅权杖 Skeleton Scepter | 8 骷髅头围不死图腾 Skulls around Totem | 右键释放箭雨，10 秒冷却 Arrow rain, 10s cooldown |
| 寒冰弓 Frost Bow | 蓝冰 + 木棍 Blue Ice + Stick | 射出 3 支箭，缓慢+冻结 3 arrows, slowness+freeze |
| 火焰戟 Flame Halberd | 下界合金锭 + 火焰弹 + 烈焰棒 Netherite+Fire Charge+Blaze Rod | 近战 +30 火焰伤害，投掷灼烧 +30 fire dmg, throwing |
| 雨御前 Rain Sword | 海晶碎片围下界合金剑 Prismarine around Netherite Sword | 右键 3 秒隐身+无敌；Shift+右键瞬移 3s stealth+invuln, shift-teleport |
| 飞天御剑 Flying Sword | 幻翼膜围下界合金剑 Phantom Membrane around Netherite Sword | 右键悬空飞行 (8m/s) Flight at 8m/s |
| 瞬步刃 Flash Step Blade | 末影珍珠围下界合金剑 Ender Pearl around Netherite Sword | 右键瞬移至目标身后造成 15 伤害 Teleport behind target, 15 dmg |

---

## 魔法球 | Magic Balls

使用经验值在菜单商店中兑换，右键随机抽取附魔书。\
Exchange XP in the menu shop, right-click for random enchanted books.

| 等级 Tier | 需求经验 XP Cost | 保底层数 Guaranteed Layers |
|-----------|-----------------|---------------------------|
| 寻常 Common | 30 级 Levels | 1 层 Layer |
| 稀罕 Rare | 50 级 Levels | 2 层 Layers |
| 史诗 Epic | 70 级 Levels | 3 层 Layers |
| 传奇 Legendary | 150 级 Levels | 4 层 Layers |

每层独立抽选，10% 概率获得对应附魔书（随机等级）。\
Each layer rolls independently, 10% chance for an enchanted book (random level).

---

## 食物与药品 | Food & Medicine

| 物品 Item | 合成 Recipe | 效果 Effect |
|-----------|------------|-------------|
| 回春散 Rejuvenation Powder | 8 闪烁西瓜片 + 玻璃瓶 8 Glistering Melon + Glass Bottle | 生命恢复 V 3 秒 Regeneration V 3s |
| · 上品 Fine (10%) | 合成时 10% 概率 10% chance | 生命恢复 V 6 秒 Regeneration V 6s |
| · 极品 Excellent (1%) | 合成时 1% 概率 1% chance | 生命恢复 X 40 秒 + 清除负面 Regeneration X 40s + clear effects |
| · 仙品 Transcendent (0.1%) | 合成时 0.1% 概率 0.1% chance | 生命恢复 X 120 秒 + 清除负面 Regeneration X 120s + clear effects |
| 止血绷带 Bandage | 地狱疣 + 白色羊毛 Nether Wart + White Wool | 瞬间恢复 6 HP Instant Heal 6 HP |
| 压缩饼干 Compressed Biscuit | 9×面包 9 Bread | 瞬间恢复 20 饱食度 Restore 20 Saturation |
| 盐 Salt | 沙子 Sand | 调味料 Seasoning |
| 肉干 Jerky | 盐 + 熟肉 Salt + Cooked Meat | 恢复 6 饥饿值，7.2 饱和度 Restore 6 Hunger, 7.2 Saturation |
| 甜浆果派 Sweet Berry Pie | 3 甜浆果 + 3 小麦 3 Berries + 3 Wheat | 恢复 9 饥饿值，8 饱和度 Restore 9 Hunger, 8 Saturation |
| 酒桶 Wine Barrel | 3 小麦 + 木桶 3 Wheat + Barrel | 放置后右键打开，随机获得酒 Place, right-click for random wine |
| · 酒 Wine (90%) | — | 力量 II 30 秒 Strength II 30s |
| · 佳酿 Fine Wine (9.9%) | — | 力量 III 45 秒 Strength III 45s |
| · 金樽清酒 Golden Wine (0.1%) | — | 力量 V 140 秒，死亡复活 Strength V 140s, revive on death |
| 腐肉干 Rotten Jerky | 腐肉 + 盐 Rotten Flesh + Salt | 恢复 4 饥饿值，3 饱和度 Restore 4 Hunger, 3 Saturation |

---

## BOSS 系统 | Boss System

在主菜单 → BOSS 清单中召唤，**需先进入 BOSS 世界**。\
Summon via Menu → Boss List. **Must enter the Boss world first.**

| BOSS | 生命值 HP | 特性 Traits |
|------|----------|------------|
| 急冻树 Cryo Regisvine | 750 | 冰元素，树形结构，火/雷触发双倍伤害 Cryo, tree structure, Fire/Electro = 2x dmg |
| 爆炎树 Pyro Regisvine | 750 | 火元素，树形结构，雷/冰触发双倍伤害 Pyro, tree structure, Electro/Cryo = 2x dmg |
| 史莱姆王 Slime King | 750 | 巨型史莱姆，跳跃粉碎攻击，半血分裂激怒 Giant slime, jump smash, enrage at 50% HP |
| 僵尸巨人 Zombie Giant | 1500 | 攻击力 80，附带缓慢与击退 80 ATK, applies slowness & knockback |
| 小僵尸 Double Baby Zombie | 600×2 | 两只保护 IV 下界合金套小僵尸 Two Protection IV netherite baby zombies |

- 攻击**躯干部位**：3 倍武器伤害 | Body hit: 3x weapon damage
- 攻击**核心部位**：15 倍武器伤害 | Core hit: 15x weapon damage
- 元素反应命中时 50% 概率暴露核心 | Elemental reaction: 50% chance to expose core

---

## 公海世界 | OpenSea World

独立世界地图，含宝藏抽奖与守护者挑战。\
A standalone world with treasure lottery and guardian challenges.

**配置 `config.yml`:**

```yaml
SpawnOpenSea: false          # 是否启用公海世界 Enable OpenSea world
AutoResetOpenSeaMap: false   # 每次重启自动重置地图 Auto-reset on restart
OpenSeaName: "OpenSea"       # 世界文件夹名 World folder name
MapZipName: "OpenSea.zip"    # 地图压缩包名 Map zip file name
```

---

## 指令参考 | Commands

需要 `ArmsorPlus.op` 权限（默认 OP）。Requires `ArmsorPlus.op` permission (OP by default).

| 指令 Command | 说明 Description |
|-------------|-----------------|
| `/ArmsorPlus help` | 查看帮助 Help |
| `/ArmsorPlus info` | 插件信息 Plugin info |
| `/ArmsorPlus guide` | 获得高级附魔向导书 Get Guide Book |
| `/ArmsorPlus give <item> [amount] [level]` | 给予物品 Give item |
| `/ArmsorPlus getEnchantmentLevel <enchant>` | 查看手持物品附魔等级 Check enchant level |
| `/ArmsorPlus removeEnchant <enchant\|all>` | 移除手持物品附魔 Remove enchant |
| `/ArmsorPlus reloadmap` | 重载公海地图 Reload OpenSea map |

### 可用物品名 | Available Item IDs

**强化石 / 物品 Stones / Items:** `Arms_I` `Arms_II` `Armor_I` `Armor_II` `Bow_I` `DiamondPlus` `BasicStone` `GuideBook`

**魔法球 Magic Balls:** `MagicBal_I` `MagicBal_II` `MagicBal_III` `MagicBal_IV`

**武器 Weapons:** `Blood_Sword` `Iron_Epee` `Dagger` `ThrowingAxe` `SkeletonScepter` `FrostBow` `FlameHalberd` `RainSword` `FlyingSword` `FlashStepBlade`

**附魔书 Enchanted Books:** `Dodge` `Famine` `Ripples` `BloodSacrifice` `EffectClear` `Freeze` `ShadowDodge` `Blocking` `Withering` `Survivor` `HealthBoost` `Revenge` `ExplosiveArrow` `Sniping` `ArrowSpeed` `DoubleHit` `Feeding` `QuickThrust` `DiamondDrill` `Blindness` `Indestructible` + 后缀 suffix `_EnchantedBook`

**食物/药品 Food / Medicine:** `RejuvenationPowder` `HemostaticBandage` `CompressedBiscuit` `Salt` `Jerky` `SweetBerryPie` `WineBarrel` `Wine` `RottenJerky`

---

## 合成配方 | Crafting Recipes

| 产物 Result | 配方 Recipe |
|------------|------------|
| 高级附魔向导书 Guide Book | 1×圆石 Cobblestone |
| 基础强化石 Basic Stone | 4×钻石块 Diamond Block |
| 精炼金刚石 Refined Diamond | 9×钻石块 Diamond Block |
| 血祭之剑 Blood Sword | 5 红石 Redstone 围 around 骷髅头 Skull |
| 重剑 Iron Epee | 2 铁块 Iron Block + 木棍 Stick (竖 vertical) |
| 匕首 Dagger | 绿宝石 Emerald + 木棍 Stick (竖 vertical) |
| 飞斧 Throwing Axe | 下界合金锭 Netherite Ingot + 绿宝石块 Emerald Block + 木棍 Stick |
| 骷髅权杖 Skeleton Scepter | 8 骷髅头 Skulls 围 around 不死图腾 Totem |
| 寒冰弓 Frost Bow | 蓝冰 Blue Ice + 木棍 Stick |
| 火焰戟 Flame Halberd | 下界合金锭 Netherite + 火焰弹 Fire Charge + 烈焰棒 Blaze Rod |
| 雨御前 Rain Sword | 海晶碎片 Prismarine Shard 围 around 下界合金剑 Netherite Sword |
| 飞天御剑 Flying Sword | 幻翼膜 Phantom Membrane 围 around 下界合金剑 Netherite Sword |
| 瞬步刃 Flash Step Blade | 末影珍珠 Ender Pearl 围 around 下界合金剑 Netherite Sword |
| 回春散 Rejuvenation Powder | 8 闪烁西瓜片 Glistering Melon + 玻璃瓶 Glass Bottle |
| 止血绷带 Bandage | 2 地狱疣 Nether Wart + 白色羊毛 White Wool |
| 压缩饼干 Compressed Biscuit | 9×面包 Bread (3×3) |
| 盐 Salt | 1×沙子 Sand |
| 肉干 Jerky | 盐 Salt + 熟肉 Cooked Meat |
| 甜浆果派 Sweet Berry Pie | 3 甜浆果 Sweet Berries + 3 小麦 Wheat |
| 酒桶 Wine Barrel | 3 小麦 Wheat + 木桶 Barrel |
| 腐肉干 Rotten Jerky | 腐肉 Rotten Flesh + 盐 Salt |

> 完整配方可在菜单中点击物品查看 3×3 合成演示。\
> Click any item in the menu to see its full 3×3 crafting demo.

---

## 构建 | Build

```bash
# 环境要求 Requires: JDK 21+
./gradlew build
```

构建产物位于 `build/libs/ArmsorPlus-1.0.jar`。Output: `build/libs/ArmsorPlus-1.0.jar`

### 开发 | Development

```bash
# 启动测试服务端 (Purpur 1.21.11) Start test server
./gradlew runPurpur

# 仅更新插件 jar（热重载） Update plugin jar only
./gradlew reload
```

---

## 许可 | License

源码开放，**不可用于商业用途**。\
Open source. **Not for commercial use.**

[GitHub: LJRDE](https://github.com/LJRDE)
