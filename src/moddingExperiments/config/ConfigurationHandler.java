package moddingExperiments.config;

import java.io.File;

import moddingExperiments.util.LogHelper;
import net.minecraftforge.common.Configuration;

public class ConfigurationHandler {
    public static void init(File file) {
        Configuration config = new Configuration(file);
        
        try {
            config.load();
            
            
        } catch (Exception e) {
            LogHelper.config("There was a problem while loading the config, Please report this to this mod's authors.");
            LogHelper.severe(e);
        } finally {
            if (config.hasChanged()) {
                config.save();
                LogHelper.config("Config saved!");
            }
        }
    }
}