CC = gcc
SOURCENAME = LibusbWin
DDLNAME = LibusbWin
EXENAME = LibusbTest
JAVAPATH = "C:/Program Files/Java/jdk1.5.0_04"
TARGETDIR = D:/work/USB/libusb/libusbdllMinGW
## -IDLOUT:$(DDLNAME) -MIDL:$(DDLNAME) -TLBOUT:$(DDLNAME)
## 	$(CC) -LD $(SOURCENAME).c -Fe$(DDLNAME).dll -link libusb.lib 

dll: $(DDLNAME).dll
$(DDLNAME).dll: $(SOURCENAME).cpp
	$(CC) -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I$(JAVAPATH)/include -I$(JAVAPATH)/include/win32 -shared $(SOURCENAME).cpp -o $(DDLNAME).dll libusb.lib

cleanDll:
	rm $(DDLNAME).dll

testExe: $(EXENAME).exe
$(EXENAME).exe: $(EXENAME).cpp
	$(CC) $(EXENAME).cpp -o $(EXENAME).exe $(TARGETDIR)/libusb.a

