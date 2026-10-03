param(
    [string]$AdbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
)

$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $AdbPath)) {
    $command = Get-Command adb -ErrorAction SilentlyContinue
    if (-not $command) { throw 'Google Android SDK Platform Tools ADB was not found.' }
    $AdbPath = $command.Source
}

$lines = @(& $AdbPath devices)
if ($LASTEXITCODE -ne 0) { throw 'ADB could not enumerate devices.' }
$devices = @($lines | Select-Object -Skip 1 | Where-Object { $_ -match '^\S+\s+device\s*$' })
if ($devices.Count -ne 1) { throw "Expected one authorized Android device; found $($devices.Count)." }

function Read-Prop([string]$Name) {
    $value = & $AdbPath shell getprop $Name
    if ($LASTEXITCODE -ne 0) { throw "Could not read $Name." }
    return ($value | Out-String).Trim()
}

$result = [ordered]@{
    model = Read-Prop 'ro.product.model'
    device = Read-Prop 'ro.product.device'
    product = Read-Prop 'ro.product.name'
    fingerprint = Read-Prop 'ro.build.fingerprint'
    build_display_id = Read-Prop 'ro.build.display.id'
    android_api = Read-Prop 'ro.build.version.sdk'
    slot = Read-Prop 'ro.boot.slot_suffix'
    bootloader_state = Read-Prop 'ro.boot.vbmeta.device_state'
    verified_boot = Read-Prop 'ro.boot.verifiedbootstate'
    selinux = ((& $AdbPath shell getenforce) | Out-String).Trim()
}

$expectedFingerprint = 'TECNO/KL5-OP/TECNO-KL5:14/UP1A.231005.007/260615V2258:user/release-keys'
$result.supported_build_match = ($result.fingerprint -ceq $expectedFingerprint)
[pscustomobject]$result | Format-List
if (-not $result.supported_build_match) {
    throw 'The connected firmware does not match the investigated KL5 build.'
}

