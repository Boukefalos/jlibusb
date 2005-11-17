package ch.ntb.mcdp.mc68332;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ntb.mcdp.dict.RegisterDict;

public class MC68332RegisterDict extends RegisterDict {

	private static final String REGISTER_CLASS = "ch.ntb.mcdp.mc68332.MC68332Register";

	private static final String PATH_TO_REGISTER_FILE = "resources/targets/mc68332/registerDictionary.xml";

	MC68332RegisterDict() {
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
