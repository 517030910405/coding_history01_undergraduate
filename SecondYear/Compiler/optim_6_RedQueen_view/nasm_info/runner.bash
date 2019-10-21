#!/bin/bash
set -e
C_FILE="$1"
BASE_NAME="${C_FILE%}"
O_FILE="$BASE_NAME.o"
NASM_FILE="$BASE_NAME.asm"
nasm -f elf64 -o my.o -g "$NASM_FILE"
gcc -no-pie -g -o my my.o
#gcc -no-pie -o my my.o

./my
#echo $?
#./my
#gdb ./my
