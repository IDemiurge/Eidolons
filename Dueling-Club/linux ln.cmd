rm "target/resources"

ln -s "$(dirname "$PWD")/resources/res" "target/resources"

rm "target/XML"

ln -s "$(dirname "$PWD")/C-Engine/src/main/resources/XML" "target/XML"
