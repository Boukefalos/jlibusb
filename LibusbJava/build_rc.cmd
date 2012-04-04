@echo off
echo #include ^<winver.h^> > %1
echo.  >> %1
echo VS_VERSION_INFO VERSIONINFO >> %1
echo FILEVERSION    %2, %3, %4, %5 >> %1
echo PRODUCTVERSION %2, %3, %4, %5 >> %1
echo FILEFLAGSMASK  0x3FL >> %1
echo FILEFLAGS      0x0L >> %1
echo FILEOS         VOS_NT_WINDOWS32 >> %1
echo FILETYPE       VFT_DLL >> %1
echo FILESUBTYPE    VFT2_UNKNOWN >> %1
echo BEGIN >> %1
echo	BLOCK "StringFileInfo" >> %1
echo     BEGIN >> %1
echo         BLOCK "040904b0"   >> %1
echo         BEGIN >> %1
echo             VALUE "CompanyName",     "http://libusbjava.sourceforge.net" >> %1
echo             VALUE "FileDescription", "JNI Library for LibusbJava stub" >> %1
echo             VALUE "FileVersion",     "%2.%3.%4.%5\0" >> %1
echo             VALUE "InternalName",    "LibusbJava-1_0.dll" >> %1
echo             VALUE "LegalCopyright",  "2012 NTB Interstate University of Applied Sciences of Technology Buchs/Chur/St.Gallen" >> %1
echo             VALUE "OriginalFilename","LibusbJava-1_0.dll" >> %1
echo             VALUE "ProductName",     "LibusbJava" >> %1
echo             VALUE "ProductVersion",  "%2.%3.%4.%5\0" >> %1
echo         END >> %1
echo     END >> %1
echo.  >> %1
echo     BLOCK "VarFileInfo" >> %1
echo     BEGIN >> %1
echo         VALUE "Translation", 0x409, 1200 >> %1
echo     END >> %1
echo END  >> %1
