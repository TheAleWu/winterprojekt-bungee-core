package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandMessage {

    public static MlCommandMessage get(String lang) {
        return ml().translate(MlCommandMessage.class, lang);
    }

    @TranslationDefinition("cmd.message.wrong-usage")
    public String wrongUsage() {
        return "§6/msg <Spieler> <Nachricht>";
    }

    @TranslationDefinition("cmd.message.player-not-online")
    public String playerNotOnline() {
        return "§6%s §cist derzeit nicht online.";
    }

    @TranslationDefinition("cmd.message.replied-player-not-online")
    public String repliedPlayerNotOnline() {
        return "§cDein Kommunikationspartner ist derzeit nicht online.";
    }

    @TranslationDefinition("cmd.message.player-not-messageable")
    public String playerNotMessageable() {
        return "§6%s §ckann derzeit aufgrund eines Fehlers keine Nachrichten empfangen.";
    }

    @TranslationDefinition("cmd.message.player-toggled-messages")
    public String playerToggledMessages() {
        return "§6%s §cmöchte derzeit keine Nachrichten empfangen.";
    }

    @TranslationDefinition("cmd.message.player-muted")
    public String playerMuted() {
        return "§6%s §cist gemuted und kann derzeit nicht antworten.";
    }

    @TranslationDefinition("cmd.message.turn-on-private-messages")
    public String turnOnPrivateMessages() {
        return "§cSchalte deine privaten Nachrichten an, um welche zu versenden!";
    }

    @TranslationDefinition("cmd.message.format-to")
    public String formatTo() {
        return "§6%1$s §8» §6%2$s§8: §7%3$s";
    }

    @TranslationDefinition("cmd.message.format-from")
    public String formatFrom() {
        return "§6%1$s §8« §6%2$s§8: §7%3$s";
    }

    @TranslationDefinition("cmd.message.message-self")
    public String messageSelf() {
        return "§cDu armer... Rede doch mit Bob:";
    }

    @TranslationDefinition("cmd.toggle-messages.receive-on")
    public String toggleReceiveOn() {
        return "§aDu empfängst nun wieder private Nachrichten.";
    }

    @TranslationDefinition("cmd.toggle-messages.receive-off")
    public String toggleReceiveOff() {
        return "§cDu empfängst nun keine private Nachrichten mehr.";
    }

    @TranslationDefinition("cmd.reply.no-one-messaged-yet")
    public String noOneMessagedYet() {
        return "§cDu hast noch keinem Spieler eine private Nachricht gesendet.";
    }

}
