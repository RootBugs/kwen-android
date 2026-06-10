Get-ChildItem 'C:\Users\Dc\.gradle\caches' -Recurse -Filter 'postgrest-kt*' -Directory -ErrorAction SilentlyContinue | Select-Object FullName
