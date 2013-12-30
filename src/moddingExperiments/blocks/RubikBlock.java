package moddingExperiments.blocks;

import moddingExperiments.lib.ModInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RubikBlock extends BlockContainer {

	public RubikBlock(int id, Material material) {
		super(id, material);
		System.out.println("block created");
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
				if (((RubikTileEntity) te).setMove(side, false)) {
					world.markBlockForUpdate(x, y, z);
				}
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new RubikTileEntity();
	}

}
