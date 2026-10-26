param(
    [string]$DreamBotHome = (Join-Path $env:USERPROFILE 'DreamBot'),
    [string]$JdkHome = $env:JAVA_HOME
)
$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build.ps1') -DreamBotHome $DreamBotHome -JdkHome $JdkHome
$taskJavac = if ($JdkHome) { Join-Path $JdkHome 'bin\javac.exe' } else { 'javac' }
$taskJava = if ($JdkHome) { Join-Path $JdkHome 'bin\java.exe' } else { 'java' }
$taskTests = Join-Path $PSScriptRoot ('build\tests-' + [Guid]::NewGuid().ToString('N'))
$taskRepository = Join-Path $DreamBotHome 'BotData\repository2'
$taskClasspath = "$taskRepository\*;$(Join-Path $PSScriptRoot 'build\TreeBranchExample.jar')"
New-Item -ItemType Directory -Path $taskTests -Force | Out-Null
try {
    & $taskJavac --release 11 -encoding UTF-8 -Xlint:all,-path -Werror -cp $taskClasspath -d $taskTests (Join-Path $PSScriptRoot 'tests\FrameworkSmokeTest.java')
    if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
    & $taskJava '-Djava.awt.headless=true' -cp "$taskClasspath;$taskTests" FrameworkSmokeTest
    if ($LASTEXITCODE -ne 0) { throw 'Regression checks failed.' }
} finally {
    $taskResolved = [IO.Path]::GetFullPath($taskTests)
    $taskBuildRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot 'build')) + [IO.Path]::DirectorySeparatorChar
    if (-not $taskResolved.StartsWith($taskBuildRoot, [StringComparison]::OrdinalIgnoreCase)) {
        throw 'Refusing to remove staging directory outside build.'
    }
    Remove-Item -LiteralPath $taskResolved -Recurse -Force
}
