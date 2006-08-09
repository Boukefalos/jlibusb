package ch.ntb.usb;

/**
 * Represents the descriptor of a USB endpoint.<br>
 * Endpoint descriptors are used to describe endpoints other than endpoint zero.
 * Endpoint zero is always assumed to be a control endpoint and is configured
 * before any descriptors are even requested. The host will use the information
 * returned from these descriptors to determine the bandwidth requirements of
 * the bus.<br>
 * <br>
 * The length of the configuration descriptor is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_ENDPOINT_SIZE} and the type is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_ENDPOINT}.
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Endpoint_Descriptor extends Usb_Descriptor {

	/**
	 * Maximum number of endpoints
	 */
	public static final int USB_MAXENDPOINTS = 32;

	/**
	 * Endpoint address mask (in bEndpointAddress).
	 */
	public static final int USB_ENDPOINT_ADDRESS_MASK = 0x0f,
			USB_ENDPOINT_DIR_MASK = 0x80;

	/**
	 * Endpoint type mask (in bmAttributes).
	 */
	public static final int USB_ENDPOINT_TYPE_MASK = 0x03;
	
	/**
	 * Possible endpoint types (in bmAttributes).
	 */
	public static final int USB_ENDPOINT_TYPE_CONTROL = 0,
			USB_ENDPOINT_TYPE_ISOCHRONOUS = 1, USB_ENDPOINT_TYPE_BULK = 2,
			USB_ENDPOINT_TYPE_INTERRUPT = 3;

	/**
	 * Endpoint Address<br>
	 * <br>
	 * Bits 3..0: Endpoint number <br>
	 * Bits 6..4: Reserved. Set to zero <br>
	 * Bit 7: Direction. 0 = Out, 1 = In (ignored for control endpoints)<br>
	 */
	public byte bEndpointAddress;

	/**
	 * Bits 1..0: Transfer Type (see <i>USB_ENDPOINT_TYPE_XXX</i>).<br>
	 * Bits 7..2: Reserved.<br>
	 * 
	 * <pre>
	 * If isochronous endpoint:
	 * 		Bits 3..2: Synchronisation type
	 * 			00 = No synchronisation
	 * 			01 = Asynchronous
	 * 			10 = Adaptive
	 * 			11 = Synchronous
	 * 		Bits 5..4: Usage Type
	 * 			00 = Data endpoint
	 * 			01 = Feedback endpoint
	 * 			10 = Explicit feedback data endpoint
	 * 			11 = Reserved
	 * </pre>
	 */
	public byte bmAttributes;

	/**
	 * Maximum packet size this endpoint is capable of sending or receiving
	 */
	public short wMaxPacketSize;

	/**
	 * Intervall for polling endpoint data transfers.<br>
	 * Value in frame counts. Ignored for Bulk & Control eEndpoints. Isochronous
	 * endpoints must equal 1 and field may range from 1 to 255 for interrupt
	 * endpoints.
	 */
	public byte bInterval;

	public byte bRefresh;

	public byte bSynchAddress;

	/**
	 * Extra descriptors are currently not interpreted because of their unknown
	 * structure.
	 */
	public Usb_Endpoint_Descriptor extra; /* Extra descriptors */
	// TODO

	public int extralen;

	@Override
	public String toString() {
		return "Usb_Endpoint_Descriptor bEndpointAddress: 0x"
				+ Integer.toHexString(bEndpointAddress & 0xFF);
	}
}
