@echo off

chcp 65001

cd ../

echo 拉取远程分支

call git fetch github dev:dev
call git fetch github master:master

call git fetch gitee dev:dev
call git fetch gitee master:master

echo 开始切换分支
call git checkout master

echo 开始合并分支
call git merge dev

echo 推送到gitee
call git push gitee master

echo 推送到github
call git push github dev

call git push github master

echo 推送tags
call git push github --tags

call git push gitee --tags

call git checkout dev