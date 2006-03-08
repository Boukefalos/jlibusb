package ch.ntb.mcdp.bdi.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ch.ntb.mcdp.bdi.MC68332;
import ch.ntb.mcdp.usb.USBDevice;
import ch.ntb.usb.USBException;

public class BDI332App {

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

	private Button button20 = null;

	private Button button16 = null;

	private Button button17 = null;

	private Button button18 = null;

	private Button button19 = null;

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell();
		sShell.setText("Shell");
		sShell.setLayout(new RowLayout());
		sShell.setSize(new org.eclipse.swt.graphics.Point(320, 134));
		button1 = new Button(sShell, SWT.NONE);
		button1.setText("testBdiTransaction");
		button1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button1();
					}
				});
		button2 = new Button(sShell, SWT.NONE);
		button2.setText("reset_target");
		button2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button2();
					}
				});
		button3 = new Button(sShell, SWT.NONE);
		button3.setText("go");
		button3
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button3();
					}
				});
		button4 = new Button(sShell, SWT.NONE);
		button4.setText("break_");
		button4
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button4();
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
						BDI332test.button6();
					}
				});
		button7 = new Button(sShell, SWT.NONE);
		button7.setText("readMem");
		button8 = new Button(sShell, SWT.NONE);
		button8.setText("dumpMem");
		button8
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button8();
					}
				});
		button9 = new Button(sShell, SWT.NONE);
		button9.setText("NOP");
		button10 = new Button(sShell, SWT.NONE);
		button10.setText("fillMem");
		button11 = new Button(sShell, SWT.NONE);
		button11.setText("initTarget");
		button11
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button11();
					}
				});
		button12 = new Button(sShell, SWT.NONE);
		button12.setText("replaceA");
		button12
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button12();
					}
				});
		button13 = new Button(sShell, SWT.NONE);
		button13.setText("compare1");
		button13
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button13();
					}
				});
		button14 = new Button(sShell, SWT.NONE);
		button14.setText("compare2");
		button14
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button14();
					}
				});
		button15 = new Button(sShell, SWT.NONE);
		button15.setText("resetUSB");
		button16 = new Button(sShell, SWT.NONE);
		button16.setText("replaceE");
		button16
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button16();
					}
				});
		button17 = new Button(sShell, SWT.NONE);
		button17.setText("-");
		button17
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button17();
					}
				});
		button18 = new Button(sShell, SWT.NONE);
		button18.setText("-");
		button18
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button18();
					}
				});
		button19 = new Button(sShell, SWT.NONE);
		button19.setText("-");
		button19
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						Dump332.dumpToBin();
					}
				});
		button20 = new Button(sShell, SWT.NONE);
		button20.setText("-");
		button20
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						Dump332.dumpToHex();
					}
				});
		button15
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button15();
					}
				});
		button10
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button10();
					}
				});
		button9
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button9();
					}
				});
		button7
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button7();
					}
				});
		button5
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						BDI332test.button5();
					}
				});
	}

	public static void main(String[] args) {
		BDI332App app = new BDI332App();
		app.createSShell();
		app.sShell.open();

		Display display = app.sShell.getDisplay();

		try {
			USBDevice.open();
			MC68332 bdi = new MC68332(USBDevice.getDevice());
			BDI332test.bdi = bdi;
			Dump332.bdi = bdi;
			System.out.println("open device...");
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
			System.out.println("closing device...");
		} catch (USBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
