package me.hydos.J64.util;

import me.hydos.J64.*;
import me.hydos.J64.debug.Debug;
import me.hydos.J64.hardware.*;
import me.hydos.J64.savechips.Eeprom;
import me.hydos.J64.savechips.FlashRam;
import me.hydos.J64.savechips.Mempak;
import me.hydos.J64.savechips.Sram;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Properties;

public class EmuManager {

    public static boolean GLOBAL_DEBUG = true;
    public static OpcodeBuilder opcodeBuilder;

    private static final boolean DEBUG = true;

    public static final boolean showUnhandledMemory = true;
    public static final boolean showPifRamErrors = true;
    public static final boolean useTlb = true;
    public static final boolean cacheInstructions = false;
    public static final boolean useAudio = true;
    public static final boolean frameLimit = true;

    public static final String AUTO_SAVE_DIR = "./sve/";

    private static final int Default_CountPerOp = 2;

    public static Sram sram;
    public static Mempak mempak;
    public static FlashRam flashRam;
    public static Eeprom eeprom;

    public static Registers regs;
    public static Memory mem;
    public static Pif pif;
    public static DirectMemoryAccess dma;

    public static RegisterSP rsp;
    public static Video video;
    public static Audio audio;

    public static N64Cpu cpu;

    public static boolean ShowUnhandledMemory = showUnhandledMemory;

    public static boolean UseTlb = useTlb;
    public static boolean CacheInstructions = cacheInstructions;

    public static boolean ShowPifRamErrors = showPifRamErrors;

    public static boolean UseAudio = useAudio;
    public static boolean FrameLimit = frameLimit;



    public static void loadRom() {
        File romFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open ROM");
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION)
            romFile = fileChooser.getSelectedFile();

        closeCpu();

        Rom rom = new Rom(romFile);
        if (DEBUG)
            System.out.printf("Opened rom: %s\n", romFile);

        File Directory = new File(AUTO_SAVE_DIR);
        if (!Directory.exists())
            Directory.mkdirs();
        assert romFile != null;
        String romName = romFile.getName().substring(0, romFile.getName().lastIndexOf('.'));
        String path = Directory.getAbsolutePath() + "/" + romName;

        sram = new Sram(path + ".sra");
        mempak = new Mempak(path + ".mpk");
        flashRam = new FlashRam(path + ".fla");
        eeprom = new Eeprom(path + ".eep");

        pif.setSaveChips(eeprom, mempak);
        pif.showPifRamErrors(ShowPifRamErrors);
        dma.setSaveChips(sram, flashRam);
        dma.showUnhandledMemory(ShowUnhandledMemory);

        dma.connect(pif, rsp, rom.data);
        mem.map(cpu.CheckInterrupts, rsp, video, audio, pif, dma, flashRam, regs, rom.data);

        int countPerOp;
        countPerOp = Default_CountPerOp;
        regs.saveUsing = Registers.AUTO;

        Cop0 cop0 = null;
        try {
            cop0 = new Cop0(countPerOp);
        } catch (Cop0.TlbException ex) {
            System.err.print("Failed to allocate Memory\n");
            exit();
        }
        assert cop0 != null;
        cop0.useTlb(UseTlb);
        cop0.setTimerInterrupts(cpu.CheckInterrupts, pif.timerInterrupt, dma.timerInterrupt, video.timerInterrupt);
        video.setTimer(cop0);
        video.setFrameLimit(FrameLimit);
        mem.setTimer(cop0);

        Cop1 cop1 = new Cop1();

        cpu.cacheInstructions(CacheInstructions);
        cpu.connect(cop0, cop1);
        cpu.setOps(opcodeBuilder.buildInterpreterOps(cpu, cop1, cop0));
        // cpu.setOps(opcodeBuilder.buildDebugCpuOps(cpu)); TODO: when you toggle debug bool it changes cpu to debug cpu

        closeCpu();

        initialize(pif, rom, mem, cpu);

        if (video.gfxPlugin != null)
            video.gfxPlugin.romOpen();
        if (pif.inputPlugin != null)
            pif.inputPlugin.romOpen();

        if (!UseAudio && audio.audioPlugin != null) {
            audio.audioPlugin.closePlugin();
            audio.audioPlugin = null;
        }
        cpu.startEmulation();
    }

    private static void initialize(Pif pif, Rom rom, Memory mem, N64Cpu cpu) {
        boolean usePif = pif.loadPifRom(rom.data.get(0x3E));
        int country = rom.data.get(0x3E);
        int cicChip = pif.getCicChipID(rom.data);
        if (cicChip < 0) {
            System.err.printf("Unknown Cic Chip: %d", cicChip);
            cicChip = 2;
        }

        if (usePif) {
            System.out.println("Using PIF");
            cpu.pc = 0xBFC00000;
            pif.initalizeRegisters(cicChip);
        } else {
            if (rom.data != null) {
                rom.data.position(0x040);
                mem.DMEM.position(0x040);
                for (int i = 0; i < 0xFBC; i++)
                    mem.DMEM.put(rom.data.get());
            }
            cpu.pc = 0xA4000040;

            cpu.GPR[0] = 0x00000000;
            cpu.GPR[6] = 0xA4001F0C;
            cpu.GPR[7] = 0xA4001F08;
            cpu.GPR[8] = 0x000000C0;
            cpu.GPR[9] = 0x00000000;
            cpu.GPR[10] = 0x00000040;
            cpu.GPR[11] = 0xA4000040;
            cpu.GPR[16] = 0x00000000;
            cpu.GPR[17] = 0x00000000;
            cpu.GPR[18] = 0x00000000;
            cpu.GPR[19] = 0x00000000;
            cpu.GPR[21] = 0x00000000;
            cpu.GPR[26] = 0x00000000;
            cpu.GPR[27] = 0x00000000;
            cpu.GPR[28] = 0x00000000;
            cpu.GPR[29] = 0xA4001FF0;
            cpu.GPR[30] = 0x00000000;

            switch (country) {
                case 0x44, 0x46, 0x49, 0x50, 0x53, 0x55, 0x58, 0x59 -> {
                    switch (cicChip) {
                        case 2 -> {
                            cpu.GPR[5] = 0xC0F1D859;
                            cpu.GPR[14] = 0x2DE108EA;
                            cpu.GPR[24] = 0x00000000;
                        }
                        case 3 -> {
                            cpu.GPR[5] = 0xD4646273;
                            cpu.GPR[14] = 0x1AF99984;
                            cpu.GPR[24] = 0x00000000;
                        }
                        case 5 -> {
                            mem.DMEM.putInt(0x1004, 0xBDA807FC);
                            cpu.GPR[5] = 0xDECAAAD1;
                            cpu.GPR[14] = 0x0CF85C13;
                            cpu.GPR[24] = 0x00000002;
                        }
                        case 6 -> {
                            cpu.GPR[5] = 0xB04DC903;
                            cpu.GPR[14] = 0x1AF99984;
                            cpu.GPR[24] = 0x00000002;
                        }
                    }
                    cpu.GPR[20] = 0x00000000;
                    cpu.GPR[23] = 0x00000006;
                    cpu.GPR[31] = 0xA4001554;
                }
                default -> {
                    switch (cicChip) {
                        case 2:
                            cpu.GPR[5] = 0xC95973D5;
                            cpu.GPR[14] = 0x2449A366;
                            break;
                        case 3:
                            cpu.GPR[5] = 0x95315A28;
                            cpu.GPR[14] = 0x5BACA1DF;
                            break;
                        case 5:
                            mem.DMEM.putInt(0x1004, 0x8DA807FC);
                            cpu.GPR[5] = 0x5493FB9A;
                            cpu.GPR[14] = 0xC2C20384;
                        case 6:
                            cpu.GPR[5] = 0xE067221F;
                            cpu.GPR[14] = 0x5CD2B70F;
                            break;
                    }
                    cpu.GPR[20] = 0x00000001;
                    cpu.GPR[23] = 0x00000000;
                    cpu.GPR[24] = 0x00000003;
                    cpu.GPR[31] = 0xA4001550;
                }
            }
            switch (cicChip) {
                case 1 -> cpu.GPR[22] = 0x0000003F;
                case 2 -> {
                    cpu.GPR[1] = 0x00000001;
                    cpu.GPR[2] = 0x0EBDA536;
                    cpu.GPR[3] = 0x0EBDA536;
                    cpu.GPR[4] = 0x0000A536;
                    cpu.GPR[12] = 0xED10D0B3;
                    cpu.GPR[13] = 0x1402A4CC;
                    cpu.GPR[15] = 0x3103E121;
                    cpu.GPR[22] = 0x0000003F;
                    cpu.GPR[25] = 0x9DEBB54F;
                }
                case 3 -> {
                    cpu.GPR[1] = 0x00000001;
                    cpu.GPR[2] = 0x49A5EE96;
                    cpu.GPR[3] = 0x49A5EE96;
                    cpu.GPR[4] = 0x0000EE96;
                    cpu.GPR[12] = 0xCE9DFBF7;
                    cpu.GPR[13] = 0xCE9DFBF7;
                    cpu.GPR[15] = 0x18B63D28;
                    cpu.GPR[22] = 0x00000078;
                    cpu.GPR[25] = 0x825B21C9;
                }
                case 5 -> {
                    mem.DMEM.putInt(0x1000, 0x3C0DBFC0);
                    mem.DMEM.putInt(0x1008, 0x25AD07C0);
                    mem.DMEM.putInt(0x100C, 0x31080080);
                    mem.DMEM.putInt(0x1010, 0x5500FFFC);
                    mem.DMEM.putInt(0x1014, 0x3C0DBFC0);
                    mem.DMEM.putInt(0x1018, 0x8DA80024);
                    mem.DMEM.putInt(0x101C, 0x3C0BB000);
                    cpu.GPR[1] = 0x00000000;
                    cpu.GPR[2] = 0xF58B0FBF;
                    cpu.GPR[3] = 0xF58B0FBF;
                    cpu.GPR[4] = 0x00000FBF;
                    cpu.GPR[12] = 0x9651F81E;
                    cpu.GPR[13] = 0x2D42AAC5;
                    cpu.GPR[15] = 0x56584D60;
                    cpu.GPR[22] = 0x00000091;
                    cpu.GPR[25] = 0xCDCE565F;
                }
                case 6 -> {
                    cpu.GPR[1] = 0x00000000;
                    cpu.GPR[2] = 0xA95930A4;
                    cpu.GPR[3] = 0xA95930A4;
                    cpu.GPR[4] = 0x000030A4;
                    cpu.GPR[12] = 0xBCB59510;
                    cpu.GPR[13] = 0xBCB59510;
                    cpu.GPR[15] = 0x7A3C07F4;
                    cpu.GPR[22] = 0x00000085;
                    cpu.GPR[25] = 0x465E3F72;
                }
            }
        }
    }

    public static void exit() {
        closeCpu();
        System.out.println("Stopping...");
        Debug.close();
        System.exit(0);
    }

    private static void closeCpu() {
        if (eeprom != null)
            eeprom.close();
        if (mempak != null)
            mempak.close();
        if (sram != null)
            sram.close();
        if (video.gfxPlugin != null)
            video.gfxPlugin.romClosed();
        if (audio.audioPlugin != null)
            audio.audioPlugin.romClosed();
        if (pif.inputPlugin != null)
            pif.inputPlugin.romClosed();
    }

    public static void setup() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Debug.init();

        int WindowWidth = 640;
        int WindowHeight = 480;

        regs = new Registers();

        try {
            mem = new Memory(0x00800000);
        } catch (Memory.MemoryException ex) {
            System.err.print("Failed to allocate Memory\n");
            exit();
        }

        cpu = new N64Cpu(regs, mem);

        rsp = new RegisterSP(cpu.CheckInterrupts, regs, mem.DMEM);

        video = new Video(cpu.CheckInterrupts, regs, mem.RDRAM, mem.DMEM, mem.IMEM);

        audio = new Audio(cpu.AiCheckInterrupts, mem.RDRAM, mem.DMEM, mem.IMEM);

        pif = new Pif(cpu.CheckInterrupts, regs);

        dma = new DirectMemoryAccess(cpu.CheckInterrupts, regs, mem.RDRAM, mem.DMEM);

        opcodeBuilder = new OpcodeBuilder();

        JFrame hMainWindow = new JFrame("N64");
        hMainWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        hMainWindow.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
                if (pif.inputPlugin != null)
                    pif.inputPlugin.wmKeyUp(e.getKeyCode(), 0);
            }

            public void keyPressed(KeyEvent e) {
                if (pif.inputPlugin != null)
                    pif.inputPlugin.wmKeyDown(e.getKeyCode(), 0);
            }

            public void keyTyped(KeyEvent e) {
            }
        });

        hMainWindow.setJMenuBar(new MenuBar());

        JPanel panel = new JPanel();
        panel.setPreferredSize(new java.awt.Dimension(WindowWidth, WindowHeight));
        hMainWindow.getContentPane().add(panel);

        hMainWindow.pack();
        hMainWindow.setVisible(true);

        URL pluginUrl = null;
        File file = new File("plugins.properties");
        if (file.exists()) {
            try {
                pluginUrl = file.toURI().toURL();
            } catch (Exception ignored) {
            }
        } else {
            pluginUrl = Main.class.getClassLoader().getResource("plugins.properties");
        }

        if (pluginUrl == null) {
            System.out.println("can't find resource: " + "plugins.properties");
            return;
        }

        Properties cfg = new Properties();
        try {
            cfg.load(pluginUrl.openStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        video.setupPlugin(hMainWindow, cfg);
        audio.setupPlugin(hMainWindow, cfg);
        rsp.setupPlugin(video.gfxPlugin, audio.audioPlugin);
        pif.setupPlugin(hMainWindow, cfg);
        if (DEBUG)
            System.out.print("Plugins Setup\n");
    }
}
