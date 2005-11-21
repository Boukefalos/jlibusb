package ch.ntb.mcdp.dict.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ntb.mcdp.mc68332.MC68332RegisterDict;

public class Test332Dict {
	
	private static final String PATH_TO_REGISTER_FILE = "resources/targets/mc68332/registerDictionary.xml";
	
	public static void main(String[] args) {
		MC68332RegisterDict regdict = new MC68332RegisterDict();

		try {
			regdict.addRegistersFromFile(PATH_TO_REGISTER_FILE);
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
		regdict.printRegisters();
}
}
