package me.hydos.J64.audio;

import javax.swing.JFrame;
import plugin.AudioPlugin;

public class Default implements AudioPlugin {

	public static final String PLUGIN_VERSION = "1";

	private AudioInfo audioInfo;
	private static AudioCode snd;
	private static int dacrate = 0;

	public Default() {
		snd = new AudioCode();
	}

	public void aiDacrateChanged(int systemType) {
		int frequency = 0;
		if (dacrate != audioInfo.aiRegisters[AI_DACRATE_REG]) {
			dacrate = audioInfo.aiRegisters[AI_DACRATE_REG];
			switch (systemType) {
			case SYSTEM_NTSC:
				frequency = 48681812 / (dacrate + 1);
				break;
			case SYSTEM_PAL:
				frequency = 49656530 / (dacrate + 1);
				break;
			case SYSTEM_MPAL:
				frequency = 48628316 / (dacrate + 1);
				break;
			}
			snd.SetFrequency(frequency);
		}
	}

	public void aiLenChanged() {
		snd.AddBuffer(audioInfo.rdram, audioInfo.aiRegisters[AI_DRAM_ADDR_REG] & 0x00FFFFF8,
				audioInfo.aiRegisters[AI_LEN_REG] & 0x3FFF8);
	}

	public int aiReadLength() {
		audioInfo.aiRegisters[AI_LEN_REG] = snd.GetReadStatus();
		return audioInfo.aiRegisters[AI_LEN_REG];
	}

	public void aiUpdate(boolean wait) {
	}

	public void closePlugin() {
		snd.DeInitialize();
	}

	public void pluginAbout(JFrame hParent) {
	}

	public void pluginConfig(JFrame hParent) {
	}

	public void pluginTest(JFrame hParent) {
	}

	public void getPluginInfo(PluginInfo pluginInfo) {
		pluginInfo.memoryBswaped = true;
		pluginInfo.normalMemory = false;
		pluginInfo.name = "Jario64 Audio based on: " + "Azimer's HLE Audio v";
		pluginInfo.name += PLUGIN_VERSION;
		pluginInfo.type = PLUGIN_TYPE_AUDIO;
		pluginInfo.version = 0x0101; // Set this to retain backwards compatibility
	}

	// Note: We call ClosePlugin just in case the audio plugin was already
	// initialized...
	public boolean initiateAudio(AudioInfo audioInfo) {
		closePlugin();
		this.audioInfo = audioInfo;
		HLEMain.dmem = audioInfo.dmem;
		HLEMain.rdram = audioInfo.rdram;
		snd.Initialize(null, audioInfo);
		return true;
	}

	public void processAList() {
		HLEMain.HLEStart();
	}

	public void romClosed() {
//        ChangeABI (0);
		snd.StopAudio();
		dacrate = 0;
		snd.Initialize(null, audioInfo);
	}

}
