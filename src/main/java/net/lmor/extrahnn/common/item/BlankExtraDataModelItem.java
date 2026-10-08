package net.lmor.extrahnn.common.item;

import dev.shadowsoffire.hostilenetworks.util.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlankExtraDataModelItem extends Item {
    public BlankExtraDataModelItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> consumer, @NotNull TooltipFlag flag) {
        List<Component> list = new java.util.ArrayList<>();
        if (net.minecraft.client.Minecraft.getInstance().hasShiftDown()) {
            list.add(Component.translatable("extrahnn.info.blank_extra_data_model.shift").withStyle(ChatFormatting.AQUA));
        } else {
            list.add(Component.translatable("extrahnn.info.blank_extra_data_model",
                    Color.withColor("hostilenetworks.tier.self_aware", ChatFormatting.GOLD.getColor())).withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(""));
            list.add(Component.translatable("hostilenetworks.info.hold_shift", Color.withColor("hostilenetworks.color_text.shift", ChatFormatting.WHITE.getColor())).withStyle(ChatFormatting.GRAY));
        }
    
        list.forEach(consumer);
}
}
