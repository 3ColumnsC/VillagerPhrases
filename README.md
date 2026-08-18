# VillagerPhrases

Adds personality to villagers with contextual chat phrases — interact, proximity, nighttime, rain, hit & death responses. Multi-Language

---

<img src="https://res.cloudinary.com/dbtdewiqk/image/upload/v1787055469/1_fjstak.jpg" alt="Villager Phrases: Different Color per Profession" width="900">
<img src="https://res.cloudinary.com/dbtdewiqk/image/upload/v1787055469/2_mnsyb8.jpg" alt="Villager Phrases: Normal Villagers" width="900">
<img src="https://res.cloudinary.com/dbtdewiqk/image/upload/v1787055468/3_s6jhnd.jpg" alt="Villager Phrases: Compatibility with Villager Names" width="900">

---

### 🌐 Multi-Language

| Language             | Main    | Variants                         | 
|----------------------|---------|----------------------------------| 
| English              | `en_us` | —                                | 
| Español              | `es_es` | `es_ar`, `es_mx`, `es_cl`, `...` | 
| Portuguese           | `pt_br` | `pt_pt`                          | 
| Français             | `fr_fr` | `fr_ca`, `fr_ch`, `fr_be`        | 
| Deutsch              | `de_de` | `de_at`, `de_ch`                 | 
| Русский              | `ru_ru` | —                                | 
| Italiano             | `it_it` | —                                | 
| Polski               | `pl_pl` | —                                | 
| Nederlands           | `nl_nl` | —                                | 
| Türkçe               | `tr_tr` | —                                | 
| Tiếng Việt           | `vi_vn` | —                                | 
| 简体中文                 | `zh_cn` | —                                | 
| 繁體中文                 | `zh_tw` | `zh_hk`                          | 
| 日本語                  | `ja_jp` | —                                | 
| 한국어                  | `ko_kr` | —                                | 
| Українська           | `uk_ua` | —                                | 
| Svenska              | `sv_se` | —                                | 
| Čeština              | `cs_cz` | —                                | 
| Magyar               | `hu_hu` | —                                | 
| Suomi                | `fi_fi` | —                                | 
| Dansk                | `da_dk` | —                                | 
| Norsk bokmål (Norge) | `no_no` | —                                | 
| Română               | `ro_ro` | —                                | 
| български            | `bg_bg` | —                                | 
| ไทย                  | `th_th` | —                                |

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

### 🔗 Compatibility

* **VillagerNames**: fully compatible — custom names appear as the message prefix automatically

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

## 💬 Suggestions & Issues

If you have an idea, a suggestion, or found a bug, feel free to leave a comment on the CurseForge page or open an issue on the GitHub repository.

---

## 📜 License

This project is licensed under the MIT License.
