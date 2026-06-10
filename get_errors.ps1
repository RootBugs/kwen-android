$errors = Get-Content 'D:\kwen-android\build.log' | Where-Object { $_ -match '^e: file:' }
$errors | Select-Object -First 60 | ForEach-Object { Write-Output $_ }
Write-Output "---TOTAL: $($errors.Count) errors---"
