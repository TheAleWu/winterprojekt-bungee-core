package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandCreateKey {

    public static MlCommandCreateKey get(String lang) {
        return ml().translate(MlCommandCreateKey.class, lang);
    }

    @TranslationDefinition("cmd.create-game-key.error-while-creating")
    public String errorWhileCreating() {
        return "§cFehler beim Generieren eines GameKeys!";
    }

    @TranslationDefinition("cmd.create-game-key.try-again-or-check")
    public String tryAgainOrCheck() {
        return "§cVersuch es erneut oder überprüfe die Logdateien.";
    }

    @TranslationDefinition("cmd.create-game-key.successfully-created")
    public String successfullyCreated() {
        return "§aDer GameKey §6%s §awurde erfolgreich generiert.";
    }

}
