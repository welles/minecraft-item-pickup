package dev.welles.itempickup;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ItemPickupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ItemPickup.init(FabricLoader.getInstance().getConfigDir());
    }
}
//?}
