filename=$(basename $1)
filename=${filename%.*}
java -jar Jacaranda.jar $1
gcc -g $filename.s -o $filename
