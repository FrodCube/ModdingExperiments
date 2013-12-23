package moddingExperiments.blocks;

import moddingExperiments.tileEntities.RubikTE;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class RubikBlock extends BlockContainer {

	public RubikBlock(int id, Material material) {
		super(id, material);		
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new RubikTE();
	}

}
