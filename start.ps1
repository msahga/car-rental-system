Set-Location $PSScriptRoot

if (-not (Test-Path "node_modules")) {
    Write-Host "首次运行，正在安装依赖..."
    npm install
}

Write-Host "正在启动后端 (8080) 和前端 (3000)..."
npm run dev
