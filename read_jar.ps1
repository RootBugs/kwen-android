Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead('C:\Users\Dc\.gradle\caches\9.4.1\transforms\4044811e133060492bd21f2eba62914a\transformed\postgrest-kt-debug-runtime\postgrest-kt-debug-runtime.jar')
$entries = $zip.Entries | Where-Object { $_.FullName -like '*supabase*' }
$entries | Select-Object FullName
$zip.Dispose()
