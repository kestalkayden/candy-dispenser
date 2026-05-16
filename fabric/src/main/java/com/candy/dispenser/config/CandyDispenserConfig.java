package com.candy.dispenser.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "candy_dispenser")
public class CandyDispenserConfig implements ConfigData {

    @ConfigEntry.Category("candy_dispenser")
    @ConfigEntry.Gui.TransitiveObject
    public CandyDispenserSettings candyDispenser = new CandyDispenserSettings();

    public static class CandyDispenserSettings {

        @ConfigEntry.Gui.Tooltip(count = 1)
        public boolean enabled = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        public boolean boostWhenDamaged = true;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 18, max = 20)
        public int healingHungerTarget = 18;

        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 15, max = 20)
        public int healingSaturationTarget = 18;

        @ConfigEntry.Gui.Tooltip(count = 1)
        public boolean recipeEnabled = true;
    }
}
