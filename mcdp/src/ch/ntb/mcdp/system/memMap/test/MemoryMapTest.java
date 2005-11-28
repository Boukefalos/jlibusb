package ch.ntb.mcdp.system.memMap.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ch.ntb.mcdp.system.memMap.MemoryMap;

public class MemoryMapTest {
	
	private static String filePath = "resources/targets/mpc555/memoryMap.xml";
	
	public static void main(String[] args) {
		MemoryMap map = null;
		try {
			map = new MemoryMap(filePath);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
		map.printMemMap();
	}

}
