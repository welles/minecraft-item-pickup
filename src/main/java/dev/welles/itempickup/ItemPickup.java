package dev.welles.itempickup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class ItemPickup {
    public static final String MOD_ID = "itempickup";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ItemPickup() {}

    public static void init(Path configDir) {
        PickupConfig.init(configDir);
        LOGGER.info("Item Pickup loaded");
    }
}
