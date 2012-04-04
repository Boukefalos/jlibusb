package ch.ntb.inf.libusbJava;

public class Libusb_event {
	
	public void transferCallback(){
		
	}
	
	public void fdAddedCallback(int fd){
		System.out.println("addedCallback");
		
	}
	
	public void fdRemovedCallback(int fd){
		System.out.println("removedCallback");
		
	}

}
