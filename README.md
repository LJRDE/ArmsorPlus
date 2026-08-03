# ArmsorPlus

> **Minecraft 武器装备强化插件 — A Minecraft Weapon & Armor Enhancement Plugin**
>
> Paper / Purpur 1.21+（向下兼容至 1.13）| Backward compatible to 1.13
>
> 作者 Author: **Dim_LJR** · 版本 Version: **0.3-J**

---

## 概述 | Overview

ArmsorPlus 是一体化的 RPG 强化插件：**自定义附魔、强化石、魔法武器、BOSS 战斗、食物与药品**全部内置，零前置依赖，开箱即用。本版本为原版本的重制版，解决了原版本遗留的大量 Bug。

ArmsorPlus is an all-in-one RPG enhancement plugin: custom enchants, enhancement stones, magic weapons, boss fights, food & medicine — all built-in with zero dependencies.

- **48 种自定义附魔** — 基于 PDC 独立存储，不与原版附魔冲突 | 48 custom enchants (PDC-based, no vanilla conflict)
- **强化石系统** — 拖动强化石到装备上强化属性/附魔 | Enhancement stone system (drag onto gear)
- **41 件武器/装备** — 魔法武器 + 鱼骨系列 + 钢装备 | 41 weapons & gear (magic weapons + fish-bone series + steel set)
- **9 个 BOSS** — 元素反应、躯干/核心弱点机制 | 9 bosses with elemental reactions & body/core weak points
- **70+ 食物/药品** — 水果、树苗、料理、药剂 | 70+ food & medicine (fruits, saplings, meals, potions)
- **魔法球抽奖** — 经验值兑换，右键随机抽取附魔书 | Magic ball lottery (XP → random enchants)
- **公海世界** — 自定义地图 + 宝藏抽奖 + 守护者 | OpenSea world — custom map & treasure lottery
- **GUI 菜单** — 向导书一键打开 | GUI menu via guide book
- **玩家设置** — 附魔通知开关、管理员模式 | Player settings (enchant notifications, admin mode)
- **资源包** — 内置自定义物品贴图 | Bundled resource pack for custom textures

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
3. 用 **1 个圆石**合成**高级附魔向导书**，右键打开主菜单 | Craft a **Guide Book** with 1 cobblestone, right-click to open the menu
4. OP 玩家可使用 `/ArmsorPlus help` 查看管理命令 | OP players use `/ArmsorPlus help`

---

## 自定义附魔 | Custom Enchantments

所有附魔通过 **PDC（PersistentDataContainer）** 存储，将附魔书**拖动到装备上**即可应用，不占用原版附魔槽位。
All enchants are PDC-based — drag the book onto your gear to apply, no vanilla enchant slot used.

### 武器附魔 | Weapon Enchants

| 附魔 Enchant | 效果 Effect |
|-------------|-------------|
| 血祭 Blood Sacrifice | 概率消耗自身生命造成多倍伤害 Consume HP for multiplied damage |
| 双重打击 Double Hit | 20%/级概率双倍伤害 Chance for double damage |
| 吸血 Feeding | 攻击吸取生命值 Life steal on hit |
| 饥荒 Famine | 攻击附加饥饿效果 Applies hunger |
| 凋零 Withering | 攻击附加凋零效果 Applies wither |
| 寒冻 Freeze | 攻击附加缓慢效果 Applies slowness |
| 失明 Blindness | 攻击附加失明效果 Applies blindness |
| 剧毒 Poison | 攻击附加中毒效果 Applies poison |
| 眩晕 Stun | 剑；攻击造成反胃，0.7s/级，满级 V Applies nausea |
| 暴击 Critical Strike | 斧；15%/级概率造成 25%/级额外伤害，满级 V Bonus crit damage |
| 利刃 Sharp Blade | 目标护甲越低伤害越高（护甲>18 仅提升 10%）Scales with low armor |
| 火刃 / 霜刃 / 雷刃 / 魔刃 | 攻击转化为火焰/冰冻/雷电/魔法伤害，带元素印记系统（满级 III）Elemental damage conversion + mark system |
| 冰刺 Ice Spike | 额外冰冻伤害，满级 III Bonus ice damage |
| 烈焰 Inferno | 额外火焰伤害，满级 III Bonus fire damage |

### 装备附魔 | Armor Enchants

| 附魔 Enchant | 槽位 Slot | 效果 Effect |
|-------------|-----------|-------------|
| 闪避 Dodge | 靴子 Boots | 8%/级概率闪避攻击，满级 V Dodge attacks |
| 影避 Shadow Dodge | 靴子 Boots | 8%/级概率闪避**所有类型**伤害，满级 V Dodge all damage types |
| 涟漪 Ripples | 靴子 Boots | 受击减缓血量下降（20%/级，满级 III）Reduce health loss |
| 熔岩行者 Lava Walker | 靴子 Boots | 岩浆上行走时 5 格内岩浆变岩浆块 Walk on lava |
| 格挡 Blocking | 头盔 Helmet | 固定 10%/级伤害减免，满级 V Flat damage reduction |
| 生命提升 Health Boost | 胸甲 Chestplate | 每级 +5 最大生命值 +5 max HP per level |
| 涤魂 Effect Clear | 胸甲 Chestplate | 周期性解除负面效果 Periodically clear negative effects |
| 复仇 Revenge | 胸甲 Chestplate | 20%/级概率反弹 50% 伤害 Chance to reflect 50% damage |
| 保护PRO Protection PRO | 胸甲 Chestplate | 每级额外减少 6% 伤害，满级 V -6% damage per level |
| 傀儡守护者 Golem Guardian | 胸甲 Chestplate | 受伤时召唤铁傀儡反击，冷却 300s，满级 V Summon golems |
| 卸力 Damage Dispersal | 胸甲 Chestplate | 将单次高额伤害分解为多次，满级 V Split heavy hits |
| 百草 Herb Guard | 胸甲 Chestplate | 减少 20%/级魔法伤害，满级 IV -20% magic damage per level |
| 重甲 Heavy Armor | 胸甲 Chestplate | 穿戴时给予缓慢 II + 抗性提升 II Slowness II + Resistance II |
| 幸存 Survivor | 护腿 Leggings | 致命伤 10%/级概率复活 Chance to revive on fatal hit |

### 工具附魔 | Tool Enchants

| 附魔 Enchant | 工具 Tool | 效果 Effect |
|-------------|----------|-------------|
| 金刚钻 Diamond Drill | 镐 Pickaxe | 挖黑曜石 20%/级概率瞬间挖掉，满级 V Instant obsidian mining |
| 丰收 Harvest | 锄 Hoe | 30%/级概率多倍收获，满级 III Bonus harvests |
| 自动种植 Auto Plant | 锄 Hoe | 采集作物时自动补种副手种子 Auto-replant |
| 地之眷顾 Earth Favor | 铲 Shovel | 挖泥土概率掉落矿粒 Dig mineral nuggets |
| 疾刺 Quick Thrust | 三叉戟/长矛 | 右键获得 10%/级速度提升 Speed boost on right-click |
| 唤雷 Lightning Call | 三叉戟 Trident | 无视天气召唤 level 道雷，满级 III Summon lightning |
| 强风暴 Strong Burst | 重锤 Mace | 无需下落即可触发风暴 Trigger mace smash instantly |

### 远程附魔 | Ranged Enchants

| 附魔 Enchant | 武器 Weapon | 效果 Effect |
|-------------|------------|-------------|
| 蓄爆 Explosive Arrow | 弓/弩 Bow/Xbow | 箭矢替换为烟花火箭，命中范围爆炸 AoE explosion |
| 弹道 Arrow Speed | 弓/弩 Bow/Xbow | 箭速每级翻倍 Arrow speed doubles per level |
| 狙击 Sniping | 弓/弩 Bow/Xbow | 箭速每级 ×5（**需弹道前置**）5x arrow speed (**requires Arrow Speed**) |
| 穿甲 Piercing | 弓/弩 Bow/Xbow | 概率使盾牌进入冷却，命中附加 5 点穿透伤害 Pierce shields |
| 千重射击 Multi-Shot | 弩 Crossbow | 额外射出 level 支箭，满级 III Extra arrows |
| 追踪 Tracking | 弓 Bow | 箭矢追踪 450 格内指向的目标 Homing arrows |
| 惊雷 Thunderclap | 弓 Bow | 命中/未命中召唤 level 道雷，满级 III Summon lightning |

### 盾牌与特殊附魔 | Shield & Special Enchants

| 附魔 Enchant | 槽位 Slot | 效果 Effect |
|-------------|-----------|-------------|
| 全息 Holographic | 盾牌 Shield | 持盾防御扩展到全角度 Full-angle blocking |
| 伏击 Ambush | 盾牌 Shield | 收起盾牌后短时间增伤，满级 III Bonus damage after sheathing |
| 不灭 Indestructible | 任意 Any | 受到致命伤免疫死亡并回满状态（**仅管理员指令获取**）Prevent death (**command-only**) |

> 蓄爆 与 弹道/狙击 互斥，不可共存。Explosive Arrow is mutually exclusive with Arrow Speed / Sniping.

---

## 强化石 | Enhancement Stones

将强化石**拖动到装备上**即可使用。所有强化石均不可放置。

| 物品 Item | 合成/来源 Recipe/Source | 效果 Effect |
|-----------|----------------------|-------------|
| 基础强化石 Basic Stone | 4×钻石块 4 Diamond Blocks | 右键随机获得一种强化石 Random stone on right-click |
| 武器强化石 I Weapon Stone I | 魔法球抽取 Magic Ball | 锋利 +1 Sharpness +1 |
| 武器强化石 II Weapon Stone II | 魔法球抽取 Magic Ball | 攻击伤害 +1 Attack Damage +1 |
| 护甲强化石 I Armor Stone I | 魔法球抽取 Magic Ball | 保护 +1 Protection +1 |
| 护甲强化石 II Armor Stone II | 魔法球抽取 Magic Ball | 护甲/韧性/击退抗性各 +1 Armor/Toughness/KB Resist +1 |
| 弓箭强化石 Bow Stone | 魔法球抽取 Magic Ball | 力量 +1 Power +1 |
| 精炼金刚石 Refined Diamond | 9×钻石块 9 Diamond Blocks | 无限耐久 Infinite Durability |

---

## 武器与装备 | Weapons & Gear

### 魔法武器 | Magic Weapons

每件拥有独特的右键技能，多数有专属合成配方。

| 武器 Weapon | 特性 Trait |
|------------|-----------|
| 血祭之剑 Blood Sword | 自带血祭 V Blood Sacrifice V |
| 重剑 Iron Epee | 攻击 7.5 Base damage 7.5 |
| 尸王 Corpse King | 9 伤害 + 饥荒 II + 剧毒 II |
| 星痕剑 Star Trace | 夜晚伤害 +100% +100% damage at night |
| 玄武剑 Black Tortoise | 每累积 200 伤害 +1，上限 +8 Stacking bonus up to +8 |
| 噬生剑 Devour Life | 击杀成长，积累伤害加成 Life-draining growth |
| 匕首 Dagger | 额外 5 点真实伤害 +5 true damage |
| 飞斧 Throwing Axe | 右键蓄力 3 秒掷出，20 伤害 Charged throw |
| 骷髅权杖 Skeleton Scepter | 右键释放箭雨，10 秒冷却 Arrow rain, 10s cooldown |
| 寒冰弓 Frost Bow | 3 支箭，缓慢 + 冻结 3 arrows, slowness + freeze |
| 火焰戟 Flame Halberd | 近战 +30 火焰伤害，可投掷 +30 fire dmg, throwable |
| 雨御前 Rain Sword | 右键 3s 隐身无敌；Shift+右键瞬移 Stealth + teleport |
| 飞天御剑 Flying Sword | 右键悬空飞行 8m/s Flight at 8m/s |
| 瞬步刃 Flash Step | 右键瞬移至目标身后造成 15 伤害 Teleport behind target |
| 魔法杖 Magic Stick | 左键发射魔法球（直射）Fire magic ball |
| 寒冰剑 Ice Sword | 攻击施加缓慢 II 3s Applies slowness II |
| 盘丝弓 Web Bow | 命中生成蜘蛛网，使用 9 次后损坏 Web on hit (9 uses) |
| 爆炸弓 Explosion Bow | 命中爆炸，使用 9 次后损坏 Explosion on hit (9 uses) |
| 雷光 Thunder Glow | 铁剑 +8，雷雨天伤害 +25% Bonus on thunderstorm |
| 烈阳 Blazing Sun | 铁剑 +8，白天 +4 + 火焰附加 V |
| 桃木剑 Peach Wood | 亡灵杀手 V Smite V |

### 鱼骨系列 | Fish-Bone Series（0.3-J 新增）

全系铁剑基底，AttributeModifier 调伤，事件监听器直接操作伤害，不依赖原版 BUFF。剑/刀双线，5 个主等级 + 2 个分支。

| 等级 | 武器 | 伤害 | 特性 |
|------|------|------|------|
| Lv1 | 鱼骨剑 / 鱼骨刀 | 5.0 | 鲑鱼/鳕鱼 + 骨头合成 |
| Lv2 | 鱼刺剑 / 鱼刺刀 | 6.5 | 骨块 + 海晶沙粒升级 |
| Lv3 | 海骨剑 / 海骨刀 | 7.5 | 水中增伤 / 加速 |
| Lv4 | 灵骨剑 / 灵骨刀 | 8.0 | 光灵 + 水中概率穿透/双倍 |
| Lv5 | 海刺剑 / 海刺刀 | 10.0 / 12.0 | 水中/雨天增幅 + 水下呼吸 |
| 分支 | 蚀骨剑 | 13.0 | 自带凋零 III |
| Lv6 | 灵刺剑 / 灵刺刀 | 13.0 / 14.0 | 穿透 / 增伤 |
| Lv7 | 海哭剑 / 海哭刀 | 16.0 | 被动 3 药水 + 发光 + 水中倍率 |

### 钢装备 | Steel Set

| 装备 | 属性 |
|------|------|
| 钢剑 Steel Sword | 攻击 7 |
| 钢头盔 Steel Helmet | 2 护甲 / 1 韧性 |
| 钢胸甲 Steel Chestplate | 7 护甲 / 2 韧性 |
| 钢护腿 Steel Leggings | 6 护甲 / 2 韧性 |
| 钢靴子 Steel Boots | 3 护甲 / 1 韧性 |

---

## 魔法球 | Magic Balls

在主菜单商店使用**经验值**兑换，右键随机抽取附魔书。每层独立抽选，10% 概率获得对应附魔书（随机等级）。
Exchange XP in the menu shop, right-click for random enchanted books. Each layer rolls independently, 10% chance for an enchanted book.

| 等级 Tier | 需求经验 XP Cost | 保底层数 Guaranteed Layers |
|-----------|-----------------|---------------------------|
| 寻常 Common | 30 级 Levels | 1 层 Layer |
| 稀罕 Rare | 50 级 Levels | 2 层 Layers |
| 史诗 Epic | 70 级 Levels | 3 层 Layers |
| 传奇 Legendary | 150 级 Levels | 4 层 Layers |

---

## 食物与药品 | Food & Medicine

### 药品 | Medicine

| 物品 Item | 合成 Recipe | 效果 Effect |
|-----------|------------|-------------|
| 回春散 Rejuvenation Powder | 8 闪烁西瓜片 + 玻璃瓶 8 Glistering Melon + Glass Bottle | 生命恢复 V 3 秒 Regeneration V 3s |
| · 上品 Fine (10%) | 合成时 10% 概率 | 生命恢复 V 6 秒 Regeneration V 6s |
| · 极品 Excellent (1%) | 合成时 1% 概率 | 生命恢复 X 40 秒 + 清除负面 Regeneration X 40s + clear effects |
| · 仙品 Transcendent (0.1%) | 合成时 0.1% 概率 | 生命恢复 X 120 秒 + 清除负面 Regeneration X 120s + clear effects |
| 止血绷带 Bandage | 地狱疣 + 白色羊毛 Nether Wart + White Wool | 瞬间恢复 6 HP Instant Heal 6 |
| 压缩饼干 Compressed Biscuit | 9×面包 9 Bread | 瞬间恢复 20 饱食度 Restore 20 Saturation |
| 金盾丹 Gold Shield Elixir | 8 金块 + 地狱疣 | 抗性提升 X 12 秒 Resistance X 12s |

### 料理 | Meals

| 物品 Item | 合成 Recipe | 效果 Effect |
|-----------|------------|-------------|
| 盐 Salt | 沙子 Sand | 调味料 Seasoning |
| 肉干 / 猪肉干 / 羊肉干 | 盐 + 熟肉 Salt + Cooked Meat | 恢复饥饿与饱和度 Restore hunger & saturation |
| 腐肉干 Rotten Jerky | 腐肉 + 盐 Rotten Flesh + Salt | 恢复 4 饥饿，3 饱和度 Restore 4 Hunger |
| 甜浆果派 Sweet Berry Pie | 3 甜浆果 + 3 小麦 3 Berries + 3 Wheat | 恢复 9 饥饿，8 饱和度 Restore 9 Hunger |
| 酒桶 Wine Barrel | 3 小麦 + 木桶 3 Wheat + Barrel | 放置后右键打开，随机获得酒 Place, right-click for wine |
| · 酒 Wine (90%) | — | 力量 II 30 秒 Strength II 30s |
| · 佳酿 Fine Wine (9.9%) | — | 力量 III 45 秒 Strength III 45s |
| · 金樽清酒 Golden Wine (0.1%) | — | 力量 V 140 秒，持续期间死亡可复活一次 Strength V + revive |
| 汉堡 / 热狗 / 披萨 / 薄饼等 | 多种配方 | 快餐类食物 Fast food |

### 水果与树苗 | Fruits & Saplings

李子、榛子、椰子、菠萝、草莓、蓝莓、橙子、橘子、无花果、枣子、柿子、山竹、圣女果、西红柿、葡萄、石榴、栗子、猕猴桃、龙眼、荔枝、樱桃、桃子等 **22 种水果**，各有对应**树苗**；果树生长时有概率在底层树叶下生成果实（`EnablePlants` 开关）。

### 材料 | Materials

辣椒、洋葱、卷心菜、黄油、大便、冰核、火核、BOSS 掉落物（如幻术师的遗骨/遗骸）等。

---

## BOSS 系统 | Boss System

在主菜单 → BOSS 清单中召唤，**需先前往 BOSS 世界**。BOSS 采用**躯干（3×伤害）**与**核心（15×伤害）**弱点机制，元素反应命中时 50% 概率暴露核心。
Summon via Menu → Boss List. **Must enter the Boss world first.** Body hit: 3x, Core hit: 15x. Elemental reaction: 50% chance to expose core.

| BOSS | 生命 HP | 特性 Traits |
|------|---------|------------|
| 急冻树 Cryo Regisvine | 700 | 冰元素，火/雷触发双倍伤害 Cryo, Fire/Electro = 2x |
| 爆炎树 Pyro Regisvine | 700 | 火元素，雷/冰触发双倍伤害 Pyro, Electro/Cryo = 2x |
| 史莱姆王 Slime King | 750 | 跳跃粉碎攻击，半血分裂激怒 Jump smash, enrage at 50% |
| 僵尸巨人 Zombie Giant | 1500 | 攻击 80，附带缓慢与击退 80 ATK + slowness |
| 小僵尸Double Baby Zombie | 600×2 | 两只保护 IV 下界合金套小僵尸 Two netherite babies |
| 宝藏守护者 Treasure Guardian | 250 | 守护深海宝藏的神秘骷髅 Deep-sea treasure guardian |
| 骷髅王 Skeleton King | 600 | 力量 X 神弓，每 10 秒召唤箭雨 Power X bow, arrow rain |
| 虚空幽魂 Void Wraith | 800 | 来自虚空的远古凋灵骷髅 Ancient wither skeleton |
| 幻术师 Illusioner | 500 | 幻影分身；40% 血召唤 4 名护卫；20% 血降下箭雨；首次死亡复活并复制攻击者装备 Illusions, guards, revive |

---

## 公海世界 | OpenSea World

独立世界地图，含宝藏抽奖、地图保护与守护者挑战。通过 `config.yml` 开关。
A standalone world with treasure lottery, block protection and guardian challenges.

```yaml
OpenSeaName: OpenSea         # 世界文件夹名 World folder name
MapZipName: OpenSea.zip      # 地图压缩包名 Map zip file name
SpawnOpenSea: true           # 是否启用公海世界 Enable OpenSea world
AutoResetOpenSeaMap: true    # 每次重启自动重置地图 Auto-reset on restart
EnablePlants: false          # 植物/水果/树苗系统开关 Plants system toggle
ResourcePackUrl: ""          # 资源包下载地址 Resource pack download URL
```

---

## 菜单系统 | GUI Menu

向导书右键打开主菜单，包含：**魔法武器列表、兑换魔法球、高级附魔书列表、魔法物品列表、公海世界传送、BOSS 清单、BOSS 世界传送、食物、药品、原材料、护甲、树苗商店（植物系统开启时）、回到主世界、个人设置**。

---

## 指令 | Commands

需要 `ArmsorPlus.op` 权限（默认 OP）。Requires `ArmsorPlus.op` permission (OP by default).

| 指令 Command | 说明 Description |
|-------------|-----------------|
| `/ArmsorPlus help` | 查看帮助 Help |
| `/ArmsorPlus info` | 插件信息 Plugin info |
| `/ArmsorPlus guide` | 获得高级附魔向导书 Get Guide Book |
| `/ArmsorPlus give <item> [amount] [level]` | 给予物品/附魔书 Give item |
| `/ArmsorPlus spawn <boss>` | 召唤 BOSS Spawn boss |
| `/ArmsorPlus getEnchantmentLevel <enchant>` | 查看手持物品附魔等级 Check enchant level |
| `/ArmsorPlus removeEnchant <enchant\|all>` | 移除手持物品附魔 Remove enchant |
| `/ArmsorPlus reloadmap` | 重载公海地图 Reload OpenSea map |

### 可用物品名 | Available Item IDs

**强化石/物品 Stones & Items:** `Arms_I` `Arms_II` `Armor_I` `Armor_II` `Bow_I` `DiamondPlus` `BasicStone` `GuideBook`

**魔法球 Magic Balls:** `MagicBal_I` `MagicBal_II` `MagicBal_III` `MagicBal_IV`

**武器 Weapons:** `Blood_Sword` `Iron_Epee` `StarTraceSword` `BlackTortoiseSword` `DevourLifeSword` `Dagger` `ThrowingAxe` `SkeletonScepter` `FrostBow` `FlameHalberd` `RainSword` `FlyingSword` `FlashStepBlade` `MagicStick` `IceSword` `WebBow` `ExplosionBow` `ThunderGlow` `BlazingSun` `PeachWoodSword` `SteelSword` + 鱼骨系列 `FishBoneSword` `FishBoneKnife` `FishSpineSword` `FishSpineKnife` `SeaBoneSword` `SeaBoneKnife` `SpiritBoneSword` `SpiritBoneKnife` `SeaSpineSword` `SeaSpineKnife` `CorrodeBoneSword` `SpiritSpineSword` `SpiritSpineKnife` `SeaCrySword` `SeaCryKnife`

**附魔书 Enchanted Books:** 全部附魔 + 后缀 `_EnchantedBook`，如 `HealthBoost_EnchantedBook`、`FireBlade_EnchantedBook` 等

**食物/药品 Food & Medicine:** `RejuvenationPowder` `HemostaticBandage` `CompressedBiscuit` `Salt` `Jerky` `SweetBerryPie` `WineBarrel` `Wine` `RottenJerky` 等

---

## 合成配方 | Crafting Recipes

| 产物 Result | 配方 Recipe |
|------------|------------|
| 高级附魔向导书 Guide Book | 1×圆石 Cobblestone |
| 基础强化石 Basic Stone | 4×钻石块 Diamond Block |
| 精炼金刚石 Refined Diamond | 9×钻石块 Diamond Block |
| 血祭之剑 Blood Sword | 红石 Redstone 围 骷髅头 Skull |
| 重剑 Iron Epee | 铁块 Iron Block + 木棍 Stick |
| 匕首 Dagger | 绿宝石 Emerald + 木棍 Stick |
| 飞斧 Throwing Axe | 下界合金锭 Netherite Ingot + 绿宝石块 Emerald Block + 木棍 Stick |
| 骷髅权杖 Skeleton Scepter | 8 骷髅头 Skulls 围 不死图腾 Totem |
| 寒冰弓 Frost Bow | 蓝冰 Blue Ice + 木棍 Stick |
| 火焰戟 Flame Halberd | 下界合金锭 Netherite + 火焰弹 Fire Charge + 烈焰棒 Blaze Rod |
| 雨御前 Rain Sword | 海晶碎片 Prismarine Shard 围 下界合金剑 Netherite Sword |
| 飞天御剑 Flying Sword | 幻翼膜 Phantom Membrane 围 下界合金剑 Netherite Sword |
| 瞬步刃 Flash Step Blade | 末影珍珠 Ender Pearl 围 下界合金剑 Netherite Sword |
| 寒冰剑 Ice Sword | 8 蓝冰 + 钻石剑 |
| 盘丝弓 Web Bow | 8 蜘蛛网 + 弓 |
| 爆炸弓 Explosion Bow | 8 TNT + 弓 |
| 回春散 Rejuvenation Powder | 8 闪烁西瓜片 Glistering Melon + 玻璃瓶 Glass Bottle |
| 止血绷带 Bandage | 地狱疣 Nether Wart + 白色羊毛 White Wool |
| 压缩饼干 Compressed Biscuit | 9×面包 Bread (3×3) |
| 盐 Salt | 1×沙子 Sand |
| 肉干 Jerky | 盐 Salt + 熟肉 Cooked Meat |
| 甜浆果派 Sweet Berry Pie | 3 甜浆果 Sweet Berries + 3 小麦 Wheat |
| 酒桶 Wine Barrel | 3 小麦 Wheat + 木桶 Barrel |
| 腐肉干 Rotten Jerky | 腐肉 Rotten Flesh + 盐 Salt |
| 金盾丹 Gold Shield Elixir | 8 金块 + 地狱疣 |
| 鱼骨系列 | 骨头/骨块/海晶沙粒/海绵/灵魂沙/鳞甲/潮涌核心等逐级升级 |

> 完整配方可在菜单中点击物品查看 3×3 合成演示。Click any item in the menu to see its full 3×3 crafting demo.

---

## 构建 | Build

```bash
# 环境要求 Requires: JDK 21+
./gradlew build
```

构建产物位于 `build/libs/`（如 `ArmsorPlus1.21.11-0.3-J.jar`）。Output: `build/libs/` (e.g. `ArmsorPlus1.21.11-0.3-J.jar`)

### 开发 | Development

```bash
# 启动测试服务端 (Paper 1.21.11) Start test server
./gradlew runPurpur

# 仅更新插件 jar（热重载） Update plugin jar only
./gradlew reload
```

---

## 许可 | License

源码开放，**不可用于商业用途**。\
Open source. **Not for commercial use.**

[GitHub: LJRDE](https://github.com/LJRDE)

## 更新日志 | Changelog

详见 `更新日志.txt`（0.1-a → 0.3-J）。See `更新日志.txt` for full history (0.1-a → 0.3-J).
