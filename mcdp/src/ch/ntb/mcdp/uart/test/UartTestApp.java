package ch.ntb.mcdp.uart.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ch.ntb.mcdp.uart.UartDispatch;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class UartTestApp {

	private Shell sShell = null; // @jve:decl-index=0:visual-constraint="10,10"

	private Button button1 = null;

	private Button button2 = null;

	private Button button3 = null;

	private Button button4 = null;

	private Button button5 = null;

	private Button button6 = null;

	private Button button7 = null;

	private Button button8 = null;

	private Button button9 = null;

	private Button button10 = null;

	private Button button15 = null;

	private Button button11 = null;

	private Button button12 = null;

	private Button button13 = null;

	private Button button14 = null;

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setLayout(new RowLayout());
		sShell.setSize(new org.eclipse.swt.graphics.Point(312,110));
		button1 = new Button(sShell, SWT.NONE);
		button1.setText("writeData");
		button1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button1();
					}
				});
		button2 = new Button(sShell, SWT.NONE);
		button2.setText("not assigned");
		button2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button2();
					}
				});
		button3 = new Button(sShell, SWT.NONE);
		button3.setText("not assigned");
		button3
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button3();
					}
				});
		button4 = new Button(sShell, SWT.NONE);
		button4.setText("not assigned");
		button4
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button4();
					}
				});
		button5 = new Button(sShell, SWT.NONE);
		button5.setText("not assigned");
		button6 = new Button(sShell, SWT.NONE);
		button6.setText("not assigned");
		button6
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button6();
					}
				});
		button7 = new Button(sShell, SWT.NONE);
		button7.setText("not assigned");
		button8 = new Button(sShell, SWT.NONE);
		button8.setText("not assigned");
		button8
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button8();
					}
				});
		button9 = new Button(sShell, SWT.NONE);
		button9.setText("not assigned");
		button10 = new Button(sShell, SWT.NONE);
		button10.setText("not assigned");
		button11 = new Button(sShell, SWT.NONE);
		button11.setText("not assigned");
		button11.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				UartTest.button11();
			}
		});
		button12 = new Button(sShell, SWT.NONE);
		button12.setText("not assigned");
		button12.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				UartTest.button12();
			}
		});
		button13 = new Button(sShell, SWT.NONE);
		button13.setText("not assigned");
		button13.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				UartTest.button13();
			}
		});
		button14 = new Button(sShell, SWT.NONE);
		button14.setText("not assigned");
		button14.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				UartTest.button14();
			}
		});
		button15 = new Button(sShell, SWT.NONE);
		button15.setText("not assigned");
		button15.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				UartTest.button15();
			}
		});
		button10
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button10();
					}
				});
		button9
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button9();
					}
				});
		button7
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button7();
					}
				});
		button5
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						UartTest.button5();
					}
				});
	}

	public static void main(String[] args) {
		UartTestApp app = new UartTestApp();
		app.createSShell();
		app.sShell.open();

		Display display = app.sShell.getDisplay();

		try {
			USBDevice.open();
			System.out.println("open device...");
		} catch (USBException e) {
			e.printStackTrace();
			return;
		}
		UartDispatch.start();
		UartTest.init();

		while (!app.sShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		UartDispatch.stop();

		try {
			USBDevice.close();
			System.out.println("closing device...");
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
}
