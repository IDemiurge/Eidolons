@ECHO OFF
chcp 65001
set resourcePath="target/resources"
set xmlPath="target/XML"
if exist %resourcePath% (rmdir %resourcePath%)
mklink /D %resourcePath% "%CD%/../resources/res"

if exist %xmlPath% (rmdir %xmlPath%)
mklink /D %xmlPath% "%CD%/../C-Engine/src/main/resources/XML"
