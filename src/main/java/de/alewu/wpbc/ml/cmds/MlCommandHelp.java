package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandHelp {

    public static MlCommandHelp get(String lang) {
        return ml().translate(MlCommandHelp.class, lang);
    }

    @TranslationDefinition("cmd.help.help")
    public String help() {
        return "§7Zeigt diese Seite lol";
    }

    @TranslationDefinition("cmd.help.changelog")
    public String changelog() {
        return "§7Zeige die Updatehistorie an";
    }

    @TranslationDefinition("cmd.help.language")
    public String language() {
        return "§7Stelle deine InGame-Sprache um";
    }

    @TranslationDefinition("cmd.help.ping")
    public String ping() {
        return "§7Zeige deinen aktuellen Ping";
    }

    @TranslationDefinition("cmd.help.spawn")
    public String spawn() {
        return "§7Teleportiere dich zum Spawn";
    }

    @TranslationDefinition("cmd.help.farmwelt")
    public String farmwelt() {
        return "§7Teleportiere dich zur Farmwelt";
    }

    @TranslationDefinition("cmd.help.msg")
    public String msg() {
        return "§7Sende eine private Nachricht an einen Spieler";
    }

    @TranslationDefinition("cmd.help.togglemsg")
    public String togglemsg() {
        return "§7Schalte deine Privatnachrichten an/aus";
    }

    @TranslationDefinition("cmd.help.status")
    public String status() {
        return "§7Ändere deinen Status";
    }

    @TranslationDefinition("cmd.help.rang")
    public String rang() {
        return "§7Zeige deinen aktuellen Rang";
    }

    @TranslationDefinition("cmd.help.trade")
    public String trade() {
        return "§7Stelle einem Spieler einen Handelsantrag";
    }

    @TranslationDefinition("cmd.help.sit")
    public String sit() {
        return "§7Setze dich hin";
    }

    @TranslationDefinition("cmd.help.home")
    public String home() {
        return "§7Teleportiere dich zu einem Home";
    }

    @TranslationDefinition("cmd.help.sethome")
    public String sethome() {
        return "§7Setze ein Home";
    }

    @TranslationDefinition("cmd.help.delhome")
    public String delhome() {
        return "§7Lösche ein Home";
    }

    @TranslationDefinition("cmd.help.dsgvo")
    public String dsgvo() {
        return "§7Datenschutzbedingungen anzeigen";
    }

    @TranslationDefinition("cmd.help.wiki")
    public String wiki() {
        return "§7Monster-Wiki anschauen";
    }

    @TranslationDefinition("cmd.help.nether")
    public String nether() {
        return "§7Teleportiere dich in den Nether";
    }

    @TranslationDefinition("cmd.help.reply")
    public String reply() {
        return "§7Antworte auf deine letzte Nachricht";
    }

    @TranslationDefinition("cmd.help.tpa")
    public String tpa() {
        return "§7Teleportanfrage stellen";
    }

    @TranslationDefinition("cmd.help.list")
    public String list() {
        return "§7Globale Spielerliste anzeigen";
    }

    @TranslationDefinition("cmd.help.ontime")
    public String ontime() {
        return "§7Spielzeit anzeigen";
    }

    @TranslationDefinition("cmd.help.deaths")
    public String deaths() {
        return "§7Eigene Todesanzahl anzeigen";
    }

    @TranslationDefinition("cmd.help.deathtop")
    public String deathTop() {
        return "§7Todesrangliste anzeigen";
    }

    @TranslationDefinition("cmd.help.lock")
    public String lock() {
        return "§7Sichert die Kiste/Tür/etc nachdem du sie anklickst";
    }

    @TranslationDefinition("cmd.help.unlock")
    public String unlock() {
        return "§7Entfernt die Sicherung";
    }

    @TranslationDefinition("cmd.help.cpassword")
    public String cpassword() {
        return "§7Erstellt eine Passwort gesicherte Kiste/Tür/etc";
    }

    @TranslationDefinition("cmd.help.cmodify")
    public String cmodify() {
        return "§7Ändert eine vorhandene Sicherung, indem Sie User und / oder Gruppen hinzufügen oder entfernen";
    }

    @TranslationDefinition("cmd.help.cinfo")
    public String cinfo() {
        return "§7Klicke auf eine Sicherung, um Informationen darauf anzuzeigen";
    }

    @TranslationDefinition("cmd.help.lwc-hopper")
    public String lwcHopper() {
        return "§7Schaltet das Nutzen von Trichtern für eine Kiste an/aus";
    }

}
