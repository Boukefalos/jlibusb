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
AppName=USB Programming and Debugging Interface
AppVerName=USB Programming and Debugging Interface 0.0.2
AppPublisher=NTB
AppPublisherURL=http://inf.ntb.ch/
AppVersion=0.0.2
DefaultDirName={pf}\USB PD-Interface
DefaultGroupName=USB PD-Interface
Compression=lzma
SolidCompression=yes
ChangesEnvironment=yes
; WinMe or higher
MinVersion=4.9,5
PrivilegesRequired=admin
WizardImageFile=setupUSBPDI_2.bmp
WizardSmallImageFile=setupUSBPDI_2small.bmp

[Files]
; copy the file to the App folder
Source: "*.sys"; DestDir: "{app}\driver"
Source: "*.cat"; DestDir: "{app}\driver"
Source: "*.dll"; DestDir: "{app}\driver"
Source: "*.inf"; DestDir: "{app}\driver"

; also copy the DLL to the system folders so that rundll32.exe will find it
Source: "*.dll"; DestDir: "{win}\system32"; FLags: replacesameversion restartreplace uninsneveruninstall

[Icons]
Name: "{group}\Uninstall USB PD-Interface"; Filename: "{uninstallexe}"

[Run]
; invoke libusb's DLL to install the .inf file
Filename: "rundll32"; Parameters: "libusb0.dll,usb_install_driver_np_rundll {app}\driver\USBPDI.inf"; StatusMsg: "Installing Programming and Debugging Interface driver (this may take a few seconds) ..."

[Code]
const
 WM_SETTINGCHANGE = $1A;
 SMTO_ABORTIFHUNG = $2;

function SendMessageTimeout(hwnd :LongInt; msg :LongInt; wParam :LongInt;
 lParam :String; fuFlags :LongInt; uTimeout :LongInt; var lpdwResult :LongInt): LongInt;
 external 'SendMessageTimeoutA@user32.dll stdcall';

// NotifyWindows: The function will notify other running applications
//                (notably Windows Explorer) that they should reload their environment
//                variables from the registry.
//                On Windows NT platforms, if the funtion is not called, the
//                environment variable will not be seen by applications launched from
//                Explorer until the user logs off or restarts the computer.
procedure NotifyWindows();
var
 res :LOngInt;
begin
 SendMessageTimeout(HWND_BROADCAST, WM_SETTINGCHANGE, 0,'Environment', SMTO_ABORTIFHUNG, 5000, res);
end;

{returns true if s1 contains s2, else false (Case insensitive)}
function ContainsString(const s1, s2 :String):Boolean;
var
 s1Lower, s2Lower :String; i, j, s1Length, s2Length:Integer;
begin
 s1Lower := Lowercase(s1);
 s2Lower := Lowercase(s2);
 s1Length := Length(s1);
 s2Length := Length(s2);
 if s1Length < s2Length then begin
  Result := False;
  Exit;
 end;
 j := 1;
 for i := 1 TO s1Length do begin
  if j > s2Length then begin
   Result := True;
   Exit
  end;
  if s1Lower[i] = s2Lower[j] then begin
   j := j + 1;
  end else begin
   j := 1;
  end
 end;
 Result := False;
end;

function InitializeSetup(): Boolean;
var
 Rootkey, Res: Integer; SubKeyName, ResultStr, ValueName, messageStr, JREPath, JREBin, JREBinClient, EnvPath: String;
 javaInstalled, continueOnError, writeRegistry: Boolean;
begin
 javaInstalled := True;
 continueOnError := False;
 rootKey := HKLM;
 SubKeyName := 'SOFTWARE\JavaSoft\Java Runtime Environment';
 ValueName := 'CurrentVersion';
 if not RegQueryStringValue(RootKey, SubKeyName, ValueName, ResultStr) then
  javaInstalled := False;
 if javaInstalled then begin
  if not RegQueryStringValue(RootKey, SubKeyName, ValueName, ResultStr) then begin
   javaInstalled := False;
  end else begin
   SubKeyName := SubKeyName + '\' + ResultStr;
   ValueName := 'JavaHome';
   if not RegQueryStringValue(RootKey, SubKeyName, ValueName, JREPath) then
    javaInstalled := False;
  end;
 end;
 if not javaInstalled then begin
  messageStr := 'No Java Runtime Environment found!' #13#10;
  messageStr := messageStr + 'The JRE is needed to run this program.' #13#13;
  messageStr := messageStr + 'If you continue, you need to set your PATH environment variable manualy to' #13#10;
  messageStr := messageStr + 'JRE\bin and JRE\bin\client after installing the JRE from java.sun.com.' #13#13;
  messageStr := messageStr + 'Do you want to continue?';
  if MsgBox(messageStr, mbError, MB_YESNO) = idYes then
   continueOnError := True;
 end;
 if javaInstalled then begin
  Result := True;
  {JREPath contains the path to the JRE directory (e.g. C:\Program Files\Java\jre1.5.0_06)}
  {get the USER Path variable}
  RootKey := HKCU;
  SubKeyName := 'Environment';
  ValueName := 'PATH';
  JREBin := JREPath + '\bin';
  JREBinClient := JREPath + '\bin\client';
  {check if PATH is already set}
  if not RegValueExists(RootKey, SubKeyName, ValueName) then begin
   {create new PATH entry}
   messageStr := 'In order to run the program the path to your JRE\bin and JRE\bin\client must be added.' #13#10;
   messageStr := messageStr + 'Setup will added this paths to your User PATH variable.' #13#13;
   messageStr := messageStr + 'Do you want Setup to modify your PATH variable?';
   if MsgBox(messageStr, mbConfirmation, MB_YESNO) = idYes then begin
    RegWriteStringValue(RootKey, SubKeyName, ValueName, JREBin + ';' + JREBinClient);
    NotifyWindows();
   end
  end else begin
   if RegQueryStringValue(RootKey, SubKeyName, ValueName, EnvPath) then begin
    writeRegistry := False;
    {check if EnvPath contains the right paths}
    if not ContainsString(EnvPath, JREBin) then begin
      {add to Path}
      Res := Length(EnvPath);
      if EnvPath[Res] = ';' then begin
      end else begin
       EnvPath := EnvPath + ';';
      end;
      EnvPath := EnvPath + JREBin;
      writeRegistry := True;
    end
    if not ContainsString(EnvPath, JREBinClient) then begin
      {add to Path}
      Res := Length(EnvPath);
      if EnvPath[Res] = ';' then begin
      end else begin
       EnvPath := EnvPath + ';';
      end;
      EnvPath := EnvPath + JREBinClient;
      writeRegistry := True;
    end
    if writeRegistry then begin
     messageStr := 'In order to run the program the path to your JRE\bin and JRE\bin\client must be added.' #13#10;
     messageStr := messageStr + 'Setup will added this paths to your User PATH variable.' #13#13;
     messageStr := messageStr + 'Do you want Setup to modify your PATH variable?';
     if MsgBox(messageStr, mbConfirmation, MB_YESNO) = idYes then begin
      RegWriteStringValue(RootKey, SubKeyName, ValueName, EnvPath);
      NotifyWindows();
     end
    end
   end else
    continueOnError := True;
  end;
 end else
  Result := False;
 if continueOnError then
  Result := True;
end;

