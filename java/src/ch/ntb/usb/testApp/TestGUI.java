package ch.ntb.usb.testApp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TestGUI {

	private static final int HEX_WIDTH = 5;
	private Shell rootShell = null; // @jve:decl-index=0:visual-constraint="10,10"
	private Group vendorIDGroup = null;
	private Text vendorID = null;
	private Group productIDGroup = null;
	private Text productID = null;
	private Group configGroup = null;
	private Text configuration = null;
	private Group interfaceGroup = null;
	private Text interface_ = null;
	private Group altIntGroup = null;
	private Text altInt = null;
	private Group deviceGroup = null;
	private Group endpointGroup = null;
	private Group deviceGroup2 = null;
	private Group outEPGroup = null;
	private Text outEP = null;
	private Group inEPGroup = null;
	private Text inEP = null;
	private Group timeoutGroup = null;
	private Text timeout = null;
	private Group dataGroup = null;
	private Composite dataButtonComp = null;
	private Button sendButton = null;
	private Button recButton = null;
	private Composite devComp = null;
	private Composite devButtonComp = null;
	private Button devOpenButton = null;
	private Button devCloseButton = null;
	private Group dataFieldGoup = null;
	private Text dataField = null;
	private Button resetButton = null;

	private int parseInt(String s) {
		if (s == "")
			return 0;
		if (s.indexOf('x') > 0) {
			// is hex number
			if (s.length() <= 2) { // exception for "0x"
				return 0;
			}
			return Integer.parseInt(
					s.substring(s.indexOf('x') + 1, s.length()), 16);
		} else {
			// is decimal number
			return Integer.parseInt(s);
		}
	}

	private byte[] parseByteArray(String s) {
		StringBuffer sb = new StringBuffer();
		int stringIndex = 0, spaceIndex = 0;
		String ss;
		while (stringIndex + 3 < s.length()) {
			ss = s.substring(spaceIndex, spaceIndex + 4);
			spaceIndex = s.indexOf(' ', stringIndex) + 1;
			sb.append((char) parseInt(ss));
			stringIndex += HEX_WIDTH;
		}
		return sb.toString().getBytes();
	}

	private void createSShell() {
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout.justify = true;
		rowLayout.fill = true;
		rootShell = new Shell();
		rootShell.setText("Usb TestApplication");
		rootShell.setLayout(rowLayout);
		createDeviceGroup();
		createDataGroup();
		rootShell.setSize(new org.eclipse.swt.graphics.Point(466, 315));
	}

	/**
	 * This method initializes vendorIDGroup
	 * 
	 */
	private void createVendorIDGroup() {
		vendorIDGroup = new Group(deviceGroup2, SWT.NONE);
		vendorIDGroup.setText("VendorID");
		vendorID = new Text(vendorIDGroup, SWT.BORDER | SWT.RIGHT);
		vendorID
				.setBounds(new org.eclipse.swt.graphics.Rectangle(7, 23, 76, 19));
		vendorID.setText("0x"
				+ Integer.toHexString(TestImplementation.IdVendor & 0xffff));
		TestImplementation.IdVendor = (short) parseInt(vendorID.getText());
		vendorID.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				TestImplementation.IdVendor = (short) parseInt(vendorID
						.getText());
			}
		});
	}

	/**
	 * This method initializes productIDGroup
	 * 
	 */
	private void createProductIDGroup() {
		productIDGroup = new Group(deviceGroup2, SWT.NONE);
		productIDGroup.setText("ProductID");
		productID = new Text(productIDGroup, SWT.BORDER | SWT.RIGHT);
		productID.setBounds(new org.eclipse.swt.graphics.Rectangle(4, 24, 76,
				19));
		productID.setText("0x"
				+ Integer.toHexString(TestImplementation.IdProduct & 0xffff));
		TestImplementation.IdProduct = (short) parseInt(productID.getText());
		productID
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						TestImplementation.IdProduct = (short) parseInt(productID
								.getText());
					}
				});
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup() {
		configGroup = new Group(deviceGroup2, SWT.NONE);
		configGroup.setText("Configuration");
		configuration = new Text(configGroup, SWT.BORDER | SWT.RIGHT);
		configuration.setBounds(new org.eclipse.swt.graphics.Rectangle(4, 24,
				75, 19));
		configuration.setText(Integer
				.toString(TestImplementation.CONFIGURATION));
		configuration
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						TestImplementation.CONFIGURATION = parseInt(configuration
								.getText());
					}
				});
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup2() {
		interfaceGroup = new Group(deviceGroup2, SWT.NONE);
		interfaceGroup.setText("Interface");
		interface_ = new Text(interfaceGroup, SWT.BORDER | SWT.RIGHT);
		interface_.setBounds(new org.eclipse.swt.graphics.Rectangle(4, 24, 57,
				19));
		interface_.setText(Integer.toString(TestImplementation.INTERFACE));
		interface_
				.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
					public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
						TestImplementation.INTERFACE = parseInt(interface_
								.getText());
					}
				});
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup3() {
		altIntGroup = new Group(deviceGroup2, SWT.NONE);
		altIntGroup.setText("Alternative Int");
		altInt = new Text(altIntGroup, SWT.BORDER | SWT.RIGHT);
		altInt.setBounds(new Rectangle(4, 24, 76, 19));
		altInt.setText(Integer.toString(TestImplementation.ALTINTERFACE));
		altInt.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				TestImplementation.ALTINTERFACE = parseInt(altInt.getText());
			}
		});
	}

	/**
	 * This method initializes deviceGroup
	 * 
	 */
	private void createDeviceGroup() {
		RowLayout rowLayout1 = new RowLayout();
		rowLayout1.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout1.fill = true;
		deviceGroup = new Group(rootShell, SWT.NONE);
		deviceGroup.setText("Device Settings");
		createDeviceGroup2();
		deviceGroup.setLayout(rowLayout1);
		createDevComp();
	}

	/**
	 * This method initializes endpointGroup
	 * 
	 */
	private void createEndpointGroup() {
		endpointGroup = new Group(devComp, SWT.NONE);
		endpointGroup.setLayout(new RowLayout());
		createGroup4();
		createGroup5();
		createGroup6();
	}

	/**
	 * This method initializes deviceGroup2
	 * 
	 */
	private void createDeviceGroup2() {
		deviceGroup2 = new Group(deviceGroup, SWT.NONE);
		deviceGroup2.setLayout(new RowLayout());
		createVendorIDGroup();
		createProductIDGroup();
		createGroup();
		createGroup2();
		createGroup3();
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup4() {
		outEPGroup = new Group(endpointGroup, SWT.NONE);
		outEPGroup.setText("OUT EP");
		outEP = new Text(outEPGroup, SWT.BORDER | SWT.RIGHT);
		outEP.setBounds(new org.eclipse.swt.graphics.Rectangle(4, 24, 46, 19));
		outEP.setText("0x"
				+ Integer.toHexString(TestImplementation.OUT_ENDPOINT));
		outEP.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				TestImplementation.OUT_ENDPOINT = parseInt(outEP.getText());
			}
		});
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup5() {
		inEPGroup = new Group(endpointGroup, SWT.NONE);
		inEPGroup.setText("IN EP");
		inEP = new Text(inEPGroup, SWT.BORDER | SWT.RIGHT);
		inEP.setBounds(new org.eclipse.swt.graphics.Rectangle(4, 24, 46, 19));
		inEP
				.setText("0x"
						+ Integer.toHexString(TestImplementation.IN_ENDPOINT));
		inEP.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				TestImplementation.IN_ENDPOINT = parseInt(inEP.getText());
			}
		});
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup6() {
		timeoutGroup = new Group(endpointGroup, SWT.NONE);
		timeoutGroup.setText("Timeout");
		timeout = new Text(timeoutGroup, SWT.BORDER | SWT.RIGHT);
		timeout.setBounds(new Rectangle(4, 24, 46, 19));
		timeout.setText(Integer.toString(TestImplementation.TIMEOUT));
		timeout.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				TestImplementation.TIMEOUT = parseInt(timeout.getText());
			}
		});
	}

	/**
	 * This method initializes dataGroup
	 * 
	 */
	private void createDataGroup() {
		RowLayout rowLayout5 = new RowLayout();
		rowLayout5.type = org.eclipse.swt.SWT.HORIZONTAL;
		rowLayout5.spacing = 10;
		dataGroup = new Group(rootShell, SWT.NONE);
		dataGroup.setText("Send and Receive Data");
		dataGroup.setLayout(rowLayout5);
		createDataFieldGoup();
		createButtonComp();
	}

	/**
	 * This method initializes buttonComp
	 * 
	 */
	private void createButtonComp() {
		RowLayout rowLayout3 = new RowLayout();
		rowLayout3.type = org.eclipse.swt.SWT.VERTICAL;
		rowLayout3.justify = true;
		rowLayout3.fill = true;
		dataButtonComp = new Composite(dataGroup, SWT.NONE);
		sendButton = new Button(dataButtonComp, SWT.NONE);
		sendButton.setText("Send");
		sendButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						byte[] b = parseByteArray(dataField.getText());
						TestImplementation.write(b, b.length);
					}
				});
		recButton = new Button(dataButtonComp, SWT.NONE);
		dataButtonComp.setLayout(rowLayout3);
		recButton.setText("Receive");
		recButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						TestImplementation.read();
					}
				});
	}

	/**
	 * This method initializes devComp
	 * 
	 */
	private void createDevComp() {
		RowLayout rowLayout4 = new RowLayout();
		rowLayout4.fill = true;
		rowLayout4.spacing = 50;
		devComp = new Composite(deviceGroup, SWT.NONE);
		createEndpointGroup();
		devComp.setLayout(rowLayout4);
		createDevButtonComp();
	}

	/**
	 * This method initializes devButtonComp
	 * 
	 */
	private void createDevButtonComp() {
		RowLayout rowLayout2 = new RowLayout();
		rowLayout2.marginHeight = 25;
		rowLayout2.spacing = 5;
		devButtonComp = new Composite(devComp, SWT.NONE);
		devButtonComp.setLayout(rowLayout2);
		devOpenButton = new Button(devButtonComp, SWT.NONE);
		devOpenButton.setText("Open Device");
		devOpenButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						TestImplementation.openUsbDevice();
					}
				});
		devCloseButton = new Button(devButtonComp, SWT.NONE);
		devCloseButton.setText("Close Device");
		resetButton = new Button(devButtonComp, SWT.NONE);
		resetButton.setText("Reset");
		resetButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						TestImplementation.resetUsbDevice();
					}
				});
		devCloseButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						TestImplementation.closeUsbDevice();
					}
				});
	}

	/**
	 * This method initializes dataFieldGoup
	 * 
	 */
	private void createDataFieldGoup() {
		RowData rowData = new org.eclipse.swt.layout.RowData();
		rowData.width = 340;
		RowLayout rowLayout6 = new RowLayout();
		rowLayout6.fill = true;
		rowLayout6.marginHeight = 5;
		rowLayout6.justify = true;
		dataFieldGoup = new Group(dataGroup, SWT.NONE);
		dataFieldGoup.setText("Data to send [hex]");
		dataFieldGoup.setLayout(rowLayout6);
		dataField = new Text(dataFieldGoup, SWT.BORDER);
		dataField.setText(TestImplementation.sendData);
		dataField.setLayoutData(rowData);
	}

	public static void main(String[] args) {
		TestGUI app = new TestGUI();
		app.createSShell();
		app.rootShell.open();

		Display display = app.rootShell.getDisplay();

		while (!app.rootShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
