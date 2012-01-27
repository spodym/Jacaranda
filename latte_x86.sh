filename=$(basename $1)
filename=${filename%.*}
java -jar Jacaranda.jar x86 $1
gcc -g $filename.s -o $filename
