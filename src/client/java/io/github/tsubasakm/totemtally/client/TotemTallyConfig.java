package io.github.tsubasakm.totemtally.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.tsubasakm.totemtally.TotemTally;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Client settings, stored in config/totem_tally.json. */
public final class TotemTallyConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(TotemTally.MOD_ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Show the totem counter on the HUD. */
    public boolean showHud = true;
    /** Keep the counter visible when you carry no totems. */
    public boolean showWhenEmpty = false;
    /** Which side of the hotbar the counter sits on: "left" or "right". */
    public String side = "left";
    /** Extra offset in GUI pixels, added after the default position. */
    public int xOffset = 0;
    public int yOffset = 0;

    public boolean isRightSide() {
        return "right".equalsIgnoreCase(side);
    }

    static TotemTallyConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(TotemTally.MOD_ID + ".json");
        TotemTallyConfig config = new TotemTallyConfig();
        if (Files.exists(path)) {
            try {
                TotemTallyConfig loaded = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), TotemTallyConfig.class);
                if (loaded != null) {
                    config = loaded;
                }
            } catch (IOException | RuntimeException e) {
                LOGGER.error("Failed to read {}, using defaults", path, e);
            }
        }
        // Write it back so new options show up in the file
        try {
            Files.writeString(path, GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Failed to write {}", path, e);
        }
        return config;
    }
}
