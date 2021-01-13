package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandToggleRank {

    public static MlCommandToggleRank get(String lang) {
        return ml().translate(MlCommandToggleRank.class, lang);
    }

    @TranslationDefinition("cmd.toggle-rank.toggled-on")
    public String toggledOn() {
        return "§aDu wirst nun als Spieler angezeigt.";
    }

    @TranslationDefinition("cmd.toggle-rank.toggled-off")
    public String toggledOff() {
        return "§aEs wird nun dein Standardrang angezeigt.";
    }

}
