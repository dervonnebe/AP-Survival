# Diese infos sind nicht Aktuel!

---

# AP-Survival Plugin

Das AP-Survival Plugin bietet eine Reihe von Funktionen für Minecraft-Server, um das Spielerlebnis zu verbessern. Es enthält Messaging- und Blockierungsmechanismen, um die Interaktion zwischen Spielern zu steuern.

## Features

- **Private Nachrichten**: Spieler können direkt miteinander kommunizieren.
- **Blockierungsmechanismus**: Spieler können andere Spieler blockieren, um keine Nachrichten mehr von ihnen zu erhalten.
- **Befehlsverarbeitung**: Benutzerfreundliche Befehle zur Verwaltung von Nachrichten und Blockierungen.

## Installation

1. Lade die JAR-Datei des Plugins herunter.
2. Platziere die JAR-Datei im `plugins`-Ordner deines Minecraft-Servers.
3. Starte den Server neu.
4. Konfiguriere das Plugin nach Bedarf in der `config.yml`.

## Konfiguration

Die Konfiguration des Plugins erfolgt über die `config.yml`. Hier kannst du Einstellungen wie Nachrichtenformate, Berechtigungen und andere Optionen anpassen.

## Commands

Hier ist eine Liste der verfügbaren Befehle im AP-Survival Plugin:

### `/msg <player> <message>`
Sendet eine private Nachricht an den angegebenen Spieler.

- **Berechtigung**: `aps.command.msg.send`
- **Beispiel**: `/msg Spieler Hallo!`

### `/reply <message>` (oder `/r <message>`)
Sendet eine Antwort an den zuletzt kontaktierten Spieler.

- **Berechtigung**: `aps.command.reply`
- **Beispiel**: `/reply Wie geht's?`

### Blockierung

Wenn ein Spieler einen anderen blockiert, kann dieser keine Nachrichten mehr senden. Spieler können sich gegenseitig blockieren, indem sie das `block`-System verwenden (implementiere die Befehle entsprechend in deinem Plugin).

## Berechtigungen

Hier sind die Berechtigungen, die für die Nutzung des Plugins erforderlich sind:

- `aps.command.msg.send`: Erlaubt das Senden von Nachrichten.
- `aps.command.msg.receive`: Erlaubt das Empfangen von Nachrichten.
- `aps.command.reply`: Erlaubt das Antworten auf Nachrichten.

## Hinweise

- Achte darauf, dass alle Spieler die erforderlichen Berechtigungen haben, um die Funktionen des Plugins nutzen zu können.
- Stelle sicher, dass die Datenbankverbindung korrekt konfiguriert ist, um die Blockierungsmechanismen ordnungsgemäß zu unterstützen.

## Support

Für Unterstützung und Fehlermeldungen wende dich bitte an den Entwickler oder besuche das entsprechende Forum.

## Code Stats
Gesamt im Projekt: 
Dateien: 1122,
Zeilen Code: 24789

---

**Viel Spaß mit dem AP-Survival Plugin!**
