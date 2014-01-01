package moddingExperiments.config;

import java.io.File;

import moddingExperiments.blocks.Blocks;
import moddingExperiments.items.Items;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigurationHandler {

	public static int MAX_SIZE;

	public static void init(File file) {
		Configuration config = new Configuration(file);

		config.load();

		int pps = getIntWithComment(config, "Options", "Maximum cube size", "The number of pieces on the side that the biggest cube can have (If the cube is bigger than 6x6 it will look a bit ugly)", 5);
		MAX_SIZE = Math.max(2, Math.min(16, pps));

		Blocks.readBlockInfoFromConfig(config);
		Items.readItemInfoFromConfig(config);

		config.save();
	}

	public static int getIntWithComment(Configuration config, String category, String s, String comment, int defaultValue)
	{
		Property prop = config.get(category, s, defaultValue);
		if (!comment.equalsIgnoreCase("")) {
			prop.comment = comment;
		}
		return prop.getInt();
	}
}