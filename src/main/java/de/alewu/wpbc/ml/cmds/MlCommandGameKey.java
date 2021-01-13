package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandGameKey {

    public static MlCommandGameKey get(String lang) {
        return ml().translate(MlCommandGameKey.class, lang);
    }

    @TranslationDefinition("cmd.game-key.wrong-usage")
    public String wrongUsage() {
        return "§cFalsche Nutzung! /gamekey <Key>";
    }

    @TranslationDefinition("cmd.game-key.do-not-need-one")
    public String doNotNeedOne() {
        return "§cDu benötigst keinen GameKey um spielen zu können.";
    }

    @TranslationDefinition("cmd.game-key.already-used-one")
    public String alreadyUsedOne() {
        return "§cDu hast bereits einen GameKey eingelöst.";
    }

    @TranslationDefinition("cmd.game-key.already-in-use")
    public String alreadyInUse() {
        return "§cDieser GameKey ist bereits in Verwendung!";
    }

    @TranslationDefinition("cmd.game-key.no-key-found")
    public String noKeyFound() {
        return "§cEs existiert kein GameKey mit der Bezeichnung §6%s§c.";
    }

    @TranslationDefinition("cmd.game-key.successfully-used")
    public String successfullyUsed() {
        return "§aDu hast den GameKey §6%s §aerfolreich eingelöst.";
    }

    @TranslationDefinition("cmd.game-key.reconnect-to-play")
    public String reconnectToPlay() {
        return "§aReconnecte, um auf dem Server spielen zu können.";
    }

}
