package com.candy.dispenser;

import com.candy.dispenser.item.CandyDispenserItem;
import com.candy.dispenser.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CandyDispenserHandler {
    private static final int TICK_RATE = 20;
    private static final int MAX_CANDY_PER_FEEDING = 3;
    private static final long DAMAGE_WINDOW_MS = 30_000L;
    private static final long HEALING_COOLDOWN_MS = 5_000L;
    private static final long PLAYER_DATA_TTL_MS = 300_000L;

    private static int tickCounter = 0;
    private static final Map<UUID, PlayerHealthData> playerHealthData = new HashMap<>();

    private static class PlayerHealthData {
        float lastHealth;
        long lastDamageTime;
        long lastHealingTime;

        PlayerHealthData(float health) {
            this.lastHealth = health;
            this.lastDamageTime = 0;
            this.lastHealingTime = 0;
        }
    }

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(CandyDispenserHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < TICK_RATE) return;
        tickCounter = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            handleCandyDispenser(player);
        }
        cleanupOldData();
    }

    private static void handleCandyDispenser(ServerPlayer player) {
        if (!CandyDispenser.CONFIG.candyDispenser.enabled) return;

        ItemStack dispenserStack = getCandyDispenserStack(player);
        if (dispenserStack == null) return;
        if (!CandyDispenserItem.hasCandy(dispenserStack)) return;

        int currentHunger = player.getFoodData().getFoodLevel();
        float currentSaturation = player.getFoodData().getSaturationLevel();
        float currentHealth = player.getHealth();

        UUID playerId = player.getUUID();
        PlayerHealthData healthData = playerHealthData.computeIfAbsent(playerId, id -> new PlayerHealthData(currentHealth));

        long now = System.currentTimeMillis();
        if (currentHealth < healthData.lastHealth) {
            healthData.lastDamageTime = now;
        }
        healthData.lastHealth = currentHealth;

        long timeSinceDamage = now - healthData.lastDamageTime;
        long timeSinceLastHealing = now - healthData.lastHealingTime;
        boolean recentlyDamaged = healthData.lastDamageTime > 0 && timeSinceDamage <= DAMAGE_WINDOW_MS;
        boolean canHealFromDamage = recentlyDamaged && timeSinceLastHealing >= HEALING_COOLDOWN_MS;

        if (canHealFromDamage && CandyDispenser.CONFIG.candyDispenser.boostWhenDamaged) {
            float minSaturationForHealing = 2.0f;
            if (currentSaturation < minSaturationForHealing) {
                feedPlayer(player, dispenserStack, minSaturationForHealing, true);
                healthData.lastHealingTime = now;
            }
            return;
        }

        if (!recentlyDamaged && currentHunger < 20) {
            feedPlayer(player, dispenserStack, 0f, false);
        }
    }

    private static void feedPlayer(ServerPlayer player, ItemStack dispenserStack, float targetSaturation, boolean damageHealing) {
        int currentHunger = player.getFoodData().getFoodLevel();
        float currentSaturation = player.getFoodData().getSaturationLevel();

        int candyNeeded;
        if (damageHealing) {
            float saturationNeeded = Math.max(0, targetSaturation - currentSaturation);
            candyNeeded = (int) Math.ceil(saturationNeeded / 0.8f);
        } else {
            candyNeeded = 20 - currentHunger;
        }
        candyNeeded = Math.min(candyNeeded, MAX_CANDY_PER_FEEDING);
        if (candyNeeded <= 0) return;

        int candyUsed = CandyDispenserItem.removeCandy(dispenserStack, candyNeeded);
        if (candyUsed <= 0) return;

        int newHunger = Math.min(20, currentHunger + candyUsed);
        player.getFoodData().setFoodLevel(newHunger);
        if (damageHealing) {
            float newSaturation = Math.min(20.0f, currentSaturation + (candyUsed * 0.8f));
            player.getFoodData().setSaturation(newSaturation);
        }
    }

    private static ItemStack getCandyDispenserStack(ServerPlayer player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.CANDY_DISPENSER) {
                return stack;
            }
        }
        return null;
    }

    private static void cleanupOldData() {
        long now = System.currentTimeMillis();
        playerHealthData.entrySet().removeIf(entry -> now - entry.getValue().lastDamageTime > PLAYER_DATA_TTL_MS);
    }
}
