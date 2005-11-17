package ch.ntb.mcdp.mpc555;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ntb.mcdp.dict.RegisterDict;

public class MPC555RegisterDict extends RegisterDict {

	private static final String REGISTER_CLASS = "ch.ntb.mcdp.mpc555.MPC555RegisterDict";

	private static final String PATH_TO_REGISTER_FILE = "resources/targets/mpc555/registerDictionary.xml";

	MPC555RegisterDict() {
		super(REGISTER_CLASS);
		
		// TODO: remove
		try {
			addRegistersFromFile(PATH_TO_REGISTER_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		printRegisters();
	}
}
