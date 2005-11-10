package ch.ntb.mcdp.mc68332;

import ch.ntb.mcdp.bdi.BDIException;
import ch.ntb.mcdp.bdi.MC68332;
import ch.ntb.mcdp.usb.DispatchException;
import ch.ntb.usb.USBException;

public class IMCBTargetBoard {

	private static void writeRegister(String name, int value)
			throws USBException, DispatchException, BDIException {
		System.out.println("0x" + Integer.toHexString(readRegister(name)));
		System.out.println("writeRegister: " + name + ", value: 0x"
				+ Integer.toHexString(value));
		Register r = RegisterDict.getRegister(name);
		switch (r.type) {
		case Register.CtrReg:
			System.out.println("writeMem");
			MC68332.writeMem(r.addr, value, r.size);
			break;
		case Register.SysReg:
			System.out.println("writeSysReg");
			MC68332.writeSysReg(r.addr, value);
			break;
		case Register.UserReg:
			System.out.println("writeUserReg");
			MC68332.writeUserReg(r.addr, value);
			break;
		}
		System.out.println("0x" + Integer.toHexString(readRegister(name)));
	}

	private static int readRegister(String name) throws USBException,
			DispatchException, BDIException {
		System.out.print("readRegister: " + name);
		Register r = RegisterDict.getRegister(name);
		switch (r.type) {
		case Register.CtrReg:
			System.out.println("\treadMem");
			return MC68332.readMem(r.addr, r.size);
		case Register.SysReg:
			System.out.println("\treadSysReg");
			return MC68332.readSysReg(r.addr);
		case Register.UserReg:
			System.out.println("\treadUserReg");
			return MC68332.readUserReg(r.addr);
		}
		return -1;
	}

	public static void init() throws USBException, DispatchException,
			BDIException {

		MC68332.reset_target();
		
		// RegisterDict.printRegisters();

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
