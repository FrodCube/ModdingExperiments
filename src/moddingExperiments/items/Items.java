package moddingExperiments.items;

import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.lib.BlockInfo;
import moddingExperiments.lib.ItemInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Items {

	public static Item rubikItem;
	public static Item scramblerItem;

	public static void init() {
		rubikItem = new RubikItem(ItemInfo.RUBIK_ITEM_ID);
		GameRegistry.registerItem(rubikItem, ItemInfo.RUBIK_ITEM_KEY);
		
		scramblerItem = new ScramblerItem(ItemInfo.SCRAMBLER_ITEM_ID);
		GameRegistry.registerItem(scramblerItem, ItemInfo.SCRAMBLER_ITEM_KEY);
	}

	public static void registerNames() {
		for (int i = 0; i < ConfigurationHandler.MAX_SIZE - 1; i++) {
			LanguageRegistry.addName(new ItemStack(rubikItem.itemID, 1, i), (i + 2) + "x" + (i + 2) + " " + ItemInfo.RUBIK_ITEM_NAME);
		}
		
		LanguageRegistry.addName(scramblerItem, ItemInfo.SCRAMBLER_ITEM_NAME);
	}

	public static void registerRecipies() {

	}

	public static void readItemInfoFromConfig(Configuration config) {
		ItemInfo.RUBIK_ITEM_ID = config.getItem(ItemInfo.RUBIK_ITEM_KEY, ItemInfo.RUBIK_ITEM_DEFAULT).getInt() - 256;
		ItemInfo.SCRAMBLER_ITEM_ID = config.getItem(ItemInfo.SCRAMBLER_ITEM_KEY, ItemInfo.SCRAMBLER_ITEM_DEFAULT).getInt() - 256;
	}
}