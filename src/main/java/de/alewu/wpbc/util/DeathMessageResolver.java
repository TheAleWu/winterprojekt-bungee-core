package de.alewu.wpbc.util;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlPlayerAction;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.User;
import java.util.Random;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DeathMessageResolver {

    private static final float SECRET_CHANCE = 0.02f;
    private final UserCache userCache;
    private final String died;
    private final String damagerName;
    private DeathCause deathCause;

    public DeathMessageResolver(ProxiedPlayer died, String deathCauseName, String damagerName) {
        this.userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        GroupCache groupCache = CacheRegistry.getCache(GroupCache.class).orElseThrow(() -> new CachingException("groupCache not registered"));
        User user = userCache.findById(died.getUniqueId()).orElseThrow(() -> new CachingException("user not found"));
        user.setDeathCount(user.getDeathCount() + 1);
        userCache.save(user);
        Group group = groupCache.findById(user.getGroupId()).orElse(groupCache.getDefaultGroup().orElseThrow(() -> new CachingException("defaultGroup not defined")));
        this.died = (group.getChatPrefix() + user.getUsername());
        this.damagerName = damagerName;
        if (new Random().nextFloat() <= SECRET_CHANCE) {
            deathCause = DeathCause.SECRET;
        } else {
            try {
                deathCause = DeathCause.valueOf(deathCauseName);
            } catch (IllegalArgumentException e) {
                deathCause = DeathCause.CUSTOM;
            }
        }
    }

    public void solo(ProxiedPlayer receiver) {
        User user = userCache.findById(receiver.getUniqueId()).orElse(null);
        String lang = (user != null ? user.getLanguage().toString() : "de");
        MlPlayerAction ml = MlPlayerAction.get(lang);
        switch (deathCause) {
            case CONTACT:
                receiver.sendMessage(tc(String.format(ml.soloDeathByContact(), died)));
                break;
            case ENTITY_ATTACK:
                receiver.sendMessage(tc(String.format(ml.soloDeathByEntityAttack(), died)));
                break;
            case ENTITY_SWEEP_ATTACK:
                receiver.sendMessage(tc(String.format(ml.soloDeathByEntitySweepAttack(), died)));
                break;
            case PROJECTILE:
                receiver.sendMessage(tc(String.format(ml.soloDeathByProjectile(), died)));
                break;
            case SUFFOCATION:
                receiver.sendMessage(tc(String.format(ml.soloDeathBySuffocation(), died)));
                break;
            case FALL:
                receiver.sendMessage(tc(String.format(ml.soloDeathByFall(), died)));
                break;
            case FIRE:
                receiver.sendMessage(tc(String.format(ml.soloDeathByFire(), died)));
                break;
            case FIRE_TICK:
                receiver.sendMessage(tc(String.format(ml.soloDeathByFireTick(), died)));
                break;
            case MELTING:
                receiver.sendMessage(tc(String.format(ml.soloDeathByMelting(), died)));
                break;
            case LAVA:
                receiver.sendMessage(tc(String.format(ml.soloDeathByLava(), died)));
                break;
            case DROWNING:
                receiver.sendMessage(tc(String.format(ml.soloDeathByDrowning(), died)));
                break;
            case BLOCK_EXPLOSION:
                receiver.sendMessage(tc(String.format(ml.soloDeathByBlockExplosion(), died)));
                break;
            case ENTITY_EXPLOSION:
                receiver.sendMessage(tc(String.format(ml.soloDeathByEntityExplosion(), died)));
                break;
            case VOID:
                receiver.sendMessage(tc(String.format(ml.soloDeathByVoid(), died)));
                break;
            case LIGHTNING:
                receiver.sendMessage(tc(String.format(ml.soloDeathByLightning(), died)));
                break;
            case SUICIDE:
                receiver.sendMessage(tc(String.format(ml.soloDeathBySuicide(), died)));
                break;
            case STARVATION:
                receiver.sendMessage(tc(String.format(ml.soloDeathByStarvation(), died)));
                break;
            case POISON:
                receiver.sendMessage(tc(String.format(ml.soloDeathByPoison(), died)));
                break;
            case MAGIC:
                receiver.sendMessage(tc(String.format(ml.soloDeathByMagic(), died)));
                break;
            case WITHER:
                receiver.sendMessage(tc(String.format(ml.soloDeathByWither(), died)));
                break;
            case FALLING_BLOCK:
                receiver.sendMessage(tc(String.format(ml.soloDeathByFallingBlock(), died)));
                break;
            case THORNS:
                receiver.sendMessage(tc(String.format(ml.soloDeathByThorns(), died)));
                break;
            case DRAGON_BREATH:
                receiver.sendMessage(tc(String.format(ml.soloDeathByDragonBreath(), died)));
                break;
            case CUSTOM:
                receiver.sendMessage(tc(String.format(ml.soloDeathByCustom(), died)));
                break;
            case FLY_INTO_WALL:
                receiver.sendMessage(tc(String.format(ml.soloDeathByFlyIntoWall(), died)));
                break;
            case HOT_FLOOR:
                receiver.sendMessage(tc(String.format(ml.soloDeathByHotFloor(), died)));
                break;
            case CRAMMING:
                receiver.sendMessage(tc(String.format(ml.soloDeathByCramming(), died)));
                break;
            case DRYOUT:
                receiver.sendMessage(tc(String.format(ml.soloDeathByDryout(), died)));
                break;
            case SECRET:
                receiver.sendMessage(tc(String.format(ml.soloDeathBySecret(), died)));
                break;
        }
    }

    public void byOther(ProxiedPlayer receiver) {
        User user = userCache.findById(receiver.getUniqueId()).orElse(null);
        String lang = (user != null ? user.getLanguage().toString() : "de");
        MlPlayerAction ml = MlPlayerAction.get(lang);
        switch (deathCause) {
            case CONTACT:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByContact(), died, damagerName)));
                break;
            case ENTITY_ATTACK:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByEntityAttack(), died, damagerName)));
                break;
            case ENTITY_SWEEP_ATTACK:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByEntitySweepAttack(), died, damagerName)));
                break;
            case PROJECTILE:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByProjectile(), died, damagerName)));
                break;
            case SUFFOCATION:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathBySuffocation(), died, damagerName)));
                break;
            case FALL:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByFall(), died, damagerName)));
                break;
            case FIRE:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByFire(), died, damagerName)));
                break;
            case FIRE_TICK:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByFireTick(), died, damagerName)));
                break;
            case MELTING:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByMelting(), died, damagerName)));
                break;
            case LAVA:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByLava(), died, damagerName)));
                break;
            case DROWNING:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByDrowning(), died, damagerName)));
                break;
            case BLOCK_EXPLOSION:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByBlockExplosion(), died, damagerName)));
                break;
            case ENTITY_EXPLOSION:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByEntityExplosion(), died, damagerName)));
                break;
            case VOID:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByVoid(), died, damagerName)));
                break;
            case LIGHTNING:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByLightning(), died, damagerName)));
                break;
            case SUICIDE:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathBySuicide(), died, damagerName)));
                break;
            case STARVATION:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByStarvation(), died, damagerName)));
                break;
            case POISON:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByPoison(), died, damagerName)));
                break;
            case MAGIC:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByMagic(), died, damagerName)));
                break;
            case WITHER:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByWither(), died, damagerName)));
                break;
            case FALLING_BLOCK:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByFallingBlock(), died, damagerName)));
                break;
            case THORNS:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByThorns(), died, damagerName)));
                break;
            case DRAGON_BREATH:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByDragonBreath(), died, damagerName)));
                break;
            case CUSTOM:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByCustom(), died, damagerName)));
                break;
            case FLY_INTO_WALL:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByFlyIntoWall(), died, damagerName)));
                break;
            case HOT_FLOOR:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByHotFloor(), died, damagerName)));
                break;
            case CRAMMING:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByCramming(), died, damagerName)));
                break;
            case DRYOUT:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathByDryout(), died, damagerName)));
                break;
            case SECRET:
                receiver.sendMessage(tc(String.format(ml.byOtherDeathBySecret(), died, damagerName)));
                break;
        }
    }
}
