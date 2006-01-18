package ch.ntb.mcdp.mc68332;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.bdi.MC68332;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.mcdp.utils.logger.LogUtil;
import ch.ntb.mcdp.utils.logger.McdpLogger;
import ch.ntb.usb.USBException;

public class IMCBTargetBoard {

	static final McdpLogger logger = LogUtil.ch_ntb_mcdp_mc68332;

	final static String dictionaryPath = "resources/targets/mc68332/registerDictionary.xml";

	static MC68332RegisterDict regDict = new MC68332RegisterDict();

	private static void writeRegister(String name, int value)
			throws USBException, DispatchException, BDIException {
		logger.info("writeRegister: " + name + ", value: 0x"
				+ Integer.toHexString(value));
		MC68332Register r = (MC68332Register) regDict.getRegister(name);
		switch (r.type) {
		case MC68332Register.CtrlReg:
			MC68332.writeMem(r.value, value, r.size);
			break;
		case MC68332Register.SysReg:
			MC68332.writeSysReg(r.value, value);
			break;
		case MC68332Register.UserReg:
			MC68332.writeUserReg(r.value, value);
			break;
		}
	}

	private static int readRegister(String name) throws USBException,
			DispatchException, BDIException {
		logger.info("readRegister: " + name);
		MC68332Register r = (MC68332Register) regDict.getRegister(name);
		switch (r.type) {
		case MC68332Register.CtrlReg:
			return MC68332.readMem(r.value, r.size);
		case MC68332Register.SysReg:
			return MC68332.readSysReg(r.value);
		case MC68332Register.UserReg:
			return MC68332.readUserReg(r.value);
		}
		return -1;
	}

	public static void init() throws USBException, DispatchException,
			BDIException, IOException, ParserConfigurationException,
			SAXException {

		logger.info("reading dictionary file from " + dictionaryPath);
		regDict.addRegistersFromFile(dictionaryPath);

		MC68332.reset_target();

		// regDict.printRegisters();

		writeRegister("SR", 0x2700);
		writeRegister("SFC", 0x05);
		writeRegister("DFC", 0x05);
		writeRegister("VBR", 0x100000);

		writeRegister("SIMCR", 0x0404F);
		writeRegister("SYNCR", 0x7F80);
		writeRegister("SYPCR", 0x04);
		writeRegister("CSPAR0", 0x03FF);
		writeRegister("CSPAR1", 0x01);
		writeRegister("CSBARBT", 0x07);
		writeRegister("CSORBT", 0x06830);
		writeRegister("CSBAR0", 0x07);
		writeRegister("CSOR0", 0x07430);
		writeRegister("CSBAR1", 0x01005);
		writeRegister("CSOR1", 0x06C30);
		writeRegister("CSBAR2", 0x01005);
		writeRegister("CSOR2", 0x05030);
		writeRegister("CSBAR3", 0x01005);
		writeRegister("CSOR3", 0x03030);
	}
}
