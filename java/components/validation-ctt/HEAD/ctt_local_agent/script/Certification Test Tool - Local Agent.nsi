############################################################################################
#
#Script for create installer for the Certification Test Tool - Local Agent
#            
############################################################################################

!define APP_NAME "Certification Test Tool - Local Agent"
!define COMP_NAME "AT4 wireless"
!define VERSION "2.02.00.00"
!define COPYRIGHT "AllSeen Alliance 2016"
!define DESCRIPTION "Installer for CTT-Local Agent"
!define MAIN_APP_EXE "CTT_Local_Agent.exe"
!define INSTALL_TYPE "SetShellVarContext current"
!define REG_ROOT "HKCU"
!define REG_APP_PATH "Software\Microsoft\Windows\CurrentVersion\App Paths\${MAIN_APP_EXE}"
!define UNINSTALL_PATH "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
!define INSTDIR "C:\Program Files\CTTLocalAgent"
!define REG_START_MENU "Start Menu Folder"
######################################################################
;Modify values to fit your project
!define INSTALLER_NAME "C:\Users\jtf\Desktop\CTT_Local_Agent_v2.2.0_Installer.exe"
!define LOCAL_AGENT_PATH "C:\Users\jtf\Desktop\ctt_local_agent"
!define MSVS2012_PATH "C:\Users\jtf\Desktop\MSVS2012"
!define MSVS2013_PATH "C:\Users\jtf\Desktop\MSVS2013"
!define MSVS2015_PATH "C:\Users\jtf\Desktop\MSVS2015"

var SM_Folder

######################################################################

VIProductVersion  "${VERSION}"
VIAddVersionKey "ProductName"  "${APP_NAME}"
VIAddVersionKey "CompanyName"  "${COMP_NAME}"
VIAddVersionKey "LegalCopyright"  "${COPYRIGHT}"
VIAddVersionKey "FileDescription"  "${DESCRIPTION}"
VIAddVersionKey "FileVersion"  "${VERSION}"

######################################################################

SetCompressor ZLIB
Name "${APP_NAME}"
Caption "${APP_NAME}"
OutFile "${INSTALLER_NAME}"
RequestExecutionLevel admin ;Request application privileges for Windows Vista, 7, 8
BrandingText "${APP_NAME}"
XPStyle on
InstallDirRegKey "${REG_ROOT}" "${REG_APP_PATH}" ""
InstallDir "C:\Program Files\CTTLocalAgent"

######################################################################

!include LogicLib.nsh

Function .onInit
	UserInfo::GetAccountType
	pop $0
	${If} $0 != "admin" ;Require admin rights on NT4+
		MessageBox mb_iconstop "Administrator rights required!"
		SetErrorLevel 740 ;ERROR_ELEVATION_REQUIRED
		Quit
	${EndIf}
FunctionEnd

######################################################################

;Visual 2012, 2013 and 2015
!define ICON_PATH "${LOCAL_AGENT_PATH}\res\drawable"
!define MSVS2012_DIR "${MSVS2012_PATH}"
!define MSVS2013_DIR "${MSVS2013_PATH}"
!define MSVS2015_DIR "${MSVS2015_PATH}"


; Definitions for Java 8.0
!define JRE_VERSION "8.0"
!define JRE_URL "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jre-8u60-windows-i586.exe"
!define JRE64_URL "http://download.oracle.com/otn-pub/java/jdk/8u60-b27/jre-8u60-windows-x64.exe"

; use javaw.exe to avoid dosbox.
; use java.exe to keep stdout/stderr
!define JAVAEXE "javaw.exe"

;SilentInstall silent
AutoCloseWindow true
;ShowInstDetails nevershow

!include "FileFunc.nsh"
!insertmacro GetFileVersion
!insertmacro GetParameters
!include "WordFunc.nsh"
!insertmacro VersionCompare
!include "x64.nsh"


######################################################################

!include "MUI.nsh"
!define MUI_ICON "${ICON_PATH}\allseen128.ico"
!define MUI_UNICON "${ICON_PATH}\allseen128.ico"

!insertmacro MUI_PAGE_WELCOME

!ifdef LICENSE_TXT
	!insertmacro MUI_PAGE_LICENSE "${LICENSE_TXT}"
!endif

!insertmacro MUI_PAGE_DIRECTORY

!ifdef REG_START_MENU
	!define MUI_STARTMENUPAGE_DEFAULTFOLDER "CTTLocalAgent"
	!define MUI_STARTMENUPAGE_REGISTRY_ROOT "${REG_ROOT}"
	!define MUI_STARTMENUPAGE_REGISTRY_KEY "${UNINSTALL_PATH}"
	!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${REG_START_MENU}"
	!insertmacro MUI_PAGE_STARTMENU Application $SM_Folder
!endif

!insertmacro MUI_PAGE_INSTFILES

!define MUI_FINISHPAGE_RUN "$INSTDIR\${MAIN_APP_EXE}"
!insertmacro MUI_PAGE_FINISH

!insertmacro MUI_UNPAGE_CONFIRM

!insertmacro MUI_UNPAGE_INSTFILES


!insertmacro MUI_UNPAGE_FINISH

!insertmacro MUI_LANGUAGE "English"

######################################################################

Section -MainProgram
${INSTALL_TYPE}

SetOverwrite ifnewer
SetOutPath "$INSTDIR"
File "${LOCAL_AGENT_PATH}\config.xml"
File "${LOCAL_AGENT_PATH}\CTT_Local_Agent.exe"
File /r "${LOCAL_AGENT_PATH}\lib"
File /r "${LOCAL_AGENT_PATH}\testcases"

SetOutPath "$INSTDIR\res\drawable"
File "${LOCAL_AGENT_PATH}\res\drawable\*.*"

SectionEnd

######################################################################
Section -Icons_Reg
SetOutPath "$INSTDIR"
WriteUninstaller "$INSTDIR\uninstall.exe"

!ifdef REG_START_MENU
!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
CreateDirectory "$SMPROGRAMS\$SM_Folder"
CreateShortCut "$SMPROGRAMS\$SM_Folder\${APP_NAME}.lnk" "$INSTDIR\${MAIN_APP_EXE}"
CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${MAIN_APP_EXE}" "" "$INSTDIR\res\drawable\allseen128.ico" 0
CreateShortCut "$SMPROGRAMS\$SM_Folder\Uninstall ${APP_NAME}.lnk" "$INSTDIR\uninstall.exe"

!ifdef WEB_SITE
WriteIniStr "$INSTDIR\${APP_NAME} website.url" "InternetShortcut" "URL" "${WEB_SITE}"
CreateShortCut "$SMPROGRAMS\$SM_Folder\${APP_NAME} Website.lnk" "$INSTDIR\${APP_NAME} website.url"
!endif
!insertmacro MUI_STARTMENU_WRITE_END
!endif

!ifndef REG_START_MENU
CreateDirectory "$SMPROGRAMS\CTTLocalAgent"
CreateShortCut "$SMPROGRAMS\CTTLocalAgent\${APP_NAME}.lnk" "$INSTDIR\${MAIN_APP_EXE}"
CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${MAIN_APP_EXE}"
CreateShortCut "$SMPROGRAMS\CTTLocalAgent\Uninstall ${APP_NAME}.lnk" "$INSTDIR\uninstall.exe"

!ifdef WEB_SITE
WriteIniStr "$INSTDIR\${APP_NAME} website.url" "InternetShortcut" "URL" "${WEB_SITE}"
CreateShortCut "$SMPROGRAMS\CTTLocalAgent\${APP_NAME} Website.lnk" "$INSTDIR\${APP_NAME} website.url"
!endif
!endif

WriteRegStr ${REG_ROOT} "${REG_APP_PATH}" "" "$INSTDIR\${MAIN_APP_EXE}"
WriteRegStr ${REG_ROOT} "${UNINSTALL_PATH}"  "DisplayName" "${APP_NAME}"
WriteRegStr ${REG_ROOT} "${UNINSTALL_PATH}"  "UninstallString" "$INSTDIR\uninstall.exe"
WriteRegStr ${REG_ROOT} "${UNINSTALL_PATH}"  "DisplayIcon" "$INSTDIR\${MAIN_APP_EXE}"
WriteRegStr ${REG_ROOT} "${UNINSTALL_PATH}"  "DisplayVersion" "${VERSION}"
WriteRegStr ${REG_ROOT} "${UNINSTALL_PATH}"  "Publisher" "${COMP_NAME}"

!ifdef WEB_SITE
WriteRegStr ${REG_ROOT} "${UNINSTALL_PATH}"  "URLInfoAbout" "${WEB_SITE}"
!endif
SectionEnd

######################################################################

Section Uninstall
${INSTALL_TYPE}
Delete "$INSTDIR\config.xml"
Delete "$INSTDIR\icon.ico"
Delete "$INSTDIR\CTT_Local_Agent.exe"
Delete "$INSTDIR\testcases\*.jar"
Delete "$INSTDIR\res\drawable\*.*"
Delete "$INSTDIR\log\*.log"
RmDir /r "$INSTDIR\lib\"
RmDir "$INSTDIR\res\drawable"
RmDir "$INSTDIR\res"
RmDir "$INSTDIR\log"
RmDir /r "$INSTDIR\testcases"
RmDir "$INSTDIR"
 
Delete "$INSTDIR\uninstall.exe"
!ifdef WEB_SITE
Delete "$INSTDIR\${APP_NAME} website.url"
!endif

RmDir "$INSTDIR"

!ifdef REG_START_MENU
!insertmacro MUI_STARTMENU_GETFOLDER "Application" $SM_Folder
Delete "$SMPROGRAMS\$SM_Folder\${APP_NAME}.lnk"
Delete "$SMPROGRAMS\$SM_Folder\Uninstall ${APP_NAME}.lnk"
!ifdef WEB_SITE
Delete "$SMPROGRAMS\$SM_Folder\${APP_NAME} Website.lnk"
!endif
Delete "$DESKTOP\${APP_NAME}.lnk"

RmDir "$SMPROGRAMS\$SM_Folder"
!endif

!ifndef REG_START_MENU
Delete "$SMPROGRAMS\CTTLocalAgent\${APP_NAME}.lnk"
Delete "$SMPROGRAMS\CTTLocalAgent\Uninstall ${APP_NAME}.lnk"
!ifdef WEB_SITE
Delete "$SMPROGRAMS\CTTLocalAgent\${APP_NAME} Website.lnk"
!endif
Delete "$DESKTOP\${APP_NAME}.lnk"

RmDir "$SMPROGRAMS\CTTLocalAgent"
!endif

DeleteRegKey ${REG_ROOT} "${REG_APP_PATH}"
DeleteRegKey ${REG_ROOT} "${UNINSTALL_PATH}"
SectionEnd

######################################################################



Section ""
  Call GetJRE
 ; Pop $R0

  ; change for your purpose (-jar etc.)
 ; ${GetParameters} $1
  ;StrCpy $0 '"$R0" -classpath update.jar;pms.jar -Xmx768M -Dfile.encoding=UTF-8 ${CLASS} $1'

 ; SetOutPath $EXEDIR
  ;Exec $0
SectionEnd

;  returns the full path of a valid java.exe

Function GetJRE
    Push $R0
    Push $R1
    Push $2

  
  ;  Check for registry
  CheckRegistry1:
    ClearErrors
   ; We use x64
 	SetRegView 64
 	
    ReadRegStr $R1 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors CheckRegistry2     
    IfFileExists $R0 0 CheckRegistry2
    Call CheckJREVersion
    IfErrors CheckRegistry2 JreFound

  ;  Check for registry 
  CheckRegistry2:
   ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
   IfErrors CheckRegistry3
    IfFileExists $R0 0 CheckRegistry3
    Call CheckJREVersion
    IfErrors CheckRegistry3 JreFound

  ;  Check for registry
  CheckRegistry3:
    ClearErrors
  
 	Goto DownloadJRE
 	
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors CheckRegistry4
    IfFileExists $R0 0 CheckRegistry4
    Call CheckJREVersion
    IfErrors CheckRegistry4 JreFound

  ;  Check for registry
  CheckRegistry4:
    ClearErrors
    ReadRegStr $R1 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\Wow6432Node\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfErrors DownloadJRE
    IfFileExists $R0 0 DownloadJRE
    Call CheckJREVersion
    IfErrors DownloadJRE JreFound

  DownloadJRE:
    
    MessageBox MB_ICONINFORMATION "Certification Test Tool-Local Agent needs to install Java 8 (64 bits). Press OK to continue"
	ExecShell "open" "http://www.oracle.com/technetwork/es/java/javase/downloads/jre8-downloads-2133155.html"
  

    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${JAVAEXE}"
    IfFileExists $R0 0 GoodLuck
    Call CheckJREVersion
    IfErrors GoodLuck JreFound

  ; wishing you good luck
  GoodLuck:
    StrCpy $R0 "${JAVAEXE}"
     

  JreFound:
   
FunctionEnd

; Pass the "javaw.exe" path by $R0
Function CheckJREVersion
    Push $R1

    ; Get the file version of javaw.exe
    ${GetFileVersion} $R0 $R1
    ${VersionCompare} ${JRE_VERSION} $R1 $R1

    ; Check whether $R1 != "1"
    ClearErrors
    StrCmp $R1 "1" 0 CheckDone
    SetErrors

  CheckDone:
    Pop $R1
FunctionEnd


Section "MyApp" MyApp
 
SetOutPath "$INSTDIR"
 
;Check if we have redistributable 2012 installed
Call  CheckRedistributable2012Installed
Pop $R0
 
${If} $R0 == 0
  SetOutPath "$INSTDIR\msvs2012"
  File "${MSVS2012_DIR}\vcredist_x64.exe" 	
  ExecWait '"$INSTDIR\msvs2012\vcredist_x64.exe"  /passive /norestart'
${EndIf}

;Check if we have redistributable 2013 installed
Call  CheckRedistributable2013Installed
Pop $R0
 
${If} $R0 == 0
  SetOutPath "$INSTDIR\msvs2013"
  File "${MSVS2013_DIR}\vcredist_x64.exe" 	
  ExecWait '"$INSTDIR\msvs2013\vcredist_x64.exe"  /passive /norestart'
${EndIf}

;Check if we have redistributable 2015 installed
Call  CheckRedistributable2015Installed
Pop $R0
 
${If} $R0 == 0
  SetOutPath "$INSTDIR\msvs2015"
  File "${MSVS2015_DIR}\vc_redist.x64.exe" 	
  ExecWait '"$INSTDIR\msvs2015\vc_redist.x64.exe"  /passive /norestart'
${EndIf}

SetOutPath "$INSTDIR"

;do other stuff
 
SectionEnd

 
Function CheckRedistributable2012Installed

	; Microsoft Visual C++ 2012 Redistributable (x64) - 11.0.61030
	
	Push $R0
	ClearErrors
   
	; try to read Installed subkey to R0
	ReadRegDword $R0 HKLM "SOFTWARE\Wow6432Node\Microsoft\VisualStudio\11.0\VC\Runtimes\x64" "Installed"
 
	; if there was an error, set as non installed
	IfErrors 0 NoErrors
	StrCpy $R0 0
	NoErrors:
  
    Exch $R0 
FunctionEnd

Function CheckRedistributable2013Installed

	; Microsoft Visual C++ 2013 Redistributable (x64) - 12.0.30501
   
	Push $R0
	ClearErrors
   
	; try to read Installed subkey to R0
	ReadRegDword $R0 HKLM "SOFTWARE\Wow6432Node\Microsoft\VisualStudio\12.0\VC\Runtimes\x64" "Installed"
 
	; if there was an error, set as non installed
	IfErrors 0 NoErrors
	StrCpy $R0 0
	NoErrors:
  
    Exch $R0 
FunctionEnd

Function CheckRedistributable2015Installed

	; Microsoft Visual C++ 2015 Redistributable (x64) - 14.0.23026
   
	Push $R0
	ClearErrors
   
	; try to read Installed subkey to R0
	ReadRegDword $R0 HKLM "SOFTWARE\Wow6432Node\Microsoft\VisualStudio\14.0\VC\Runtimes\x64" "Installed"
 
	; if there was an error, set as non installed
	IfErrors 0 NoErrors
	StrCpy $R0 0
	NoErrors:
  
	Exch $R0 
FunctionEnd

Function .onInstSuccess

	RMDir /r "$INSTDIR\msvs2012"
	RMDir /r "$INSTDIR\msvs2013"
	RMDir /r "$INSTDIR\msvs2015"
FunctionEnd




