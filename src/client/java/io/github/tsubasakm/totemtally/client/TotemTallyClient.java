package io.github.tsubasakm.totemtally.client;

import io.github.tsubasakm.totemtally.TotemTally;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Draws a totem gauge above the hunger bar, like the armor bar above the hearts.
 * Bright icons are totems in your hands, which are the ones that can save you;
 * dark icons are totems that are only in your inventory.
 */
public final class TotemTallyClient implements ClientModInitializer {
    private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(TotemTally.MOD_ID, "totem_gauge");
    private static final Identifier FULL_SPRITE = Identifier.fromNamespaceAndPath(TotemTally.MOD_ID, "hud/totem_full");
    private static final Identifier EMPTY_SPRITE = Identifier.fromNamespaceAndPath(TotemTally.MOD_ID, "hud/totem_empty");
    private static final int MAX_ICONS = 10;
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 8;
    // The hunger bar ends 91 px right of the screen center; its row starts 39 px above the bottom
    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int ROW_ABOVE_FOOD = 49;
    private static final int ROW_HEIGHT = 10;
    private static final int COUNT_COLOR = 0xFFFFFFFF;

    private static TotemTallyConfig config;

    @Override
    public void onInitializeClient() {
        config = TotemTallyConfig.load();
        HudElementRegistry.attachElementAfter(VanillaHudElements.FOOD_BAR, HUD_ID, TotemTallyClient::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!config.showHud || player == null || player.isSpectator() || player.isCreative()) {
            return;
        }
        int total = countTotems(player.getInventory());
        if (total == 0 && !config.showWhenEmpty) {
            return;
        }
        int held = countHeld(player, InteractionHand.MAIN_HAND) + countHeld(player, InteractionHand.OFF_HAND);
        int icons = Math.max(1, Math.min(total, MAX_ICONS));
        int ready = Math.min(held, icons);

        int right = graphics.guiWidth() / 2 + HOTBAR_HALF_WIDTH + config.xOffset;
        int y = graphics.guiHeight() - ROW_ABOVE_FOOD - extraRows(player) * ROW_HEIGHT + config.yOffset;
        // Filled from the right, like the hunger bar below it
        for (int i = 0; i < icons; i++) {
            int x = right - ICON_SIZE - i * ICON_SPACING;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, i < ready ? FULL_SPRITE : EMPTY_SPRITE, x, y, ICON_SIZE, ICON_SIZE);
        }
        // Past ten icons the gauge is full, so show the total to the left of it
        if (total > MAX_ICONS) {
            String text = Integer.toString(total);
            int left = right - ICON_SIZE - (icons - 1) * ICON_SPACING;
            graphics.text(minecraft.font, text, left - 2 - minecraft.font.width(text), y + 1, COUNT_COLOR, true);
        }
    }

    /** Rows that vanilla adds above the hunger bar: mount hearts beyond the first row, and air bubbles. */
    private static int extraRows(LocalPlayer player) {
        int rows = 0;
        if (player.getVehicle() instanceof LivingEntity mount && mount.showVehicleHealth()) {
            int hearts = Math.min((int) (mount.getMaxHealth() + 0.5F) / 2, 30);
            rows += Math.max(0, (hearts + 9) / 10 - 1);
        }
        if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply()) {
            rows++;
        }
        return rows;
    }

    private static int countHeld(LocalPlayer player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return stack.is(Items.TOTEM_OF_UNDYING) ? stack.getCount() : 0;
    }

    private static int countTotems(Inventory inventory) {
        int count = 0;
        // Covers the main inventory, the hotbar, armor and the offhand
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
