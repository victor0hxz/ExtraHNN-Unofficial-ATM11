package net.lmor.extrahnn.api;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface IRegTile {
    EnergyHandler getEnergy();

    ResourceHandler<ItemResource> getInventory();
}
