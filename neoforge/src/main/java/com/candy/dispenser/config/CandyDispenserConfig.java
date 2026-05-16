package com.candy.dispenser.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CandyDispenserConfig {

    public static final CandyDispenserSettings CONFIG;
    public static final ModConfigSpec SPEC;

    static {
        final Pair<CandyDispenserSettings, ModConfigSpec> specPair =
            new ModConfigSpec.Builder().configure(CandyDispenserSettings::new);
        CONFIG = specPair.getLeft();
        SPEC = specPair.getRight();
    }

    public static class CandyDispenserSettings {

        public final ModConfigSpec.BooleanValue enabled;
        public final ModConfigSpec.BooleanValue boostWhenDamaged;
        public final ModConfigSpec.IntValue healingHungerTarget;
        public final ModConfigSpec.IntValue healingSaturationTarget;
        public final ModConfigSpec.BooleanValue recipeEnabled;

        public CandyDispenserSettings(ModConfigSpec.Builder builder) {
            builder.push("candy_dispenser");

            enabled = builder
                .comment("Enable/disable candy dispenser functionality")
                .define("enabled", true);

            boostWhenDamaged = builder
                .comment("Boost saturation when player takes damage",
                        "Provides emergency healing by boosting saturation to enable natural regeneration")
                .define("boostWhenDamaged", true);

            healingHungerTarget = builder
                .comment("Target hunger level for healing boost (18-20)")
                .defineInRange("healingHungerTarget", 18, 18, 20);

            healingSaturationTarget = builder
                .comment("Target saturation level for healing boost (15-20)")
                .defineInRange("healingSaturationTarget", 18, 15, 20);

            recipeEnabled = builder
                .comment("Enable/disable candy dispenser recipe")
                .define("recipeEnabled", true);

            builder.pop();
        }
    }
}
