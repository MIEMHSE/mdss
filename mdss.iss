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

[Icons]
Name: "{group}\mDSS"; Filename: "{app}\mdss.exe"
Name: "{group}\Uninstall mDSS"; Filename: "{uninstallexe}"

[Code]
procedure DecodeVersion (verstr: String; var verint: array of Integer);
var
  i, p: Integer;
  s: string;
begin
  verint := [0, 0, 0, 0];
  i := 0;
  while ((Length(verstr) > 0) and (i < 4)) do
  begin
    p := pos ('.', verstr);
    if p > 0 then
    begin
      if p = 1 then s := '0' else s := Copy (verstr, 1, p - 1);
      verint[i] := StrToInt(s);
      i := i + 1;
      verstr := Copy (verstr, p + 1, Length(verstr));
    end
    else
    begin
      verint[i] := StrToInt (verstr);
      verstr := '';
    end;
  end;

end;

function CompareVersion (ver1, ver2: String) : Integer;
var
  verint1, verint2: array of Integer;
  i: integer;
begin
  SetArrayLength (verint1, 4);
  DecodeVersion (ver1, verint1);

  SetArrayLength (verint2, 4);
  DecodeVersion (ver2, verint2);

  Result := 0; i := 0;
  while ((Result = 0) and (i < 4)) do
  begin
    if verint1[i] > verint2[i] then
      Result := 1
    else
      if verint1[i] < verint2[i] then
        Result := -1
      else
        Result := 0;
    i := i + 1;
  end;

end;

function InitializeSetup(): Boolean;
var
  ErrorCode: Integer;
  JavaVer : String;
  Result1 : Boolean;
begin
    RegQueryStringValue (HKLM, 'SOFTWARE\JavaSoft\Java Runtime Environment', 'CurrentVersion', JavaVer);
    Result := false;
    if Length (JavaVer) > 0 then
    begin
    	if CompareVersion (JavaVer, '1.6') >= 0 then
    	begin
    		Result := true;
    	end;
    end;
    if Result = false then
    begin
    	Result1 := MsgBox('This tool requires Java Runtime Environment v1.6 or older to run. Please download and install JRE and run this setup again.' + #13 + #10 +
        'Do you want to download it now?',
    	  mbConfirmation, MB_YESNO) = idYes;
    	if Result1 = true then
    	begin
    		ShellExec('open',
    		  'http://www.java.com/en/download/manual.jsp#win',
    		  '', '', SW_SHOWNORMAL, ewNoWait, ErrorCode);
    	end;
    end;
end;
