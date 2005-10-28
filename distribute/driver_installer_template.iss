; This examples demonstrates how libusb's drivers
; can be installed automatically along with your application using an installer.
;
; Requirements: Inno Setup (http://www.jrsoftware.org/isdl.php)
;
; To use this script, do the following:
; - copy libusb's driver (libusb0.sys, libusb0.dll) to this folder
; - create an .inf and .cab file using libusb's 'inf-wiward.exe'
;   and save the generated files in this folder.
; - in this script replace <your_inf_file.inf> with the name of your .inf file
; - customize other settings (strings)
; - open this scipt with Inno Setup
; - compile and run

[Setup]
AppName=NTB MC-Interface
AppVerName=NTB MC-Interface 0.0.1
AppPublisher=NTB
AppPublisherURL=http://inf.ntb.ch/
AppVersion=0.0.1
DefaultDirName={pf}\MC-Interface
DefaultGroupName=MC-Interface
Compression=lzma
SolidCompression=yes
; WinMe or higher
MinVersion=4.9,5
PrivilegesRequired=admin

[Files]
; copy the file to the App folder
Source: "*.sys"; DestDir: "{app}\driver"
Source: "*.cat"; DestDir: "{app}\driver"
Source: "*.dll"; DestDir: "{app}\driver"
Source: "*.inf"; DestDir: "{app}\driver"

; also copy the DLL to the system folders so that rundll32.exe will find it
Source: "*.dll"; DestDir: "{win}\system32"; FLags: replacesameversion restartreplace uninsneveruninstall

[Icons]
Name: "{group}\Uninstall MC-Interface"; Filename: "{uninstallexe}"

[Run]
; invoke libusb's DLL to install the .inf file
Filename: "rundll32"; Parameters: "libusb0.dll,usb_install_driver_np_rundll {app}\driver\MCInt.inf"; StatusMsg: "Installing NTB MC-Interface driver (this may take a few seconds) ..."


