
cd ../

call git checkout master

echo 合并分支

call git merge dev

call git push gitee master

call git fetch github master:master

call git push github dev

call git push github master

call git checkout dev