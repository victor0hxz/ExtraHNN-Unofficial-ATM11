package net.lmor.extrahnn.common.item;

import net.lmor.extrahnn.ExtraHostile;
import net.lmor.extrahnn.api.SettingCardMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingCard extends Item {
    public SettingCard(Properties properties) {
        super(properties.stacksTo(1));
    }

    public void setData(ItemStack item, CompoundTag data) {
        item.set(ExtraHostile.Components.SETTING_CARD, data);
    }

    public CompoundTag getData(ItemStack item) {
        return item.getOrDefault(ExtraHostile.Components.SETTING_CARD, new CompoundTag());
    }

    public void clearDataCard(Player player, InteractionHand hand){
        SettingCard mem = (SettingCard)player.getItemInHand(hand).getItem();
        mem.notifyUser(player, SettingCardMessage.SETTINGS_CLEARED);
        player.getItemInHand(hand).set(ExtraHostile.Components.SETTING_CARD, new CompoundTag());
    }

    public boolean isTag(ItemStack item){
        return !item.getOrDefault(ExtraHostile.Components.SETTING_CARD, new CompoundTag()).equals(new CompoundTag());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() == HitResult.Type.MISS) {
            if (!level.isClientSide()) {
                this.clearDataCard(player, hand);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public void notifyUser(Player player, SettingCardMessage msg) {
        if (!player.level().isClientSide()) {
            ((net.minecraft.server.level.ServerPlayer) player).sendSystemMessage(msg.getTranslate(), true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> consumer, @NotNull TooltipFlag flag) {
        List<Component> list = new java.util.ArrayList<>();
        if (isTag(stack)) {
            list.add(Component.translatable("extrahnn.info.setting_card").withStyle(ChatFormatting.AQUA));
        }
    
        list.forEach(consumer);
}
}

