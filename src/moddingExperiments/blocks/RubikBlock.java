package moddingExperiments.blocks;

import moddingExperiments.tileEntities.RubikTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
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
	public Icon getIcon(int par1, int par2) {
		return Block.cloth.getIcon(0, 15);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			world.setBlockMetadataWithNotify(x, y, z, side + 1, 3);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new RubikTE();
	}

}
