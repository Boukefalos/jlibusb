CC = gcc
RM=rm -f
WINDRES = windres
SOURCENAME = LibusbWin
DDLNAME = LibusbWin
EXENAME = LibusbTest
JAVAPATH = "C:/Program Files/Java/jdk1.5.0_04"
## -IDLOUT:$(DDLNAME) -MIDL:$(DDLNAME) -TLBOUT:$(DDLNAME)
## 	$(CC) -LD $(SOURCENAME).c -Fe$(DDLNAME).dll -link libusb.lib 

VERSION_MAJOR = 0
VERSION_MINOR = 0
VERSION_MICRO = 3
VERSION_NANO = 0

VERSION = $(VERSION_MAJOR).$(VERSION_MINOR).$(VERSION_MICRO).$(VERSION_NANO)
RC_VERSION = $(VERSION_MAJOR),$(VERSION_MINOR),$(VERSION_MICRO),$(VERSION_NANO)
RC_VERSION_STR = '\"$(VERSION)\"'

dll: $(DDLNAME).dll
$(DDLNAME).dll: $(SOURCENAME).cpp
	$(WINDRES) -o resource.o resource.rc -DRC_VERSION='$(RC_VERSION)' \
								-DRC_VERSION_STR=$(RC_VERSION_STR)
	$(CC) -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -mwindows \
				-I$(JAVAPATH)/include -I$(JAVAPATH)/include/win32 -shared \
				$(SOURCENAME).cpp -o $(DDLNAME).dll resource.o libusb.a

clean:
	$(RM) $(DDLNAME).dll
	$(RM) *.o
	$(RM) *.exe

testExe: $(EXENAME).exe
$(EXENAME).exe: $(EXENAME).cpp
	$(CC) $(EXENAME).cpp -o $(EXENAME).exe libusb.a
