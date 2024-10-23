package de.dervonnebe.aps.commands.tpa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Date;

@Getter
@AllArgsConstructor
public class TeleportRequest {
    private final Player requester;
    private final Player target;
    private final long timestamp; // Zeitstempel in Millisekunden

    public Date getRequestTime() {
        return new Date(timestamp);
    }
}
