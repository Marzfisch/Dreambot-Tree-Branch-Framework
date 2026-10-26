param(
    [string]$DreamBotHome = (Join-Path $env:USERPROFILE 'DreamBot'),
    [string]$JdkHome = $env:JAVA_HOME
)
$ErrorActionPreference = 'Stop'
$taskJavac = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { 'javac' }
$taskJar = if ($JdkHome) { Join-Path $JdkHome 'bin\jar.exe' } else { 'jar' }
if (-not (Get-Command $taskJavac -ErrorAction SilentlyContinue)) {
    throw 'Install JDK 11 or newer and set JAVA_HOME (a JRE cannot compile scripts).'
}
if (-not (Get-Command $taskJar -ErrorAction SilentlyContinue)) {
    throw 'The JDK jar tool is missing. Check JAVA_HOME.'
}
$taskRepository = Join-Path $DreamBotHome 'BotData\repository2'
if (-not (Test-Path -LiteralPath (Join-Path $taskRepository 'dreambot-client.jar'))) {
    throw "DreamBot API not found under $taskRepository. Launch DreamBot to update its libraries."
}
$taskOutput = Join-Path $PSScriptRoot 'build'
# Use a fresh staging folder so renamed/deleted classes cannot enter the artifact.
$taskClasses = Join-Path $taskOutput ('classes-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $taskClasses -Force | Out-Null
try {
    $taskSources = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'src') -Recurse -Filter '*.java' |
        ForEach-Object FullName)
    if ($taskSources.Count -eq 0) { throw 'No Java sources found.' }
    # DreamBot dependency manifests reference optional absent JARs; skip only path lint.
    & $taskJavac --release 11 -encoding UTF-8 -Xlint:all,-path -Werror -classpath "$taskRepository\*" -d $taskClasses @taskSources
    if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
    $taskArtifact = Join-Path $taskOutput 'TreeBranchExample.jar'
    & $taskJar --create --file $taskArtifact -C $taskClasses .
    if ($LASTEXITCODE -ne 0) { throw 'JAR packaging failed.' }
    Write-Output "Built $taskArtifact"
} finally {
    # This exact directory was created above within this project's build folder.
    $taskResolved = [IO.Path]::GetFullPath($taskClasses)
    $taskBuildRoot = [IO.Path]::GetFullPath($taskOutput) + [IO.Path]::DirectorySeparatorChar
    if (-not $taskResolved.StartsWith($taskBuildRoot, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Refusing to remove staging directory outside build.'
    }
    Remove-Item -LiteralPath $taskResolved -Recurse -Force
}
