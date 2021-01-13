package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandSpectate {

    public static MlCommandSpectate get(String lang) {
        return ml().translate(MlCommandSpectate.class, lang);
    }

    @TranslationDefinition("cmd.spectate.wrong-usage")
    public String wrongUsage() {
        return "§cNutze §6/spec <Username> §cum einen Spieler zu spectaten.";
    }

    @TranslationDefinition("cmd.spectate.not-online")
    public String notOnline() {
        return "§6%s §cist derzeit nicht online.";
    }

    @TranslationDefinition("cmd.spectate.now-spectating")
    public String nowSpectating() {
        return "§aDu spectatest nun §6%s§a.";
    }

    @TranslationDefinition("cmd.spectate.followed-to-server")
    public String followedToServer() {
        return "§aDu bist §6%1s §aauf den Server §6%2s §agefolgt.";
    }

    @TranslationDefinition("cmd.spectate.player-disconnected")
    public String playerDisconnected() {
        return "§cDer spectatete Spieler hat den Server verlassen.";
    }

    @TranslationDefinition("cmd.spectate.already-spectating")
    public String alreadySpectating() {
        return "§cDu spectatest bereits §6%s§c.";
    }

    @TranslationDefinition("cmd.spectate.left-spectator-mode")
    public String leftSpectatorMode() {
        return "§aDu hast den Spectator-Modus verlassen.";
    }

    @TranslationDefinition("cmd.spectate.already-spectating-another-player")
    public String alreadySpectatingAnotherPlayer() {
        return "§6%s §cspectatet bereits einen anderen Spieler.";
    }

    @TranslationDefinition("cmd.spectate.cannot-be-spectated")
    public String cannotBeSpectated() {
        return "§6%s §ckann nicht spectatet werden.";
    }

}
