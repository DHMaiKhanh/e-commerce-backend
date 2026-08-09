<#
.SYNOPSIS
    Tạo database local `ecommerce` trên MariaDB/MySQL, chạy toàn bộ migration
    (V1..V4) để tạo bảng, rồi nạp data giả (seed_dev.sql) để xem thử.

.DESCRIPTION
    KHÔNG cần Docker. Dùng luôn MariaDB đã cài sẵn trên máy.
    Script sẽ DROP rồi tạo lại database => chạy lại nhiều lần đều sạch sẽ.

.EXAMPLE
    # Nhập mật khẩu root khi được hỏi (an toàn, không lộ ra màn hình)
    .\setup-local-db.ps1

.EXAMPLE
    # Truyền thẳng mật khẩu root
    .\setup-local-db.ps1 -RootPassword 'matkhau_cua_ban'
#>
param(
    [string]$RootPassword,
    [string]$DbName   = 'ecommerce',
    [string]$DbHost   = '127.0.0.1',
    [int]   $Port     = 3306,
    [string]$DbUser   = 'root',
    [string]$MysqlExe = 'C:\Program Files\MariaDB 12.0\bin\mysql.exe'
)

$ErrorActionPreference = 'Stop'

if (-not (Test-Path $MysqlExe)) {
    throw "Khong tim thay mysql client tai: $MysqlExe . Sua tham so -MysqlExe."
}

# Neu chua truyen -RootPassword: thu doc tu file .env (gitignored) o goc project
if ([string]::IsNullOrEmpty($RootPassword)) {
    $envFile = Join-Path $PSScriptRoot '..\..\.env'
    if (Test-Path $envFile) {
        Write-Host "==> Doc thong tin dang nhap tu .env" -ForegroundColor DarkGray
        foreach ($line in Get-Content -LiteralPath $envFile) {
            if ($line -match '^\s*DB_PASSWORD\s*=\s*(.+?)\s*$') { $RootPassword = $Matches[1] }
            if ($line -match '^\s*DB_USERNAME\s*=\s*(.+?)\s*$') { $DbUser       = $Matches[1] }
        }
    }
}

# Van chua co mat khau => hoi truc tiep (go an)
if ([string]::IsNullOrEmpty($RootPassword)) {
    $secure = Read-Host -AsSecureString "Nhap mat khau MariaDB cua user '$DbUser'"
    $bstr   = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    $RootPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
}

# Dùng biến môi trường để không đưa mật khẩu lên dòng lệnh (tránh cảnh báo/lộ)
$env:MYSQL_PWD = $RootPassword

$scriptDir = $PSScriptRoot
$migDir    = Resolve-Path (Join-Path $scriptDir '..\..\src\main\resources\db\migration')
$common    = @('-u', $DbUser, '-h', $DbHost, '-P', "$Port", '--default-character-set=utf8mb4')

function Invoke-Mysql {
    param([string[]]$ExtraArgs)
    & $MysqlExe @common @ExtraArgs
    if ($LASTEXITCODE -ne 0) { throw "Lenh mysql that bai (exit $LASTEXITCODE)." }
}

function Invoke-SqlFile {
    param([string]$Path)
    $p = ($Path -replace '\\', '/')
    Invoke-Mysql -ExtraArgs @($DbName, '-e', "SOURCE $p")
}

Write-Host "==> Kiem tra ket noi MariaDB ($DbHost`:$Port)..." -ForegroundColor Cyan
Invoke-Mysql -ExtraArgs @('-e', 'SELECT VERSION();')

Write-Host "==> Tao lai database '$DbName' (drop neu da ton tai)..." -ForegroundColor Cyan
Invoke-Mysql -ExtraArgs @('-e', "DROP DATABASE IF EXISTS $DbName; CREATE DATABASE $DbName CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")

$migrationFiles = @(
    'V1__init_users.sql',
    'V2__init_catalog.sql',
    'V3__init_cart_orders.sql',
    'V4__frontend_alignment.sql'
)
foreach ($f in $migrationFiles) {
    Write-Host "==> Chay migration: $f" -ForegroundColor Cyan
    Invoke-SqlFile -Path (Join-Path $migDir $f)
}

Write-Host "==> Nap data gia: seed_dev.sql" -ForegroundColor Cyan
Invoke-SqlFile -Path (Join-Path $scriptDir 'seed_dev.sql')

Write-Host "`n==> HOAN TAT. Thong ke ban ghi:" -ForegroundColor Green
$summary = @"
SELECT 'categories' AS bang, COUNT(*) AS so_dong FROM categories
UNION ALL SELECT 'products',        COUNT(*) FROM products
UNION ALL SELECT 'product_images',  COUNT(*) FROM product_images
UNION ALL SELECT 'users',           COUNT(*) FROM users
UNION ALL SELECT 'user_roles',      COUNT(*) FROM user_roles
UNION ALL SELECT 'user_addresses',  COUNT(*) FROM user_addresses
UNION ALL SELECT 'vouchers',        COUNT(*) FROM vouchers
UNION ALL SELECT 'reviews',         COUNT(*) FROM reviews
UNION ALL SELECT 'carts',           COUNT(*) FROM carts
UNION ALL SELECT 'cart_items',      COUNT(*) FROM cart_items
UNION ALL SELECT 'orders',          COUNT(*) FROM orders
UNION ALL SELECT 'order_items',     COUNT(*) FROM order_items;
"@
Invoke-Mysql -ExtraArgs @($DbName, '--table', '-e', $summary)

Remove-Item Env:\MYSQL_PWD -ErrorAction SilentlyContinue
Write-Host "`nXong! Mo HeidiSQL, ket noi localhost:3306, chon database '$DbName' de xem." -ForegroundColor Green
