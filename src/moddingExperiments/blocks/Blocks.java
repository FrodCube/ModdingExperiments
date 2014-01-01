package moddingExperiments.blocks;

import moddingExperiments.items.RubikItem;
import moddingExperiments.lib.BlockInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Blocks {
	
	public static Block rubik;

	public static void init() {
		rubik = new RubikBlock(BlockInfo.RUBIK_ID, Material.rock);
		GameRegistry.registerBlock(rubik, BlockInfo.RUBIK_KEY);
	}

	public static void registerNames() {
		LanguageRegistry.addName(rubik, BlockInfo.RUBIK_NAME);
	}

	public static void registerRecipies() {
	}
	
	public static void registerTileEntities() {
		GameRegistry.registerTileEntity(RubikTileEntity.class, BlockInfo.RUBIK_TE);
	}
	
	public static void readBlockInfoFromConfig(Configuration config) {
		BlockInfo.RUBIK_ID = config.getBlock(BlockInfo.RUBIK_KEY, BlockInfo.RUBIK_DEFAULT).getInt();
	}

}
