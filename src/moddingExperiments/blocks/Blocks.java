package moddingExperiments.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import moddingExperiments.lib.BlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.Configuration;

public class Blocks {
	
	public static Block rubik;

	public static void init() {
		rubik = (new Block(BlockInfo.RUBIK_ID, Material.rock)).setHardness(1.25F).setResistance(7.0F).setStepSound(Block.soundStoneFootstep).setUnlocalizedName(BlockInfo.RUBIK_UNLOCALIZED).setCreativeTab(CreativeTabs.tabMisc);
		GameRegistry.registerBlock(rubik, BlockInfo.RUBIK_UNLOCALIZED);
	}

	public static void registerNames() {
		LanguageRegistry.addName(rubik, BlockInfo.RUBIK_NAME);
	}

	public static void registerRecipies() {

	}
	
	public static void registerTileEntities() {

	}
	
	public static void readIdFromConfig(Configuration config) {
		BlockInfo.RUBIK_ID = config.getBlock(BlockInfo.RUBIK_KEY, BlockInfo.RUBIK_DEFAULT).getInt();
	}

}
