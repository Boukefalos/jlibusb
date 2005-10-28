package ch.ntb.usb;

public class Usb_Device {
	public Usb_Device next, prev;

	public String filename;

	public Usb_Bus bus;

	public Usb_Device_Descriptor descriptor;

	public Usb_Config_Descriptor[] config;

	public byte devnum;

	public byte num_children;

	public Usb_Device children;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("** Usb_Device **\n");
		sb.append("\tfilename: " + filename + "\n");
		sb.append("\tdevnum: " + devnum + "\n");
		sb.append("\tnum_children: " + num_children + "\n");
		return sb.toString();
	}

}