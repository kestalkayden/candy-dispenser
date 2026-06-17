package com.candy.dispenser.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Gson-backed config for Candy Dispenser (Fabric).
 *
 * <p>Replaces the former Cloth AutoConfig POJO. Cloth Config has no Minecraft 26.2 build, so its
 * in-game screen threw on open. Storage is now a plain {@code config/candy_dispenser.json} written
 * with Gson; the in-game screen is the hand-built
 * {@link com.candy.dispenser.client.CandyDispenserConfigScreen}.
 *
 * <p>The JSON keeps the same nested {@code candyDispenser} object and field names the Cloth POJO
 * used, so server-side reads via {@code CandyDispenser.CONFIG.candyDispenser.*} are unchanged.
 *
 * <p>NeoForge is unaffected: it uses NeoForge's native {@code ModConfigSpec} and its built-in
 * config UI, and shares none of this code.
 */
public class CandyDispenserConfig {

    public CandyDispenserSettings candyDispenser = new CandyDispenserSettings();

    public static class CandyDispenserSettings {
        /** Master on/off for the auto-feeding behaviour. */
        public boolean enabled = true;
        /** Feed to healing levels shortly after the player takes damage. */
        public boolean boostWhenDamaged = true;
        /** Target hunger level when boosting for healing. Range: 18-20. */
        public int healingHungerTarget = 18;
        /** Target saturation level when boosting for healing. Range: 15-20. */
        public int healingSaturationTarget = 18;
        /** Whether the Candy Dispenser crafting recipe is enabled. */
        public boolean recipeEnabled = true;
    }

    // -------------------------------------------------------------------------
    // Static infrastructure
    // -------------------------------------------------------------------------

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH =
            FabricLoader.getInstance().getConfigDir().resolve("candy_dispenser.json");

    /** The live singleton, populated by {@link #load()}. */
    private static CandyDispenserConfig instance;

    // -------------------------------------------------------------------------
    // Load / save
    // -------------------------------------------------------------------------

    /**
     * The live config instance, lazily loaded on first access. Mutate fields directly (the config
     * screen does), then call {@link #save()}.
     */
    public static CandyDispenserConfig get() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    /**
     * Load (or create) {@code config/candy_dispenser.json}. A missing or unreadable file falls back
     * to defaults, which are then written so the file is self-documenting on first run. Always
     * leaves {@link #instance} non-null.
     */
    public static CandyDispenserConfig load() {
        CandyDispenserConfig cfg = new CandyDispenserConfig();
        if (Files.exists(PATH)) {
            try (Reader r = Files.newBufferedReader(PATH)) {
                CandyDispenserConfig loaded = GSON.fromJson(r, CandyDispenserConfig.class);
                if (loaded != null) {
                    cfg = loaded;
                }
            } catch (IOException | RuntimeException e) {
                // Keep defaults; the broken file is overwritten by the save() below.
            }
        }
        if (cfg.candyDispenser == null) {
            cfg.candyDispenser = new CandyDispenserSettings();
        }
        instance = cfg;
        clamp();
        save();
        return instance;
    }

    /** Clamp bounded fields to their documented ranges, then persist to disk. */
    public static void save() {
        if (instance == null) {
            return;
        }
        clamp();
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer w = Files.newBufferedWriter(PATH)) {
                GSON.toJson(instance, w);
            }
        } catch (IOException e) {
            // Non-fatal: the in-memory state is already correct.
        }
    }

    private static void clamp() {
        CandyDispenserSettings s = instance.candyDispenser;
        s.healingHungerTarget = clampInt(s.healingHungerTarget, 18, 20);
        s.healingSaturationTarget = clampInt(s.healingSaturationTarget, 15, 20);
    }

    private static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
