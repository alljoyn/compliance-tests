############################################################################################
#
#Script for create installer for the Certification Test Tool - Local Agent
#            
############################################################################################

!define APP_NAME "Certification Test Tool - Local Agent"
!define COMP_NAME "AT4 wireless"
!define WEB_SITE "http://www.at4wireless.com/"
!define VERSION "1.00.00.00"
!define COPYRIGHT "AllSeen Alliance 2015"
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
!define INSTALLER_NAME "C:\Users\at4wireless\Desktop\CTT_Local_Agent_v1.0.0_Installer.exe"
!define LOCAL_AGENT_PATH "C:\Users\at4wireless\Desktop\TestToolLocalAgent-4"
!define MSVS2012_PATH "C:\Users\at4wireless\Desktop\MSVS2012"

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
BrandingText "${APP_NAME}"
XPStyle on
InstallDirRegKey "${REG_ROOT}" "${REG_APP_PATH}" ""
InstallDir "C:\Program Files\CTTLocalAgent"

######################################################################


;Visual 2012
!define ICON_PATH "${LOCAL_AGENT_PATH}\res\drawable"
!define MSVS_DIR "${MSVS2012_PATH}"
 
;Request application privileges for Windows Vista, 7, 8
RequestExecutionLevel admin 
 


; Definitions for Java 7.0
!define JRE_VERSION "7.0"
!define JRE_URL "http://download.oracle.com/otn-pub/java/jdk/7u2-b13/jre-7u2-windows-i586.exe"
!define JRE64_URL "http://download.oracle.com/otn-pub/java/jdk/7u2-b13/jre-7u2-windows-x64.exe"

; use javaw.exe to avoid dosbox.
; use java.exe to keep stdout/stderr
!define JAVAEXE "javaw.exe"

RequestExecutionLevel user
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
!define MUI_ICON "${ICON_PATH}\icon.ico"
 !define MUI_UNICON "${ICON_PATH}\icon.ico"

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

SetOutPath "$INSTDIR\TestToolLocalAgent_lib"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\commons-logging-1.2.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\forms-1.3.0.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\httpclient-4.4.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\httpcore-4.4.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\httpmime-4.4.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\jersey-client-1.18.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\jersey-core-1.18.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\miglayout15-swing.jar"
File "${LOCAL_AGENT_PATH}\TestToolLocalAgent_lib\seaglasslookandfeel-0.2.jar"
SetOutPath "$INSTDIR\TestCases"
File "${LOCAL_AGENT_PATH}\TestCases\TestCases_Package_v14.06.00a.jar"
File "${LOCAL_AGENT_PATH}\TestCases\TestCases_Package_v14.12.00a.jar"
SetOutPath "$INSTDIR\res\drawable"
File "${LOCAL_AGENT_PATH}\res\drawable\back.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\footer.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\header.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\ico_close.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\ico_login.png"
File "${LOCAL_AGENT_PATH}\res\drawable\ico_next.png"
File "${LOCAL_AGENT_PATH}\res\drawable\ico_refresh.png"
File "${LOCAL_AGENT_PATH}\res\drawable\icon.ico"
File "${LOCAL_AGENT_PATH}\res\drawable\install.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\log.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\MainWindowBackground.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\MainWindowBackground2.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\results.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\run.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\run_all.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\save.jpg"
File "${LOCAL_AGENT_PATH}\res\drawable\short_footer.jpg"
SetOutPath "$INSTDIR\lib14.12.00"
File "${LOCAL_AGENT_PATH}\lib14.12.00\alljoyn_java.dll"
File "${LOCAL_AGENT_PATH}\lib14.12.00\alljoyn_java.exp"
File "${LOCAL_AGENT_PATH}\lib14.12.00\alljoyn_java.lib"
SetOutPath "$INSTDIR\lib14.06.00"
File "${LOCAL_AGENT_PATH}\lib14.06.00\alljoyn_java.dll"
File "${LOCAL_AGENT_PATH}\lib14.06.00\alljoyn_java.exp"
File "${LOCAL_AGENT_PATH}\lib14.06.00\alljoyn_java.lib"
SectionEnd

######################################################################
Section -Icons_Reg
SetOutPath "$INSTDIR"
WriteUninstaller "$INSTDIR\uninstall.exe"

!ifdef REG_START_MENU
!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
CreateDirectory "$SMPROGRAMS\$SM_Folder"
CreateShortCut "$SMPROGRAMS\$SM_Folder\${APP_NAME}.lnk" "$INSTDIR\${MAIN_APP_EXE}"
CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${MAIN_APP_EXE}" "" "$INSTDIR\res\drawable\icon.ico" 0
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
Delete "$INSTDIR\TestToolLocalAgent_lib\commons-logging-1.2.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\forms-1.3.0.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\httpclient-4.4.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\httpcore-4.4.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\httpmime-4.4.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\jersey-client-1.18.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\jersey-core-1.18.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\miglayout15-swing.jar"
Delete "$INSTDIR\TestToolLocalAgent_lib\seaglasslookandfeel-0.2.jar"
Delete "$INSTDIR\TestCases\TestCases_Package_v14.06.00a.jar"
Delete "$INSTDIR\TestCases\TestCases_Package_v14.12.00a.jar"
Delete "$INSTDIR\res\drawable\back.jpg"
Delete "$INSTDIR\res\drawable\footer.jpg"
Delete "$INSTDIR\res\drawable\header.jpg"
Delete "$INSTDIR\res\drawable\ico_close.jpg"
Delete "$INSTDIR\res\drawable\ico_login.png"
Delete "$INSTDIR\res\drawable\ico_next.png"
Delete "$INSTDIR\res\drawable\ico_refresh.png"
Delete "$INSTDIR\res\drawable\ic_AllSeen.ico"
Delete "$INSTDIR\res\drawable\install.jpg"
Delete "$INSTDIR\res\drawable\log.jpg"
Delete "$INSTDIR\res\drawable\MainWindowBackground.jpg"
Delete "$INSTDIR\res\drawable\MainWindowBackground2.jpg"
Delete "$INSTDIR\res\drawable\results.jpg"
Delete "$INSTDIR\res\drawable\run.jpg"
Delete "$INSTDIR\res\drawable\run_all.jpg"
Delete "$INSTDIR\res\drawable\save.jpg"
Delete "$INSTDIR\res\drawable\short_footer.jpg"
Delete "$INSTDIR\lib14.12.00\alljoyn_java.dll"
Delete "$INSTDIR\lib14.12.00\alljoyn_java.exp"
Delete "$INSTDIR\lib14.12.00\alljoyn_java.lib"
Delete "$INSTDIR\lib14.06.00\alljoyn_java.dll"
Delete "$INSTDIR\lib14.06.00\alljoyn_java.exp"
Delete "$INSTDIR\lib14.06.00\alljoyn_java.lib"
 
RmDir "$INSTDIR\lib14.06.00"
RmDir "$INSTDIR\lib14.12.00"
RmDir "$INSTDIR\res\drawable"
RmDir "$INSTDIR\res"
RmDir "$INSTDIR\log"
RmDir "$INSTDIR\TestCases"
RmDir "$INSTDIR\TestToolLocalAgent_lib"
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
    
    MessageBox MB_ICONINFORMATION "Certification Test Tool-Local Agent needs to install Java 7 (64 bits). Press OK to continue"
	ExecShell "open" "http://www.oracle.com/technetwork/es/java/javase/downloads/jre7-downloads-1880261.html"
  

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
 
;Check if we have redistributable installed
Call  CheckRedistributableInstalled
Pop $R0
 
${If} $R0 == "Error"
  File "${MSVS_DIR}\vcredist_x64.exe" 	
  ExecWait '"$INSTDIR\vcredist_x64.exe"  /passive /norestart'	
${EndIf}
 
;do other stuff
 
SectionEnd

 
Function CheckRedistributableInstalled
 
  ;{F0C3E5D1-1ADE-321E-8167-68EF0DE699A5} - msvs2010 sp1
  ;{CF2BEA3C-26EA-32F8-AA9B-331F7E34BA97} - msvs2012 sp1    HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{CF2BEA3C-26EA-32F8-AA9B-331F7E34BA97}
  ;{A749D8E6-B613-3BE3-8F5F-045C84EBA29B}- msvs2013 sp1   
  Push $R0
  ClearErrors
   
  ;try to read Version subkey to R0
  ReadRegDword $R0 HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\{CF2BEA3C-26EA-32F8-AA9B-331F7E34BA97}" "Version"
 
  ;was there error or not?
  IfErrors 0 NoErrors
   
  ;error occured, copy "Error" to R0
  StrCpy $R0 "Error"
 
  NoErrors:
  
    Exch $R0 
FunctionEnd






