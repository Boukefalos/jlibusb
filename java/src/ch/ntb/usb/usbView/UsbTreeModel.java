package ch.ntb.usb.usbView;

import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ch.ntb.usb.LibusbWin;
import ch.ntb.usb.Usb_Bus;
import ch.ntb.usb.Usb_Config_Descriptor;
import ch.ntb.usb.Usb_Device;
import ch.ntb.usb.Usb_Device_Descriptor;
import ch.ntb.usb.Usb_Endpoint_Descriptor;
import ch.ntb.usb.Usb_Interface;
import ch.ntb.usb.Usb_Interface_Descriptor;

public class UsbTreeModel implements TreeModel, TreeSelectionListener {

	private Usb_Bus rootBus;
	private JTextArea textArea;

	private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

	public UsbTreeModel(Usb_Bus rootBus, JTextArea textArea) {
		this.rootBus = rootBus;
		this.textArea = textArea;
	}

	/**
	 * Returns the root of the tree.
	 */
	public Object getRoot() {
		return rootBus;
	}

	/**
	 * Returns the child of parent at index index in the parent's child array.
	 */
	public Object getChild(Object parent, int index) {
		if (parent instanceof Usb_Bus) {
			Usb_Device device = ((Usb_Bus) parent).devices;
			int count = 0;
			while (device != null) {
				if (count == index)
					return device;
				count++;
				device = device.next;
			}
			return null;
		} else if (parent instanceof Usb_Device) {
			Usb_Device dev = (Usb_Device) parent;
			// return the Usb_Device_Descriptor at index 0
			if (index == 0) {
				return dev.descriptor;
			}
			Usb_Config_Descriptor[] confDescs = dev.config;
			if (index >= confDescs.length + 1)
				return null;
			return confDescs[index - 1];
		} else if (parent instanceof Usb_Config_Descriptor) {
			Usb_Interface[] intDescs = ((Usb_Config_Descriptor) parent).interface_;
			if (index >= intDescs.length)
				return null;
			return intDescs[index];
		} else if (parent instanceof Usb_Interface) {
			Usb_Interface_Descriptor[] altSettings = ((Usb_Interface) parent).altsetting;
			if (index >= altSettings.length)
				return null;
			return altSettings[index];
		} else if (parent instanceof Usb_Interface_Descriptor) {
			Usb_Endpoint_Descriptor[] endpoints = ((Usb_Interface_Descriptor) parent).endpoint;
			if (index >= endpoints.length)
				return null;
			return endpoints[index];
		}
		return null;
	}

	/**
	 * Returns the number of children of parent.
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof Usb_Bus) {
			Usb_Device device = ((Usb_Bus) parent).devices;
			int count = 0;
			while (device != null) {
				count++;
				device = device.next;
			}
			return count;
		} else if (parent instanceof Usb_Device) {
			// add the Usb_Device_Descriptor
			return ((Usb_Device) parent).config.length + 1;
		} else if (parent instanceof Usb_Config_Descriptor) {
			return ((Usb_Config_Descriptor) parent).interface_.length;
		} else if (parent instanceof Usb_Interface) {
			return ((Usb_Interface) parent).altsetting.length;
		} else if (parent instanceof Usb_Interface_Descriptor) {
			return ((Usb_Interface_Descriptor) parent).endpoint.length;
		}
		return 0;
	}

	/**
	 * Returns true if node is a leaf.
	 */
	public boolean isLeaf(Object node) {
		return false;
	}

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * path to newValue. Not used by this model.
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		System.out.println("*** valueForPathChanged : " + path + " --> "
				+ newValue);
	}

	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild(Object parent, Object child) {
		return 0;
	}

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	/**
	 * The only event raised by this model is TreeStructureChanged with the root
	 * as path, i.e. the whole tree has changed.
	 */
	protected void fireTreeStructureChanged(Usb_Bus oldRootBus) {
		int len = treeModelListeners.size();
		TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRootBus });
		for (int i = 0; i < len; i++) {
			((TreeModelListener) treeModelListeners.elementAt(i))
					.treeStructureChanged(e);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		JTree tree = (JTree) e.getSource();
		Object component = tree.getLastSelectedPathComponent();
		if (component instanceof Usb_Bus) {
			Usb_Bus bus = (Usb_Bus) component;
			StringBuffer sb = new StringBuffer("Usb_Bus\n");
			sb.append("\tdirname: " + bus.dirname + "\n");
			sb.append("\tlocation: 0x" + Long.toHexString(bus.location) + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Device) {
			Usb_Device device = (Usb_Device) component;
			StringBuffer sb = new StringBuffer("Usb_Device\n");
			sb.append("\tfilename: " + device.filename + "\n");
			sb.append("\tdevnum: " + device.devnum + "\n");
			sb.append("\tnum_children: " + device.num_children + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Device_Descriptor) {
			Usb_Device_Descriptor devDesc = (Usb_Device_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Device_Descriptor\n");
			sb.append("\tblenght: 0x" + Integer.toHexString(devDesc.bLength)
					+ "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(devDesc.bDescriptorType) + "\n");
			sb.append("\tbcdUSB: 0x" + Integer.toHexString(devDesc.bcdUSB)
					+ "\n");
			sb.append("\tbDeviceClass: 0x"
					+ Integer.toHexString(devDesc.bDeviceClass) + "\n");
			sb.append("\tbDeviceSubClass: 0x"
					+ Integer.toHexString(devDesc.bDeviceSubClass) + "\n");
			sb.append("\tbDeviceProtocol: 0x"
					+ Integer.toHexString(devDesc.bDeviceProtocol) + "\n");
			sb.append("\tbMaxPacketSize0: " + devDesc.bMaxPacketSize0 + "\n");
			sb.append("\tidVendor: 0x"
					+ Integer.toHexString(devDesc.idVendor & 0xFFFF) + "\n");
			sb.append("\tidProduct: 0x"
					+ Integer.toHexString(devDesc.idProduct & 0xFFFF) + "\n");
			sb.append("\tbcdDevice: 0x"
					+ Integer.toHexString(devDesc.bcdDevice) + "\n");
			sb.append("\tiManufacturer: 0x"
					+ Integer.toHexString(devDesc.iManufacturer) + "\n");
			sb.append("\tiProduct: 0x" + Integer.toHexString(devDesc.iProduct)
					+ "\n");
			sb.append("\tiSerialNumber: 0x"
					+ Integer.toHexString(devDesc.iSerialNumber) + "\n");
			sb.append("\tbNumConfigurations: 0x"
					+ Integer.toHexString(devDesc.bNumConfigurations) + "\n");
			// get device handle to retrieve string descriptors
			Usb_Bus bus = rootBus;
			while (bus != null) {
				Usb_Device dev = bus.devices;
				while (dev != null) {
					Usb_Device_Descriptor tmpDevDesc = dev.descriptor;
					if ((dev.descriptor != null)
							&& ((dev.descriptor.iManufacturer > 0)
									|| (dev.descriptor.iProduct > 0) || (dev.descriptor.iSerialNumber > 0))) {
						if (tmpDevDesc.equals(devDesc)) {
							int handle = LibusbWin.usb_open(dev);
							sb.append("\nString descriptors\n");
							if (handle <= 0) {
								sb.append("\terror opening the device\n");
								break;
							}
							if (dev.descriptor.iManufacturer > 0) {
								String manufacturer = LibusbWin
										.usb_get_string_simple(handle,
												devDesc.iManufacturer);
								if (manufacturer == null)
									manufacturer = "unable to fetch manufacturer string";
								sb.append("\tiManufacturer: " + manufacturer
										+ "\n");
							}
							if (dev.descriptor.iProduct > 0) {
								String product = LibusbWin
										.usb_get_string_simple(handle,
												devDesc.iProduct);
								if (product == null)
									product = "unable to fetch product string";
								sb.append("\tiProduct: " + product + "\n");
							}
							if (dev.descriptor.iSerialNumber > 0) {
								String serialNumber = LibusbWin
										.usb_get_string_simple(handle,
												devDesc.iSerialNumber);
								if (serialNumber == null)
									serialNumber = "unable to fetch serial number string";
								sb.append("\tiSerialNumber: " + serialNumber
										+ "\n");
							}
							LibusbWin.usb_close(handle);
						}
					}
					dev = dev.next;
				}
				bus = bus.next;
			}
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Config_Descriptor) {
			Usb_Config_Descriptor confDesc = (Usb_Config_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Config_Descriptor\n");
			sb.append("\tblenght: 0x" + Integer.toHexString(confDesc.bLength)
					+ "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(confDesc.bDescriptorType) + "\n");
			sb.append("\tbNumInterfaces: 0x"
					+ Integer.toHexString(confDesc.bNumInterfaces) + "\n");
			sb.append("\tbConfigurationValue: 0x"
					+ Integer.toHexString(confDesc.bConfigurationValue) + "\n");
			sb.append("\tiConfiguration: 0x"
					+ Integer.toHexString(confDesc.iConfiguration) + "\n");
			sb.append("\tbmAttributes: 0x"
					+ Integer.toHexString(confDesc.bmAttributes & 0xFF) + "\n");
			sb.append("\tMaxPower [mA]: 0x"
					+ Integer.toHexString(confDesc.MaxPower) + "\n");
			sb.append("\textralen: 0x" + Integer.toHexString(confDesc.extralen)
					+ "\n");
			sb.append("\textra: " + confDesc.extra + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Interface) {
			Usb_Interface int_ = (Usb_Interface) component;
			StringBuffer sb = new StringBuffer("Usb_Interface\n");
			sb.append("\tnum_altsetting: 0x"
					+ Integer.toHexString(int_.num_altsetting) + "\n");
			sb.append("\taltsetting: " + int_.altsetting + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Interface_Descriptor) {
			Usb_Interface_Descriptor intDesc = (Usb_Interface_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Interface_Descriptor\n");
			sb.append("\tblenght: 0x" + Integer.toHexString(intDesc.bLength)
					+ "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(intDesc.bDescriptorType) + "\n");
			sb.append("\tbInterfaceNumber: 0x"
					+ Integer.toHexString(intDesc.bInterfaceNumber) + "\n");
			sb.append("\tbAlternateSetting: 0x"
					+ Integer.toHexString(intDesc.bAlternateSetting) + "\n");
			sb.append("\tbNumEndpoints: 0x"
					+ Integer.toHexString(intDesc.bNumEndpoints) + "\n");
			sb.append("\tbInterfaceClass: 0x"
					+ Integer.toHexString(intDesc.bInterfaceClass & 0xFF)
					+ "\n");
			sb.append("\tbInterfaceSubClass: 0x"
					+ Integer.toHexString(intDesc.bInterfaceSubClass & 0xFF)
					+ "\n");
			sb.append("\tbInterfaceProtocol: 0x"
					+ Integer.toHexString(intDesc.bInterfaceProtocol & 0xFF)
					+ "\n");
			sb.append("\tiInterface: 0x"
					+ Integer.toHexString(intDesc.iInterface) + "\n");
			sb.append("\textralen: 0x" + Integer.toHexString(intDesc.extralen)
					+ "\n");
			sb.append("\textra: " + intDesc.extra + "\n");
			textArea.setText(sb.toString());
		} else if (component instanceof Usb_Endpoint_Descriptor) {
			Usb_Endpoint_Descriptor epDesc = (Usb_Endpoint_Descriptor) component;
			StringBuffer sb = new StringBuffer("Usb_Endpoint_Descriptor\n");
			sb.append("\tblenght: 0x" + Integer.toHexString(epDesc.bLength)
					+ "\n");
			sb.append("\tbDescriptorType: 0x"
					+ Integer.toHexString(epDesc.bDescriptorType) + "\n");
			sb.append("\tbEndpointAddress: 0x"
					+ Integer.toHexString(epDesc.bEndpointAddress & 0xFF)
					+ "\n");
			sb.append("\tbmAttributes: 0x"
					+ Integer.toHexString(epDesc.bmAttributes & 0xFF) + "\n");
			sb.append("\twMaxPacketSize: 0x"
					+ Integer.toHexString(epDesc.wMaxPacketSize) + "\n");
			sb.append("\tbInterval: 0x" + Integer.toHexString(epDesc.bInterval)
					+ "\n");
			sb.append("\tbRefresh: 0x" + Integer.toHexString(epDesc.bRefresh)
					+ "\n");
			sb.append("\tbSynchAddress: 0x"
					+ Integer.toHexString(epDesc.bSynchAddress) + "\n");
			sb.append("\textralen: 0x" + Integer.toHexString(epDesc.extralen)
					+ "\n");
			sb.append("\textra: " + epDesc.extra + "\n");
			textArea.setText(sb.toString());
		}
	}
}
