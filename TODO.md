# ArmsorPlus 开发待办清单

> 由代码巡检整理，按优先级排序。
> 更新日期：2026-08-10

## ✅ 已修复（71项巡检，全部完成）

严重 BUG 4/4 · 高危 BUG 7/7 · 中危 BUG 4/6 · Lore 14/14 · Boss/菜单 14/15 · 保留项 3/3

<details>
<summary>点击展开完整修复清单</summary>

### 🔥 严重 BUG
- 法杖冷却被注释掉 → 无限发射
- RipplesHandler 空指针
- 血裂 Lore 刷新任务 NPE
- 飞剑状态泄漏 + 死亡后不清飞行

### ⚠️ 高危 BUG
- 匕首击杀不计入玩家
- 飞斧/雨御前/疾刺 左键也能触发
- 盘丝弓/爆炸弓 命中判断失效
- AutoPlant 副手减错物品
- 树苗元数据残留
- 伤害事件被取消后仍附加伤害
- 鱼骨武器 Lore 少写雨天

### 🟡 中危 BUG
- Scepter 瞄准天空 NPE
- 飞斧耐久 meta null
- percent() 每次 new Random
- 冷却显示"0秒"（3处）

### 🔵 Lore 修正
- 14项逐行实测，3项修正（雨御前冷却/大便隐藏饥饿/幻惑法杖失明），11项已一致

### 🏗️ Boss/菜单
- 星痕剑属性键冲突、元素反应 Lore 删除、核心命中 2x 倍率
- 骷髅王传送提示、IceSpike 去重、惊雷附魔书加入菜单
- NamespaceKey 拼写、SkeletonKingBowKey 删除
- Cryo/Pyro 无用 import、烈焰领主 emoji
- getPlugins[0] 修复、14个已删食物配方清理
- 菜单页数常量化

</details>

## 🟡 保留项（游戏机制，非 BUG）

- [x] ~~盘丝弓/爆炸弓 PDC 次数重置~~ — 新弓=9次是设计行为
- [x] ~~元素之刃 flag 泄漏窗口~~ — 加 try-finally 确保 flag 清除
- [x] ~~OpenSeaLottery onPlayerQuit 动画任务~~ — 设计如此（后台完成，奖品存待领取）

## 🔴 第二轮扫描（2026-08-11 遗漏检查，共 ~20 项）

## 🔴 第二轮扫描（2026-08-11 遗漏检查，共 ~20 项）

- [x] ~~疾刺附魔适配 1.21+ 长矛/重锤~~ — `isSpearOrTrident` 和右键处理器加 `_SPEAR`/`MACE` 支持

### 🔥 严重 BUG

- [ ] **SkeletonKing 空指针检查顺序颠倒**（`SkeletonKing.java:87-96`）`bossEntity.isDead()` 在 `== null` 判断前 9 行执行，并发清理时 NPE
- [ ] **18 个伤害 handler 缺 isCancelled 检查** 其他插件取消伤害事件后，这些 handler 照常执行

#### ArmsorPlusItemHandler.java（15 个）
- [ ] onMagicStickHit、onFrostArrowHit、onFlameHalberdAttack、onFlameHalberdHit
- [ ] onIceSwordAttack、onWebBowHit、onExplosionBowHit
- [ ] onSeaBoneSwordAttack、onSeaBoneKnifeAttack、onSpiritBoneKnifeAttack
- [ ] onSeaSpineSwordAttack、onSeaSpineKnifeDefend、onSpiritSpineKnifeAttack
- [ ] onSeaCrySwordAttack、onSeaCryKnifeAttack

#### ArmsorPlusEnchantEventHandler.java（~10 个）
- [ ] AmbushHandler、RevengeHandler、WitheringHandler、Dodge(OnBeaten)
- [ ] RipplesHandler、GolemGuardianHandler、PiercingHandler、HolographicHandler、StrongBurstHandler
- [ ] MergedDamageHandler 内全部附魔（血祭/锋刃/双斩/星痕/雷光/暴击/烈阳/冰刺/地狱火/吞生/吸血/剧毒/寒冻/饥荒/失明/眩晕）

### 🟡 中低 BUG

- [ ] WineRevive 元数据玩家退服未清理（热重载时残留）
- [ ] SkeletonKing:109 `getAttribute().getValue()` 缺 null 保护
- [ ] OpenSeaLottery:207 `getItemMeta()` 缺 null 检查
- [ ] FoodListeners:115 `getAttribute().getValue()` 未判空

## 💡 体系建议

### 平衡
- [ ] 鱼骨系列 15 把武器效果重叠（水中/雨天加伤），建议合并或差异化定位
- [ ] 血祭 100% 触发 + 2~6 倍 + 无视护甲，数值偏高，建议限倍率/降触发率
- [ ] 闪避 8%/级 + 80% 减免，建议满级封顶概率
- [ ] 部分附魔建议设冲突（吸血 vs 血祭）

### 成长/经济
- [ ] 强化系统加失败/保底/保护石，强化的成长感
- [ ] 魔法球抽奖加保底机制
- [ ] 强化石/魔法球加入生存获取途径（Boss掉落/钓鱼/交易）

### 内容/进度
- [ ] Boss 击杀统计/成就/掉落保底
- [ ] 新手引导任务链（收集→合成→强化→打Boss）

### 技术
- [ ] `IfUSEMagicBallEvent` 约30个重复 if 块 → 表驱动
- [ ] `handleGive` 约90个 case → 表驱动
- [ ] 资源包 7 个物品仍是汉堡占位纹理，需补真实纹理
- [ ] `percent()` 改共享 Random / ThreadLocalRandom
