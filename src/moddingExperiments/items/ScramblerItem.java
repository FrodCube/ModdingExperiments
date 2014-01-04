package moddingExperiments.items;

import java.util.List;

import moddingExperiments.blocks.Blocks;
import moddingExperiments.creativeTabs.Tabs;
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
		setCreativeTab(Tabs.rubikCreativeTab);
		setUnlocalizedName(ItemInfo.SCRAMBLER_ITEM_UNLOCALIZED);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return ItemInfo.SCRAMBLER_ITEM_UNLOCALIZED + stack.getItemDamage();
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		list.add(new ItemStack(id, 1, 0));
		list.add(new ItemStack(id, 1, 1));
		list.add(new ItemStack(id, 1, 2));
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && world.getBlockId(x, y, z) == Blocks.rubik.blockID) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te instanceof RubikTileEntity) {
				if (stack.getItemDamage() == 0) {
					((RubikTileEntity) te).scramble();
				} else if (stack.getItemDamage() == 1) {
					((RubikTileEntity) te).solve();
				} else if (stack.getItemDamage() == 2) {
					((RubikTileEntity) te).printCube();
				}
			}
		}

		return false;
	}

}
