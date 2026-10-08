package net.lmor.extrahnn.compat;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.SlotItemHandler;
public class FilteredSlot extends SlotItemHandler {
 private final InternalItemHandler handler; private final Predicate<ItemStack> filter;
 public FilteredSlot(InternalItemHandler handler,int index,int x,int y,Predicate<ItemStack> filter){super(handler,index,x,y);this.handler=handler;this.filter=filter;}
 @Override public boolean mayPlace(ItemStack stack){return filter.test(stack);}
 @Override public boolean mayPickup(Player player){return !handler.extractItemInternal(getSlotIndex(),1,true).isEmpty();}
}
