package ch.ntb.mcdp.bdi.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ch.ntb.mcdp.bdi.MPC555;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class BDI555App {

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

	private Button button11 = null;

	private Button button12 = null;

	private Button button13 = null;

	private Button button14 = null;

	private Button button15 = null;

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setLayout(new RowLayout());
		sShell.setSize(new org.eclipse.swt.graphics.Point(361, 108));
		button1 = new Button(sShell, SWT.NONE);
		button1.setText("testBdiTransaction");
		button1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button1();
					}
				});
		button2 = new Button(sShell, SWT.NONE);
		button2.setText("reset_target");
		button2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button2();
					}
				});
		button3 = new Button(sShell, SWT.NONE);
		button3.setText("go");
		button3
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button3();
					}
				});
		button4 = new Button(sShell, SWT.NONE);
		button4.setText("break_");
		button4
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button4();
					}
				});
		button5 = new Button(sShell, SWT.NONE);
		button5.setText("freeze");
		button6 = new Button(sShell, SWT.NONE);
		button6.setText("writeMem");
		button6
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button6();
					}
				});
		button7 = new Button(sShell, SWT.NONE);
		button7.setText("readMem");
		button8 = new Button(sShell, SWT.NONE);
		button8.setText("readMemSeq");
		button8
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button8();
					}
				});
		button9 = new Button(sShell, SWT.NONE);
		button9.setText("Button9");
		button10 = new Button(sShell, SWT.NONE);
		button10.setText("fastDownload");
		button11 = new Button(sShell, SWT.NONE);
		button11.setText("read/write Registers");
		button12 = new Button(sShell, SWT.NONE);
		button12.setText("Button12");
		button12
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button12();
					}
				});
		button13 = new Button(sShell, SWT.NONE);
		button13.setText("Button13");
		button13
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button13();
					}
				});
		button14 = new Button(sShell, SWT.NONE);
		button14.setText("Button14");
		button14
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button14();
					}
				});
		button15 = new Button(sShell, SWT.NONE);
		button15.setText("Button15");
		button15
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button15();
					}
				});
		button11
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button11();
					}
				});
		button10
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button10();
					}
				});
		button9
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button9();
					}
				});
		button7
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button7();
					}
				});
		button5
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI555test.button5();
					}
				});
	}

	public static void main(String[] args) {
		BDI555App app = new BDI555App();
		app.createSShell();
		app.sShell.open();

		Display display = app.sShell.getDisplay();

		try {
			USBDevice.open();
			BDI555test.bdi = new MPC555(USBDevice.getDevice());
		} catch (USBException e) {
			e.printStackTrace();
			return;
		}

		while (!app.sShell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		try {
			USBDevice.close();
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
