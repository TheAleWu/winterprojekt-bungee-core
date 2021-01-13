package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlPunishments {

    public static MlPunishments get(String lang) {
        return ml().translate(MlPunishments.class, lang);
    }

    @TranslationDefinition("punishments.unknown-template")
    public String punishmentUnknownTemplate() {
        return "§cDas Template §6%1s §cexistiert nicht.";
    }

    @TranslationDefinition("punishments.cannot-punish")
    public String notAllowedToPunish() {
        return "§cDu darfst diesen Spieler nicht bestrafen.";
    }

    /////////////////////////////
    //  COMMAND: BAN TEMPLATE  //
    /////////////////////////////

    @TranslationDefinition("cmd.ban-templates.save-successful")
    public String banTemplatesSaveSuccessful() {
        return "§aDas §6%1s Template §amit dem Namen §6%2s §awurde angelegt.";
    }

    @TranslationDefinition("cmd.ban-templates.delete-successful")
    public String banTemplatesDeleteSuccessful() {
        return "§aDas §6%1s Template §amit dem Namen §6%2s §awurde gelöscht.";
    }

    @TranslationDefinition("cmd.ban-templates.unknown-template")
    public String banTemplatesUnknownTemplate() {
        return "§cTemplate §6%1s §cvom Typ §6%2s §cnicht gefunden.";
    }

    @TranslationDefinition("cmd.ban-templates.not-unknown-template")
    public String banTemplatesNotUnknownTemplate() {
        return "§cTemplate §6%1s §cvom Typ §6%2s §cexistiert bereits.";
    }

    @TranslationDefinition("cmd.ban-templates.delete.already-in-use")
    public String banTemplatesDeleteAlreadyInUse() {
        return "§cDieses Template wird bereits verwendet!";
    }

    @TranslationDefinition("cmd.ban-templates.delete.force-required")
    public String banTemplatesDeleteForceRequired() {
        return "§cNutze den Befehl mit \"-f\", um es zu löschen.";
    }

    @TranslationDefinition("cmd.ban-templates.delete.will-delete-history")
    public String banTemplatesDeleteWillDeleteHistory() {
        return "§c§lDies wird aber auch die Einträge der Spieler entfernen!";
    }

    @TranslationDefinition("cmd.ban-templates.invalid-timespan")
    public String banTemplatesInvalidTimespan() {
        return "§6%s §cist keine valide Zeitangabe.";
    }

    @TranslationDefinition("cmd.ban-templates.help.header")
    public String banTemplatesHelpHeader() {
        return "§eTemplates";
    }

    @TranslationDefinition("cmd.ban-templates.help.help")
    public String banTemplatesHelpHelp() {
        return "§8➔ §7Zeige die Hilfeseite an.";
    }

    @TranslationDefinition("cmd.ban-templates.help.list")
    public String banTemplatesHelpList() {
        return "§8➔ §7Alle Templates einer Kategorie auflisten.";
    }

    @TranslationDefinition("cmd.ban-templates.list.is-empty")
    public String banTemplatesListIsEmpty() {
        return "§8➔ §cKeine Templates vom Typ §6%s §cgefunden.";
    }

    @TranslationDefinition("cmd.ban-templates.help.addkick")
    public String banTemplatesHelpAddKick() {
        return "§8➔ §7Neues Kick-Template anlegen";
    }

    @TranslationDefinition("cmd.ban-templates.help.removekick")
    public String banTemplatesHelpRemoveKick() {
        return "§8➔ §7Kick-Template löschen";
    }

    @TranslationDefinition("cmd.ban-templates.help.addmute")
    public String banTemplatesHelpAddMute() {
        return "§8➔ §7Neues Mute-Template anlegen";
    }

    @TranslationDefinition("cmd.ban-templates.help.removemute")
    public String banTemplatesHelpRemoveMute() {
        return "§8➔ §7Mute-Template löschen";
    }

    @TranslationDefinition("cmd.ban-templates.help.addban")
    public String banTemplatesHelpAddBan() {
        return "§8➔ §7Neues Ban-Template anlegen";
    }

    @TranslationDefinition("cmd.ban-templates.help.removeban")
    public String banTemplatesHelpRemoveBan() {
        return "§8➔ §7Ban-Template löschen";
    }

    @TranslationDefinition("cmd.ban-templates.help.addunban")
    public String banTemplatesHelpAddUnban() {
        return "§8➔ §7Neues Unban-Template anlegen";
    }

    @TranslationDefinition("cmd.ban-templates.help.removeunban")
    public String banTemplatesHelpRemoveUnban() {
        return "§8➔ §7Unban-Template löschen";
    }

    @TranslationDefinition("cmd.ban-templates.help.addunban")
    public String banTemplatesHelpAddUnmute() {
        return "§8➔ §7Neues Unmute-Template anlegen";
    }

    @TranslationDefinition("cmd.ban-templates.help.removeunban")
    public String banTemplatesHelpRemoveUnmute() {
        return "§8➔ §7Unmute-Template löschen";
    }

    ///////////////////////
    //  COMMAND: BAN LOG //
    ///////////////////////

    @TranslationDefinition("cmd.bans.wrong-usage")
    public String bansWrongUsage() {
        return "§cFalsche Nutzung! /bans <Username> [Seite]";
    }

    @TranslationDefinition("cmd.bans.no-punishments")
    public String bansNoPunishments() {
        return "§cDer Spieler hat noch keine Bestrafungen erhalten.";
    }

    @TranslationDefinition("cmd.bans.punishment-info.header")
    public String bansPunishmentInfoHeader() {
        return "§6Banlog von %s";
    }

    @TranslationDefinition("cmd.bans.punishment-info.punisher")
    public String bansPunishmentInfoPunisher() {
        return "§6Erteilt von: §7%s";
    }

    @TranslationDefinition("cmd.bans.punishment-info.template")
    public String bansPunishmentInfoTemplate() {
        return "§6Template: §7%s";
    }

    ////////////////////
    //  COMMAND: KICK //
    ////////////////////

    @TranslationDefinition("cmd.kick.wrong-usage")
    public String kickWrongUsage() {
        return "§cFalsche Nutzung! /kick <Username> <Template>";
    }

    @TranslationDefinition("cmd.admin-kick.wrong-usage")
    public String adminKickWrongUsage() {
        return "§cFalsche Nutzung! /ak <Username> <Grund>";
    }

    ////////////////////
    //  COMMAND: MUTE //
    ////////////////////

    @TranslationDefinition("cmd.mute.wrong-usage")
    public String muteWrongUsage() {
        return "§cFalsche Nutzung! /mute <Username> <Template>";
    }

    @TranslationDefinition("cmd.mute.already-muted")
    public String muteAlreadyMuted() {
        return "§6%s §cist bereits gemuted.";
    }

    @TranslationDefinition("cmd.admin-mute.wrong-usage")
    public String adminMuteWrongUsage() {
        return "§cFalsche Nutzung! /am <Username> <Dauer> <Grund>";
    }

    ///////////////////
    //  COMMAND: BAN //
    ///////////////////

    @TranslationDefinition("cmd.ban.wrong-usage")
    public String banWrongUsage() {
        return "§cFalsche Nutzung! /ban <Username> <Template>";
    }

    @TranslationDefinition("cmd.ban.already-banned")
    public String banAlreadyBanned() {
        return "§6%s §cist bereits gebannt.";
    }

    @TranslationDefinition("cmd.admin-ban.wrong-usage")
    public String adminBanWrongUsage() {
        return "§cFalsche Nutzung! /ab <Username> <Dauer> <Grund>";
    }

    //////////////////////
    //  COMMAND: UNMUTE //
    //////////////////////

    @TranslationDefinition("cmd.unmute.wrong-usage")
    public String unmuteWrongUsage() {
        return "§cFalsche Nutzung! /unmute <Username> <Template>";
    }

    @TranslationDefinition("cmd.unmute.not-muted")
    public String unmuteNotMuted() {
        return "§6%s §cist nicht gemuted.";
    }

    /////////////////////
    //  COMMAND: UNBAN //
    /////////////////////

    @TranslationDefinition("cmd.unban.wrong-usage")
    public String unbanWrongUsage() {
        return "§cFalsche Nutzung! /unban <Username> <Template>";
    }

    @TranslationDefinition("cmd.unban.not-banned")
    public String unbanNotBanned() {
        return "§6%s §cist nicht gebannt.";
    }

    ////////////////////
    //  COMMUNICATION //
    ////////////////////

    @TranslationDefinition("punishments.communication.kick")
    public String kickDisconnectScreen() {
        return "§cDu wurdest gekickt.\n§3Grund: §c%s";
    }

    @TranslationDefinition("punishments.communication.mute")
    public String muteChatMessage() {
        return "%1$s§cDu wurdest gemuted.\n%1$s§3Grund: §c%2$s\n%1$s§3Dauer: §c%3$s";
    }

    @TranslationDefinition("punishments.communication.ban")
    public String banDisconnectScreen() {
        return "§cDu wurdest gebannt.\n§3Grund: §c%1s\n§3Dauer: §c%2s";
    }

    @TranslationDefinition("punishments.communication.broadcast.kick")
    public String kickBroadcast() {
        return "%1$s§6%2$s §7hat §6%3$s §7vom Server gekickt!\n%1$s§cGrund: §7%4$s";
    }

    @TranslationDefinition("punishments.communication.broadcast.mute")
    public String muteBroadcast() {
        return "%1$s§6%2$s §7hat §6%3$s §7vom Chat gemuted!\n%1$s§cGrund: §7%4$s\n%1$s§cDauer: §7%5$s";
    }

    @TranslationDefinition("punishments.communication.broadcast.ban")
    public String banBroadcast() {
        return "%1$s§6%2$s §7hat §6%3$s §7vom Server gebannt!\n%1$s§cGrund: §7%4$s\n%1$s§cDauer: §7%5$s";
    }

    @TranslationDefinition("punishments.communication.broadcast.unmute")
    public String unmuteBroadcast() {
        return "%1$s§6%2$s §7hat §6%3$s §7entmuted!\n%1$s§cGrund: §7%4$s";
    }

    @TranslationDefinition("punishments.communication.broadcast.unban")
    public String unbanBroadcast() {
        return "%1$s§6%2$s §7hat §6%3$s §7entbannt!\n%1$s§cGrund: §7%4$s";
    }

    @TranslationDefinition("punishments.communication.duration.permanent")
    public String durationPermanent() {
        return "§4Permanent";
    }

}
