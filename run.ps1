$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$javacCmd = Get-Command javac -ErrorAction SilentlyContinue
$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javacCmd -and $env:JAVA_HOME) {
    $javacCmd = Join-Path $env:JAVA_HOME "bin\javac.exe"
    $javaCmd = Join-Path $env:JAVA_HOME "bin\java.exe"
}

# Tu tim JDK neu javac khong co trong PATH.
if (-not $javacCmd) {
    $roots = @(
        (Join-Path $env:USERPROFILE "Java"),
        (Join-Path $env:ProgramFiles "Java"),
        (Join-Path $env:ProgramFiles "Eclipse Adoptium"),
        (Join-Path $env:ProgramFiles "Android\Android Studio")
    )
    foreach ($root in $roots) {
        if (-not (Test-Path $root)) {
            continue
        }
        $candidate = Get-ChildItem $root -Directory -ErrorAction SilentlyContinue |
            Where-Object { Test-Path (Join-Path $_.FullName "bin\javac.exe") } |
            Sort-Object Name -Descending |
            Select-Object -First 1
        if ($candidate) {
            $javacCmd = Join-Path $candidate.FullName "bin\javac.exe"
            $javaCmd = Join-Path $candidate.FullName "bin\java.exe"
            break
        }
    }
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
