package de.alewu.wpbc.util;

import static de.alewu.wpbc.util.StaticMethodCollection.sendToServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class PlayerCapabilities {

    private static final Map<UUID, Capabilities> CAPABILITIES = new HashMap<>();

    public static void send(ProxiedPlayer pp) {
        Capabilities capabilities = CAPABILITIES.getOrDefault(pp.getUniqueId(), null);
        Server server = pp.getServer();
        if (server != null) {
            if (capabilities != null) {
                sendToServer(server.getInfo(), "PlayerCapabilities",
                    pp.getUniqueId().toString(),
                    String.valueOf(capabilities.getHealth()),
                    String.valueOf(capabilities.getMaxHealth()),
                    capabilities.getPotions(),
                    String.valueOf(capabilities.getLevel()),
                    String.valueOf(capabilities.getXp()),
                    String.valueOf(capabilities.getFoodLevel()),
                    capabilities.getGameMode());
            } else {
                sendToServer(server.getInfo(), "NoPlayerCapabilitiesSaved", pp.getUniqueId().toString());
            }
        }
    }

    public static void receive(Object... data) {
        if (data.length == 8) {
            UUID uuid = (UUID) data[0];
            Capabilities capabilities = CAPABILITIES.computeIfAbsent(uuid, x -> new Capabilities());
            capabilities.setHealth((Float) data[1]);
            capabilities.setMaxHealth((Float) data[2]);
            capabilities.setPotions((String) data[3]);
            capabilities.setLevel((Integer) data[4]);
            capabilities.setXp((Float) data[5]);
            capabilities.setFoodLevel((Integer) data[6]);
            capabilities.setGameMode((String) data[7]);
        }
    }

    public static Capabilities getCapabilities(UUID uuid) {
        return CAPABILITIES.get(uuid);
    }

    public static class Capabilities {

        private float health;
        private float maxHealth;
        private List<PotionEffect> potions;
        private int level;
        private float xp;
        private int foodLevel;
        private String gameMode;

        public void setHealth(float health) {
            this.health = health;
        }

        public float getHealth() {
            return health;
        }

        public void setMaxHealth(float maxHealth) {
            this.maxHealth = maxHealth;
        }

        public float getMaxHealth() {
            return maxHealth;
        }

        public void setPotions(String potions) {
            List<PotionEffect> l = new ArrayList<>();
            for (String s : potions.split(";")) {
                String[] args = s.split("#");
                if (args.length == 3) {
                    String id = args[0];
                    int amplifier = Integer.parseInt(args[1]);
                    int duration = Integer.parseInt(args[2]);
                    l.add(new PotionEffect(id, amplifier, duration));
                }
            }
            this.potions = l;
        }

        public String getPotions() {
            return potions.stream().map(PotionEffect::toString).collect(Collectors.joining(";"));
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public void setXp(float xp) {
            this.xp = xp;
        }

        public float getXp() {
            return xp;
        }

        public void setFoodLevel(int foodLevel) {
            this.foodLevel = foodLevel;
        }

        public int getFoodLevel() {
            return foodLevel;
        }

        public void setGameMode(String gameMode) {
            this.gameMode = gameMode;
        }

        public String getGameMode() {
            return gameMode;
        }

        @Override
        public String toString() {
            return "Capabilities{" +
                "health=" + health +
                ", maxHealth=" + maxHealth +
                ", potions=" + potions +
                ", level=" + level +
                ", xp=" + xp +
                ", foodLevel=" + foodLevel +
                ", gameMode='" + gameMode + '\'' +
                '}';
        }
    }

    private static class PotionEffect {

        private final String potionId;
        private final int amplifier;
        private final int duration;

        public PotionEffect(String potionId, int amplifier, int duration) {
            this.potionId = potionId;
            this.amplifier = amplifier;
            this.duration = duration;
        }

        public String getPotionId() {
            return potionId;
        }

        public int getDuration() {
            return duration;
        }

        public int getAmplifier() {
            return amplifier;
        }

        @Override
        public String toString() {
            return potionId + "#" + amplifier + "#" + duration;
        }
    }

}
