package ch.ntb.usb;

/**
 * Represents the descriptor of a USB interface.<br>
 * The interface descriptor could be seen as a header or grouping of the
 * endpoints into a functional group performing a single feature of the device.<br>
 * <br>
 * The length of the interface descriptor is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_INTERFACE_SIZE} and the type is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_INTERFACE}.
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Interface_Descriptor extends Usb_Descriptor {

	/**
	 * Maximal number of interfaces
	 */
	public static final int USB_MAXINTERFACES = 32;

	/**
	 * Number (identifier) of interface
	 */
	public byte bInterfaceNumber;

	/**
	 * Value used to select alternate setting ({@link LibusbWin#usb_set_altinterface(int, int)}).
	 */
	public byte bAlternateSetting;

	/**
	 * Number of Endpoints used for this interface
	 */
	public byte bNumEndpoints;

	/**
	 * Class code (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public byte bInterfaceClass;

	/**
	 * Subclass code (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public byte bInterfaceSubClass;

	/**
	 * Protocol code (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public byte bInterfaceProtocol;

	/**
	 * Index of String descriptor describing this interface
	 */
	public byte iInterface;

	/**
	 * Endpoint descriptors
	 */
	public Usb_Endpoint_Descriptor[] endpoint;

	/**
	 * Extra descriptors are currently not interpreted because of their unknown
	 * structure.
	 */
	public Usb_Interface_Descriptor extra; /* Extra descriptors */
	// TODO

	public int extralen;

	public String toString() {
		return "Usb_Interface_Descriptor bNumEndpoints: 0x"
				+ Integer.toHexString(bNumEndpoints);
	}
}
