package moddingExperiments.blocks;

import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.lib.BlockInfo;
import moddingExperiments.lib.ModInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RubikBlock extends BlockContainer {

	public RubikBlock(int id, Material material) {
		super(id, material);
		setHardness(1.25F);
		setResistance(7.0F);
		setStepSound(Block.soundStoneFootstep);
		setUnlocalizedName(BlockInfo.RUBIK_UNLOCALIZED);
		
		//TODO remove this
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister register) {
		blockIcon = register.registerIcon(ModInfo.TEXTURE_FOLDER + ":rubik");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te instanceof RubikTileEntity) {
				// TODO change setMove thing
				if (side == 1) {
					((RubikTileEntity) te).printCube();
					return true;
				} else if (side == 0) {
					((RubikTileEntity) te).clearCube();
					return true;
				}

				if (((RubikTileEntity) te).setMove(side, false)) {
					world.markBlockForUpdate(x, y, z);
				}
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new RubikTileEntity(2);
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		int pps = Math.min(metadata + 2, ConfigurationHandler.MAX_SIZE);
		return new RubikTileEntity(pps);
	}

}
