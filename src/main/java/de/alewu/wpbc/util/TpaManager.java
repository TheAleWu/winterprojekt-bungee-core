package de.alewu.wpbc.util;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;
import static de.alewu.wpbc.util.StaticMethodCollection.tc;

import de.alewu.coreapi.db.caching.CacheRegistry;
import de.alewu.coreapi.exception.CachingException;
import de.alewu.coreapi.ml.Iso2CountryCode;
import de.alewu.coreapi.ml.LanguageContext;
import de.alewu.wpbc.WinterProjectBungeeCore;
import de.alewu.wpbc.ml.MlGeneral;
import de.alewu.wpbc.ml.cmds.MlCommandTpa;
import de.alewu.wpc.repository.cache.UserCache;
import de.alewu.wpc.repository.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TpaManager {

    private static final UserCache USER_CACHE = CacheRegistry.getCache(UserCache.class)
        .orElseThrow(() -> new CachingException("userCache not registered"));
    private static final List<TpaRequest> REQUESTS = new ArrayList<>();

    public static void createRequest(String requestorName, String requestedName) {
        ProxiedPlayer requestor = ProxyServer.getInstance().getPlayer(requestorName);
        if (requestor == null) {
            return;
        }
        User requestorUser = USER_CACHE.findById(requestor.getUniqueId()).orElse(null);
        if (requestorUser == null) {
            requestor.sendMessage(tc(Constants.PREFIX + String.format(MlGeneral.get("de").noCacheObjectFound(), User.class.getSimpleName())));
            return;
        }
        MlCommandTpa mlRequestor = MlCommandTpa.get(requestorUser.getLanguage().toString());
        if (requestorName.equalsIgnoreCase(requestedName)) {
            requestor.sendMessage(tc(Constants.PREFIX + mlRequestor.requestSelf()));
            return;
        }
        ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(requestedName);
        if (requested == null) {
            requestor.sendMessage(tc(Constants.PREFIX + String.format(mlRequestor.notOnline(), requestedName)));
            return;
        }
        User requestedUser = USER_CACHE.findById(requested.getUniqueId()).orElse(null);
        if (requestedUser == null) {
            requestor.sendMessage(tc(Constants.PREFIX + String.format(mlRequestor.notOnline(), requested.getName())));
            return;
        }
        if (getRequest(requestor.getUniqueId(), requested.getUniqueId()).isPresent()) {
            requestor.sendMessage(tc(Constants.PREFIX + String.format(mlRequestor.alreadySentRequest(), requested.getName())));
            return;
        }
        requestor.sendMessage(tc(Constants.PREFIX + String.format(mlRequestor.requestSent(), requested.getName())));

        MlCommandTpa mlRequested = MlCommandTpa.get(requestedUser.getLanguage().toString());
        requested.sendMessage(tc(Constants.PREFIX + String.format(mlRequested.requestReceived(), requestor.getName())));
        requested.sendMessage(tc(Constants.PREFIX));

        TextComponent acceptMessage = new TextComponent(Constants.PREFIX + String.format(mlRequested.requestReceivedAcceptBy(), requestor.getName()));
        acceptMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpaccept " + requestor.getName()));
        requested.sendMessage(acceptMessage);

        requested.sendMessage(tc(Constants.PREFIX));

        TextComponent denyMessage = new TextComponent(Constants.PREFIX + String.format(mlRequested.requestReceivedDenyBy(), requestor.getName()));
        denyMessage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/tpdeny " + requestor.getName()));
        requested.sendMessage(denyMessage);

        TpaRequest request = new TpaRequest(requestor.getUniqueId(), requested.getUniqueId());
        REQUESTS.add(request);

        ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () -> denyRequest(requestor.getUniqueId(), requested.getUniqueId(), true),
            Constants.TPA_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    public static void acceptRequest(UUID requestorUuid, UUID requestedUuid) {
        ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(requestedUuid);
        if (requested != null) {
            User requestedUser = USER_CACHE.findById(requested.getUniqueId()).orElse(null);
            Iso2CountryCode requestedLang = requestedUser != null ? requestedUser.getLanguage() : new Iso2CountryCode("de");
            MlCommandTpa mlRequested = MlCommandTpa.get(requestedLang.toString());

            Optional<TpaRequest> opt = getRequest(requestorUuid, requestedUuid);
            if (!opt.isPresent()) {
                requested.sendMessage(tc(Constants.PREFIX + mlRequested.noRequestReceived()));
                return;
            }
            TpaRequest request = opt.get();

            ProxiedPlayer requestor = ProxyServer.getInstance().getPlayer(request.getRequestor());
            if (requestor != null) {
                User requestorUser = USER_CACHE.findById(requestor.getUniqueId()).orElse(null);
                Iso2CountryCode requestorLang = requestorUser != null ? requestorUser.getLanguage() : new Iso2CountryCode("de");

                if (!requestor.getServer().getInfo().equals(requested.getServer().getInfo())) {
                    requestor.connect(requested.getServer().getInfo());
                }
                requested.sendMessage(tc(Constants.PREFIX + String.format(mlRequested.youveAccepted(), requestor.getName())));

                MlCommandTpa mlRequestor = MlCommandTpa.get(requestorLang.toString());
                requestor.sendMessage(tc(Constants.PREFIX + String.format(mlRequestor.requestedAccepted(), requested.getName())));

                ProxyServer.getInstance().getScheduler().schedule(WinterProjectBungeeCore.INSTANCE, () -> sendToServer(requested.getServer().getInfo(), "TeleportPlayerToPlayer",
                    requestor.getUniqueId().toString(), requested.getUniqueId().toString()), 1, TimeUnit.SECONDS);
            } else {
                requested.sendMessage(tc(Constants.PREFIX + mlRequested.requestorNotOnlineAnymore()));
                requested.sendMessage(tc(Constants.PREFIX + mlRequested.requestDeleted()));
            }

            REQUESTS.remove(request);
        }
    }

    public static void denyRequest(UUID requestorUuid, UUID requestedUuid, boolean ignoreNoRequestReceived) {
        ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(requestedUuid);
        if (requested != null) {
            User requestedUser = USER_CACHE.findById(requested.getUniqueId()).orElse(null);
            Iso2CountryCode requestedLang = requestedUser != null ? requestedUser.getLanguage() : new Iso2CountryCode("de");
            MlCommandTpa mlRequested = MlCommandTpa.get(requestedLang.toString());

            Optional<TpaRequest> opt = getRequest(requestorUuid, requestedUuid);
            if (!opt.isPresent()) {
                if (!ignoreNoRequestReceived) {
                    requested.sendMessage(tc(Constants.PREFIX + mlRequested.noRequestReceived()));
                }
                return;
            }
            TpaRequest request = opt.get();

            ProxiedPlayer requestor = ProxyServer.getInstance().getPlayer(request.getRequestor());
            if (requestor != null) {
                User requestorUser = USER_CACHE.findById(requestor.getUniqueId()).orElse(null);
                Iso2CountryCode requestorLang = requestorUser != null ? requestorUser.getLanguage() : new Iso2CountryCode("de");

                requested.sendMessage(tc(Constants.PREFIX + String.format(mlRequested.youveDenied(), requestor.getName())));

                MlCommandTpa mlRequestor = MlCommandTpa.get(requestorLang.toString());
                requestor.sendMessage(tc(Constants.PREFIX + String.format(mlRequestor.requestedDenied(), requested.getName())));
            } else {
                requested.sendMessage(tc(Constants.PREFIX + mlRequested.requestorNotOnlineAnymore()));
                requested.sendMessage(tc(Constants.PREFIX + mlRequested.requestDeleted()));
            }

            REQUESTS.remove(request);
        }
    }

    public static void denyRequestByRequestorQuit(TpaRequest request) {
        ProxiedPlayer requested = ProxyServer.getInstance().getPlayer(request.getRequested());
        if (requested != null) {
            User requestedUser = USER_CACHE.findById(requested.getUniqueId()).orElse(null);
            Iso2CountryCode requestedLang = requestedUser != null ? requestedUser.getLanguage() : new Iso2CountryCode("de");
            MlCommandTpa mlRequested = MlCommandTpa.get(requestedLang.toString());

            USER_CACHE.findById(request.getRequestor())
                .ifPresent(requestorUser -> requested.sendMessage(tc(Constants.PREFIX + String.format(mlRequested.requestedRejected(), requestorUser.getUsername()))));
        }

        REQUESTS.remove(request);
    }

    public static List<TpaRequest> getAllRequests(UUID requestor) {
        return REQUESTS.stream().filter(x -> x.requestor.equals(requestor)).collect(Collectors.toList());
    }

    public static List<TpaRequest> getAllRequested(UUID requested) {
        return REQUESTS.stream().filter(x -> x.requested.equals(requested)).collect(Collectors.toList());
    }

    public static Optional<TpaRequest> getRequest(UUID requestor, UUID requested) {
        return getAllRequests(requestor).stream().filter(x -> x.requested.equals(requested)).findFirst();
    }

    public static class TpaRequest {

        private final UUID requestor;
        private final UUID requested;
        private final long requestTimestamp;

        public TpaRequest(UUID requestor, UUID requested) {
            this.requestor = requestor;
            this.requested = requested;
            this.requestTimestamp = System.currentTimeMillis();
        }

        public UUID getRequestor() {
            return requestor;
        }

        public UUID getRequested() {
            return requested;
        }

        public long getRequestTimestamp() {
            return requestTimestamp;
        }
    }

}
