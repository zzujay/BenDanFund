#!/bin/bash

echo "================================================"
echo "  笨蛋基基 - GitHub上传脚本"
echo "================================================"
echo ""

# 检查是否安装了git
if ! command -v git &> /dev/null; then
    echo "❌ 错误: 未检测到Git，请先安装Git"
    exit 1
fi

# 获取用户输入
read -p "请输入GitHub用户名: " GH_USER
read -p "请输入仓库名称 (默认: BenDanFund): " REPO_NAME
REPO_NAME=${REPO_NAME:-BenDanFund}

echo ""
echo "📦 准备上传到: github.com/$GH_USER/$REPO_NAME"
echo ""

# 初始化git仓库
if [ ! -d .git ]; then
    echo "🔧 初始化Git仓库..."
    git init
    git add .
    git commit -m "Initial commit: 笨蛋基基基金APP"
else
    echo "📁 已存在Git仓库"
    git add .
    git commit -m "Update: 添加GitHub Actions自动打包" 2>/dev/null || echo "无需提交"
fi

# 添加远程仓库
echo ""
echo "🔗 添加远程仓库..."
git remote remove origin 2>/dev/null
git remote add origin "https://github.com/$GH_USER/$REPO_NAME.git"

echo ""
echo "✅ 完成！请执行以下命令:"
echo ""
echo "  1. 推送代码到GitHub:"
echo "     git push -u origin main"
echo ""
echo "  2. 在GitHub仓库页面查看Actions:"
echo "     https://github.com/$GH_USER/$REPO_NAME/actions"
echo ""
echo "  3. 构建完成后下载APK:"
echo "     https://github.com/$GH_USER/$REPO_NAME/actions"
echo ""
echo "================================================"
