package moddingExperiments.client.models;

import moddingExperiments.tileEntities.RubikTE;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class RubikModel extends ModelBase {

	public static final int PIECES_PER_SIDE = 3;
	public static final int PIECES_PER_FACE = PIECES_PER_SIDE * PIECES_PER_SIDE;
	public static final int PIECES_PER_CUBE = PIECES_PER_SIDE * PIECES_PER_SIDE * PIECES_PER_SIDE;
	public static final float BLOCK_SIZE = 16.0F;
	public static final float CENTER = BLOCK_SIZE / 2;
	public static final float PIECE_WIDTH = BLOCK_SIZE / PIECES_PER_SIDE;
	public static final int INT_PIECE_WIDTH = (int) BLOCK_SIZE / PIECES_PER_SIDE;
	public static final float OFFSET = 0.5F * (BLOCK_SIZE - PIECES_PER_SIDE * PIECE_WIDTH);

	ModelRenderer[] pieces = new ModelRenderer[PIECES_PER_CUBE];

	public RubikModel() {
		for (int i = 0; i < pieces.length; i++) {
			ModelRenderer piece = new ModelRenderer(this, 0, 0);			
			// TODO textures
			piece.addBox(OFFSET + PIECE_WIDTH * (i % PIECES_PER_SIDE) - CENTER, OFFSET + PIECE_WIDTH * (i / PIECES_PER_FACE) - CENTER, OFFSET + PIECE_WIDTH * ((i % PIECES_PER_FACE) / PIECES_PER_SIDE) - CENTER, INT_PIECE_WIDTH, INT_PIECE_WIDTH, INT_PIECE_WIDTH);
			piece.setRotationPoint(CENTER, -CENTER, -CENTER);
			pieces[i] = piece;
		}
	}

	private int i = 0;

	public void render(RubikTE rubik, float scale) {
		for (int j = 0; j < pieces.length; j++) {
			pieces[j].rotateAngleX = (float) Math.toRadians(rubik.getAngleX(j));
			pieces[j].rotateAngleY = (float) Math.toRadians(rubik.getAngleY(j));
			pieces[j].rotateAngleZ = (float) Math.toRadians(rubik.getAngleZ(j));
			pieces[j].render(scale);
		}
	}

}
