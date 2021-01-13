package de.alewu.wpbc.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.alewu.coreapi.ml.MultiLanguage;
import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpc.repository.entity.GroupPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaticMethodCollection {

    private static final List<QueuedCommunicationData> queuedCommunicationData = new ArrayList<>();
    private static boolean queuedCommunicationDataProcessorStarted = false;

    private StaticMethodCollection() {
        // Util class
    }

    public static MultiLanguage ml() {
        return WinterProjectBungeeCore.coreAPI.translations();
    }

    public static BaseComponent[] tc(String string) {
        return TextComponent.fromLegacyText(string);
    }

    public static void reloadPermissions(ProxiedPlayer pp, List<GroupPermission> groupPermissions) {
        new ArrayList<>(pp.getPermissions()).forEach(perm -> pp.setPermission(perm, false));
        groupPermissions.forEach(gp -> pp.setPermission(gp.getId().getPermission(), true));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void sendToServers(Iterable<ServerInfo> servers, String subChannel, String... data) {
        startQueuedCommunicationDataProcessor();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        for (String s : data) {
            out.writeUTF(s);
        }
        servers.forEach(si -> {
            if (!si.getPlayers().isEmpty()) {
                si.sendData("winterproject:proxyinstruction", out.toByteArray());
            } else {
                clearOldData(subChannel);
                queuedCommunicationData.add(new QueuedCommunicationData(si, subChannel, data));
            }
        });
    }

    private static void clearOldData(String subChannel) {
        if (subChannel.equals("PlayerCapabilities")) {
            queuedCommunicationData.removeIf(x -> x.subChannel.equals(subChannel) && x.timestamp <= System.currentTimeMillis());
        }
    }

    public static void sendToServer(ServerInfo server, String subChannel, String... data) {
        sendToServers(Collections.singleton(server), subChannel, data);
    }

    private static void startQueuedCommunicationDataProcessor() {
        if (queuedCommunicationDataProcessorStarted) {
            return;
        }
        queuedCommunicationDataProcessorStarted = true;
        ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () -> {
            List<QueuedCommunicationData> processed = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                try {
                    QueuedCommunicationData x = queuedCommunicationData.get(i);
                    if (!x.target.getPlayers().isEmpty()) {
                        sendToServer(x.target, x.subChannel, x.data);
                        processed.add(x);
                    }
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            queuedCommunicationData.removeAll(processed);
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    private static class QueuedCommunicationData {

        private final ServerInfo target;
        private final String subChannel;
        private final long timestamp;
        private final String[] data;

        public QueuedCommunicationData(ServerInfo target, String subChannel, String[] data) {
            this.target = target;
            this.subChannel = subChannel;
            this.timestamp = System.currentTimeMillis();
            this.data = data;
        }
    }

}
