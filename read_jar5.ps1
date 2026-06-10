Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead('C:\Users\Dc\.gradle\caches\9.4.1\transforms\58d97543bbf1ac74e3a0956f09352304\transformed\postgrest-kt-debug-api.jar')
$entries = $zip.Entries | Where-Object { $_.FullName -like '*PostgrestQueryBuilder*' -and $_.Name -like '*.class' -and $_.Name -notlike '*$*' } | Select-Object FullName
$entries
$zip.Dispose()
