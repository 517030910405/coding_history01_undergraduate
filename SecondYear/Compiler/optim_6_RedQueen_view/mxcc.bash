#!/bin/bash
set -e
C_FILE="$1"
BASE_NAME="${C_FILE%.*}"
O_FILE="$BASE_NAME.o"
NASM_FILE="$BASE_NAME.asm"
cp "$C_FILE" ./program.txt
./codegen.bash
./run.bash result_all
cp ./my "$BASE_NAME"
