package ch.ntb.mcdp.uart;


public class Uart0 extends Uart {
	
	// UART 0 Subtypes
	/**
	 * Data to UART 0
	 */
	private static final byte STYPE_UART_0_IN = 0x11;

	/**
	 * Data from UART 0
	 */
	private static final byte STYPE_UART_0_OUT = 0x22;

	@Override
	protected byte getSTYPE_OUT() {
		return STYPE_UART_0_OUT;
	}

	@Override
	protected byte getSTYPE_IN() {
		return STYPE_UART_0_IN;
	}
}
