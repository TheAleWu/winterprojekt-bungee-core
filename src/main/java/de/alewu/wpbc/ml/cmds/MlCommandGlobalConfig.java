package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandGlobalConfig {

    public static MlCommandGlobalConfig get(String lang) {
        return ml().translate(MlCommandGlobalConfig.class, lang);
    }

    @TranslationDefinition("cmd.global-config.help.header")
    public String helpHeader() {
        return "§eGlobale Konfiguration";
    }

    @TranslationDefinition("cmd.global-config.help.help")
    public String helpHelp() {
        return "§8➔ §7Zeige die Hilfeseite an.";
    }

    @TranslationDefinition("cmd.global-config.help.cps")
    public String helpCps() {
        return "§8➔ §7Zeige alle Chat-Prefixed-Servers an.";
    }

    @TranslationDefinition("cmd.global-config.help.cps-add")
    public String helpCpsAdd() {
        return "§8➔ §7Füge einen Chat-Prefixed-Server hinzu.";
    }

    @TranslationDefinition("cmd.global-config.help.cps-rem")
    public String helpCpsRem() {
        return "§8➔ §7Entferne einen Chat-Prefixed-Server.";
    }

    @TranslationDefinition("cmd.global-config.help.queue")
    public String helpQueue() {
        return "§8➔ §7Zeige den Queue-Server an.";
    }

    @TranslationDefinition("cmd.global-config.help.queue-set")
    public String helpQueueSet() {
        return "§8➔ §7Setze den Queue-Server.";
    }

    @TranslationDefinition("cmd.global-config.help.first-server")
    public String helpFirst() {
        return "§8➔ §7Zeige den ersten Join-Server an.";
    }

    @TranslationDefinition("cmd.global-config.help.first-server-set")
    public String helpFirstSet() {
        return "§8➔ §7Setze den ersten Join-Server.";
    }

    @TranslationDefinition("cmd.global-config.help.farm-server")
    public String helpFarm() {
        return "§8➔ §7Zeige den Farm-Server an.";
    }

    @TranslationDefinition("cmd.global-config.help.farm-server-set")
    public String helpFarmSet() {
        return "§8➔ §7Setze den Farm-Server.";
    }

    @TranslationDefinition("cmd.global-config.help.build-server")
    public String helpBuild() {
        return "§8➔ §7Zeige den Build-Server an.";
    }

    @TranslationDefinition("cmd.global-config.help.build-server-set")
    public String helpBuildSet() {
        return "§8➔ §7Setze den Build-Server.";
    }

    @TranslationDefinition("cmd.global-config.help.maintenance-ping-message-set")
    public String helpMaintenancePingMessageSet() {
        return "§8➔ §7Setze die Maintenance Ping Nachricht.";
    }

    @TranslationDefinition("cmd.global-config.help.maintenance-ping-message-get")
    public String helpMaintenancePingMessageGet() {
        return "§8➔ §7Zeige die Maintenance Ping Nachricht an.";
    }

    @TranslationDefinition("cmd.global-config.help.maintenance-bypassed-set")
    public String helpMaintenanceBypassedSet() {
        return "§8➔ §7Füge einen Spieler zur Bypass-Liste hinzu oder entferne ihn.";
    }

    @TranslationDefinition("cmd.global-config.help.maintenance-bypassed-set.added")
    public String helpMaintenanceBypassedSetAdded() {
        return "§8➔ §6%s §7gebypassed";
    }

    @TranslationDefinition("cmd.global-config.help.maintenance-bypassed-set.removed")
    public String helpMaintenanceBypassedSetRemoved() {
        return "§8➔ §6%s §7nicht mehr gebypassed";
    }

    @TranslationDefinition("cmd.global-config.help.maintenance-bypassed-get")
    public String helpMaintenanceBypassedGet() {
        return "§8➔ §7Zeige die Maintenance Bypass Liste an.";
    }

    @TranslationDefinition("cmd.global-config.cps.no-entries")
    public String cpsNoEntries() {
        return "§cEs sind keine Chat-Prefixed-Servers hinterlegt.";
    }

    @TranslationDefinition("cmd.global-config.cps-add.updated")
    public String cpsAddUpdated() {
        return "§aChat-Prefix des Servers §6%1s §awurde zu §6%2s §ageupdatet.";
    }

    @TranslationDefinition("cmd.global-config.cps-rem.not-exists")
    public String cpsRemNotExists() {
        return "§cEs existiert kein Chat-Prefix für den Server §6%s§c.";
    }

    @TranslationDefinition("cmd.global-config.cps-rem.success")
    public String cpsRemSuccess() {
        return "§aChat-Prefix des Servers §6%s §awurde gelöscht.";
    }

    @TranslationDefinition("cmd.global-config.queue-and-first.server-unknown")
    public String serverUnknown() {
        return "§cEs konnte kein Server mit dem Namen §6%s §cgefunden werden.";
    }

    @TranslationDefinition("cmd.global-config.queue.success")
    public String queueSetSuccess() {
        return "§aDer Queue-Server wurde zu §6%s §ageupdatet.";
    }

    @TranslationDefinition("cmd.global-config.first-server.success")
    public String firstServerSetSuccess() {
        return "§aDer erste Join-Server wurde zu §6%s §ageupdatet.";
    }

}
