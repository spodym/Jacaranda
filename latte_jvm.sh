filename=$(basename $1)
filename=${filename%.*}
java -jar Jacaranda.jar jvm $1
