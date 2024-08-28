#!/bin/bash
STR="Appended String"
FILE_NAME="work_file.txt"

echo "Creating text file" > $FILE_NAME
mv $FILE_NAME same_file.txt
echo "$STR" >> same_file.txt

echo "Output of same_file.txt contents"
cat same_file.txt

rm -f same_file.txt
