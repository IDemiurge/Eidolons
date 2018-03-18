rm "target\resources"

ln -s  "target\resources" "$PWD\..\resources\res"

rm "target\XML"

ln -s "target\XML" "$PWD\..\C-Engine\src\main\resources\XML"
