;
; TimeCult InnoSetup configuration file for Win64
; Copyright (c) Rustam Vishnyakov, 2007-2013
;
#include "build\temp\version.iss"

[Setup]
AppName=TimeCult
AppPublisher=TimeCult Project Team
AppPublisherURL=http://timecult.sf.net
AppSupportURL=http://timecult.sf.net
AppUpdatesURL=http://timecult.sf.net
ArchitecturesAllowed=x64
ArchitecturesInstallIn64BitMode=x64
DefaultDirName={pf}\TimeCult
DefaultGroupName=TimeCult
LicenseFile=gpl.txt
WizardImageFile=wiz_image.bmp
WizardSmallImageFile=wiz_icon.bmp
ShowLanguageDialog=yes

[Languages]
Name: "en"; MessagesFile: "compiler:Default.isl"
Name: "de"; MessagesFile: "compiler:Languages\German.isl"
Name: "fr"; MessagesFile: "compiler:Languages\French.isl"
Name: "ru"; MessagesFile: "compiler:Languages\Russian.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop icon"; GroupDescription: "Additional icons:"; MinVersion: 4,4

[Files]
Source:  "dist\*.*"; DestDir: "{app}"; Flags: ignoreversion
Source:  "dist\lib\*.*"; DestDir: "{app}\lib"; Flags: ignoreversion
Source:  "dist\jre\*.*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs

[Icons]
Name: "{group}\TimeCult"; Filename: "{app}\timecult.exe" ; WorkingDir: "{app}\"
Name: "{group}\Uninstall"; Filename: "{app}\unins000.exe" ; WorkingDir: "{app}\"
Name: "{userdesktop}\TimeCult"; Filename: "{app}\timecult.exe"; MinVersion: 4,4; Tasks: desktopicon ; WorkingDir: "{app}\"


[Run]
Filename: "{app}\timecult.exe"; Description: "Run TimeCult now"; Flags: shellexec postinstall skipifsilent

[Registry]
Root: HKCR; Subkey: ".tmt"; ValueType: string; ValueData: "TmtFile"; Flags: uninsdeletekey;
Root: HKCR; Subkey: "TmtFile"; ValueType: string; ValueData: "TimeCult Data File"; Flags: uninsdeletekey;
Root: HKCR; Subkey: "TmtFile\shell\open\command"; ValueType: string; ValueData: """{app}\timecult.exe"" ""%1""";

[Code]
function GetSwtDir: String;
begin
  Result := GetIniString('setup','SwtDir','','setup.ini');
end;




