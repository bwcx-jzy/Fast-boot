@echo off

chcp 65001

cd ../


echo ��ȡԶ�̷�֧[master]
call git checkout dev

call git fetch github master:master

call git fetch gitee master:master

echo ��ȡԶ�̷�֧[dev]
call git checkout master

call git fetch github dev:dev

call git fetch gitee dev:dev

echo ��ʼ�ϲ���֧[master]
call git checkout dev
call git merge master

echo ��ʼ�ϲ���֧[dev]
call git checkout master
call git merge dev

echo ���͵�gitee

call git push gitee dev

call git push gitee master

echo ���͵�github
call git push github dev

call git push github master

echo ����tags
call git push github --tags

call git push gitee --tags

call git checkout dev