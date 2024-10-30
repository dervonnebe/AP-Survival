package de.dervonnebe.aps.events;

import de.dervonnebe.aps.APSurvival;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLinksSendEvent;

public class ServerLinksEvent implements Listener {
    APSurvival plugin;

    public ServerLinksEvent(APSurvival plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLinksSend(PlayerLinksSendEvent event) {
        for (String key : plugin.getServerLinks().keySet()) {
            event.getLinks().addLink(key, plugin.getServerLinks().get(key));
        }
    }
}
