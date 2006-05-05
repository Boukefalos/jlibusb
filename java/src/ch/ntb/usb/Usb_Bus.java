package ch.ntb.usb;

public class Usb_Bus {

	public Usb_Bus next, prev;

	public String dirname;

	public Usb_Device devices;

	public long location;

	public Usb_Device root_dev;
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("** Usb_Bus **\n");
		sb.append("\tdirname: " + dirname + "\n");
		sb.append("\tlocation: " + location + "\n");
		return sb.toString();
	}

}