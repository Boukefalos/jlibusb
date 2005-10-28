package ch.ntb.usb;

public class Usb_Interface {
	public Usb_Interface_Descriptor[] altsetting;

	public int num_altsetting;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("** Usb_Interface **\n");
		sb.append("\tnum_altsetting: " + num_altsetting + "\n");
		return sb.toString();
	}

}
