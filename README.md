本项目为Player VOX的分支，旨在修复原项目存在的bug，以及新增一些新功能

# PlayerVOX Plus

**PlayerVOX Plus** 是一个 Minecraft Forge 模组，为玩家角色添加语音。语音包创作者通过 JSON 数据文件和 OGG 音频，精确定义角色在何时发声——受伤、击杀、死亡、获得进度等。

每位玩家在游戏内独立选择自己的语音包，语音以空间音效播放，附近玩家均可听到。

---

## 需求

- Minecraft **1.20.1**
- Forge **47.2.0** 或更高
- **TACZ**（可选）— 启用枪械相关语音触发器

---

## 安装

1. 从 [Releases](../../releases) 下载最新的 `PlayerVOX_Plus*.jar`。
2. 放入 `mods/` 文件夹。
3. *（可选）* 将 TACZ 放入 `mods/` 以启用枪械触发器。
4. 启动游戏。

---

## 安装语音包

1. 在 Minecraft 实例根目录（与 `mods/` 同级）创建 `vox_packs/` 文件夹。
2. 将语音包文件夹或 `.zip` 放入其中。
3. 重载或重启游戏。

语音包在服务器启动时以及执行 `/reload` 时自动加载。

---

## 选择语音包

按 **V**（默认快捷键，可在设置中修改）打开语音包选择界面。  
点击语音包即可选择，选择结果保存在服务端，重新登录后保留。

选择**"无语音包"**可为自己禁用语音。

---

## 语音包创作者

详细语音包制作与触发器说明请参考原项目 Wiki：  
[PlayerVOX Wiki](https://github.com/scarls686/PlayerVOX/wiki)

对于本Mod新增的内容，请参考  
[PlayerVOX Plus Wiki](https://github.com/Nyakano521/PlayerVOX_Plus/wiki)


## 许可证

MIT
