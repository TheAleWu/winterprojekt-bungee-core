package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandShowKeys {

    public static MlCommandShowKeys get(String lang) {
        return ml().translate(MlCommandShowKeys.class, lang);
    }

    @TranslationDefinition("cmd.show-key.wrong-usage")
    public String wrongUsage() {
        return "§cFalsche Nutzung! /showkeys <1 - %s>";
    }

    @TranslationDefinition("cmd.show-key.no-keys-exist")
    public String noKeysExist() {
        return "§cDerzeit existieren keine GameKeys!";
    }

    @TranslationDefinition("cmd.show-key.entered-page-no-valid-number")
    public String enteredPageNoValidNumber() {
        return "§cDie gewünschte Seite ist keine valide Zahl!";
    }

    @TranslationDefinition("cmd.show-key.unused")
    public String unused() {
        return "§7Nicht eingelöst";
    }

    @TranslationDefinition("cmd.show-key.used-by")
    public String usedBy() {
        return "§6Eingelöst von: §7%s";
    }

    @TranslationDefinition("cmd.show-key.used-at")
    public String usedAt() {
        return "§6Eingelöst am: §7%s";
    }

    @TranslationDefinition("cmd.show-key.not-used-yet")
    public String notUsedYet() {
        return "§6Eingelöst am: §7-";
    }

    @TranslationDefinition("cmd.show-key.created-by")
    public String createdBy() {
        return "§6Erstellt von: §7%s";
    }

    @TranslationDefinition("cmd.show-key.created-at")
    public String createdAt() {
        return "§6Erstellt am: §7%s";
    }

    @TranslationDefinition("cmd.show-key.hover-for-more-info")
    public String hoverForMoreInfo() {
        return "§7Hover über einen GameKey für mehr Informationen!";
    }

}
