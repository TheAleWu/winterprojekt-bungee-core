package de.alewu.wpbc.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class GlobalConfigFile extends FileHandle {

    private boolean farm1 = false;

    public GlobalConfigFile() {
        super(new File("plugins/WinterProject", "global_config.yml"));
    }

    public void setChatPrefixedServer(String server, String displayName) {
        String section = "chat-prefixed-servers";
        cfg.set(section + "." + server, displayName);
        save();
    }

    public void removeChatPrefixedServer(String server) {
        String section = "chat-prefixed-servers";
        if (!cfg.contains(section + "." + server)) {
            return;
        }
        cfg.set(section + "." + server, null);
        save();
    }

    public String getDisplayName(String server) {
        String section = "chat-prefixed-servers";
        return cfg.contains(section + "." + server) ? cfg.getString(section + "." + server) : server;
    }

    public Map<String, String> getChatPrefixedServers() {
        String section = "chat-prefixed-servers";
        if (!cfg.contains(section)) {
            return new HashMap<>();
        }
        Map<String, String> results = new HashMap<>();
        Collection<String> keys = cfg.getSection(section).getKeys();
        for (String key : keys) {
            results.put(key, cfg.getString(section + "." + key));
        }
        return results;
    }

    public void setQueueServer(String queueServer) {
        cfg.set("queue-server", queueServer);
        save();
    }

    public String getQueueServer() {
        return cfg.contains("queue-server") ? cfg.getString("queue-server") : "queue";
    }

    public void setFirstServer(String firstServer) {
        cfg.set("first-server", firstServer);
        save();
    }

    public String getFirstServer() {
        return cfg.contains("first-server") ? cfg.getString("first-server") : "build";
    }

    public String getRandomFarmServer() {
        farm1 = !farm1;
        return farm1 ? getFarmServer1() : getFarmServer2();
    }

    public void setFarmServer1(String farm1) {
        cfg.set("farm-server-1", farm1);
        save();
    }

    public String getFarmServer1() {
        return cfg.contains("farm-server-1") ? cfg.getString("farm-server-1") : "farm1";
    }

    public void setFarmServer2(String farm2) {
        cfg.set("farm-server-2", farm2);
        save();
    }

    public String getFarmServer2() {
        return cfg.contains("farm-server-2") ? cfg.getString("farm-server-2") : "farm2";
    }

    public void setBuildServer(String firstServer) {
        cfg.set("build-server", firstServer);
        save();
    }

    public String getBuildServer() {
        return cfg.contains("build-server") ? cfg.getString("build-server") : "build";
    }

    public void setMaintenancePingMessage(String maintenancePingMessage) {
        cfg.set("maintenance-ping-message", maintenancePingMessage);
        save();
    }

    public String getMaintenancePingMessage() {
        return cfg.contains("maintenance-ping-message") ? cfg.getString("maintenance-ping-message") : "Wartung";
    }

    public void setMaintenanceBypassed(List<UUID> maintenanceBypassed) {
        cfg.set("maintenance-bypassed", maintenanceBypassed.stream().map(UUID::toString).collect(Collectors.toList()));
        save();
    }

    public List<UUID> getMaintenanceBypassed() {
        return cfg.contains("maintenance-bypassed") ? cfg.getStringList("maintenance-bypassed").stream().map(UUID::fromString).collect(Collectors.toList()) : new ArrayList<>();
    }

    public void setMaintenance(boolean maintenance) {
        cfg.set("maintenance", maintenance);
        save();
    }

    public boolean isMaintenance() {
        return cfg.contains("maintenance") && cfg.getBoolean("maintenance");
    }
}
