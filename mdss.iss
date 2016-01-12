[Setup]
AppName=mDSS
AppVersion=0.1.0
DefaultDirName={pf}\mDSS
DefaultGroupName=mDSS
UninstallDisplayIcon={app}\mdss.exe
Compression=lzma2
SolidCompression=yes
OutputDir=target\

[Files]
Source: "target\mdss.exe"; DestDir: "{app}"
Source: "ws\*.cljw"; DestDir: "{app}\ws"

[Dirs]
Name: "{%APPDATA}\.mdss"

[Icons]
Name: "{group}\mDSS"; Filename: "{app}\mdss.exe"
Name: "{group}\Uninstall mDSS"; Filename: "{uninstallexe}"

[Code]
function InitializeSetup(): boolean;
var
  ResultCode: integer;
begin
  if Exec('java', '-version', '', SW_SHOW, ewWaitUntilTerminated, ResultCode) then
  begin
    Result := true;
  end
  else
  begin
    if MsgBox('mDSS requires Java Runtime Environment version 1.6 or newer to run. Please download and install the JRE and run this setup again. Do you want to download it now?', mbConfirmation, MB_YESNO) = idYes then
    begin
      Result := false;
      ShellExec('open', 'https://java.com/download/', '', '', SW_SHOWNORMAL, ewNoWait, ResultCode);
    end;
  end;
end;
