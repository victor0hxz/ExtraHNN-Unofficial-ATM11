package net.lmor.extrahnn;

import net.lmor.extrahnn.api.ISettingCard;
import net.lmor.extrahnn.api.SettingCardMessage;
import net.lmor.extrahnn.client.ExtraDataModelItemStackRenderer;
import net.lmor.extrahnn.client.screen.MergerCameraScreen;
import net.lmor.extrahnn.client.screen.SimulationModelingScreen;
import net.lmor.extrahnn.client.screen.UltimateLootFabScreen;
import net.lmor.extrahnn.client.screen.UltimateSimChamberScreen;
import net.lmor.extrahnn.common.item.SettingCard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(value = Dist.CLIENT, modid = ExtraHostileNetworks.MOD_ID)
public class ExtraHostileClient {
    public ExtraHostileClient() {
    }

    @SubscribeEvent
    public static void itemModels(net.neoforged.neoforge.client.event.RegisterItemModelsEvent e) {
        e.register(ExtraHostileNetworks.local("extra_data_model"), ExtraDataModelItemStackRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent e) {
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V1, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V2, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V3, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_SIM_CHAMBER_V4, UltimateSimChamberScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V1, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V2, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V3, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.ULTIMATE_LOOT_FABRICATOR_V4, UltimateLootFabScreen::new);
        e.register(ExtraHostile.Containers.MERGER_CAMERA, MergerCameraScreen::new);
        e.register(ExtraHostile.Containers.SIMULATOR_MODELING, SimulationModelingScreen::new);
    }
}