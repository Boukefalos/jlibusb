#include <stdio.h>
#include <jni.h>

#if defined(_WIN32) || defined(_WIN64)
#	include <windows.h>

	typedef HINSTANCE	tLibHandle;
	typedef FARPROC		tCallbackPtr;
#else
#	error	"Unit-Tests are not available on this platform"
#endif

#include "CuTest.h"

#define PATH_SEPARATOR ';' /* define it to be ':' on Solaris */
#define USER_CLASSPATH "../java/bin" /* where Prog.class is */

const char *lib_paths[] = {
#if defined(_WIN32) || defined(_WIN64)
		"server\\jvm.dll",
		"client\\jvm.dll",
		"..\\jre\\bin\\server\\jvm.dll",
		"..\\jre\\bin\\client\\jvm.dll",
#else
#	error	"Library names are unknown for this platform"
#endif
		NULL
};

/*!	\brief Signature of the JNI call for creating a new VM */
typedef jint (CALLBACK *tJavaVMcreate)(JavaVM** jvm, void** env, JavaVMInitArgs* args);

/*!	\brief Structure holding the data for the created JVM */
typedef struct JVM {
	JavaVM *jvm;	/*!< Pointer to the JVM the tests run in */
	JNIEnv *env;	/*!< Pointer to the environment received while creating the
						 JVM */
}tJVM;

static inline	int				CreateVM(tJVM *vmdata);
static inline	tCallbackPtr	JvmLibraryFunctionGet(const char *name);
static inline	void			JvmLibraryFree(void);
static inline	tLibHandle		JvmLibraryLoad(void);
static inline	long int		JvmLibraryLastErrorGet(void);
static inline 	int				RunAllTests(void);

/*!	\brief Contains Environment data for the test program */
static struct
{
	struct {
		tLibHandle		lib;		/*!< Contains the handle for the loaded JVM-Library */
		tJavaVMcreate	VMcreate;	/*!< Holds the pointer to the call for creating a JVM
										 instance. */
	}jni;

	tJVM	jvm;					/*!< Contains the data for the created JVM used
										 for tests. */

	JNIEnv*	thread_env;				/*!< Holds the pointer to the environment that can be
										 used for the tests. (after the thread has been
										 attached) */
}globals = { { NULL, NULL }, { NULL, NULL }, NULL };
int main(void)
{
	int test_fail_count = -1;

	jint 	result = -1;

	globals.jni.lib = JvmLibraryLoad();
	if (globals.jni.lib == NULL)
	{
		fprintf(stderr, "Error loading library (%ld)\n", JvmLibraryLastErrorGet());
		goto end_no_jvm_lib;
	}

	// get the address of the JNI call to create the VM from the loaded library.
	globals.jni.VMcreate = (tJavaVMcreate)JvmLibraryFunctionGet("JNI_CreateJavaVM");
	if (globals.jni.VMcreate == NULL)
	{
		fprintf(stderr, "Error getting JNI_CreateJavaVM (%ld)", JvmLibraryLastErrorGet());
		goto end_no_CreateJavaVM;
	}

	// Create the VM the tests should be done within
	result = CreateVM(&globals.jvm);
	if (result < 0) {
		fprintf(stderr, "Can't create Java VM (%ld)\n", result);
		goto end_no_java_vm;
	}

	// In order to be able to interact with the VM we have to attach our thread to the VM
	result = globals.jvm.jvm->AttachCurrentThread((void**)&globals.thread_env, NULL);
	if (result < 0) {
		fprintf(stderr, "Can't attach thread to Java VM (%ld)\n", result);
		goto end_no_attach;
	}

	test_fail_count = RunAllTests();

	/* Check if there is still a pending exception. Usually all the tests should clear the
	 * exceptions if any have been expected. If this is not the case something went wrong... */
	if (globals.jvm.env->ExceptionOccurred()) {
		globals.jvm.env->ExceptionDescribe();
		test_fail_count++;
	}

	/* After cleaning up the java environment we can safely detach our thread from the JVM */
	globals.jvm.jvm->DetachCurrentThread();

end_no_attach:
	globals.jvm.jvm->DestroyJavaVM();

end_no_java_vm:
end_no_CreateJavaVM:
	/* Unload the JNI Library */
	JvmLibraryFree();

end_no_jvm_lib:
	return test_fail_count;

}

typedef CuSuite* (*tSuiteNew)(void);
typedef CuSuite* (*tGetDLLtests)(tSuiteNew SuiteNew, JNIEnv *env);

/*!	\brief Executes all the tests
 *
 * 	\return Number of tests that failed */
static inline int RunAllTests(void)
{
	int result = 0;
	tGetDLLtests getTestSuite = NULL;
	tLibHandle libusb = LoadLibrary("LibusbJava-1_0.dll");

	if (libusb == NULL) {
		printf("Failed to load LibusbJava-1_0.dll: %lu", GetLastError());
		goto no_lib;
	}

	getTestSuite = (tGetDLLtests)GetProcAddress(libusb, "GetLibusbJavaSuite");
	if (getTestSuite == NULL)
	{
		printf("Failed to get unit tests: %lu", GetLastError());
		goto no_suite_new;
	}

	/* Run the test procedures */
	{
		CuSuite *suite = getTestSuite(&CuSuiteNew, globals.thread_env);
		CuString *output = CuStringNew();


		CuSuiteRun(suite);
		CuSuiteSummary(suite, output);
		CuSuiteDetails(suite, output);

		printf("%s\n", output->buffer);

		result = suite->failCount;
	}

	FreeLibrary(libusb);

	return result;

no_suite_new:
	FreeLibrary(libusb);

no_lib:
	return -1;
}

/*!	\brief Creates a java virtual machine and places all the received handles into
 * 		   the given structure
 *
 *	\param	vmdata	Pointer to the structure that should hold all the handles
 *					of the VM created.
 *
 *	\return
 *			- JNI_OK on success
 *			- <0 on failure (see jni.h)
 */
static inline int CreateVM(tJVM *vmdata)
{
	JavaVMInitArgs vm_args;
	JavaVMOption   options[1];

	options[0].optionString = "-Djava.class.path=" USER_CLASSPATH;
	vm_args.version = JNI_VERSION_1_2;
	vm_args.options = options;
	vm_args.nOptions = 1;
	vm_args.ignoreUnrecognized = JNI_TRUE;

	/* Create the Java VM for unit tests */
	return globals.jni.VMcreate(&vmdata->jvm, (void**) &vmdata->env, &vm_args);
}

/*!	\brief 	Searches and tries to load the JVM library
 *
 * 			As there are some issues to find the appropriate library in the given path
 * 			this function searches a several options for loading a library.
 *
 * 	\return
 * 			- Handle for the loaded library
 * 			- NULL if the library could not be loaded. Use GetLastError() to determine
 * 			  the reason.
 */
static inline tLibHandle JvmLibraryLoad(void)
{
	unsigned int pos = 0;
	tLibHandle result = NULL;

	while ((result == NULL) && (lib_paths[pos] != NULL))
	{
#if defined(_WIN32) || defined(_WIN64)
		result = LoadLibrary(lib_paths[pos++]);
#else
#	error	"Unable to load the jvm library for this platform"
#endif
	}

	return result;
}

/*!	\brief	Wrapper for freeing a loaded JVM library */
static inline void	JvmLibraryFree(void)
{
#if defined(_WIN32) || defined(_WIN64)
	FreeLibrary(globals.jni.lib);
#else
#	error	"Unable to unload the jvm library for this platform"
#endif
}

/*!	\brief 	Wrapper for getting a specific function pointer from the loaded JVM library
 *
 * 	\param	name	Name of the function to be retrieved.
 *
 * 	\return Function pointer to the given function or NULL if the function could not be found
 */
static inline tCallbackPtr	JvmLibraryFunctionGet(const char *name)
{
#if defined(_WIN32) || defined(_WIN64)
	return GetProcAddress(globals.jni.lib, name);
#else
#	error	"Unable to get library function pointer for this platform"
#endif
}

/*!	\brief	Wrapper for getting the reason why the last JvmLibrary*-Call failed
 *
 * 	\return	Errorcode describing the error that happened. */
static inline long int JvmLibraryLastErrorGet(void)
{
#if defined(_WIN32) || defined(_WIN64)
	return GetLastError();
#else
#	error	"Unable to determine last error for this platform"
#endif
}
