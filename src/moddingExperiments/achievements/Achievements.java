package moddingExperiments.achievements;

import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.items.Items;
import moddingExperiments.lib.AchievementInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class Achievements {
	
	public static Achievement pocketCube;
	public static Achievement rubikCube;
	public static Achievement revengeCube;
	public static Achievement professorCube;

	public static void init() {
		pocketCube = new Achievement(AchievementInfo.POCKET_ACHIEVEMENT_ID, AchievementInfo.POCKET_ACHIEVEMENT_KEY, -2, 0, new ItemStack(Items.rubikItem, 1, 0), null).registerAchievement();
		rubikCube = new Achievement(AchievementInfo.RUBIK_ACHIEVEMENT_ID, AchievementInfo.RUBIK_ACHIEVEMENT_KEY, 2, 0, new ItemStack(Items.rubikItem, 1, 1), pocketCube).registerAchievement();
		revengeCube = new Achievement(AchievementInfo.REVENGE_ACHIEVEMENT_ID, AchievementInfo.REVENGE_ACHIEVEMENT_KEY, 2, 2, new ItemStack(Items.rubikItem, 1, 2), rubikCube).registerAchievement().setSpecial();
		professorCube = new Achievement(AchievementInfo.PROFESSOR_ACHIEVEMENT_ID, AchievementInfo.PROFESSOR_ACHIEVEMENT_KEY, -2, 2, new ItemStack(Items.rubikItem, 1, 3), revengeCube).registerAchievement().setSpecial();
	}

	public static void registerNames() {
		 LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.POCKET_ACHIEVEMENT_KEY, "en_US", AchievementInfo.POCKET_ACHIEVEMENT_NAME);
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.POCKET_ACHIEVEMENT_KEY + ".desc", "en_US", AchievementInfo.POCKET_ACHIEVEMENT_DESC);
	     
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.RUBIK_ACHIEVEMENT_KEY, "en_US", AchievementInfo.RUBIK_ACHIEVEMENT_NAME);
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.RUBIK_ACHIEVEMENT_KEY + ".desc", "en_US", AchievementInfo.RUBIK_ACHIEVEMENT_DESC);
	     
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.REVENGE_ACHIEVEMENT_KEY, "en_US", AchievementInfo.REVENGE_ACHIEVEMENT_NAME);
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.REVENGE_ACHIEVEMENT_KEY + ".desc", "en_US", AchievementInfo.REVENGE_ACHIEVEMENT_DESC);
	     
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.PROFESSOR_ACHIEVEMENT_KEY, "en_US", AchievementInfo.PROFESSOR_ACHIEVEMENT_NAME);
	     LanguageRegistry.instance().addStringLocalization("achievement." + AchievementInfo.PROFESSOR_ACHIEVEMENT_KEY + ".desc", "en_US", AchievementInfo.PROFESSOR_ACHIEVEMENT_DESC);
	}

	public static void readAchievementInfoFromConfig(Configuration config) {
		AchievementInfo.POCKET_ACHIEVEMENT_ID = ConfigurationHandler.getIntWithComment(config, "Achievements", AchievementInfo.POCKET_ACHIEVEMENT_KEY, "Achievement ID for " + AchievementInfo.POCKET_ACHIEVEMENT_NAME, AchievementInfo.POCKET_ACHIEVEMENT_DEFAULT);
		AchievementInfo.RUBIK_ACHIEVEMENT_ID = ConfigurationHandler.getIntWithComment(config, "Achievements", AchievementInfo.RUBIK_ACHIEVEMENT_KEY, "Achievement ID for " + AchievementInfo.RUBIK_ACHIEVEMENT_NAME, AchievementInfo.RUBIK_ACHIEVEMENT_DEFAULT);
		AchievementInfo.REVENGE_ACHIEVEMENT_ID = ConfigurationHandler.getIntWithComment(config, "Achievements", AchievementInfo.REVENGE_ACHIEVEMENT_KEY, "Achievement ID for " + AchievementInfo.REVENGE_ACHIEVEMENT_NAME, AchievementInfo.REVENGE_ACHIEVEMENT_DEFAULT);
		AchievementInfo.PROFESSOR_ACHIEVEMENT_ID = ConfigurationHandler.getIntWithComment(config, "Achievements", AchievementInfo.PROFESSOR_ACHIEVEMENT_KEY, "Achievement ID for " + AchievementInfo.PROFESSOR_ACHIEVEMENT_NAME, AchievementInfo.PROFESSOR_ACHIEVEMENT_DEFAULT);
	}
}
