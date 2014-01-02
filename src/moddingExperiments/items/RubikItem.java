package moddingExperiments.items;

import java.util.List;

import moddingExperiments.blocks.Blocks;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.lib.ItemInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RubikItem extends Item {

	public RubikItem(int id) {
		super(id);
		setCreativeTab(CreativeTabs.tabMisc);
		setUnlocalizedName(ItemInfo.RUBIK_ITEM_UNLOCALIZED);
		setHasSubtypes(true);
		setMaxStackSize(64);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return ItemInfo.RUBIK_ITEM_UNLOCALIZED + stack.getItemDamage();
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List list) {
		for (int i = 0; i < ConfigurationHandler.MAX_SIZE - 1; i++) {
			list.add(new ItemStack(id, 1, i));
		}
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hiYZ, float hitZ) {
		int id = world.getBlockId(x, y, z);

		if (id == Block.snow.blockID && (world.getBlockMetadata(x, y, z) & 7) < 1) {
			side = 1;
		} else if (id != Block.vine.blockID && id != Block.tallGrass.blockID && id != Block.deadBush.blockID && (Block.blocksList[id] == null || !Block.blocksList[id].isBlockReplaceable(world, x, y, z))) {
			if (side == 0) {
				--y;
			}

			if (side == 1) {
				++y;
			}

			if (side == 2) {
				--z;
			}

			if (side == 3) {
				++z;
			}

			if (side == 4) {
				--x;
			}

			if (side == 5) {
				++x;
			}
		}

		if (stack.stackSize == 0 || !player.canPlayerEdit(x, y, z, side, stack)) {
			return false;
		} else {
			Block block = Blocks.rubik;
			if (world.canPlaceEntityOnSide(block.blockID, x, y, z, false, side, player, stack)) {

				if (world.setBlock(x, y, z, block.blockID, stack.getItemDamage(), 3)) {
					world.playSoundEffect((double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
					stack.stackSize--;
				}

			}
			return true;
		}

	}

}
