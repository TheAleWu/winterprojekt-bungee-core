package de.alewu.wpbc.commands;

import static de.alewu.wpbc.util.StaticMethodCollection.reloadPermissions;
import static de.alewu.wpbc.util.StaticMethodCollection.sendToServers;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandGroup;
import de.alewu.wpbc.util.Constants;
import de.alewu.wpc.repository.cache.GroupCache;
import de.alewu.wpc.repository.cache.GroupPermissionCache;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.Group;
import de.alewu.wpc.repository.entity.GroupPermission;
import de.alewu.wpc.repository.entity.GroupPermissionPK;
import de.alewu.wpc.repository.entity.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandGroup extends Command {

    private final UserCache userCache;
    private final GroupCache groupCache;
    private final GroupPermissionCache groupPermissionCache;

    public CommandGroup() {
        super("group", "wp.group");
        this.userCache = CacheRegistry.getCache(UserCache.class)
            .orElseThrow(() -> new CachingException("userCache not registered"));
        this.groupCache = CacheRegistry.getCache(GroupCache.class)
            .orElseThrow(() -> new CachingException("groupCache not registered"));
        this.groupPermissionCache = CacheRegistry.getCache(GroupPermissionCache.class)
            .orElseThrow(() -> new CachingException("groupPermissionCache not registered"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            User user = userCache.findById(((ProxiedPlayer) sender).getUniqueId())
                .orElse(null);
            if (user == null) {
                sender.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
                return;
            }
            execute(sender, args, user.getLanguage().toString());
        } else {
            execute(sender, args, "de");
        }
    }

    private void execute(CommandSender sender, String[] args, String lang) {
        MlGeneral mlGeneral = MlGeneral.get(lang);
        MlCommandGroup mlCommandGroup = MlCommandGroup.get(lang);
        if (args.length == 0) {
            wrongUsage(sender, mlGeneral);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                List<Group> groups = new ArrayList<>(groupCache.getCache());
                groups.sort(Comparator.comparing(Group::getTabSortOrder));
                for (Group g : groups) {
                    sender.sendMessage(tc(Constants.PREFIX + "§7" + g.getChatPrefix() + g.getGroupName() +
                        " §8(§6" + g.getTabSortOrder() + "§8) " + (g.isDefaultGroup() ? "§e★" : "")));
                }
            } else {
                wrongUsage(sender, mlGeneral);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("help")) {
                try {
                    int page = Integer.parseInt(args[1]);
                    sendHelp(sender, lang, page);
                } catch (NumberFormatException e) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), args[1])));
                }
            } else if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];
                if (groupCache.findByGroupName(name).isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.createAlreadyExists(), name)));
                    return;
                }
                Group g = Group.newInstance(name);
                groupCache.save(g);
                groupCache.updateToDatabase();
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.createSuccess(), name)));
            } else if (args[0].equalsIgnoreCase("delete")) {
                String name = args[1];
                Optional<Group> opt = groupCache.findByGroupName(name);
                if (!opt.isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.notFound(), name)));
                    return;
                }
                Group g = opt.get();
                if (g.isDefaultGroup()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.cannotDeleteDefault(), name)));
                    return;
                }
                List<User> assignedToGroup = userCache.getCache().stream().filter(u -> Objects.equals(u.getGroupId(), g.getId())).collect(Collectors.toList());
                if (!assignedToGroup.isEmpty()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.cannotDeletePopulated(), name)));
                    return;
                }
                groupCache.removeFromCache(g);
                groupCache.updateToDatabase();
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.deleteSuccess(), name)));
            } else if (args[0].equalsIgnoreCase("default")) {
                String name = args[1];
                Optional<Group> opt = groupCache.findByGroupName(name);
                if (!opt.isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.notFound(), name)));
                    return;
                }
                Group g = opt.get();
                Group defaultGroup = groupCache.getDefaultGroup().orElse(null);
                if (defaultGroup != null) {
                    defaultGroup.setDefaultGroup(false);
                    groupCache.save(defaultGroup);
                }
                g.setDefaultGroup(true);
                groupCache.save(g);
                groupCache.updateToDatabase();
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.defaultSuccess(), name)));
            } else if (args[0].equalsIgnoreCase("perm")) {
                String name = args[1];
                Optional<Group> opt = groupCache.findByGroupName(name);
                if (!opt.isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.notFound(), name)));
                    return;
                }
                Group g = opt.get();
                List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                if (groupPermissions.isEmpty()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.permNoEntries(), name)));
                    return;
                }
                groupPermissions.forEach(gp -> sender.sendMessage(tc(Constants.PREFIX + "§8- §6" + gp.getId().getPermission())));
            } else if (args[0].equalsIgnoreCase("player")) {
                String name = args[1];
                Optional<Group> opt = groupCache.findByGroupName(name);
                if (!opt.isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.notFound(), name)));
                    return;
                }
                Group g = opt.get();
                List<User> assignedToGroup = userCache.getCache().stream().filter(u -> Objects.equals(u.getGroupId(), g.getId())).collect(Collectors.toList());
                if (assignedToGroup.isEmpty()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerNoEntries(), name)));
                    return;
                }
                assignedToGroup.forEach(u -> sender.sendMessage(tc(Constants.PREFIX + "§8- " + g.getChatPrefix() + u.getUsername())));
            } else if (args[0].equalsIgnoreCase("info")) {
                String name = args[1];
                Optional<Group> opt = groupCache.findByGroupName(name);
                if (!opt.isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.notFound(), name)));
                    return;
                }
                Group g = opt.get();
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.infoGroupName(), g.getGroupName())));
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.infoChatPrefix(), g.getChatPrefix() + sender.getName())));
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.infoTabPrefix(), g.getTabPrefix() + sender.getName())));
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.infoSortOrder(), g.getTabSortOrder())));
            } else {
                wrongUsage(sender, mlGeneral);
            }
        } else {
            String name = args[1];
            Optional<Group> opt = groupCache.findByGroupName(name);
            if (!opt.isPresent()) {
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.notFound(), name)));
                return;
            }
            Group g = opt.get();
            String value = args[2];
            if (args[0].equalsIgnoreCase("tabsort")) {
                if (!value.matches("[0-9]{1,4}")) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), value)));
                    return;
                }
                g.setTabSortOrder(value);
                groupCache.save(g);
                groupCache.updateToDatabase();
                List<User> targetUsers = userCache.getCache().stream().filter(u -> Objects.equals(u.getGroupId(), g.getId())).collect(Collectors.toList());
                for (User u : targetUsers) {
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(u.getId());
                    if (targetPlayer != null) {
                        List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                        reloadPermissions(targetPlayer, groupPermissions);
                        sendToServers(Collections.singleton(targetPlayer.getServer().getInfo()), "RefreshPlayer", targetPlayer.getUniqueId().toString());
                    }
                }
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.tabSortSuccess(), value)));
            } else if (args[0].equalsIgnoreCase("permadd")) {
                GroupPermissionPK pk = new GroupPermissionPK();
                pk.setGroupId(g.getId());
                pk.setPermission(value);
                if (groupPermissionCache.findById(pk).isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.permAddAlreadyContains(), value)));
                    return;
                }
                GroupPermission perm = new GroupPermission();
                perm.setId(pk);
                groupPermissionCache.save(perm);
                groupPermissionCache.updateToDatabase();
                List<User> targetUsers = userCache.getCache().stream().filter(u -> Objects.equals(u.getGroupId(), g.getId())).collect(Collectors.toList());
                for (User u : targetUsers) {
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(u.getId());
                    if (targetPlayer != null) {
                        List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                        reloadPermissions(targetPlayer, groupPermissions);
                        sendToServers(Collections.singleton(targetPlayer.getServer().getInfo()), "RefreshPlayer", targetPlayer.getUniqueId().toString());
                    }
                }
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.permAddSuccess(), value)));
            } else if (args[0].equalsIgnoreCase("permrem")) {
                GroupPermissionPK pk = new GroupPermissionPK();
                pk.setGroupId(g.getId());
                pk.setPermission(value);
                Optional<GroupPermission> gpOpt = groupPermissionCache.findById(pk);
                if (!gpOpt.isPresent()) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.permRemNotContains(), value)));
                    return;
                }
                GroupPermission perm = gpOpt.get();
                groupPermissionCache.removeFromCache(perm);
                groupPermissionCache.updateToDatabase();
                List<User> targetUsers = userCache.getCache().stream().filter(u -> Objects.equals(u.getGroupId(), g.getId())).collect(Collectors.toList());
                for (User u : targetUsers) {
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(u.getId());
                    if (targetPlayer != null) {
                        List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                        reloadPermissions(targetPlayer, groupPermissions);
                        sendToServers(Collections.singleton(targetPlayer.getServer().getInfo()), "RefreshPlayer", targetPlayer.getUniqueId().toString());
                    }
                }
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.permRemSuccess(), value)));
            } else if (args[0].equalsIgnoreCase("playeradd")) {
                User target = userCache.findByUsername(value).orElse(null);
                if (target == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerNotFound(), value)));
                    return;
                }
                if (Objects.equals(target.getGroupId(), g.getId())) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerAddAlreadyContains(), target.getUsername())));
                    return;
                }
                target.setGroupId(g.getId());
                userCache.save(target);
                userCache.updateToDatabase();
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target.getId());
                if (targetPlayer != null) {
                    List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                    reloadPermissions(targetPlayer, groupPermissions);
                    sendToServers(Collections.singleton(targetPlayer.getServer().getInfo()), "RefreshPlayer", targetPlayer.getUniqueId().toString());
                }
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerAddSuccess(), target.getUsername())));
            } else if (args[0].equalsIgnoreCase("playerrem")) {
                User target = userCache.findByUsername(value).orElse(null);
                if (target == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerNotFound(), value)));
                    return;
                }
                if (!Objects.equals(target.getGroupId(), g.getId())) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerRemNotContains(), target.getUsername())));
                    return;
                }
                Group defaultGroup = groupCache.getDefaultGroup().orElse(null);
                if (defaultGroup == null) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerRemNoDefaultGroup(), target.getUsername())));
                    return;
                }
                target.setGroupId(defaultGroup.getId());
                userCache.save(target);
                userCache.updateToDatabase();
                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target.getId());
                if (targetPlayer != null) {
                    List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                    reloadPermissions(targetPlayer, groupPermissions);
                    sendToServers(Collections.singleton(targetPlayer.getServer().getInfo()), "RefreshPlayer", targetPlayer.getUniqueId().toString());
                }
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.playerRemSuccess(), target.getUsername())));
            } else if (args[0].equalsIgnoreCase("tabprefix")) {
                StringJoiner thisValue = new StringJoiner(" ");
                for (int i = 2; i < args.length; i++) {
                    thisValue.add(args[i]);
                }
                if (thisValue.length() > 64) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), thisValue)));
                    return;
                }
                g.setTabPrefix(ChatColor.translateAlternateColorCodes('&', thisValue.toString()));
                groupCache.save(g);
                groupCache.updateToDatabase();
                List<User> targetUsers = userCache.getCache().stream().filter(u -> Objects.equals(u.getGroupId(), g.getId())).collect(Collectors.toList());
                for (User u : targetUsers) {
                    ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(u.getId());
                    if (targetPlayer != null) {
                        List<GroupPermission> groupPermissions = groupPermissionCache.getAllPermissions(g.getId());
                        reloadPermissions(targetPlayer, groupPermissions);
                        sendToServers(Collections.singleton(targetPlayer.getServer().getInfo()), "RefreshPlayer", targetPlayer.getUniqueId().toString());
                    }
                }
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.tabPrefixSuccess(), thisValue)));
            } else if (args[0].equalsIgnoreCase("chatprefix")) {
                StringJoiner thisValue = new StringJoiner(" ");
                for (int i = 2; i < args.length; i++) {
                    thisValue.add(args[i]);
                }
                if (thisValue.length() > 64) {
                    sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), thisValue)));
                    return;
                }
                g.setChatPrefix(ChatColor.translateAlternateColorCodes('&', thisValue.toString()));
                groupCache.save(g);
                groupCache.updateToDatabase();
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlCommandGroup.chatPrefixSuccess(), thisValue)));
            } else {
                wrongUsage(sender, mlGeneral);
            }
        }

    }

    private void wrongUsage(CommandSender sender, MlGeneral mlGeneral) {
        sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.wrongUsage(), "/group help 1")));
    }

    private void sendHelp(CommandSender sender, String lang, int page) {
        MlGeneral mlGeneral = MlGeneral.get(lang);
        MlCommandGroup mlCommandGroup = MlCommandGroup.get(lang);
        switch (page) {
            case 1:
                sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGroup.helpHeader() + " 1/3 §8---"));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group help <1|2|3>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpHelp()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group list"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpList()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group create <groupName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpCreate()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group delete <groupName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpDelete()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group default <groupName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpDefault()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group tabsort <groupName> <sortOrder>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpTabSort()));
                sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGroup.helpHeader() + " 1/3 §8---"));
                break;
            case 2:
                sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGroup.helpHeader() + " 2/3 §8---"));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group tabprefix <groupName> <prefix>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpTabPrefix()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group chatprefix <groupName> <prefix>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpChatPrefix()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group perm <groupName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpPerm()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group permadd <groupName> <permission>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpPermAdd()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group permrem <groupName> <permission>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpPermRem()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group player <groupName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpPlayer()));
                sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGroup.helpHeader() + " 2/3 §8---"));
                break;
            case 3:
                sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGroup.helpHeader() + " 3/3 §8---"));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group playeradd <groupName> <playerName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpPlayerAdd()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group playerrem <groupName> <playerName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpPlayerRem()));
                sender.sendMessage(tc(Constants.PREFIX + "§6/group info <groupName>"));
                sender.sendMessage(tc(Constants.PREFIX + mlCommandGroup.helpInfo()));
                sender.sendMessage(tc(Constants.PREFIX + "§8--- " + mlCommandGroup.helpHeader() + " 3/3 §8---"));
                break;
            default:
                sender.sendMessage(tc(Constants.PREFIX + String.format(mlGeneral.invalidValue(), page)));
                break;
        }
    }
}
