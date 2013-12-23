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

	ModelRenderer[] pieces = new ModelRenderer[PIECES_PER_CUBE];

	public RubikModel() {
		for (int i = 0; i < pieces.length; i++) {
			ModelRenderer piece = new ModelRenderer(this, 0, 0);
			// TODO textures
			piece.addBox(0.17F + 5.34F * (i % PIECES_PER_SIDE) - CENTER, 0.17F + 5.34F * (i / PIECES_PER_FACE) - CENTER, 0.17F + 5.34F * ((i % PIECES_PER_FACE) / PIECES_PER_SIDE) - CENTER, 5, 5, 5);
			piece.setRotationPoint(CENTER / 2, -CENTER / 2, -CENTER / 2);
			pieces[i] = piece;
		}
	}

	private int i = 0;

	public void render(RubikTE rubik, float scale) {
		for (int j = 0; j < pieces.length; j++) {
			pieces[j].rotateAngleX = rubik.getAngleX(j);
			pieces[j].rotateAngleY = rubik.getAngleY(j);
			pieces[j].rotateAngleZ = rubik.getAngleZ(j);
			pieces[j].render(scale);
		}
	}

}
