@ECHO OFF
chcp 65001
set resourcePath="target/resources"
set xmlPath="target/XML"
if exist %resourcePath% (rm %resourcePath%)
if exist %xmlPath% (rm %xmlPath%)
