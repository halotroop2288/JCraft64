package me.hydos.J64.savechips;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Sram {

	private RandomAccessFile hSramFile;
	private File file;

	/** Creates a new instance of Sram */
	public Sram(File file) {
		this.file = file;
	}

	public Sram(String name) {
		this(new File(name));
	}

	// called by Main
	public void close() {
		if (hSramFile != null) {
			try {
				hSramFile.close();
				hSramFile = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// called by Dma
	public void dmaFromSram(ByteBuffer dest, int startOffset, int len) {
		if (hSramFile == null) {
			if (!loadSram())
				return;
		}
		try {
			hSramFile.seek(startOffset);
			hSramFile.read(dest.array(), dest.arrayOffset(), len);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// called by Dma
	public void dmaToSram(ByteBuffer source, int startOffset, int len) {
		if (hSramFile == null) {
			if (!loadSram()) {
				return;
			}
		}
		try {
			hSramFile.seek(startOffset);
			hSramFile.write(source.array(), source.arrayOffset(), len);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean loadSram() {
		try {
			hSramFile = new RandomAccessFile(file, "rwd");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
