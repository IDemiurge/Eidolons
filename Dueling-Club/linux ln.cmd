@ECHO OFF
chcp 65001
set resourcePath="target/resources"
set xmlPath="target/XML"
if exist %resourcePath% (rmdir %resourcePath%)
ln -s /D %resourcePath% "%CD%/../resources/res"

if exist %xmlPath% (rmdir %xmlPath%)
ln -s %xmlPath% "%CD%/../C-Engine/src/main/resources/XML"
