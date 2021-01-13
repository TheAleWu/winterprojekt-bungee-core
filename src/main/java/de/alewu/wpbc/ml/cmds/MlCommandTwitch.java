package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandTwitch {

    public static MlCommandTwitch get(String lang) {
        return ml().translate(MlCommandTwitch.class, lang);
    }

    @TranslationDefinition("cmd.twitch.wrong-usage")
    public String wrongUsage() {
        return "§cNutze §6/twitch <Username> §cum deinen Twitch-Usernamen zu setzen.";
    }

    @TranslationDefinition("cmd.twitch.set-username")
    public String setUsername() {
        return "§aDein Twitch-Username wurde zu §6%s §agesetzt.";
    }

}
