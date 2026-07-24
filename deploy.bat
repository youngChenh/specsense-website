@echo off
echo ======================================
echo  开始生成 Nuxt 静态站点...
echo ======================================
npm run generate

echo.
echo ======================================
echo  开始上传到服务器...
echo ======================================
scp -r .\.output\public\* root@43.159.58.248:/usr/share/nginx/html/

echo.
echo ======================================
echo  重启 Nginx...
echo ======================================
ssh root@43.159.58.248 "nginx -s reload"

echo.
echo ======================================
echo  ✅ 部署完成！访问 http://www.spesense.com
echo ======================================
pause