package moddingExperiments.creativeTabs;

import moddingExperiments.items.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class RubikCreativeTab extends CreativeTabs {

	public RubikCreativeTab(int id, String name) {
		super(id, name);
	}
	
	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(Items.rubikItem.itemID, 1, 1);
	}

}
