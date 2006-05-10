package ch.ntb.usb;

/**
 * Represents an USB interface.<br>
 * An interface is a group of alternate settings of a configuration.<br>
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Interface {

	/**
	 * Maximal number of alternate settings
	 */
	public static final int USB_MAXALTSETTING = 128; /* Hard limit */

	/**
	 * Interface descriptors
	 */
	public Usb_Interface_Descriptor[] altsetting;

	/**
	 * Number of alternate settings
	 */
	public int num_altsetting;

	public String toString() {
		return "Usb_Interface num_altsetting: 0x"
				+ Integer.toHexString(num_altsetting);
	}

}