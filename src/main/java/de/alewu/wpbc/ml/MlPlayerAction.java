package de.alewu.wpbc.ml;

import static de.alewu.wpbc.util.StaticMethodCollection.ml;

import de.alewu.coreapi.annotations.TranslationDefinition;

public class MlPlayerAction {

    public static MlPlayerAction get(String lang) {
        return ml().translate(MlPlayerAction.class, lang);
    }

    @TranslationDefinition("player-action.join")
    public String join() {
        return "§2→ §6%s §7hat den Server betreten.";
    }

    @TranslationDefinition("player-action.quit")
    public String quit() {
        return "§4← §6%s §7hat den Server verlassen.";
    }

    @TranslationDefinition("player-action.stop-spamming")
    public String stopSpamming() {
        return "§cBitte unterlasse es, den Chat mit derselben Nachricht zu füllen.";
    }

    @TranslationDefinition("player-action.death.solo.contact")
    public String soloDeathByContact() {
        return "§c☠ §6%s §7weiß nun, dass Kakteen weh tun.";
    }

    @TranslationDefinition("player-action.death.solo.entity-attack")
    public String soloDeathByEntityAttack() {
        return "§c☠ §6%s §7wurde von einem unbekannten Wesen erledigt.";
    }

    @TranslationDefinition("player-action.death.solo.entity-sweep-attack")
    public String soloDeathByEntitySweepAttack() {
        return "§c☠ §6%s §7wurde von einer Streifattacke eines unbekannten Wesens erledigt.";
    }

    @TranslationDefinition("player-action.death.solo.projectile")
    public String soloDeathByProjectile() {
        return "§c☠ §6%s §7wurde von einem Projektil erledigt.";
    }

    @TranslationDefinition("player-action.death.solo.suffocation")
    public String soloDeathBySuffocation() {
        return "§c☠ §6%s §7hatte sich an etwas verschluckt.";
    }

    @TranslationDefinition("player-action.death.solo.fall")
    public String soloDeathByFall() {
        return "§c☠ §6%s §7ist ausgerutscht.";
    }

    @TranslationDefinition("player-action.death.solo.fire")
    public String soloDeathByFire() {
        return "§c☠ §6%s §7sollte seine Karriere als Pyrotechniker überdenken.";
    }

    @TranslationDefinition("player-action.death.solo.fire-tick")
    public String soloDeathByFireTick() {
        return "§c☠ §6%s §7hat den Anakin Skywalker gemacht.";
    }

    @TranslationDefinition("player-action.death.solo.melting")
    public String soloDeathByMelting() {
        return "§c☠ §6%s §7war es eindeutig zu warm.";
    }

    @TranslationDefinition("player-action.death.solo.lava")
    public String soloDeathByLava() {
        return "§c☠ §6%s §7war es im Lava-Jacuzzi zu warm.";
    }

    @TranslationDefinition("player-action.death.solo.drowning")
    public String soloDeathByDrowning() {
        return "§c☠ §6%s §7hatte seine eigene Lungekapazität überschätzt.";
    }

    @TranslationDefinition("player-action.death.solo.block-explosion")
    public String soloDeathByBlockExplosion() {
        return "§c☠ §6%s §7hatte mit explosiven Blöcken gekuschelt.";
    }

    @TranslationDefinition("player-action.death.solo.entity-explosion")
    public String soloDeathByEntityExplosion() {
        return "§c☠ §6%s §7hatte mit explosiven Wesen gekuschelt.";
    }

    @TranslationDefinition("player-action.death.solo.void")
    public String soloDeathByVoid() {
        return "§c☠ §6%s §7hatte zu tief gegraben und fiel ins Unendliche.";
    }

    @TranslationDefinition("player-action.death.solo.lightning")
    public String soloDeathByLightning() {
        return "§c☠ §6%s §7wurde leider nicht zur Hexe.";
    }

    @TranslationDefinition("player-action.death.solo.suicide")
    public String soloDeathBySuicide() {
        return "§c☠ §6%s§7... R.I.P.";
    }

    @TranslationDefinition("player-action.death.solo.starvation")
    public String soloDeathByStarvation() {
        return "§c☠ §6%s §7hatte zu wenig Lebkuchen mit sich.";
    }

    @TranslationDefinition("player-action.death.solo.poison")
    public String soloDeathByPoison() {
        return "§c☠ §7Der Glühwein von §6%s §7ist wohl schon abgelaufen.";
    }

    @TranslationDefinition("player-action.death.solo.magic")
    public String soloDeathByMagic() {
        return "§c☠ §6%s §7ist wohl kein guter Alchemist.";
    }

    @TranslationDefinition("player-action.death.solo.wither")
    public String soloDeathByWither() {
        return "§c☠ §6%s §7ist nach dem Kampf mit einem Witherwesen verstorben...";
    }

    @TranslationDefinition("player-action.death.solo.falling-block")
    public String soloDeathByFallingBlock() {
        return "§c☠ §6%s §7sollte besser auf fallende Objekte aufpassen.";
    }

    @TranslationDefinition("player-action.death.solo.thorns")
    public String soloDeathByThorns() {
        return "§c☠ §6%s §7ist nun Dornröschen. Nur der Kuss bringt nichts...";
    }

    @TranslationDefinition("player-action.death.solo.dragon-breath")
    public String soloDeathByDragonBreath() {
        return "§c☠ §6%s §7ist beim Sammeln von Drachenatem ausgerutscht.";
    }

    @TranslationDefinition("player-action.death.solo.custom")
    public String soloDeathByCustom() {
        return "§c☠ §6%s §7ist an etwas unerklärlichem verstorben.";
    }

    @TranslationDefinition("player-action.death.solo.fly-into-wall")
    public String soloDeathByFlyIntoWall() {
        return "§c☠ §6%s §7gibt keinen guten Crash Dummy ab.";
    }

    @TranslationDefinition("player-action.death.solo.hot-floor")
    public String soloDeathByHotFloor() {
        return "§c☠ §6%s §7sollte nicht auf heißen Kohlen laufen.";
    }

    @TranslationDefinition("player-action.death.solo.cramming")
    public String soloDeathByCramming() {
        return "§c☠ §6%s §7ist an zu viel Gekuschel erstickt.";
    }

    @TranslationDefinition("player-action.death.solo.dryout")
    public String soloDeathByDryout() {
        return "§c☠ §6%s §7ist ausgetrocknet.";
    }

    @TranslationDefinition("player-action.death.solo.secret")
    public String soloDeathBySecret() {
        return "§c☠ §6%s §7ist auf Schmand ausgerutscht.";
    }



    @TranslationDefinition("player-action.death.by-other.contact")
    public String byOtherDeathByContact() {
        return "§c☠ §6%1$s §7weiß nun dank §6%2$s§7, dass Kakteen weh tun.";
    }

    @TranslationDefinition("player-action.death.by-other.entity-attack")
    public String byOtherDeathByEntityAttack() {
        return "§c☠ §6%1$s §7wurde von §6%2$s§7 erledigt.";
    }

    @TranslationDefinition("player-action.death.by-other.entity-sweep-attack")
    public String byOtherDeathByEntitySweepAttack() {
        return "§c☠ §6%1$s §7wurde von einer Streifattacke von §6%2$s§7 erledigt.";
    }

    @TranslationDefinition("player-action.death.by-other.projectile")
    public String byOtherDeathByProjectile() {
        return "§c☠ §6%1$s §7wurde von einem §6%2$s§7 erschossen.";
    }

    @TranslationDefinition("player-action.death.by-other.suffocation")
    public String byOtherDeathBySuffocation() {
        return "§c☠ §6%1$s §7hatte sich an §6%2$s§7 verschluckt.";
    }

    @TranslationDefinition("player-action.death.by-other.fall")
    public String byOtherDeathByFall() {
        return "§c☠ §6%1$s §7ist wegen §6%2$s§7 ausgerutscht.";
    }

    @TranslationDefinition("player-action.death.by-other.fire")
    public String byOtherDeathByFire() {
        return "§c☠ §7Der Partner von §6%1$s §7 - §6%2$s§7 - sollte seine Karriere als Pyrotechniker überdenken.";
    }

    @TranslationDefinition("player-action.death.by-other.fire-tick")
    public String byOtherDeathByFireTick() {
        return "§c☠ §6%2$s§7 hat §6%1$s §7Anakin Skywalkers Schicksal nahe gebracht.";
    }

    @TranslationDefinition("player-action.death.by-other.melting")
    public String byOtherDeathByMelting() {
        return "§c☠ §6%2$s §7hat §6%1$s §7zum schmelzen gebracht.";
    }

    @TranslationDefinition("player-action.death.by-other.lava")
    public String byOtherDeathByLava() {
        return "§c☠ §6%1$s §7war es im Lava-Jacuzzi von §6%2$s§7 zu warm.";
    }

    @TranslationDefinition("player-action.death.by-other.drowning")
    public String byOtherDeathByDrowning() {
        return "§c☠ §6%1$s §7wurde von §6%2$s§7 zu lange unter Wasser gelockt.";
    }

    @TranslationDefinition("player-action.death.by-other.block-explosion")
    public String byOtherDeathByBlockExplosion() {
        return "§c☠ §6%1$s §7hatte mit den explosiven Blöcken von §6%2$s§7 gekuschelt.";
    }

    @TranslationDefinition("player-action.death.by-other.entity-explosion")
    public String byOtherDeathByEntityExplosion() {
        return "§c☠ §6%1$s §7hatte mit dem explosiven Wesen §6%2$s§7 gekuschelt.";
    }

    @TranslationDefinition("player-action.death.by-other.void")
    public String byOtherDeathByVoid() {
        return "§c☠ §6%1$s §7wurde von §6%2$s§7 das Unendliche gezeigt.";
    }

    @TranslationDefinition("player-action.death.by-other.lightning")
    public String byOtherDeathByLightning() {
        return "§c☠ §6%1$s §7hatte den Zorn von §6%2$s§7 erfahren.";
    }

    @TranslationDefinition("player-action.death.by-other.suicide")
    public String byOtherDeathBySuicide() {
        return "§c☠ §6%2$s§7 hat §6%1$s§7 zum Selbstmord verleitet... R.I.P.";
    }

    @TranslationDefinition("player-action.death.by-other.starvation")
    public String byOtherDeathByStarvation() {
        return "§c☠ §6%2$s §7hatte zu wenig Lebkuchen für §6%2$s§7 mitgenommen.";
    }

    @TranslationDefinition("player-action.death.by-other.poison")
    public String byOtherDeathByPoison() {
        return "§c☠ §6%2$s§7 hatte §6%1$s§7 verdorbenen Glühwein gegeben.";
    }

    @TranslationDefinition("player-action.death.by-other.magic")
    public String byOtherDeathByMagic() {
        return "§c☠ §6%1$s §7hat sich mit dem Alchemisten §6%2$s§7 angelegt.";
    }

    @TranslationDefinition("player-action.death.by-other.wither")
    public String byOtherDeathByWither() {
        return "§c☠ §6%1$s §7ist nach dem Kampf mit dem Witherwesen §6%2$s§7 verstorben...";
    }

    @TranslationDefinition("player-action.death.by-other.falling-block")
    public String byOtherDeathByFallingBlock() {
        return "§c☠ §6%1$s §7sollte besser auf die fallenden Objekte von §6%2$s§7 aufpassen.";
    }

    @TranslationDefinition("player-action.death.by-other.thorns")
    public String byOtherDeathByThorns() {
        return "§c☠ §6%1$s §7wurde von §6%2$s§7 verhext und starb an einem Spindelstich...";
    }

    @TranslationDefinition("player-action.death.by-other.dragon-breath")
    public String byOtherDeathByDragonBreath() {
        return "§c☠ §6%1$s §7sollte beim Aufsammeln des Atems von §6%2$s§7 aufpassen.";
    }

    @TranslationDefinition("player-action.death.by-other.custom")
    public String byOtherDeathByCustom() {
        return "§c☠ §6%1$s §7ist an etwas unerklärlichem durch §6%2$s§7 verstorben.";
    }

    @TranslationDefinition("player-action.death.by-other.fly-into-wall")
    public String byOtherDeathByFlyIntoWall() {
        return "§c☠ §6%1$s §7hat sich den Kopf zu stark an einer Wand wegen §6%2$s§7 geprellt.";
    }

    @TranslationDefinition("player-action.death.by-other.hot-floor")
    public String byOtherDeathByHotFloor() {
        return "§c☠ §6%1$s §7sollte nicht auf den heißen Kohlen von §6%2$s§7 laufen.";
    }

    @TranslationDefinition("player-action.death.by-other.cramming")
    public String byOtherDeathByCramming() {
        return "§c☠ §6%1$s §7hat zu viel mit §6%2$s§7 und den anderen gekuschelt.";
    }

    @TranslationDefinition("player-action.death.by-other.dryout")
    public String byOtherDeathByDryout() {
        return "§c☠ §6%1$s §7ist wegen §6%2$s§7 ausgetrocknet.";
    }

    @TranslationDefinition("player-action.death.by-other.secret")
    public String byOtherDeathBySecret() {
        return "§c☠ §6%1$s §7wurde von §6%2$s§7 mit Schmand erschlagen.";
    }

}
