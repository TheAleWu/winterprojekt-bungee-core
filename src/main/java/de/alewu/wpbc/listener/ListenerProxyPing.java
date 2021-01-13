package de.alewu.wpbc.listener;

import de.alewu.wpbc.files.Files;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ListenerProxyPing implements Listener {

    @EventHandler
    public void on(ProxyPingEvent e) {
        if (Files.GLOBAL_CONFIG.isMaintenance()) {
            ServerPing response = e.getResponse();
            response.setVersion(new Protocol(Files.GLOBAL_CONFIG.getMaintenancePingMessage(), ProtocolConstants.MINECRAFT_1_15_2));
            e.setResponse(response);
        }
    }

}
