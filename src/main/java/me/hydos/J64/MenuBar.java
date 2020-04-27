package me.hydos.J64;

import me.hydos.J64.util.EmuManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	/** Creates a new instance of MenuBar */
	public MenuBar() {
		add(makeFileMenu());
		add(makeSettingsMenu());
		add(makeDebugMenu());

		JCheckBoxMenuItem dump = new JCheckBoxMenuItem("Dump Instructions");
		dump.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			N64Cpu.dump_inst = i.isSelected();
		});
		add(dump);
	}

	// Private Methods

	private JMenu makeFileMenu() {
		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");

		JMenuItem loadRom = new JMenuItem("Load ROM");
		loadRom.addActionListener(evt -> {
			Thread thread = new Thread(EmuManager::loadRom);
			thread.start();
		});
		fileMenu.add(loadRom);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(evt -> EmuManager.exit());
		fileMenu.add(exit);

		return fileMenu;
	}

	private JMenu makeSettingsMenu() {
		JMenu settingsMenu = new JMenu();
		settingsMenu.setText("Settings");

		JCheckBoxMenuItem tlb = new JCheckBoxMenuItem("Use TLB");
		tlb.setState(EmuManager.useTlb);
		tlb.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.UseTlb = i.isSelected();
		});
		settingsMenu.add(tlb);

		JCheckBoxMenuItem cache = new JCheckBoxMenuItem("Cache Instructions");
		cache.setState(EmuManager.cacheInstructions);
		cache.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.CacheInstructions = i.isSelected();
		});
		settingsMenu.add(cache);

		JCheckBoxMenuItem audio = new JCheckBoxMenuItem("Enable Audio");
		audio.setState(EmuManager.useAudio);
		audio.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.UseAudio = i.isSelected();
		});
		settingsMenu.add(audio);

		JCheckBoxMenuItem frameLimit = new JCheckBoxMenuItem("Enable Frame Limit");
		frameLimit.setState(EmuManager.frameLimit);
		frameLimit.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.FrameLimit = i.isSelected();
		});
		settingsMenu.add(frameLimit);

		return settingsMenu;
	}

	private JMenu makeDebugMenu() {
		JMenu debugMenu = new JMenu();
		debugMenu.setText("Debug");

		JCheckBoxMenuItem mem = new JCheckBoxMenuItem("Show Unhandled Memory");
		mem.setState(EmuManager.showUnhandledMemory);
		mem.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.ShowUnhandledMemory = i.isSelected();
		});
		debugMenu.add(mem);

		JCheckBoxMenuItem pif = new JCheckBoxMenuItem("Show PIF Ram Errors");
		pif.setState(EmuManager.showPifRamErrors);
		pif.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.ShowPifRamErrors = i.isSelected();
		});
		debugMenu.add(pif);

		JCheckBoxMenuItem tlb = new JCheckBoxMenuItem("Show TLB Misses");
		tlb.setState(EmuManager.GLOBAL_DEBUG);
		tlb.addActionListener(evt -> {
			JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
			EmuManager.GLOBAL_DEBUG = i.isSelected();
		});
		debugMenu.add(tlb);

		return debugMenu;
	}

}
