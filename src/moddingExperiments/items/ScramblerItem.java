package moddingExperiments.items;

import cpw.mods.fml.common.FMLCommonHandler;
import moddingExperiments.blocks.Blocks;
import moddingExperiments.lib.ItemInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ScramblerItem extends Item {

	public ScramblerItem(int id) {
		super(id);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName(ItemInfo.SCRAMBLER_ITEM_UNLOCALIZED);
		setMaxStackSize(1);
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && world.getBlockId(x, y, z) == Blocks.rubik.blockID) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te instanceof RubikTileEntity) {
				((RubikTileEntity) te).scramble();
			}
		}
		
		return false;
	}

}
