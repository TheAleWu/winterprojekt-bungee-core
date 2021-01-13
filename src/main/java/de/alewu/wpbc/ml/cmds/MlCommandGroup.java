package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandGroup {

    public static MlCommandGroup get(String lang) {
        return ml().translate(MlCommandGroup.class, lang);
    }

    @TranslationDefinition("cmd.group.help.header")
    public String helpHeader() {
        return "§eGruppenverwaltung";
    }

    @TranslationDefinition("cmd.group.help.help")
    public String helpHelp() {
        return "§8➔ §7Zeige die Hilfeseite an.";
    }

    @TranslationDefinition("cmd.group.help.list")
    public String helpList() {
        return "§8➔ §7Alle Gruppen auflisten.";
    }

    @TranslationDefinition("cmd.group.help.create")
    public String helpCreate() {
        return "§8➔ §7Erstelle eine neue Gruppe.";
    }

    @TranslationDefinition("cmd.group.help.delete")
    public String helpDelete() {
        return "§8➔ §7Lösche eine existierende Gruppe.";
    }

    @TranslationDefinition("cmd.group.help.default")
    public String helpDefault() {
        return "§8➔ §7Lege eine Gruppe als Standardgruppe fest.";
    }

    @TranslationDefinition("cmd.group.help.sb-team")
    public String helpSbTeam() {
        return "§8➔ §7Lege das Scoreboard-Team fest (12 Buchstaben).";
    }

    @TranslationDefinition("cmd.group.help.tab-sort")
    public String helpTabSort() {
        return "§8➔ §7Lege die Tab-Sortierung fest (4 Zahlen).";
    }

    @TranslationDefinition("cmd.group.help.tab-prefix")
    public String helpTabPrefix() {
        return "§8➔ §7Lege den Tab-Prefix fest (max 16 Zeichen).";
    }

    @TranslationDefinition("cmd.group.help.chat-prefix")
    public String helpChatPrefix() {
        return "§8➔ §7Lege den Chat-Prefix fest (max 64 Zeichen).";
    }

    @TranslationDefinition("cmd.group.help.perm")
    public String helpPerm() {
        return "§8➔ §7Zeige alle Permissions einer Gruppe an.";
    }

    @TranslationDefinition("cmd.group.help.perm-add")
    public String helpPermAdd() {
        return "§8➔ §7Füge eine Berechtigung einer Gruppe hinzu.";
    }

    @TranslationDefinition("cmd.group.help.perm-rem")
    public String helpPermRem() {
        return "§8➔ §7Entferne eine Berechtigung einer Gruppe.";
    }

    @TranslationDefinition("cmd.group.help.player")
    public String helpPlayer() {
        return "§8➔ §7Zeige alle Gruppenmitglieder an.";
    }

    @TranslationDefinition("cmd.group.help.player-add")
    public String helpPlayerAdd() {
        return "§8➔ §7Füge ein Gruppenmitglied einer Gruppe hinzu.";
    }

    @TranslationDefinition("cmd.group.help.player-rem")
    public String helpPlayerRem() {
        return "§8➔ §7Entferne eine Gruppenmitglied einer Gruppe.";
    }

    @TranslationDefinition("cmd.group.help.info")
    public String helpInfo() {
        return "§8➔ §7Zeige Infos zur Gruppe.";
    }

    @TranslationDefinition("cmd.group.not-found")
    public String notFound() {
        return "§cEs existiert keine Gruppe mit dem Namen §6%s§c.";
    }

    @TranslationDefinition("cmd.group.create.already-exists")
    public String createAlreadyExists() {
        return "§cEs existiert bereits eine Gruppe mit dem Namen §6%s§c.";
    }

    @TranslationDefinition("cmd.group.create.success")
    public String createSuccess() {
        return "§aDie Gruppe mit dem Namen §6%s §awurde angelegt.";
    }

    @TranslationDefinition("cmd.group.delete.success")
    public String deleteSuccess() {
        return "§aDie Gruppe mit dem Namen §6%s §awurde gelöscht.";
    }

    @TranslationDefinition("cmd.group.delete.cannot-delete-default")
    public String cannotDeleteDefault() {
        return "§cDie Standardgruppe kann nicht gelöscht werden.";
    }

    @TranslationDefinition("cmd.group.delete.cannot-delete-populated")
    public String cannotDeletePopulated() {
        return "§cEs kann keine Gruppe mit Mitgliedern gelöscht werden.";
    }

    @TranslationDefinition("cmd.group.default.success")
    public String defaultSuccess() {
        return "§aDie Gruppe mit dem Namen §6%s §aist nun die Standardgruppe.";
    }

    @TranslationDefinition("cmd.group.tab-sort.success")
    public String tabSortSuccess() {
        return "§aDie Tab-Sortierung der Gruppe ist nun §6%s§a.";
    }

    @TranslationDefinition("cmd.group.tab-prefix.success")
    public String tabPrefixSuccess() {
        return "§aDer Tab-Prefix der Gruppe ist nun §6%s§a.";
    }

    @TranslationDefinition("cmd.group.chat-prefix.success")
    public String chatPrefixSuccess() {
        return "§aDer Chat-Prefix der Gruppe ist nun §6%s§a.";
    }

    @TranslationDefinition("cmd.group.perm.no-entries")
    public String permNoEntries() {
        return "§cDie Gruppe beinhaltet derzeit keine Berechtigungen.";
    }

    @TranslationDefinition("cmd.group.perm-add.already-contains")
    public String permAddAlreadyContains() {
        return "§cDie Gruppe enthält bereits die Berechtigung §6%s§c.";
    }

    @TranslationDefinition("cmd.group.perm-add.success")
    public String permAddSuccess() {
        return "§aDie Gruppe besitzt nun die Berechtigung §6%s§a.";
    }

    @TranslationDefinition("cmd.group.perm-rem.not-contains")
    public String permRemNotContains() {
        return "§cDie Gruppe enthält nicht die Berechtigung §6%s§c.";
    }

    @TranslationDefinition("cmd.group.perm-rem.success")
    public String permRemSuccess() {
        return "§aDie Gruppe besitzt nun nicht mehr die Berechtigung §6%s§a.";
    }

    @TranslationDefinition("cmd.group.player.not-found")
    public String playerNotFound() {
        return "§cEs existiert kein User mit dem Namen §6%s§c.";
    }

    @TranslationDefinition("cmd.group.player.no-entries")
    public String playerNoEntries() {
        return "§cDie Gruppe beinhaltet derzeit keine Mitglieder.";
    }

    @TranslationDefinition("cmd.group.player-add.already-contains")
    public String playerAddAlreadyContains() {
        return "§6%s §cist bereits Mitglied in der Gruppe.";
    }

    @TranslationDefinition("cmd.group.player-add.success")
    public String playerAddSuccess() {
        return "§6%s §aist nun Mitglied in der Gruppe.";
    }

    @TranslationDefinition("cmd.group.player-rem.not-contains")
    public String playerRemNotContains() {
        return "§6%s §cist nicht Mitglied in der Gruppe.";
    }

    @TranslationDefinition("cmd.group.player-rem.no-default-group")
    public String playerRemNoDefaultGroup() {
        return "§cEs existiert keine Standardgruppe.";
    }

    @TranslationDefinition("cmd.group.player-rem.success")
    public String playerRemSuccess() {
        return "§6%s §aist nun nicht mehr Mitglied der Gruppe.";
    }

    @TranslationDefinition("cmd.group.info.group-name")
    public String infoGroupName() {
        return "§6Gruppenname: §7%s";
    }

    @TranslationDefinition("cmd.group.info.tab-prefix")
    public String infoTabPrefix() {
        return "§6Tab-Prefix: §7%s";
    }

    @TranslationDefinition("cmd.group.info.chat-prefix")
    public String infoChatPrefix() {
        return "§6Chat-Prefix: §7%s";
    }

    @TranslationDefinition("cmd.group.info.sort-order")
    public String infoSortOrder() {
        return "§6Sortierung: §7%s";
    }

}
