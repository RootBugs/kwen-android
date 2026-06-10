Get-ChildItem 'C:\Users\Dc\.gradle\caches\modules-2\files-2.1' -Directory | Where-Object { $_.Name -like '*postgrest*' } | Select-Object Name
