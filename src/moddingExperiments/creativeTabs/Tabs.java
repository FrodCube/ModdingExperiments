package moddingExperiments.creativeTabs;

import moddingExperiments.lib.ModInfo;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Tabs {
	
	public static CreativeTabs rubikCreativeTab;
	
	public static void init() {
		rubikCreativeTab = new RubikCreativeTab(CreativeTabs.getNextID(), ModInfo.TAB_NAME);
		LanguageRegistry.instance().addStringLocalization("itemGroup." + ModInfo.TAB_NAME, "en_US", ModInfo.TAB_NAME);
	}

}
