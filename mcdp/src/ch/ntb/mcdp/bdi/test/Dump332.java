package ch.ntb.mcdp.bdi.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ch.ntb.mcdp.utils.intelHex.IntelHex;
import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.bdi.MC68332;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.utils.logger.LogUtil;
import ch.ntb.mcdp.utils.logger.McdpLogger;
import ch.ntb.usb.USBException;

public class Dump332 {

	private static final String pathHex = "D:\\temp\\332dump.ihx",
			pathBin = "D:\\temp\\332dump.bin";

	private static McdpLogger logger = LogUtil.ch_ntb_mcdp_bdi_test;

	public static MC68332 bdi;

	// CromBase = 0H;
	// CromSize = 100000H (* 1 MByte kByte *);
	// CromCSBAR = CromBase DIV 256 + 7;
	//		 
	// CramBase = 100000H;
	// CramSize = 040000H; (* 256 kByte *)
	// CramCSBAR = CramBase DIV 256 + 5;

	public static void dumpToHex() {
		// IntelHex.setFileName(fileName);
		IntelHex.openWrite(pathHex);

		// Read Ram
		final int BASE_ADDR = 0x100000;
		final int MEMORY_SIZE = 0x04000;

		int currentAddress = BASE_ADDR, firstResult;
		int[] result;
		int hexDataLength = 2 * bdi.getMaxNofLongsFill() + 2, hexDataIndex = 0;
		short[] hexData = new short[hexDataLength];

		logger.info("dumpToHex: memory from BASE_ADDR: 0x"
				+ Integer.toHexString(BASE_ADDR) + ", size = 0x"
				+ Integer.toHexString(MEMORY_SIZE));
		try {
			// setup base address
			firstResult = bdi.readMem(currentAddress, 4);
			hexData[hexDataIndex++] = (short) (firstResult / 0x10000);
			hexData[hexDataIndex++] = (short) firstResult;
			currentAddress += 4;
			while (currentAddress < BASE_ADDR + MEMORY_SIZE) {
				result = bdi.dumpMem(bdi.getMaxNofLongsFill());
				int i;
				for (i = 0; i < result.length; i++) {
					hexData[hexDataIndex + i * 2] = (short) (result[i] / 0x10000);
					hexData[hexDataIndex + i * 2 + 1] = (short) result[i];
				}
				IntelHex.writeDataBlock(currentAddress, hexDataIndex + i * 2,
						hexData);
				currentAddress += hexDataIndex + i * 2;
				hexDataIndex = 0;
			}
			IntelHex.close();
			logger.info("Dump finished");
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void write(OutputStream os, int data) throws IOException {
		os.write((byte) ((data / 0x1000000) & 0xFF));
		os.write((byte) ((data / 0x10000) & 0xFF));
		os.write((byte) ((data / 0x100) & 0xFF));
		os.write((byte) ((data) & 0xFF));
	}

	public static void dumpToBin() {

		// Read Ram
		final int BASE_ADDR = 0x100000;
		final int MEMORY_SIZE = 0x04000;

		int currentAddress = BASE_ADDR, firstResult;
		int[] result;

		logger.info("dumpToBin: memory from BASE_ADDR: 0x"
				+ Integer.toHexString(BASE_ADDR) + ", size = 0x"
				+ Integer.toHexString(MEMORY_SIZE));

		try {
			String path = pathBin.substring(0, pathBin.lastIndexOf('\\'));
			File pathFile = new File(path);
			pathFile.mkdirs();
			File f = new File(pathBin);
			f.createNewFile();
			OutputStream os = new FileOutputStream(f);

			// setup base address
			firstResult = bdi.readMem(currentAddress, 4);
			write(os, firstResult);
			currentAddress += 4;
			while (currentAddress < BASE_ADDR + MEMORY_SIZE) {
				result = bdi.dumpMem(bdi.getMaxNofLongsFill());
				for (int i = 0; i < result.length; i++) {
					write(os, result[i]);
				}
				currentAddress += result.length * 4;
			}
			logger.info("Dump finished");
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DispatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
