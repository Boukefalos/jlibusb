DLLSRC=BDI_dll.cpp
DLL=BDI.dll
DEF=BDI.def
JVMLIBA=libjvm.a
OBJ=$(SRC:.c=.o) # replaces the .c from SRC with .o
JAVA_HOME="C:/Program Files/Java/jdk1.5.0_04"

CC=gcc
RM=rm

dll: $(DLL)
$(DLL): $(DLLSRC)
	$(CC) -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at, \
	-I$(JAVAPATH)/include -I$(JAVAPATH)/include/win32 \
	-shared $(DLLSRC) -o $(DLL) $(DEF) $(JVMLIBA)

#dll_def: $(DLL)
#$(DLL): $(DLLSRC)
#	$(CC) -Wall -D_JNI_IMPLEMENTATION_ -Wl,--output-def,$(DEF),--kill-at, \
#	-I$(JAVAPATH)/include -I$(JAVAPATH)/include/win32 \
#	-shared $(DLLSRC) -o $(DLL) $(JVMLIBA)

clean:
	$(RM) $(DLL)
