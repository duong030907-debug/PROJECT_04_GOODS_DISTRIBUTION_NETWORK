$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$javacCmd = Get-Command javac -ErrorAction SilentlyContinue
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javacCmd -and $env:JAVA_HOME) {
    $javacCmd = Join-Path $env:JAVA_HOME "bin\javac.exe"
    $javaCmd = Join-Path $env:JAVA_HOME "bin\java.exe"
}
if (-not $javacCmd) {
    Write-Host "[ERROR] Khong tim thay javac. Hay cai JDK 17+ va them vao PATH hoac dat JAVA_HOME."
    exit 1
}

$bin = Join-Path $PSScriptRoot "bin"
if (-not (Test-Path $bin)) {
    New-Item -ItemType Directory -Path $bin | Out-Null
}

$sources = Get-ChildItem (Join-Path $PSScriptRoot "src") -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }
& $javacCmd --release 17 -encoding UTF-8 -d $bin $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Bien dich that bai."
    exit $LASTEXITCODE
}

& $javaCmd -cp $bin Main @args
exit $LASTEXITCODE
