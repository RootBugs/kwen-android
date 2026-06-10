Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead('C:\Users\Dc\.gradle\caches\9.4.1\transforms\58d97543bbf1ac74e3a0956f09352304\transformed\postgrest-kt-debug-api.jar')
# Find all class files (not inner classes, not metadata)
$entries = $zip.Entries | Where-Object { $_.Name -like '*.class' -and $_.Name -notlike '*$*' -and $_.Name -notlike '*metadata*' -and $_.FullName -like 'io/github/jan/supabase/postgrest/*' } | Select-Object FullName
$entries
$zip.Dispose()
