package com.candy.dispenser.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CandyDispenserItem extends Item {
    private static final int MAX_CANDY = 512;

    public CandyDispenserItem(Properties properties) {
        // Default DAMAGE to MAX_CANDY so every new stack (crafting output,
        // creative tab, /give) starts empty — candy count is MAX_CANDY - damage.
        super(properties.durability(MAX_CANDY)
            .component(DataComponents.DAMAGE, MAX_CANDY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack dispenserStack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            ItemStack candyStack = findCandyInInventory(player);

            if (candyStack != null && !candyStack.isEmpty()) {
                return handleLoadCandy(player, dispenserStack, candyStack);
            }

            int candyCount = getCandyCount(dispenserStack);
            if (candyCount > 0) {
                player.sendOverlayMessage(
                    Component.literal("Dispenser contains: " + candyCount + " candy").withStyle(ChatFormatting.GREEN));
            } else {
                player.sendOverlayMessage(
                    Component.literal("Dispenser is empty. Put candy in your inventory and right-click to load.").withStyle(ChatFormatting.GRAY));
            }
        }

        return InteractionResult.SUCCESS;
    }

    private ItemStack findCandyInInventory(Player player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.CANDY) {
                return stack;
            }
        }
        return null;
    }

    private InteractionResult handleLoadCandy(Player player, ItemStack dispenserStack, ItemStack candyStack) {
        int actuallyAdded = addCandy(dispenserStack, candyStack.getCount());
        if (actuallyAdded > 0) {
            candyStack.shrink(actuallyAdded);
            player.sendOverlayMessage(
                Component.literal("Added " + actuallyAdded + " candy to dispenser").withStyle(ChatFormatting.GREEN));
            return InteractionResult.SUCCESS;
        }
        player.sendOverlayMessage(
            Component.literal("Dispenser is full!").withStyle(ChatFormatting.RED));
        return InteractionResult.FAIL;
    }

    public static int getCandyCount(ItemStack stack) {
        return MAX_CANDY - stack.getDamageValue();
    }

    public static void setCandyCount(ItemStack stack, int count) {
        count = Math.max(0, Math.min(count, MAX_CANDY));
        stack.setDamageValue(MAX_CANDY - count);
    }

    public static int addCandy(ItemStack stack, int amount) {
        int currentCount = getCandyCount(stack);
        int actuallyAdded = Math.min(amount, MAX_CANDY - currentCount);
        if (actuallyAdded > 0) {
            setCandyCount(stack, currentCount + actuallyAdded);
        }
        return actuallyAdded;
    }

    public static int removeCandy(ItemStack stack, int amount) {
        int currentCount = getCandyCount(stack);
        int actuallyRemoved = Math.min(amount, currentCount);
        if (actuallyRemoved > 0) {
            setCandyCount(stack, currentCount - actuallyRemoved);
        }
        return actuallyRemoved;
    }

    public static boolean hasCandy(ItemStack stack) {
        return getCandyCount(stack) > 0;
    }

    public static int getMaxCandy() {
        return MAX_CANDY;
    }
}
