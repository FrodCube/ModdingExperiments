package moddingExperiments.blocks;

import moddingExperiments.client.sound.Sounds;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.items.Items;
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
import net.minecraft.util.MathHelper;
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
		if (world.isRemote) {
			return true;
		}

		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID == Items.scramblerItem.itemID) {
			return true;
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof RubikTileEntity) {
			RubikTileEntity rubik = (RubikTileEntity) te;
			int pps = rubik.piecesPerSide;
			double pieceWidth = 1.0 / pps;
			
			int front = ((MathHelper.floor_double((Math.toDegrees(Math.atan2(z + 0.5 - player.posZ, x + 0.5 - player.posX)) - 45) * 4.0 / 360.0)) & 3) + 1;
			front = front == 1 ? 2 : (front == 2 ? 5 : front);

			int axis;
			int slice;
			boolean clockwise = false;
			
			if (side == 0 || side == 1) {
				if (front < 4) {
					axis = 2;
					slice = (int) (hitX / pieceWidth);
					clockwise = (hitZ > 0.5);
				} else {
					axis = 1;
					slice = (int) ((1 - hitZ) / pieceWidth);
					clockwise = (hitX > 0.5);
				}
			} else if (side == front) {
				axis = 0;
				slice = (int) ((1 - hitY) / pieceWidth);
				if (front < 4) {
					clockwise = (hitX < 0.5) != (front % 2 == 0);
				} else {
					clockwise = (hitZ < 0.5) != (front % 2 == 1);
				}
			} else {
				if (front < 4) {
					axis = 1;
					slice = (int) ((1 - hitZ) / pieceWidth);					
				} else {
					axis = 2;
					slice = (int) (hitX / pieceWidth);
				}
				clockwise = (hitY < 0.5) != (side % 2 == 0);
			}

			int move = axis * pps + slice;

			if (rubik.setMove(move, clockwise, player.username)) {
				world.markBlockForUpdate(x, y, z);
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
