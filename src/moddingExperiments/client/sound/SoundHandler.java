package moddingExperiments.client.sound;

import moddingExperiments.lib.ModInfo;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundHandler {
	
	public SoundHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onSoundsLoad(SoundLoadEvent event) {
		for (Sounds sound : Sounds.values()) {
			addSound(event, sound);
		}
	}
	
	private void addSound(SoundLoadEvent event, Sounds sound) {
		event.manager.addSound(ModInfo.MODID + ":" + sound.getName() + ".ogg");
	}
}
