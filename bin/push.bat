cd ../

git checkout master

echo 合并分支

git merge dev

git push gitee master

git fetch github master:master

git push github dev

git push github master

git checkout dev