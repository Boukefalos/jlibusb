package ch.ntb.mcdp.utils.logger;

public class LogUtil {

	public static McdpLogger ch_ntb_mcdp_bdi, ch_ntb_mcdp_bdi_test;

	static {
		// set all loglevels here
		ch_ntb_mcdp_bdi = McdpLogger.getLogger("ch.ntb.mcdp.bdi");
		ch_ntb_mcdp_bdi.setLevel(LogLevel.ALL);

		ch_ntb_mcdp_bdi_test = McdpLogger.getLogger("ch.ntb.mcdp.bdi.test");
		ch_ntb_mcdp_bdi_test.setLevel(LogLevel.ALL);
	}
}
