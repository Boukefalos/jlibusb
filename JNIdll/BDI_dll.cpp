#include <jni.h>
#include <windows.h>
#include <stdlib.h>

#define EXPORT JNIEXPORT

//#define STACKTRACE_DEPTH 5
#define NOF_VM_ARGS 5
//#define PIPEBUFSIZE 4096
//#define PIPE_TIMEOUT 500 //ms
// Classes
#define USB_Device_Class "ch/ntb/mcdp/usb/USBDevice"
#define BDI555_Class "ch/ntb/mcdp/bdi/MPC555"
#define BDI332_Class "ch/ntb/mcdp/bdi/MC68332"
#define Redirect_Class "ch/ntb/mcdp/utils/Redirect"
#define Uart0_Class "ch/ntb/mcdp/uart/blackbox/Uart0"

// JVM handles
JNIEnv *env = NULL;
JavaVM *jvm = NULL;

// Pipe handles
HANDLE hStdout_pipe, hStderr_pipe;

// Java classes
jclass cls_USB_Device, cls_BDI555, cls_BDI332, cls_Redirect, cls_Uart0;
// USB_Device
jmethodID mid_USB_Dev_open, mid_USB_Dev_close, mid_USB_Dev_reset;
// BDI555
jmethodID mid_BDI555_break_, mid_BDI555_go, mid_BDI555_reset_target, \
mid_BDI555_isFreezeAsserted, mid_BDI555_startFastDownload, mid_BDI555_fastDownload, \
mid_BDI555_stopFastDownload, mid_BDI555_writeMem, mid_BDI555_readMem, \
mid_BDI555_writeMemSeq, mid_BDI555_readMemSeq, mid_BDI555_readGPR, \
mid_BDI555_writeGPR, mid_BDI555_readSPR, mid_BDI555_writeSPR, mid_BDI555_readMSR, \
mid_BDI555_writeMSR, mid_BDI555_readFPR, mid_BDI555_writeFPR, mid_BDI555_readCR, \
mid_BDI555_writeCR, mid_BDI555_readFPSCR, mid_BDI555_writeFPSCR, \
mid_BDI555_isTargetInDebugMode, mid_BDI555_setGpr31;
// BDI332
jmethodID mid_BDI332_nopsToLegalCmd, mid_BDI332_break_, mid_BDI332_go, \
mid_BDI332_reset_target, mid_BDI332_reset_peripherals, mid_BDI332_isFreezeAsserted, \
mid_BDI332_fillMem, mid_BDI332_dumpMem, mid_BDI332_writeMem, mid_BDI332_readMem, \
mid_BDI332_readUserReg, mid_BDI332_writeUserReg, mid_BDI332_readSysReg, \
mid_BDI332_writeSysReg, mid_BDI332_isTargetInDebugMode;
// Redirect
jmethodID mid_Redirect_redirect;
// Uart0
jmethodID mid_Uart0_write, mid_Uart0_read;

EXPORT HANDLE getOutPipeHandle(){
	return hStdout_pipe;
}

EXPORT HANDLE getErrPipeHandle(){
	return hStderr_pipe;
}


//BOOL redirect(){
//	
//    // Connect the reading end of the hStdout_pipe and assign it to stdout
//    if (freopen(stdout_pipename, "w", stdout) == NULL) {
//		fprintf(stderr, "freopen(stdout_pipename, w, stdout) failed\n");
//		return FALSE;
//    }
//
//    // Connect the reading end of the hStderr_pipe and assign it to sterr
//    if (freopen(stderr_pipename, "w", stderr) == NULL) {
//		fprintf(stderr, "freopen(stderr_pipename, w, stderr) failed\n");
//		return FALSE;
//    }
//	return TRUE;
//}

EXPORT int destroyJVM()
{
	jint result = -1;
	if (env) {
		if (env->ExceptionOccurred()) {
			env->ExceptionDescribe();
		}
		env = NULL;
    }
    if (jvm) {
    	result = jvm->DestroyJavaVM();
    	jvm = NULL;
		fprintf(stderr, "JVM destroyed\n");
    }
//   if (!CloseHandle(hStdout_pipe)) 
//      fprintf(stderr, "Close stdout_pipe failed\n"); 
//   if (!CloseHandle(hStderr_pipe)) 
//      fprintf(stderr, "Close stderr_pipe failed\n");
      return result;
}

jint JNICALL _vfprintf_(FILE *fp, const char *format, va_list args)
{
	jint result = vfprintf(stderr, format, args);
	return result;
}

EXPORT int createJVM(char *classpath)
{
	
	char javaclasspath[128];
	jint res;
	JavaVMInitArgs vm_args;
	JavaVMOption options[NOF_VM_ARGS];
	
	if (jvm == NULL) {
			
//		if (!setupNamedPipes()) {
//			printf("setupNamedPipes() failed\n");
//			return FALSE;
//		}
	
		sprintf(javaclasspath, "-Djava.class.path=%s", classpath);
	
	// TODO: remove
	printf("classpath: %s\n", javaclasspath);
	
		options[0].optionString = javaclasspath;
	options[1].optionString = "-Xms4M";
	options[2].optionString = "-Xmx64M";
	options[3].optionString = "-Xss512K";
	options[4].optionString = "-Xoss400K";
//		options[1].optionString = "-Xoss";
//		options[2].optionString = "-Xmx512m";
//		options[3].optionString = "-Xss5m";
//		options[1].optionString = "-XX:+StackTraceInThrowable";
//		options[2].optionString = "-Xcheck:jni";
//		
//		options[3].optionString = "vfprintf";
//		options[3].extraInfo = (void*) _vfprintf_;
	
		// sprintf(options[2].optionString, "-XX:MaxJavaStackTraceDepth=%d", STACKTRACE_DEPTH);
	
		vm_args.version = JNI_VERSION_1_4;
		vm_args.options = options;
		vm_args.nOptions = NOF_VM_ARGS;
		vm_args.ignoreUnrecognized = JNI_FALSE;

	// TODO: remove
	printf("trying to create jvm\n");
	
	    /* Create the Java VM */
	    res = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
	    if (res < 0) {
	        fprintf(stderr, "Can't create Java VM\n");
	        return FALSE;
	    }

	// TODO: remove
	printf("jvm created\n");

		/* create class pointers */
	    cls_USB_Device = env->FindClass(USB_Device_Class);
	    if (cls_USB_Device == 0) {
	        fprintf(stderr, "Can't find %s class", USB_Device_Class);
	        return FALSE;
	    }
	
	    cls_BDI555 = env->FindClass(BDI555_Class);
	    if (cls_BDI555 == 0) {
	        fprintf(stderr, "Can't find %s class", BDI555_Class);
	        return FALSE;
	    }
	
	    cls_BDI332 = env->FindClass(BDI332_Class);
	    if (cls_BDI332 == 0) {
	        fprintf(stderr, "Can't find %s class", BDI332_Class);
	        return FALSE;
	    }

	    cls_Redirect = env->FindClass(Redirect_Class);
	    if (cls_Redirect == 0) {
	        fprintf(stderr, "Can't find %s class", Redirect_Class);
	        return FALSE;
	    }

	    cls_Uart0 = env->FindClass(Uart0_Class);
	    if (cls_Uart0 == 0) {
	        fprintf(stderr, "Can't find %s class", Uart0_Class);
	        return FALSE;
	    }
	
		/* create method pointers */
		// USB_Device
	    mid_USB_Dev_open = env->GetStaticMethodID(cls_USB_Device, "open", "()V");
	    if (mid_USB_Dev_open == 0) {
	        fprintf(stderr, "Can't find USB_Device.open\n");
	        return FALSE;
	    }
	    mid_USB_Dev_close = env->GetStaticMethodID(cls_USB_Device, "close", "()V");
	    if (mid_USB_Dev_close == 0) {
	        fprintf(stderr, "Can't find USB_Device.close\n");
	        return FALSE;
	    }
	    mid_USB_Dev_reset = env->GetStaticMethodID(cls_USB_Device, "reset", "()V");
	    if (mid_USB_Dev_reset == 0) {
	        fprintf(stderr, "Can't find USB_Device.reset\n");
	        return FALSE;
	    }
	
	    // BDI555
	    mid_BDI555_break_ = env->GetStaticMethodID(cls_BDI555, "break_", "()V");
	    if (mid_BDI555_break_ == 0) {
	        fprintf(stderr, "Can't find BDI555.break_\n");
	        return FALSE;
	    }
	    mid_BDI555_go = env->GetStaticMethodID(cls_BDI555, "go", "()V");
	    if (mid_BDI555_go == 0) {
	        fprintf(stderr, "Can't find BDI555.go\n");
	
	        return FALSE;
	    }
	    mid_BDI555_reset_target = env->GetStaticMethodID(cls_BDI555, "reset_target", "()V");
	    if (mid_BDI555_reset_target == 0) {
	        fprintf(stderr, "Can't find BDI555.reset_target\n");
	        return FALSE;
	    }
	    mid_BDI555_isFreezeAsserted = env->GetStaticMethodID(cls_BDI555, "isFreezeAsserted", "()Z");
	    if (mid_BDI555_isFreezeAsserted == 0) {
	        fprintf(stderr, "Can't find BDI555.isFreezeAsserted\n");
	        return FALSE;
	    }
	    mid_BDI555_startFastDownload = env->GetStaticMethodID(cls_BDI555, "startFastDownload", "(I)V");
	    if (mid_BDI555_startFastDownload == 0) {
	        fprintf(stderr, "Can't find BDI555.startFastDownload\n");
	        return FALSE;
	    }
	    mid_BDI555_fastDownload = env->GetStaticMethodID(cls_BDI555, "fastDownload", "([II)V");
	    if (mid_BDI555_fastDownload == 0) {
	        fprintf(stderr, "Can't find BDI555.fastDownload\n");
	        return FALSE;
	    }
	    mid_BDI555_stopFastDownload = env->GetStaticMethodID(cls_BDI555, "stopFastDownload", "()V");
	    if (mid_BDI555_stopFastDownload == 0) {
	        fprintf(stderr, "Can't find BDI555.stopFastDownload\n");
	        return FALSE;
	    }
	    mid_BDI555_writeMem = env->GetStaticMethodID(cls_BDI555, "writeMem", "(III)V");
	    if (mid_BDI555_writeMem == 0) {
	        fprintf(stderr, "Can't find BDI555.writeMem\n");
	        return FALSE;
	    }
	    mid_BDI555_readMem = env->GetStaticMethodID(cls_BDI555, "readMem", "(II)I");
	    if (mid_BDI555_readMem == 0) {
	        fprintf(stderr, "Can't find BDI555.readMem\n");
	        return FALSE;
	    }
	    mid_BDI555_writeMemSeq = env->GetStaticMethodID(cls_BDI555, "writeMemSeq", "(II)V");
	    if (mid_BDI555_writeMemSeq == 0) {
	        fprintf(stderr, "Can't find BDI555.writeMemSeq\n");
	        return FALSE;
	    }
	    mid_BDI555_readMemSeq = env->GetStaticMethodID(cls_BDI555, "readMemSeq", "(I)I");
	    if (mid_BDI555_readMemSeq == 0) {
	        fprintf(stderr, "Can't find BDI555.readMemSeq\n");
	        return FALSE;
	    }
	    mid_BDI555_readGPR = env->GetStaticMethodID(cls_BDI555, "readGPR", "(I)I");
	    if (mid_BDI555_readGPR == 0) {
	        fprintf(stderr, "Can't find BDI555.readGPR\n");
	        return FALSE;
	    }
	    mid_BDI555_writeGPR = env->GetStaticMethodID(cls_BDI555, "writeGPR", "(II)V");
	    if (mid_BDI555_writeGPR == 0) {
	        fprintf(stderr, "Can't find BDI555.writeGPR\n");
	        return FALSE;
	    }
	    mid_BDI555_readSPR = env->GetStaticMethodID(cls_BDI555, "readSPR", "(I)I");
	    if (mid_BDI555_readSPR == 0) {
	        fprintf(stderr, "Can't find BDI555.readSPR\n");
	        return FALSE;
	    }
	    mid_BDI555_writeSPR = env->GetStaticMethodID(cls_BDI555, "writeSPR", "(II)V");
	    if (mid_BDI555_writeSPR == 0) {
	        fprintf(stderr, "Can't find BDI555.writeSPR\n");
	        return FALSE;
	    }
	    mid_BDI555_readMSR = env->GetStaticMethodID(cls_BDI555, "readMSR", "()I");
	    if (mid_BDI555_readMSR == 0) {
	        fprintf(stderr, "Can't find BDI555.readMSR\n");
	        return FALSE;
	    }
	    mid_BDI555_writeMSR = env->GetStaticMethodID(cls_BDI555, "writeMSR", "(I)V");
	    if (mid_BDI555_writeMSR == 0) {
	        fprintf(stderr, "Can't find BDI555.writeMSR\n");
	        return FALSE;
	    }
	    mid_BDI555_readFPR = env->GetStaticMethodID(cls_BDI555, "readFPR", "(II)J");
	    if (mid_BDI555_readFPR == 0) {
	        fprintf(stderr, "Can't find BDI555.readFPR\n");
	        return FALSE;
	    }
	    mid_BDI555_writeFPR = env->GetStaticMethodID(cls_BDI555, "writeFPR", "(IIJ)V");
	    if (mid_BDI555_writeFPR == 0) {
	        fprintf(stderr, "Can't find BDI555.writeFPR\n");
	        return FALSE;
	    }
	    mid_BDI555_readCR = env->GetStaticMethodID(cls_BDI555, "readCR", "()I");
	    if (mid_BDI555_readCR == 0) {
	        fprintf(stderr, "Can't find BDI555.readCR\n");
	        return FALSE;
	    }
	    mid_BDI555_writeCR = env->GetStaticMethodID(cls_BDI555, "writeCR", "(I)V");
	    if (mid_BDI555_writeCR == 0) {
	        fprintf(stderr, "Can't find BDI555.writeCR\n");
	        return FALSE;
	    }
	    mid_BDI555_readFPSCR = env->GetStaticMethodID(cls_BDI555, "readFPSCR", "()I");
	    if (mid_BDI555_readFPSCR == 0) {
	        fprintf(stderr, "Can't find BDI555.readFPSCR\n");
	        return FALSE;
	    }
	    mid_BDI555_writeFPSCR = env->GetStaticMethodID(cls_BDI555, "writeFPSCR", "(I)V");
	    if (mid_BDI555_writeFPSCR == 0) {
	        fprintf(stderr, "Can't find BDI555.writeFPSCR\n");
	        return FALSE;
	    }
	    mid_BDI555_isTargetInDebugMode = env->GetStaticMethodID(cls_BDI555, "isTargetInDebugMode", "()Z");
	    if (mid_BDI555_isTargetInDebugMode == 0) {
	        fprintf(stderr, "Can't find BDI555.isTargetInDebugMode\n");
	        return FALSE;
	    }
	    mid_BDI555_setGpr31 = env->GetStaticMethodID(cls_BDI555, "setGpr31", "(I)V");
	    if (mid_BDI555_setGpr31 == 0) {
	        fprintf(stderr, "Can't find BDI555.setGpr31\n");
	        return FALSE;
	    }
	    
	    // BDI332
	    mid_BDI332_nopsToLegalCmd = env->GetStaticMethodID(cls_BDI332, "nopsToLegalCmd", "()V");
	    if (mid_BDI332_nopsToLegalCmd == 0) {
	        fprintf(stderr, "Can't find BDI332.nopsToLegalCmd\n");
	        return FALSE;
	    }
	    mid_BDI332_break_ = env->GetStaticMethodID(cls_BDI332, "break_", "()V");
	    if (mid_BDI332_break_ == 0) {
	        fprintf(stderr, "Can't find BDI332.break_\n");
	        return FALSE;
	    }
	    mid_BDI332_go = env->GetStaticMethodID(cls_BDI332, "go", "()V");
	    if (mid_BDI332_go == 0) {
	        fprintf(stderr, "Can't find BDI332.go\n");
	        return FALSE;
	    }
	    mid_BDI332_reset_target = env->GetStaticMethodID(cls_BDI332, "reset_target", "()V");
	    if (mid_BDI332_reset_target == 0) {
	        fprintf(stderr, "Can't find BDI332.reset_target\n");
	        return FALSE;
	    }
	    mid_BDI332_reset_peripherals = env->GetStaticMethodID(cls_BDI332, "reset_peripherals", "()V");
	    if (mid_BDI332_reset_peripherals == 0) {
	        fprintf(stderr, "Can't find BDI332.reset_peripherals\n");
	        return FALSE;
	    }
	    mid_BDI332_isFreezeAsserted = env->GetStaticMethodID(cls_BDI332, "isFreezeAsserted", "()Z");
	    if (mid_BDI332_isFreezeAsserted == 0) {
	        fprintf(stderr, "Can't find BDI332.isFreezeAsserted\n");
	        return FALSE;
	    }
	    mid_BDI332_fillMem = env->GetStaticMethodID(cls_BDI332, "fillMem", "([II)V");
	    if (mid_BDI332_fillMem == 0) {
	        fprintf(stderr, "Can't find BDI332.fillMem\n");
	        return FALSE;
	    }
	    mid_BDI332_dumpMem = env->GetStaticMethodID(cls_BDI332, "dumpMem", "(I)[I");
	    if (mid_BDI332_dumpMem == 0) {
	        fprintf(stderr, "Can't find BDI332.dumpMem\n");
	        return FALSE;
	    }
	    mid_BDI332_writeMem = env->GetStaticMethodID(cls_BDI332, "writeMem", "(III)V");
	    if (mid_BDI332_writeMem == 0) {
	        fprintf(stderr, "Can't find BDI332.writeMem\n");
	        return FALSE;
	    }
	    mid_BDI332_readMem = env->GetStaticMethodID(cls_BDI332, "readMem", "(II)I");
	    if (mid_BDI332_readMem == 0) {
	        fprintf(stderr, "Can't find BDI332.readMem\n");
	        return FALSE;
	    }
	    mid_BDI332_readUserReg = env->GetStaticMethodID(cls_BDI332, "readUserReg", "(I)I");
	    if (mid_BDI332_readUserReg == 0) {
	        fprintf(stderr, "Can't find BDI332.readUserReg\n");
	        return FALSE;
	    }
	    mid_BDI332_writeUserReg = env->GetStaticMethodID(cls_BDI332, "writeUserReg", "(II)V");
	    if (mid_BDI332_writeUserReg == 0) {
	        fprintf(stderr, "Can't find BDI332.writeUserReg\n");
	        return FALSE;
	    }
	    mid_BDI332_readUserReg = env->GetStaticMethodID(cls_BDI332, "readUserReg", "(I)I");
	    if (mid_BDI332_readUserReg == 0) {
	        fprintf(stderr, "Can't find BDI332.readUserReg\n");
	        return FALSE;
	    }
	    mid_BDI332_writeSysReg = env->GetStaticMethodID(cls_BDI332, "writeSysReg", "(II)V");
	    if (mid_BDI332_writeSysReg == 0) {
	        fprintf(stderr, "Can't find BDI332.writeSysReg\n");
	        return FALSE;
	    }
	    mid_BDI332_readSysReg = env->GetStaticMethodID(cls_BDI332, "readSysReg", "(I)I");
	    if (mid_BDI332_readSysReg == 0) {
	        fprintf(stderr, "Can't find BDI332.readSysReg\n");
	        return FALSE;
	    }
	    mid_BDI332_isTargetInDebugMode = env->GetStaticMethodID(cls_BDI332, "isTargetInDebugMode", "()Z");
	    if (mid_BDI332_isTargetInDebugMode == 0) {
	        fprintf(stderr, "Can't find BDI332.isTargetInDebugMode\n");
	        return FALSE;
	    }
	    
	   	// Redirect Class
	    mid_Redirect_redirect = env->GetStaticMethodID(cls_Redirect, "redirect", "()V");
	    if (mid_Redirect_redirect == 0) {
	        fprintf(stderr, "Can't find Redirect.redirect\n");
	        return FALSE;
	    }
	    
	    // Call redirect
	    env->CallStaticVoidMethod(cls_Redirect, mid_Redirect_redirect);	    

	   	// Uart0 Class
	    mid_Uart0_write = env->GetStaticMethodID(cls_Uart0, "write", "([BI)Z");
	    if (mid_Uart0_write == 0) {
	        fprintf(stderr, "Can't find Uart0.write\n");
	        return FALSE;
	    }
	    mid_Uart0_read = env->GetStaticMethodID(cls_Uart0, "read", "()[B");
	    if (mid_Uart0_read == 0) {
	        fprintf(stderr, "Can't find Uart0.read\n");
	        return FALSE;
	    }
	
	}
	return TRUE;
}

/* Exception Handling
 * This function has to be called after every function (which may throw an exception)
 * returns TRUE (0) if an exception occured, FALSE (#0) if not
 */
EXPORT int checkForExceptions() {
	jthrowable exc;
	exc = env->ExceptionOccurred();
	if (exc) {
		env->ExceptionDescribe();
		env->ExceptionClear();
		return TRUE;
	}
	return FALSE;
}

// USB_Device methods
EXPORT void USB_Device_open()
{
	env->CallStaticVoidMethod(cls_USB_Device, mid_USB_Dev_open);
}

EXPORT void USB_Device_close()
{
	env->CallStaticVoidMethod(cls_USB_Device, mid_USB_Dev_close);
}

EXPORT void USB_Device_reset()
{
	env->CallStaticVoidMethod(cls_USB_Device, mid_USB_Dev_reset);
}

// BDI 555 methods
EXPORT void BDI555_break_()
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_break_);
}

EXPORT void BDI555_go()
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_go);
}

EXPORT void BDI555_reset_target()
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_reset_target);
}

EXPORT BOOL BDI555_isFreezeAsserted()
{
	jboolean isAsserted;
	isAsserted = env->CallStaticBooleanMethod(cls_BDI555, mid_BDI555_isFreezeAsserted);
	return (isAsserted != JNI_FALSE);
}

EXPORT void BDI555_startFastDownload(int startAddr)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_startFastDownload, startAddr);
}

EXPORT void BDI555_fastDownload(int downloadData[], int dataLength)
{
	jintArray jdata;
	jdata = env->NewIntArray(dataLength);
	if (jdata == NULL) {
		jclass excCls;
		excCls = env->FindClass("java/lang/OutOfMemoryError");
		if (excCls == 0) {
			return;
		}
		env->ThrowNew(excCls, "BDI555_fastDownload: not enough memory");
		return;
	}
// TODO: check type cast
	env->SetIntArrayRegion(jdata, 0, dataLength, (jint*) downloadData);
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_fastDownload, jdata, dataLength);
	env->DeleteLocalRef(jdata);
}

EXPORT void BDI555_stopFastDownload()
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_stopFastDownload);
}

EXPORT void BDI555_writeMem(int addr, int value, int size)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeMem, addr, value, size);
}

EXPORT int BDI555_readMem(int addr, int size)
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readMem, addr, size);
}

EXPORT void BDI555_writeMemSeq(int value, int size)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeMemSeq, value, size);
}

EXPORT int BDI555_readMemSeq(int size)
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readMemSeq, size);
}

EXPORT int BDI555_readGPR(int gpr)
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readGPR, gpr);
}

EXPORT void BDI555_writeGPR(int gpr, int value)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeGPR, gpr, value);
}

EXPORT int BDI555_readSPR(int spr)
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readSPR, spr);
}

EXPORT void BDI555_writeSPR(int spr, int value)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeSPR, spr, value);
}

EXPORT int BDI555_readMSR()
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readMSR);
}

EXPORT void BDI555_writeMSR(int value)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeMSR, value);
}

EXPORT long BDI555_readFPR(int fpr, int tmpMemAddr)
{
	return env->CallStaticLongMethod(cls_BDI555, mid_BDI555_readFPR, fpr, tmpMemAddr);
}

EXPORT void BDI555_writeFPR(int fpr, int tmpMemAddr, long value)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeSPR, fpr, tmpMemAddr, value);
}

EXPORT int BDI555_readCR()
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readCR);
}

EXPORT void BDI555_writeCR(int value)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeCR, value);
}

EXPORT int BDI555_readFPSCR()
{
	return env->CallStaticIntMethod(cls_BDI555, mid_BDI555_readFPSCR);
}

EXPORT void BDI555_writeFPSCR(int value)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_writeFPSCR, value);
}

EXPORT BOOL BDI555_isTargetInDebugMode()
{
	jboolean inDebugMode;
	inDebugMode = env->CallStaticBooleanMethod(cls_BDI555, mid_BDI555_isTargetInDebugMode);
	return (inDebugMode != JNI_FALSE);
}

EXPORT void BDI555_setGpr31(int gpr31)
{
	env->CallStaticVoidMethod(cls_BDI555, mid_BDI555_setGpr31, gpr31);
}


// BDI 332 methods
EXPORT void BDI332_nopsToLegalCmd()
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_nopsToLegalCmd);
}

EXPORT void BDI332_break_()
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_break_);
}

EXPORT void BDI332_go()
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_go);
}

EXPORT void BDI332_reset_target()
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_reset_target);
}

EXPORT void BDI332_reset_peripherals()
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_reset_peripherals);
}

EXPORT BOOL BDI332_isFreezeAsserted()
{
	jboolean isAsserted;
	isAsserted = env->CallStaticBooleanMethod(cls_BDI332, mid_BDI332_isFreezeAsserted);
	return (isAsserted != JNI_FALSE);
}

EXPORT void BDI332_fillMem(int downloadData[], int dataLength)
{
	jintArray jdata;
	jdata = env->NewIntArray(dataLength);
	if (jdata == NULL) {
		jclass excCls;
		excCls = env->FindClass("java/lang/OutOfMemoryError");
		if (excCls == 0) {
			return;
		}
		env->ThrowNew(excCls, "BDI332_fillMem: not enough memory");
		return;
	}
//	for (int i = 0; i < dataLength; ++i) {
//		printf("data %d: %x\n", i, downloadData[i]);
//	}

	env->SetIntArrayRegion(jdata, 0, dataLength, (jint*) downloadData);
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_fillMem, jdata, dataLength);
	env->DeleteLocalRef(jdata);
}

EXPORT int BDI332_dumpMem(int nofData, int result[])
{
	jintArray intArray;
	jsize size;
	jint *intArrayElements;
	intArray = (jintArray) env->CallStaticObjectMethod(cls_BDI332, mid_BDI332_dumpMem, nofData);
 	if (intArray == NULL)
 		return 0;
 	size = env->GetArrayLength(intArray);
	intArrayElements = env->GetIntArrayElements(intArray, 0);
	for (int i = 0; i < size; i++) {
		result[i] = intArrayElements[i];
	}
	env->ReleaseIntArrayElements(intArray, intArrayElements, 0);
	return size;
}

EXPORT void BDI332_writeMem(int addr, int value, int size)
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_writeMem, addr, value, size);
}

EXPORT int BDI332_readMem(int addr, int size)
{
	return env->CallStaticIntMethod(cls_BDI332, mid_BDI332_readMem, addr, size);
}

EXPORT int BDI332_readUserReg(int reg)
{
	return env->CallStaticIntMethod(cls_BDI332, mid_BDI332_readUserReg, reg);
}

EXPORT void BDI332_writeUserReg(int reg, int value)
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_writeUserReg, reg, value);
}

EXPORT int BDI332_readSysReg(int reg)
{
	return env->CallStaticIntMethod(cls_BDI332, mid_BDI332_readSysReg, reg);
}

EXPORT void BDI332_writeSysReg(int reg, int value)
{
	env->CallStaticVoidMethod(cls_BDI332, mid_BDI332_writeSysReg, reg, value);
}

EXPORT BOOL BDI332_isTargetInDebugMode()
{
	jboolean isAsserted;
	isAsserted = env->CallStaticBooleanMethod(cls_BDI332, mid_BDI332_isTargetInDebugMode);
	return (isAsserted != JNI_FALSE);
}

EXPORT int UART0_read(char result[])
{
	jbyteArray byteArray;
	jsize size;
	jbyte *byteArrayElements;
	byteArray = (jbyteArray) env->CallStaticObjectMethod(cls_Uart0, mid_Uart0_read);
 	if (byteArray == NULL)
 		return 0;
 	size = env->GetArrayLength(byteArray);
	byteArrayElements = env->GetByteArrayElements(byteArray, 0);
	for (int i = 0; i < size; i++) {
		result[i] = byteArrayElements[i];
	}
	env->ReleaseByteArrayElements(byteArray, byteArrayElements, 0);
	return size;
}

EXPORT int UART0_write(char data[], int dataLength)
{
	jbyteArray jdata;
	jboolean result;
	jdata = env->NewByteArray(dataLength);
	if (jdata == NULL) {
		jclass excCls;
		excCls = env->FindClass("java/lang/OutOfMemoryError");
		if (excCls == 0) {
			return FALSE;
		}
		env->ThrowNew(excCls, "UART0_write: not enough memory");
		return FALSE;
	}
//	for (int i = 0; i < dataLength; ++i) {
//		printf("data %d: %x\n", i, data[i]);
//	}

	env->SetByteArrayRegion(jdata, 0, dataLength, (jbyte*) data);
	result = env->CallStaticBooleanMethod(cls_Uart0, mid_Uart0_write, jdata, dataLength);
	env->DeleteLocalRef(jdata);
	return result;
}
