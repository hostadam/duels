package me.hostadam.duels.impl.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class Kit {

    private String name;
    private ItemStack[] armorContents, inventoryContents;
    private boolean defaultKit;
}
