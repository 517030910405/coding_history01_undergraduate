





default rel

global main

extern printf
extern _GLOBAL_OFFSET_TABLE_


SECTION .text   

main:
        push    rbp
        mov     rbp, rsp
        lea     rsi, [rel L_001]
        lea     rdi, [rel L_002]
        mov     eax, 0
        call    printf
        mov     eax, 0
        pop     rbp
        ret



SECTION .data   


SECTION .bss    


SECTION .rodata 

L_001:
        db    33H, 31H, 33H, 32H, 0aH,  00H

L_002:
        db 25H, 73H, 00H


