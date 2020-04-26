package me.hydos.J64.hardware;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Rom {

	private static final boolean DEBUG_ROM = false;

	private static final int BUFFER = 2048;

	public ByteBuffer data;

	/** Creates a new instance of Rom */
	public Rom(File file) {
		data = loadDataFromRomFile(file);
		if (data == null) {
			System.err.print("Not enough memory for rom\n");
		}
	}

	private boolean isValidRomImage(ByteBuffer test) {
		if (test.getInt(0) == 0x40123780) {
			if (DEBUG_ROM)
				System.out.print("ROM format: 0x40123780\n");
			return true;
		}
		if (test.getInt(0) == 0x12408037) {
			if (DEBUG_ROM)
				System.out.print("ROM format: 0x12408037\n");
			return true;
		}
		if (test.getInt(0) == 0x80371240) {
			if (DEBUG_ROM)
				System.out.print("ROM format: 0x80371240\n");
			return true;
		}
		return false;
	}

	private ByteBuffer loadDataFromRomFile(File fileName) {
		ByteBuffer test = ByteBuffer.allocate(4);
		test.order(ByteOrder.LITTLE_ENDIAN);
		ByteBuffer tmpData = null;
		int romSize = 0;

		if (fileName.getName().substring(fileName.getName().lastIndexOf('.')).equalsIgnoreCase(".zip")) {
			System.out.println("Opening compressed ROM.");
			try {
				boolean foundRom = false;
				FileInputStream fis = new FileInputStream(fileName);
				ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					int counter;
					byte[] data = new byte[BUFFER];
					int size = (int) entry.getSize();
					byte[] tmpbuff = new byte[size];
					int pos = 0;
					while ((counter = zis.read(data, 0, BUFFER)) != -1) {
						System.arraycopy(data, 0, tmpbuff, pos, counter);
						pos += counter;
					}

					System.arraycopy(tmpbuff, 0, test.array(), 0, 4);
					if (isValidRomImage(test)) {
						romSize = tmpbuff.length;
						tmpData = ByteBuffer.allocate(romSize);
						tmpData.order(ByteOrder.LITTLE_ENDIAN);
						System.arraycopy(tmpbuff, 0, tmpData.array(), 0, romSize);
						foundRom = true;
						break;
					}
				}
				zis.close();
				if (!foundRom) {
					System.err.println("No valid rom image found in zipfile: " + fileName);
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Opening UNcompressed ROM.");
			try {
				RandomAccessFile hFile;
				hFile = new RandomAccessFile(fileName, "r");
				hFile.read(test.array(), 0, 4);
				if (!isValidRomImage(test)) {
					hFile.close();
					System.err.printf("Not a valid rom image: %X\n", test.getInt(0));
					return null;
				}
				hFile.seek(0);
				romSize = (int) hFile.length();
				tmpData = ByteBuffer.allocate(romSize);
				tmpData.order(ByteOrder.LITTLE_ENDIAN);

				hFile.read(tmpData.array());

				hFile.close();
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		assert tmpData != null;
		byte[] data = tmpData.array();
		switch (tmpData.getInt(0)) {
		case 0x12408037:
			for (int count = 0; count < romSize; count += 4) {
				data[count] ^= data[count + 2];
				data[count + 2] ^= data[count];
				data[count] ^= data[count + 2];
				data[count + 1] ^= data[count + 3];
				data[count + 3] ^= data[count + 1];
				data[count + 1] ^= data[count + 3];
			}
			break;
		case 0x40123780:
			for (int count = 0; count < romSize; count += 4) {
				data[count] ^= data[count + 3];
				data[count + 3] ^= data[count];
				data[count] ^= data[count + 3];
				data[count + 1] ^= data[count + 2];
				data[count + 2] ^= data[count + 1];
				data[count + 1] ^= data[count + 2];
			}
			break;
		case 0x80371240:
			break;
		}

		// TMP
		for (int count = 0; count < romSize; count += 4) {
			data[count] ^= data[count + 3];
			data[count + 3] ^= data[count];
			data[count] ^= data[count + 3];
			data[count + 1] ^= data[count + 2];
			data[count + 2] ^= data[count + 1];
			data[count + 1] ^= data[count + 2];
		}
		tmpData.order(ByteOrder.BIG_ENDIAN);

		return tmpData;
	}

//    public static void main(String[] argv) {
//        File src;
//        File dst;
//
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch(Exception ex) { ex.printStackTrace(); }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Unzip");
//        int returnVal = fileChooser.showOpenDialog(null);
//        if (returnVal == JFileChooser.APPROVE_OPTION)
//            src = fileChooser.getSelectedFile();
//        else
//            return;
//
//        dst = new File(src.getParent() + "\\" + src.getName().substring(0, src.getName().lastIndexOf('.')));
//
//        fileChooser.setDialogTitle("Destination");
//        fileChooser.setSelectedFile(dst);
//        returnVal = fileChooser.showSaveDialog(null);
//        if (returnVal == JFileChooser.APPROVE_OPTION)
//            dst = fileChooser.getSelectedFile();
//        else
//            return;
//
//        if (!dst.exists())
//            dst.mkdir();
//
//        try {
//            BufferedOutputStream dest = null;
//            FileInputStream fis = new
//                    FileInputStream(src);
//            ZipInputStream zis = new
//                    ZipInputStream(new BufferedInputStream(fis));
//            ZipEntry entry;
//            while((entry = zis.getNextEntry()) != null) {
//                System.out.println("Extracting: " +entry);
//                int count;
//                byte data[] = new byte[BUFFER];
//                // write the files to the disk
//                FileOutputStream fos = new
//                        FileOutputStream(dst.getAbsolutePath() + "\\" + entry.getName());
//                dest = new
//                        BufferedOutputStream(fos, BUFFER);
//                while ((count = zis.read(data, 0, BUFFER))
//                != -1) {
//                    dest.write(data, 0, count);
//                }
//                dest.flush();
//                dest.close();
//            }
//            zis.close();
//            System.out.println("Done!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
