package io.github.tsubasakm.totemtally.client;

import io.github.tsubasakm.totemtally.TotemTally;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Draws a totem icon with the number of totems you carry next to the hotbar. */
public final class TotemTallyClient implements ClientModInitializer {
    private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(TotemTally.MOD_ID, "totem_counter");
    // Hotbar is 182 px wide; the offhand slot is drawn 29 px beyond its edge
    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int OFFHAND_SLOT_WIDTH = 29;
    private static final int GAP = 6;
    private static final int ICON_SIZE = 16;
    private static final int READY_COLOR = 0xFFFFFFFF;
    private static final int NOT_READY_COLOR = 0xFFFF5555;
    private static final int NOT_READY_SHADE = 0xA0000000;

    private static TotemTallyConfig config;
    // Created on first render: item stacks cannot be made before the item components are bound
    private static ItemStack icon;

    @Override
    public void onInitializeClient() {
        config = TotemTallyConfig.load();
        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR, HUD_ID, TotemTallyClient::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!config.showHud || player == null || player.isSpectator()) {
            return;
        }
        int count = countTotems(player.getInventory());
        if (count == 0 && !config.showWhenEmpty) {
            return;
        }

        boolean right = config.isRightSide();
        // The offhand slot sits on the side opposite the main arm
        boolean offhandOnRight = player.getMainArm() == HumanoidArm.LEFT;
        boolean offhandShown = !player.getOffhandItem().isEmpty();
        int edge = HOTBAR_HALF_WIDTH + GAP;
        if (offhandShown && offhandOnRight == right) {
            edge += OFFHAND_SLOT_WIDTH;
        }
        int centerX = graphics.guiWidth() / 2;
        int x = (right ? centerX + edge : centerX - edge - ICON_SIZE) + config.xOffset;
        int y = graphics.guiHeight() - 19 + config.yOffset;

        if (icon == null) {
            icon = new ItemStack(Items.TOTEM_OF_UNDYING);
        }
        // A totem only saves you from a hand, so show whether one is ready there
        boolean ready = player.getMainHandItem().is(Items.TOTEM_OF_UNDYING)
                || player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);

        graphics.item(icon, x, y);
        graphics.nextStratum();
        if (!ready) {
            graphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, NOT_READY_SHADE);
        }
        // Drawn like a vanilla item count, also for a single totem, so the counter reads the same at a glance
        String text = Integer.toString(count);
        graphics.text(minecraft.font, text, x + ICON_SIZE + 1 - minecraft.font.width(text), y + 9,
                ready ? READY_COLOR : NOT_READY_COLOR, true);
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
