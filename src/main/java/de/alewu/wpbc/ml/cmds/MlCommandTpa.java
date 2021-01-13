package de.alewu.wpbc.ml.cmds;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlCommandTpa {

    public static MlCommandTpa get(String lang) {
        return ml().translate(MlCommandTpa.class, lang);
    }

    @TranslationDefinition("command.tpa.requestor-not-online-anymore")
    public String requestorNotOnlineAnymore() {
        return "§cDer Anfragesteller ist nicht mehr online.";
    }

    @TranslationDefinition("command.tpa.requested-accepted")
    public String requestedAccepted() {
        return "§6%s §ahat die Teleportanfrage akzeptiert.";
    }

    @TranslationDefinition("command.tpa.youve-accepted")
    public String youveAccepted() {
        return "§aDu hast die Teleportanfrage von §6%s §aakzeptiert.";
    }

    @TranslationDefinition("command.tpa.requested-denied")
    public String requestedDenied() {
        return "§6%s §chat die Teleportanfrage abgelehnt.";
    }

    @TranslationDefinition("command.tpa.youve-denied")
    public String youveDenied() {
        return "§cDu hast die Teleportanfrage von §6%s §cabgelehnt.";
    }

    @TranslationDefinition("command.tpa.requested-rejected")
    public String requestedRejected() {
        return "§6%s §chat die Teleportanfrage zurückgezogen.";
    }

    @TranslationDefinition("command.tpa.requested-deleted")
    public String requestDeleted() {
        return "§cDie Teleportanfrage wurde aufgehoben.";
    }

    @TranslationDefinition("command.tpa.not-online")
    public String notOnline() {
        return "§6%s §cist derzeit nicht online.";
    }

    @TranslationDefinition("command.tpa.request-sent")
    public String requestSent() {
        return "§aDu hast eine Teleportanfrage an §6%s §agesendet.";
    }

    @TranslationDefinition("command.tpa.request-received")
    public String requestReceived() {
        return "§aDu hast eine Teleportanfrage von §6%s §aerhalten.";
    }

    @TranslationDefinition("command.tpa.request-received-accept-by")
    public String requestReceivedAcceptBy() {
        return "§aAkzeptiere die Anfrage mithilfe von §6/tpaccept %s§a!";
    }

    @TranslationDefinition("command.tpa.request-received-deny-by")
    public String requestReceivedDenyBy() {
        return "§cLehne die Anfrage mithilfe von §6/tpdeny %s §cab!";
    }

    @TranslationDefinition("command.tpa.already-sent-request")
    public String alreadySentRequest() {
        return "§cDu hast bereits eine Anfrage an §6%s §cgesendet!";
    }

    @TranslationDefinition("command.tpa.no-request-received")
    public String noRequestReceived() {
        return "§cDu hast von diesem Spieler keine Anfrage erhalten.";
    }

    @TranslationDefinition("command.tpa.request-self")
    public String requestSelf() {
        return "§cBob möchte keine Teleportanfragen erhalten.";
    }

    @TranslationDefinition("command.tpa.wrong-usage")
    public String wrongUsage() {
        return "§cFalsche Nutzung! /tpa <Spielername>";
    }

    @TranslationDefinition("command.tpaccept.wrong-usage")
    public String tpacceptWrongUsage() {
        return "§cFalsche Nutzung! /tpaccept <Spielername>";
    }

    @TranslationDefinition("command.tpdeny.wrong-usage")
    public String tpdenyWrongUsage() {
        return "§cFalsche Nutzung! /tpdeny <Spielername>";
    }

}
