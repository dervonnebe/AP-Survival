package de.dervonnebe.aps.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

public class CommandTabCompleteEvent implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSender();
        String buffer = event.getBuffer().toLowerCase();

        if (!player.hasPermission("aps.command.minecraft")) {
            List<String> completions = event.getCompletions().stream()
                    .filter(completion -> !completion.startsWith("minecraft:"))
                    .collect(Collectors.toList());
            event.getCompletions().clear();
            event.getCompletions().addAll(completions);
        }
    }
}

