package me.hydos.J64;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import me.hydos.J64.hardware.*;
import me.hydos.J64.hardware.Cop0.TlbException;
import me.hydos.J64.hardware.Memory.MemoryException;
import me.hydos.J64.debug.Debug;
import me.hydos.J64.savechips.Eeprom;
import me.hydos.J64.savechips.FlashRam;
import me.hydos.J64.savechips.Mempak;
import me.hydos.J64.savechips.Sram;
import me.hydos.J64.util.EmuManager;

public class Main {
	public static void main(String[] args) {
		EmuManager.setup();
	}

}
