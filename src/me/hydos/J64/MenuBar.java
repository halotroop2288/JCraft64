package me.hydos.J64;

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
		dump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				N64Cpu.dump_inst = i.isSelected();
			}
		});
		add(dump);
	}

	// Private Methods /////////////////////////////////////////////////////////

	private JMenu makeFileMenu() {
		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");

		JMenuItem loadRom = new JMenuItem("Open ROM");
		loadRom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						Main.openRom();
					}
				});
				thread.start();
			}
		});
		fileMenu.add(loadRom);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Main.exit();
			}
		});
		fileMenu.add(exit);

		return fileMenu;
	}

	private JMenu makeSettingsMenu() {
		JMenu settingsMenu = new JMenu();
		settingsMenu.setText("Settings");

		JCheckBoxMenuItem tlb = new JCheckBoxMenuItem("Use TLB");
		tlb.setState(Main.Default_UseTlb);
		tlb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.UseTlb = i.isSelected();
			}
		});
		settingsMenu.add(tlb);

		JCheckBoxMenuItem cache = new JCheckBoxMenuItem("Cache Instructions");
		cache.setState(Main.Default_CacheInstructions);
		cache.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.CacheInstructions = i.isSelected();
			}
		});
		settingsMenu.add(cache);

		JCheckBoxMenuItem audio = new JCheckBoxMenuItem("Enable Audio");
		audio.setState(Main.Default_UseAudio);
		audio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.UseAudio = i.isSelected();
			}
		});
		settingsMenu.add(audio);

		JCheckBoxMenuItem frameLimit = new JCheckBoxMenuItem("Enable Frame Limit");
		frameLimit.setState(Main.Default_FrameLimit);
		frameLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.FrameLimit = i.isSelected();
			}
		});
		settingsMenu.add(frameLimit);

		return settingsMenu;
	}

	private JMenu makeDebugMenu() {
		JMenu debugMenu = new JMenu();
		debugMenu.setText("Debug");

		JCheckBoxMenuItem mem = new JCheckBoxMenuItem("Show Unhandled Memory");
		mem.setState(Main.Default_ShowUnhandledMemory);
		mem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.ShowUnhandledMemory = i.isSelected();
			}
		});
		debugMenu.add(mem);

		JCheckBoxMenuItem pif = new JCheckBoxMenuItem("Show PIF Ram Errors");
		pif.setState(Main.Default_ShowPifRamErrors);
		pif.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.ShowPifRamErrors = i.isSelected();
			}
		});
		debugMenu.add(pif);

		JCheckBoxMenuItem tlb = new JCheckBoxMenuItem("Show TLB Misses");
		tlb.setState(Main.Default_ShowTLBMisses);
		tlb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) evt.getSource();
				Main.ShowTLBMisses = i.isSelected();
			}
		});
		debugMenu.add(tlb);

		return debugMenu;
	}

}
