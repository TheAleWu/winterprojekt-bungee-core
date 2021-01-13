package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandSocialSpy {

    public static MlCommandSocialSpy get(String lang) {
        return ml().translate(MlCommandSocialSpy.class, lang);
    }

    @TranslationDefinition("cmd.show-key.activated")
    public String activated() {
        return "§aDu hast die Überwachung der privaten Nachrichten aktiviert.";
    }

    @TranslationDefinition("cmd.show-key.deactivate-on-quit")
    public String deactivateOnQuit() {
        return "§aDie Funktion deaktiviert sich automatisch beim Verlassen des Servers.";
    }

    @TranslationDefinition("cmd.show-key.deactivated")
    public String deactivated() {
        return "§cDu hast die Überwachung der privaten Nachrichten deaktiviert.";
    }

    @TranslationDefinition("cmd.show-key.format")
    public String format() {
        return "§8[§9§l✎§8] §6%1$s §8» §6%2$s§8: §7%3$s";
    }

}
