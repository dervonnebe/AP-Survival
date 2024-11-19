# AP-Survival PlaceholderAPI Support

## Status Placeholders

Das AP-Survival Plugin bietet verschiedene Placeholders für das Status-System. Diese können in jedem Plugin verwendet werden, das PlaceholderAPI unterstützt.

### Verfügbare Placeholders

| Placeholder | Beschreibung | Beispiel |
|-------------|--------------|----------|
| `%apstatus_raw%` | Zeigt den Status ohne § Formatierung (& bleibt erhalten) | `&cBeschäftigt` |
| `%apstatus_colored%` | Zeigt den Status mit Farben | `§cBeschäftigt` |
| `%apstatus_brackets_raw%` | Zeigt den Status in Klammern ohne § Formatierung | `[&cBeschäftigt]` |
| `%apstatus_brackets_colored%` | Zeigt den Status in Klammern mit Farben | `§7[§cBeschäftigt§7]` |
| `%apstatus_formatted%` | Zeigt den Status im Format der Tab-Liste | Wie in der Config definiert |
| `%apstatus%` | Standard-Format (Status mit Farben) | `§cBeschäftigt` |

### Beispiele

Wenn ein Spieler den Status "&cBeschäftigt" hat: 
- %apstatus_raw% → "&cBeschäftigt"
- %apstatus_colored% → "§cBeschäftigt"
- %apstatus_brackets_raw% → "[&cBeschäftigt]"
- %apstatus_brackets_colored% → "§7[§cBeschäftigt§7]"
- %apstatus_formatted% → Format aus der config.yml (status.format.tab)
- %apstatus% → "§cBeschäftigt"

### Verwendung in anderen Plugins

Die Placeholders können in jedem Plugin verwendet werden, das PlaceholderAPI unterstützt, wie zum Beispiel:
- Scoreboard Plugins
- Tab-List Plugins
- Chat Plugins
- Hologram Plugins

### Farbcodes

Die Status unterstützen alle Minecraft-Farbcodes mit dem & Symbol:
- &a = Hellgrün
- &b = Hellblau
- &c = Rot
- &d = Pink
- &e = Gelb
- &f = Weiß
- &0-9 = Verschiedene Farben
- &l = Fett
- &o = Kursiv
- &n = Unterstrichen
- &m = Durchgestrichen
- &r = Reset

Hinweis: § Zeichen werden aus Sicherheitsgründen automatisch entfernt, um Kicks zu verhindern. Verwende stattdessen & für Farbcodes.