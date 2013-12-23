package moddingExperiments.config;

import java.io.File;

import moddingExperiments.blocks.Blocks;
import moddingExperiments.items.Items;
import moddingExperiments.util.LogHelper;
import net.minecraftforge.common.Configuration;

public class ConfigurationHandler {

	public static void init(File file) {
		Configuration config = new Configuration(file);
		
		Blocks.readIdFromConfig(config);
		Items.readIdFromConfig(config);

		config.load();
	}
}