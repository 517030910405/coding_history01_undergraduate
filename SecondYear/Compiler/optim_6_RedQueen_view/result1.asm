Init_1_1init_: ;Init..init
push rbp
mov rbp,rsp
sub rsp,32
mov rax,0
mov rsp,rbp
pop rbp
ret
;Return,	(Val||0)

main: ;main
push rbp
mov rbp,rsp
sub rsp,48
;push all todo
;push all done
call Init_1_1init_
add rsp,0
;pop all todo
;pop all done
;call,	,	(None)	(Func|Init..init|-1),	(Val||0),	

mov rbx,0
sub rbx,10
;-,	r,	(Reg||1),	(Val||0),	(Val||10),	

mov rax,rbx
;push all todo
mov qword [ rbp - 16], rbx
mov qword [ rbp - 24], rsi
;push all done
mov rdi,rax
call toString
;pop all todo
mov rbx,qword [ rbp - 16]
mov rsi,qword [ rbp - 24]
;pop all done
mov rsi,rax
;toString,	,	(Reg||2),	(Reg||1),	(None)	

mov rax,str_const_0
mov rdx,rsi
;push all todo
mov qword [ rbp - 16], rbx
mov qword [ rbp - 24], rsi
;push all done
mov rdi,rax
mov rsi,rdx
call str_add
;pop all todo
mov rbx,qword [ rbp - 16]
mov rsi,qword [ rbp - 24]
;pop all done
mov rbx,rax
;string_+,	lr,	(Reg||3),	(Val||0|"Hello\n 1234"),	(Reg||2),	

mov rax,rbx
;push all todo
mov qword [ rbp - 16], rbx
;push all done
mov rdi,rax
call print
;pop all todo
mov rbx,qword [ rbp - 16]
;pop all done
;print,	,	(None)	(Reg||3),	(None)	

mov rax,0
mov rsp,rbp
pop rbp
ret
;Return,	(Val||0)

