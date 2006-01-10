EXESRC=BDI_exe.cpp
SRC=invoke.cpp
DLLSRC=BDI_dll.cpp
DLL=BDI.dll
DEF=BDI.def
IMPLIB=BDI555.lib
IMPLIBA=libbdi555.a
JVMLIBA=libjvm.a
OBJ=$(SRC:.c=.o) # replaces the .c from SRC with .o
EXE=BDI_exe.exe
INVOKE=invoke.exe
JAVA_HOME="C:/Program Files/Java/jdk1.5.0_04"

CC=gcc
RM=rm
REIMP=reimp

all: $(INVOKE)      # all is dependent on $(EXE) to be complete
$(INVOKE): $(OBJ)   # $(EXE) is dependent on all of the files in $(OBJ) to exist
	$(CC) -Wall -D_JNI_IMPLEMENTATION_ \
	-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/win32 \
	-o $(INVOKE) $(SRC) $(JVMLIBA)

exe: $(EXE)      # all is dependent on $(EXE) to be complete
$(EXE): $(OBJ)   # $(EXE) is dependent on all of the files in $(OBJ) to exist
	$(CC) -Wall -D_JNI_IMPLEMENTATION_ \
	-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/win32 \
	-o $(EXE) $(EXESRC) $(JVMLIBA)

cleanExe:
	$(RM) $(EXE)

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

impLib: $(IMPLIB)
$(IMPLIB): $(DEF)
	dlltool -U -d $(DEF) -l $(IMPLIB)
	
lib: $(IMPLIBA)
$(IMPLIBA): $(DEF)
	lib /machine:i386 /def:$(DEF)
	$(REIMP) $(IMPLIB)
	
clean:
	$(RM) $(DLL)
	$(RM) $(EXE)
