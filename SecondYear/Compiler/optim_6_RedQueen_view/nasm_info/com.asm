





default rel

global h
global main

extern printf
extern _GLOBAL_OFFSET_TABLE_


SECTION .text   

h:
        push    rbp
        mov     rbp, rsp
        push    rbx
        sub     rsp, 40
        mov     dword [rbp-24H], edi
        cmp     dword [rbp-24H], 0
        jz      L_001
        cmp     dword [rbp-24H], 1
        jnz     L_002
L_001:  mov     eax, 1
        jmp     L_005

L_002:  mov     dword [rbp-18H], 0
        mov     dword [rbp-14H], 0
        jmp     L_004

L_003:  mov     eax, dword [rbp-14H]
        mov     edi, eax
        call    h
        mov     ebx, eax
        mov     eax, dword [rbp-24H]
        sub     eax, 1
        sub     eax, dword [rbp-14H]
        mov     edi, eax
        call    h
        imul    eax, ebx
        add     dword [rbp-18H], eax
        add     dword [rbp-14H], 1
L_004:  mov     eax, dword [rbp-14H]
        cmp     eax, dword [rbp-24H]
        jl      L_003
        mov     eax, dword [rbp-18H]
L_005:  add     rsp, 40
        pop     rbx
        pop     rbp
        ret


main:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     dword [rbp-4H], 17
        mov     eax, dword [rbp-4H]
        mov     edi, eax
        call    h
        mov     esi, eax
        lea     rdi, [rel L_006]
        mov     eax, 0
        call    printf
        mov     eax, 0
        leave
        ret



SECTION .data   


SECTION .bss    


SECTION .rodata 

L_006:
        db 25H, 64H, 0AH, 00H


