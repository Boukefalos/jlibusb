/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

public class Utils {

	public static void logBus(Usb_Bus bus) {
		Usb_Bus usb_Bus = bus;
		while (usb_Bus != null) {
			System.out.println(usb_Bus.toString());
			Usb_Device dev = usb_Bus.devices;
			while (dev != null) {
				System.out.println("\t" + dev.toString());
				// Usb_Device_Descriptor
				Usb_Device_Descriptor defDesc = dev.descriptor;
				System.out.println("\t\t" + defDesc.toString());
				// Usb_Config_Descriptor
				Usb_Config_Descriptor[] confDesc = dev.config;
				for (int i = 0; i < confDesc.length; i++) {
					System.out.println("\t\t" + confDesc[i].toString());
					Usb_Interface[] int_ = confDesc[i].interface_;
					if (int_ != null) {
						for (int j = 0; j < int_.length; j++) {
							System.out.println("\t\t\t" + int_[j].toString());
							Usb_Interface_Descriptor[] intDesc = int_[j].altsetting;
							if (intDesc != null) {
								for (int k = 0; k < intDesc.length; k++) {
									System.out.println("\t\t\t\t"
											+ intDesc[k].toString());
									Usb_Endpoint_Descriptor[] epDesc = intDesc[k].endpoint;
									if (epDesc != null) {
										for (int e = 0; e < epDesc.length; e++) {
											System.out.println("\t\t\t\t\t"
													+ epDesc[e].toString());
										}
									}
								}
							}
						}
					}
				}
				dev = dev.next;
			}
			usb_Bus = usb_Bus.next;
		}
	}
}
