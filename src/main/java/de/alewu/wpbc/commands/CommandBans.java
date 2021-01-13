package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.helpers.TimeUnit;
import de.alewu.wpc.helpers.TimeZoneHelper;
import de.alewu.wpc.helpers.Timespan;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.PunishmentTemplate;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.enums.PunishmentType;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandBans extends PunishmentCommand {

    private static final int ENTRIES_PER_PAGE = 10;

    public CommandBans() {
        super("bans", "wp.bans");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            if (args.length == 0) {
                sendHelp(pp, MlPunishments.get("de"), 1);
                return;
            }
            User user = userCache.findById(pp.getUniqueId()).orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral mlGeneral = MlGeneral.get(user.getLanguage().toString());
            User targetUser = userCache.findByUsername(args[0]).orElse(null);
            if (targetUser == null) {
                pp.sendMessage(tc(String.format(mlGeneral.noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlPunishments ml = MlPunishments.get(user.getLanguage().toString());
            if (args.length == 1) {
                List<Punishment> punishments = punishmentCache.findAllOfPunished(targetUser.getId());
                if (!punishments.isEmpty()) {
                    int maxPage = punishments.size() / ENTRIES_PER_PAGE;
                    if (punishments.size() % ENTRIES_PER_PAGE != 0) {
                        maxPage++;
                    }
                    final String header = String.format(ml.bansPunishmentInfoHeader(), targetUser.getUsername());
                    pp.sendMessage(tc(Constants.PREFIX + "§8===== " + header + " 1/" + maxPage + " §8====="));
                    for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
                        try {
                            pp.sendMessage(getPunishmentInfoLine(ml, punishments.get(i)));
                        } catch (IndexOutOfBoundsException ex) {
                            break;
                        }
                    }
                    pp.sendMessage(tc(Constants.PREFIX + "§8===== " + header + " 1/" + maxPage + " §8====="));
                } else {
                    pp.sendMessage(tc(Constants.PREFIX + ml.bansNoPunishments()));
                }
            } else if (args.length == 2) {
                List<Punishment> punishments = punishmentCache.findAllOfPunished(targetUser.getId());
                if (!punishments.isEmpty()) {
                    int maxPage = punishments.size() / ENTRIES_PER_PAGE;
                    if (punishments.size() % ENTRIES_PER_PAGE != 0) {
                        maxPage++;
                    }
                    try {
                        int page = Integer.parseInt(args[1]);
                        if (maxPage < page) {
                            page = maxPage;
                        }
                        final String header = String.format(ml.bansPunishmentInfoHeader(), targetUser.getUsername());
                        pp.sendMessage(tc(Constants.PREFIX + "§8===== " + header + " " + page + "/" + maxPage + " §8====="));
                        for (int i = ENTRIES_PER_PAGE * (page - 1); i < ENTRIES_PER_PAGE * page; i++) {
                            try {
                                pp.sendMessage(getPunishmentInfoLine(ml, punishments.get(i)));
                            } catch (IndexOutOfBoundsException ex) {
                                break;
                            }
                        }
                        pp.sendMessage(tc(Constants.PREFIX + "§8===== " + header + " " + page + "/" + maxPage + " §8====="));
                    } catch (NumberFormatException e) {
                        pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), args[1])));
                    }
                } else {
                    pp.sendMessage(tc(Constants.PREFIX + ml.bansNoPunishments()));
                }
            } else {
                sendHelp(pp, ml, 1);
            }
        }
    }

    private TextComponent getPunishmentInfoLine(MlPunishments ml, Punishment p) {
        Timespan punishmentDuration = new Timespan(p.getPunishmentDuration());
        String duration = "";
        if (p.getType() == PunishmentType.MUTE || p.getType() == PunishmentType.BAN) {
            if (punishmentDuration.getTimeUnit() != TimeUnit.PERMANENT) {
                duration = " §8[§7" + punishmentDuration.toString(false) + "§8]";
            } else {
                duration = " §8[" + ml.durationPermanent() + "§8]";
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        ZonedDateTime timestamp = TimeZoneHelper.fromEpochSecond(p.getPunishmentTimestamp());
        TextComponent tc = new TextComponent(Constants.PREFIX + "§e" + formatter.format(timestamp) + " §6§l" + p.getType().name() + " §8| §7"
            + p.getReason().replaceAll("_", " ") + duration);
        TextComponent hoverText = null;
        if (p.getPunisherUuid() != null) {
            User punisher = userCache.findById(p.getPunisherUuid()).orElse(null);
            if (punisher != null) {
                hoverText = getOrDefault(null, new TextComponent());
                hoverText.addExtra(String.format(ml.bansPunishmentInfoPunisher(), punisher.getUsername()));
            }
        }
        if (p.getTemplateId() != null) {
            PunishmentTemplate template = punishmentTemplateCache.findById(p.getTemplateId()).orElse(null);
            if (template != null) {
                hoverText = getOrDefault(hoverText, new TextComponent());
                hoverText.addExtra((!hoverText.getExtra().isEmpty() ? "\n" : "") +
                    String.format(ml.bansPunishmentInfoTemplate(), template.getTemplateName()));
            }
        }
        if (hoverText != null) {
            tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent[]{hoverText}));
        }
        return tc;
    }

    @Override
    void sendHelp(CommandSender pp, MlPunishments ml, int page) {
        pp.sendMessage(tc(Constants.PREFIX + ml.bansWrongUsage()));
    }

    private <T> T getOrDefault(T obj, T defaultVal) {
        return obj != null ? obj : defaultVal;
    }
}
