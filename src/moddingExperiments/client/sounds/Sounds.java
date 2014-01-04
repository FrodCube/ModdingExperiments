package moddingExperiments.client.sounds;

import moddingExperiments.lib.ModInfo;
import net.minecraft.client.Minecraft;

public enum Sounds {
	//TODO win sound? scrambling sound? solving sound?
	CUBE_0("cube0"),
	CUBE_1("cube1"),
	CUBE_2("cube2"),
	CUBE_3("cube3"),
	CUBE_4("cube4");	
	
	private String name;
	
	private Sounds(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void play(double x, double y, double z, float volume, float pitch) {
		Minecraft.getMinecraft().sndManager.playSound(ModInfo.MODID + ":" + this.name, (float)x, (float)y, (float)z, volume, pitch);
	}
	
	public void playMultiSound(double x, double y, double z, float volume, float pitch) {
		Minecraft.getMinecraft().sndManager.playSound(ModInfo.MODID + ":" + this.name.substring(0, name.length() - 1), (float)x, (float)y, (float)z, volume, pitch);
		System.out.println(this.name.substring(0, name.length() - 1));
	}	
}
