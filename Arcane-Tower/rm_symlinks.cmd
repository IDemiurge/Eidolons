@ECHO OFF
chcp 65001
set resourcePath="target/resource"
set xmlPath="target/XML"
if exist %resourcePath% (rmdir %resourcePath%)
if exist %xmlPath% (rmdir %xmlPath%)
