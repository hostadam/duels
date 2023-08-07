package me.hostadam.duels.impl.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class Kit {

    private String name;
    private ItemStack[] armorContents, inventoryContents;
    private boolean defaultKit;
}
