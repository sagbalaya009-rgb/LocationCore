param(
    [string]$AdbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
)

$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $AdbPath)) {
    $command = Get-Command adb -ErrorAction SilentlyContinue
    if (-not $command) { throw 'Google Android SDK Platform Tools ADB was not found.' }
    $AdbPath = $command.Source
}

$devices = @(& $AdbPath devices | Select-Object -Skip 1 | Where-Object { $_ -match '^\S+\s+device\s*$' })
if ($devices.Count -ne 1) { throw "Expected one authorized Android device; found $($devices.Count)." }

$fingerprint = ((& $AdbPath shell getprop ro.build.fingerprint) | Out-String).Trim()
$expected = 'FOSSiBOT/DT1/DT1:13/TP1A.220624.014/1675770926:user/release-keys'
if ($fingerprint -cne $expected) { throw 'Refusing cleanup because the connected build is not the investigated FOSSiBOT M20.' }

$locationWasEnabled = (((& $AdbPath shell cmd location is-location-enabled) | Out-String).Trim() -eq 'true')
& $AdbPath shell am force-stop dev.locationcore | Out-Null
try {
    & $AdbPath shell appops set 2000 android:mock_location allow | Out-Null
    foreach ($provider in @('fused', 'network', 'gps')) {
        & $AdbPath shell cmd location providers remove-test-provider $provider 2>$null | Out-Null
    }
    if ($locationWasEnabled) {
        & $AdbPath shell cmd location set-location-enabled false | Out-Null
        Start-Sleep -Milliseconds 750
        & $AdbPath shell cmd location set-location-enabled true | Out-Null
    }
} finally {
    & $AdbPath shell appops set 2000 android:mock_location default | Out-Null
    if ($locationWasEnabled) { & $AdbPath shell cmd location set-location-enabled true | Out-Null }
}

$locationEnabledAfter = (((& $AdbPath shell cmd location is-location-enabled) | Out-String).Trim() -eq 'true')
[pscustomobject]@{
    package_stopped = $true
    test_provider_cleanup_attempted = 'fused, network, gps'
    shell_mock_appop = ((& $AdbPath shell cmd appops get 2000 android:mock_location) | Out-String).Trim()
    location_enabled_restored = ($locationEnabledAfter -eq $locationWasEnabled)
    fingerprint_match = $true
} | Format-List
