package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.helpers.TimeUnit;
import de.alewu.wpc.helpers.Timespan;
import de.alewu.wpc.repository.cache.GroupPermissionCache;
import de.alewu.wpc.repository.cache.PunishmentCache;
import de.alewu.wpc.repository.cache.PunishmentTemplateCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.PunishmentTemplate;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.enums.PunishmentType;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public abstract class PunishmentCommand extends Command {

    UserCache userCache;
    PunishmentCache punishmentCache;
    PunishmentTemplateCache punishmentTemplateCache;
    GroupPermissionCache groupPermissionCache;

    public PunishmentCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);
        userCache = CacheRegistry.getCache(UserCache.class).orElseThrow(() -> new CachingException("userCache not registered"));
        punishmentCache = CacheRegistry.getCache(PunishmentCache.class).orElseThrow(() -> new CachingException("punishmentCache not registered"));
        punishmentTemplateCache = CacheRegistry.getCache(PunishmentTemplateCache.class).orElseThrow(() -> new CachingException("punishmentTemplateCache not registered"));
        groupPermissionCache = CacheRegistry.getCache(GroupPermissionCache.class).orElseThrow(() -> new CachingException("groupPermissionCache not registered"));
    }

    abstract void sendHelp(CommandSender pp, MlPunishments ml, int page);

    final boolean hasPermission(int groupId, String permission) {
        return groupPermissionCache.getAllPermissions(groupId).stream().anyMatch(x -> Objects.equals(x.getId().getPermission(), permission));
    }

    final boolean canPunish(User punisher, User punished) {
        return !hasPermission(punished.getGroupId(), "wp.punishment-protection") ||
            (hasPermission(punished.getGroupId(), "wp.punishment-protection") && hasPermission(punisher.getGroupId(), "wp.punishment-protection.bypass"));
    }

    final void sendTemplates(ProxiedPlayer executor, PunishmentType type, UUID target) {
        executor.sendMessage(tc(Constants.PREFIX));
        executor.sendMessage(tc(Constants.PREFIX + "§6Templates:"));
        List<PunishmentTemplate> punishmentTemplates = punishmentTemplateCache.findAll(type);
        if (punishmentTemplates.isEmpty()) {
            executor.sendMessage(tc(Constants.PREFIX + "§cKeine Vorhanden"));
            return;
        }
        List<Punishment> punishments = target != null ? punishmentCache.findAllOfPunished(target) : null;
        for (PunishmentTemplate template : punishmentTemplates) {
            TextComponent infoBase = new TextComponent("\n");
            Timespan[] timespans = template.getType() == PunishmentType.MUTE || template.getType() == PunishmentType.BAN
                ? template.toTimespanArray() : new Timespan[0];
            long currentTemplateStep = punishments != null ? punishments.stream().filter(x -> Objects.equals(x.getTemplateId(), template.getId())).count() : -1;
            for (int i = 0; i < timespans.length; i++) {
                Timespan timespan = timespans[i];
                if (!infoBase.getText().replaceAll("\n", "").isEmpty()) {
                    infoBase.setText(infoBase.getText() + "\n");
                }
                String ts = timespan.getValue() + "" + timespan.getTimeUnit().getCharacter();
                if (timespan.getTimeUnit() == TimeUnit.PERMANENT) {
                    ts = "§cPERMANENT";
                }
                if (i < currentTemplateStep) {
                    if (i + 1 >= timespans.length) {
                        infoBase.setText(infoBase.getText() + "§r§e§l»  §6#" + (i + 1) + " " + type.name() + "§8: §7" + ts);
                    } else {
                        infoBase.setText(infoBase.getText() + "§r§c§l§m»  §4§m#" + (i + 1) + " " + type.name() + "§c§m: §c§m" + ChatColor.stripColor(ts));
                    }
                } else if (i == currentTemplateStep) {
                    infoBase.setText(infoBase.getText() + "§r§e§l»  §6#" + (i + 1) + " " + type.name() + "§8: §7" + ts);
                } else {
                    infoBase.setText(infoBase.getText() + "§r§7§l»  §e#" + (i + 1) + " " + type.name() + "§8: §f" + ts);
                }
            }
            if (target != null && timespans.length > 0) {
                infoBase.setText(infoBase.getText() + "\n\n§cWarnstufe: " + currentTemplateStep);
            }
            if (!infoBase.getText().replaceAll("\n", "").isEmpty()) {
                infoBase.setText(infoBase.getText() + "\n");
            }

            TextComponent tc = new TextComponent(Constants.PREFIX + "§8- §7" + template.getTemplateName());
            tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[]{infoBase}));
            executor.sendMessage(tc);
        }
    }
}
