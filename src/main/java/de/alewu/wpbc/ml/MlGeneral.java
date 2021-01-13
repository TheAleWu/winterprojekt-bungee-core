package de.alewu.wpbc.ml;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlGeneral {

    public static MlGeneral get(String lang) {
        return ml().translate(MlGeneral.class, lang);
    }

    @TranslationDefinition("general.wrong-usage")
    public String wrongUsage() {
        return "§cFalsche Nutzung! Nutze §6%s §cfür Hilfe.";
    }

    @TranslationDefinition("general.no-cache-object-found")
    public String noCacheObjectFound() {
        return "§cKonnte kein zugehöriges %s Objekt finden.";
    }

    @TranslationDefinition("general.invalid-value")
    public String invalidValue() {
        return "§6%s §cist kein valider Wert!";
    }

    @TranslationDefinition("general.not-online")
    public String notOnline() {
        return "§6%s §cist derzeit nicht online.";
    }

    @TranslationDefinition("general.never-played")
    public String neverPlayed() {
        return "§6%s §cwar noch nie auf dem Server.";
    }

    @TranslationDefinition("general.tab-header")
    public String tabHeader() {
        return "§6Tab-Header\n";
    }

    @TranslationDefinition("general.tab-footer")
    public String tabFooter() {
        return "\n§6Tab-Footer";
    }

    @TranslationDefinition("general.please-set-twitch-username")
    public String pleaseSetTwitchUsername() {
        return "§cFür einen Broadcast musst du deinen Twitch-Usernamen hinterlegen.";
    }

    @TranslationDefinition("general.now-live")
    public String nowLive() {
        return "§5%1s §7ist nun Live auf §dTwitch§7.\n§c➣ §6https://twitch.tv/%2s";
    }

    @TranslationDefinition("general.connecting")
    public String connecting() {
        return "§aDu wirst zum §6%s §aServer verbunden!";
    }

    @TranslationDefinition("general.teleport-to-spawn")
    public String teleportToSpawn() {
        return "§aDu wirst zum Spawn teleportiert!";
    }

    @TranslationDefinition("general.no-server-online")
    public String noServerOnline() {
        return "§cEs sind derzeit keine Server online :(";
    }

    @TranslationDefinition("general.try-again-later")
    public String tryAgainLater() {
        return "§cBitte versuche es in Kürze erneut.";
    }

    @TranslationDefinition("general.stashed-items-available")
    public String stashedItemsAvailable() {
        return "§4Du hast noch §6%s Item(s) §4gestasht!";
    }

    @TranslationDefinition("general.claim-stashed-items-by-using-cmd")
    public String claimStashedItemsByUsingCmd() {
        return "§cHol' die Items mithilfe §6/stash §cab!";
    }

    @TranslationDefinition("general.changed-language-kick")
    public String changedLanguageKick() {
        return "§aDeine Sprache wurde zu §6%s §ageändert.\n§aReconnecte, damit alle Änderungen angenommen werden.";
    }

    @TranslationDefinition("general.language-unknown")
    public String languageUnknown() {
        return "§cDie Sprache mit dem Kürzel §6%s §cwird nicht unterstützt.";
    }

    @TranslationDefinition("general.ping-message")
    public String pingMessage() {
        return "§aDein Ping: §6%sms";
    }

    @TranslationDefinition("general.rank-message")
    public String rankMessage() {
        return "§aDein Rang: §6%s";
    }

    @TranslationDefinition("general.join.welcome")
    public String joinWelcome() {
        return "§6Willkommen zurück %s §3❄";
    }

    @TranslationDefinition("general.join.use-help")
    public String joinUseHelp() {
        return "§3Nutze /help für weitere Hilfe";
    }

    @TranslationDefinition("general.join.have-fun")
    public String joinHaveFun() {
        return "§6Viel Spass beim §b§lWinter Projekt 2020";
    }

    @TranslationDefinition("general.gdpr")
    public String gdpr() {
        return "§4Durch die Nutzung deines GameKeys akzeptierst du unsere Datenschutzerklärung.";
    }

    @TranslationDefinition("general.gdpr-read-here:")
    public String gdprReadHere() {
        return "§6Um die Datenschutzerklärung zu lesen, klicke auf [KLICK HIER]:";
    }

    @TranslationDefinition("general.gdpr-click")
    public String gdprClick() {
        return "§e[KLICK HIER]";
    }

    @TranslationDefinition("general.home-not-found")
    public String homeNotFound() {
        return "§cDas Home §6%s §cwurde nicht gefunden...";
    }

    @TranslationDefinition("general.home-invalid")
    public String homeInvalid() {
        return "§cDas Home §6%s §cist invalide und muss neu gesetzt werden.";
    }

    @TranslationDefinition("general.ontime-usage")
    public String ontimeUsage() {
        return "§6Falsche Nutzung! /ontime [Spieler]";
    }

    @TranslationDefinition("general.ontime-self")
    public String ontimeSelf() {
        return "§bDu hast bisher §e%s §bgespielt.";
    }

    @TranslationDefinition("general.ontime-other")
    public String ontimeOther() {
        return "§e%s §bhat bisher §e%s §bgespielt.";
    }

    @TranslationDefinition("general.deaths-message-single")
    public String deathsMessageSingle() {
        return "§7Du hast derzeit §c%s Tod §7auf deinem Konto.";
    }

    @TranslationDefinition("general.deaths-message-multiple")
    public String deathsMessageMultiple() {
        return "§7Du hast derzeit §c%s Tode §7auf deinem Konto.";
    }

    @TranslationDefinition("general.normal-farm")
    public String normalFarm() {
        return "§aDu wirst zur Farmwelt teleportiert.";
    }

    @TranslationDefinition("general.nether-farm")
    public String netherFarm() {
        return "§aDu wirst zum Nether teleportiert.";
    }

}
