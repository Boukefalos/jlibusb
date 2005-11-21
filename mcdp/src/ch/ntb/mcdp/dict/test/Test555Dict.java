package ch.ntb.mcdp.dict.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ntb.mcdp.mpc555.MPC555RegisterDict;

public class Test555Dict {

	private static final String PATH_TO_REGISTER_FILE = "resources/targets/mpc555/registerDictionary.xml";

	public static void main(String[] args) {
		MPC555RegisterDict regdict = new MPC555RegisterDict();

		try {
			regdict.addRegistersFromFile(PATH_TO_REGISTER_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		regdict.printRegisters();
	}
}
