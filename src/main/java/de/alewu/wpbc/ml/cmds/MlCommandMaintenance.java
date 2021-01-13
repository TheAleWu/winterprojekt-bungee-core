package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandMaintenance {

    public static MlCommandMaintenance get(String lang) {
        return ml().translate(MlCommandMaintenance.class, lang);
    }

    @TranslationDefinition("command.maintenance.turned-on")
    public String turnedOn() {
        return "§aDer Wartungsmodus wurde §2aktiviert§a.";
    }

    @TranslationDefinition("command.maintenance.non-bypassed-kicked")
    public String nonBypassedKicked() {
        return "§aAlle nicht gebypassten Spieler wurden gekickt!";
    }

    @TranslationDefinition("command.maintenance.turned-off")
    public String turnedOff() {
        return "§cDer Wartungsmodus wurde §4deaktiviert§c.";
    }

    @TranslationDefinition("command.maintenance.someone-turned-on")
    public String someoneTurnedOn() {
        return "§6%s §ahat den Wartungsmodus aktiviert.";
    }

    @TranslationDefinition("command.maintenance.someone-turned-off")
    public String someoneTurnedOff() {
        return "§6%s §chat den Wartungsmodus deaktiviert.";
    }

    @TranslationDefinition("command.maintenance.turn-on-kick")
    public String turnOnKick() {
        return "§6Der Server ist nun im Wartungsmodus.\n\n§aWeitere Informationen gibt es auf dem Discord\n§aoder auf dem Twitter Account!";
    }

    @TranslationDefinition("command.maintenance.join-while-maintenance")
    public String joinWhileMaintenance() {
        return "§6Der Server ist im Wartungsmodus.\n\n§aWeitere Informationen gibt es auf dem Discord\n§aoder auf dem Twitter Account!";
    }

}
