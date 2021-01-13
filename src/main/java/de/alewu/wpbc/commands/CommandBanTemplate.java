package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlPunishments;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.helpers.TimeUnit;
import de.alewu.wpc.helpers.Timespan;
import de.alewu.wpc.repository.entity.Punishment;
import de.alewu.wpc.repository.entity.PunishmentTemplate;
import de.alewu.wpc.repository.entity.User;
import de.alewu.wpc.repository.enums.PunishmentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandBanTemplate extends PunishmentCommand {

    public CommandBanTemplate() {
        super("bantemplate", "wp.bantemplate", "bt");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer) sender;
            User user = userCache.findById(pp.getUniqueId()).orElse(null);
            if (user == null) {
                pp.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            MlGeneral mlGeneral = MlGeneral.get(user.getLanguage().toString());
            MlPunishments ml = MlPunishments.get(user.getLanguage().toString());
            if (args.length == 0) {
                sendHelp(pp, ml, 1);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    sendHelp(pp, ml, 1);
                }
            } else if(args.length == 2) {
                if (args[0].equalsIgnoreCase("help")) {
                    try {
                        sendHelp(pp, ml, Integer.parseInt(args[1]));
                    } catch (NumberFormatException e) {
                        pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), args[1])));
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    try {
                        PunishmentType type = PunishmentType.valueOf(args[1].toUpperCase());
                        List<PunishmentTemplate> templates = punishmentTemplateCache.findAll(type);
                        if (templates.isEmpty()) {
                            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesListIsEmpty(), type.name())));
                            return;
                        }
                        templates.forEach(t -> {
                            TextComponent tc = new TextComponent(Constants.PREFIX + "§8- §6" + t.getTemplateName() + " §8[§7" + t.getTemplateReason() + "§8]");
                            Timespan[] timespans = t.hasPunishmentLevels() ? t.toTimespanArray() : new Timespan[0];
                            if (timespans.length > 0) {
                                List<TextComponent> list = new ArrayList<>();
                                list.add(new TextComponent("\n"));
                                int index = 1;
                                for (Timespan timespan : timespans) {
                                    if (timespan.getTimeUnit() != TimeUnit.PERMANENT) {
                                        list.add(new TextComponent("§6#" + (index++) + " " + type + "§8: §7" +
                                            timespan.getValue() + timespan.getTimeUnit().getCharacter() + "\n"));
                                    } else {
                                        list.add(new TextComponent("§6#" + (index++) + " " + type + "§8: §cPERMANENT\n"));
                                    }
                                }
                                HoverEvent event = new HoverEvent(Action.SHOW_TEXT, list.toArray(new TextComponent[0]));
                                tc.setHoverEvent(event);
                            }
                            pp.sendMessage(tc);
                        });
                    } catch (IllegalArgumentException e) {
                        pp.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), args[1])));
                    }
                } else {
                    sendHelp(pp, ml, 1);
                }
            } else {
                String templateName = args[1];
                String templateDescription = args[2];
                List<Timespan> timespanList = new ArrayList<>();
                if (args.length > 3) {
                    for (int i = 3; i < args.length; i++) {
                        String timespanString = args[i];
                        try {
                            Timespan timespan = new Timespan(timespanString);
                            if (timespan.getValue() <= 0 && timespan.getTimeUnit() != TimeUnit.PERMANENT) {
                                throw new IllegalArgumentException("Timespan <= 0");
                            }
                            timespanList.add(timespan);
                        } catch (Exception e) {
                            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesInvalidTimespan(), timespanString)));
                            return;
                        }
                    }
                }
                Timespan[] timespans = timespanList.toArray(new Timespan[0]);
                if (args[0].equalsIgnoreCase("addkick")) {
                    addTemplate(pp, ml, PunishmentType.KICK, templateName, templateDescription);
                } else if (args[0].equalsIgnoreCase("addmute")) {
                    addTemplate(pp, ml, PunishmentType.MUTE, templateName, templateDescription, timespans);
                } else if (args[0].equalsIgnoreCase("addban")) {
                    addTemplate(pp, ml, PunishmentType.BAN, templateName, templateDescription, timespans);
                } else if (args[0].equalsIgnoreCase("addunmute")) {
                    addTemplate(pp, ml, PunishmentType.UNMUTE, templateName, templateDescription, timespans);
                } else if (args[0].equalsIgnoreCase("addunban")) {
                    addTemplate(pp, ml, PunishmentType.UNBAN, templateName, templateDescription, timespans);
                } else if (args[0].startsWith("removekick")) {
                    final PunishmentType type = PunishmentType.KICK;
                    deleteTemplate(args, pp, ml, type);
                } else if (args[0].startsWith("removemute")) {
                    final PunishmentType type = PunishmentType.MUTE;
                    deleteTemplate(args, pp, ml, type);
                } else if (args[0].startsWith("removeban")) {
                    final PunishmentType type = PunishmentType.BAN;
                    deleteTemplate(args, pp, ml, type);
                } else if (args[0].startsWith("removeunmute")) {
                    final PunishmentType type = PunishmentType.UNMUTE;
                    deleteTemplate(args, pp, ml, type);
                } else if (args[0].startsWith("removeunban")) {
                    final PunishmentType type = PunishmentType.UNBAN;
                    deleteTemplate(args, pp, ml, type);
                } else {
                    sendHelp(pp, ml, 1);
                }
            }
        }
    }

    private void deleteTemplate(String[] args, ProxiedPlayer pp, MlPunishments ml, PunishmentType type) {
        boolean forceDelete = args.length == 3 && args[2].equalsIgnoreCase("-f");
        Optional<PunishmentTemplate> opt = punishmentTemplateCache.find(args[1], type);
        if (!opt.isPresent()) {
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesUnknownTemplate(), args[1], type.name())));
            return;
        }
        PunishmentTemplate template = opt.get();
        if (punishmentCache.wasAnyonePunishedWithTemplate(template.getId()) && !forceDelete) {
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesDeleteAlreadyInUse()));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesDeleteForceRequired()));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesDeleteWillDeleteHistory()));
            return;
        }
        List<Punishment> relatedPunishments = punishmentCache.getCache().stream()
            .filter(x -> Objects.equals(x.getTemplateId(), template.getId()))
            .collect(Collectors.toList());
        relatedPunishments.forEach(punishmentCache::removeFromCache);
        punishmentTemplateCache.removeFromCache(template);
        pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesDeleteSuccessful(), type.name(), args[1])));
    }

    private void addTemplate(ProxiedPlayer pp, MlPunishments ml, PunishmentType type, String templateName, String templateDescription, Timespan... timespans) {
        Optional<PunishmentTemplate> opt = punishmentTemplateCache.find(templateName, type);
        if (opt.isPresent()) {
            pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesNotUnknownTemplate(), templateName, type.name())));
            return;
        }
        PunishmentTemplate template = new PunishmentTemplate();
        template.setType(type);
        template.setTemplateName(templateName);
        template.setTemplateReason(templateDescription);
        template.setPunishmentLevels(timespans);
        punishmentTemplateCache.save(template);
        pp.sendMessage(tc(Constants.PREFIX + String.format(ml.banTemplatesSaveSuccessful(), type.name(), templateName)));
    }

    @Override
    void sendHelp(CommandSender pp, MlPunishments ml, int page) {
        if (page == 2) {
            pp.sendMessage(tc(Constants.PREFIX + "§8--- " + ml.banTemplatesHelpHeader() + " 2/2 §8---"));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt addban <name> <description> <steps>"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpAddBan()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt removeban <name> [-f]"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpRemoveBan()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt addunban <name> <description>"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpAddUnban()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt removeunban <name> [-f]"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpRemoveUnban()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt addunmute <name> <description>"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpAddUnmute()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt removeunmute <name> [-f]"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpRemoveUnmute()));
            pp.sendMessage(tc(Constants.PREFIX + "§8--- " + ml.banTemplatesHelpHeader() + " 2/2 §8---"));
        } else {
            pp.sendMessage(tc(Constants.PREFIX + "§8--- " + ml.banTemplatesHelpHeader() + " 1/2 §8---"));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt help [page]"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpHelp()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt list <kick/mute/ban/unmute/unban>"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpList()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt addkick <name> <description>"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpAddKick()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt removekick <name> [-f]"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpRemoveKick()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt addmute <name> <description> <steps>"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpAddMute()));
            pp.sendMessage(tc(Constants.PREFIX + "§6/bt removemute <name> [-f]"));
            pp.sendMessage(tc(Constants.PREFIX + ml.banTemplatesHelpRemoveMute()));
            pp.sendMessage(tc(Constants.PREFIX + "§8--- " + ml.banTemplatesHelpHeader() + " 1/2 §8---"));
        }
    }
}
