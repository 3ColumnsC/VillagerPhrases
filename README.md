# VillagerPhrases

Adds personality to villagers with contextual chat phrases — interact, proximity, nighttime, rain and hit responses — fully configurable and client-side.

![Fabric](https://img.shields.io/badge/Fabric-Supported-green)
![NeoForge](https://img.shields.io/badge/NeoForge-Supported-green)
![License](https://img.shields.io/badge/License-MIT-blue)

---

### 🌐 Multi-Language

| Language   | File     | Variants                  |
|------------|----------|---------------------------|
| English    | en_us    | —                         |
| Spanish    | es_es    | es_ar, es_mx, es_cl, ...  |
| French     | fr_fr    | fr_ca, fr_ch, fr_be       |
| German     | de_de    | de_at                     |
| Portuguese | pt_br    | pt_pt                     |

---

<img src="https://res.cloudinary.com/dbtdewiqk/image/upload/v1783320796/1_gmchmv.jpg" alt="Villager Phrases" width="900">
<img src="https://res.cloudinary.com/dbtdewiqk/image/upload/v1783320796/2_k3iltp.jpg" alt="Villager Phrases" width="900">

---

## ⚡ Features

### 🗣️ Contextual Phrases

Villagers respond in different situations:

| Situation   | Trigger         | Toggle                  |
|-------------|-----------------|-------------------------|
| Interact    | Right-click     | enableNormalPhrases     |
| Proximity   | Stand near      | enableNormalPhrases     |
| Night       | Proximity       | enableNightPhrases      |
| Rain        | Proximity       | enableRainPhrases       |
| Hit         | Attack villager | enableHitPhrases        |
| Death       | Kill villager   | enableDeathPhrases      |

---

## ⚙️ Configuration

After launching the game once, a configuration file will be generated:

```text
config/villagerphrases.json
```

Available options:

```json
{
  "enableNormalPhrases": true,
  "enableHumorPhrases": true,
  "enableNightPhrases": true,
  "enableRainPhrases": true,
  "enableHitPhrases": true,
  "enableDeathPhrases": true
}
```

Each toggle independently controls its corresponding phrase category.

---

## 📦 Requirements

### Fabric

* Fabric API
* (+26.X) Java 25 or newer

### NeoForge

* (+26.X) Java 25 or newer

---

## 🔗 Compatibility

* **VillagerNames**: fully compatible — custom names appear as the message prefix automatically

---

## 📜 License

This project is licensed under the MIT License.
