grammar Mx;
Comment : ('/*' .*? '*/'| '//'.*? '\r' ? '\n')->skip;
WS : [ \n\t\r]+ ->skip;
mx : outfit* ;
outfit : func#function|class_#class_assign|type_and_var';'#type_assign;
class_ : Class__ var_ '{'(func|type_and_var';'|self_func)*'}';
func :type_ var_ '(' (type_and_var(','type_and_var)*)? ')''{'stat'}';
self_func : var_ '(' (type_and_var(','type_and_var)*)? ')''{'stat'}';
stat : singstat* ;
singstat : ';'#empty_stmt|expr';'#sing_stmt|type_and_var';'#assign_stmt| Return__ expr*';'#return_stmt
    |Break__';'#break_stmt|Continue__';'#continue_stmt|For__ '('for_expr_in_cir';'for_expr_in_cir';'for_expr_in_cir ')' singstat#for_stmt
    | While__'(' expr ')'singstat#while_stmt|'{'stat'}'#stmtmix|If__ '('expr')'singstat(Else__ singstat)?#if_stmt;
        //singexpr : (type_ ('[' ']')*)? expr ;
//for_expr_in_cir:(type_? expr)*;
for_expr_in_cir:expr#expr_in_for|type_and_var#assign_in_for|#none_in_for;
type_and_var : type_ expr;
type_ : var_ #type_var
    | int__#type_int
    | void__#type_void
    | string__#type_string
    | bool__#type_bool
    | type_ '[' ']'#type_array
    | type_ '['expr']'#type_array_withnum;
expr : This__#exprthis|int_#id1|bool_#id1|var_#id2|string_#id1|null__#id1|void__#id1|'(' expr ')'#exprmix
    |expr '.' expr #exprsub|expr op_in_expr=('++'|'--')#opl
    |expr '[' expr? ']'#exprarr|expr '(' (expr(',' expr)*)? ')'#exprfunc
    |op_in_expr=('++'|'--') expr#opr|op_in_expr=('!'|'~') expr#opr|'new' type_('(' ')')? #exprnew
    |expr op_in_expr=Op1 expr #oplr| op_in_expr=Op2 expr#opr|expr op_in_expr=Op2 expr#oplr
    |expr op_in_expr=Shlr expr#oplr| expr op_in_expr=RelatOp1 expr #oplr| expr op_in_expr=RelatOp2 expr #oplr|expr op_in_expr='&' expr#oplr
    |expr op_in_expr='^' expr#oplr
    |expr op_in_expr='|' expr#oplr
    |expr op_in_expr='&&' expr#oplr| expr op_in_expr='||' expr#oplr|expr op_in_expr='='expr#opassign;

var_ : Var;
bool_ : True__ | False__;
string_ : String_;
int_ : Int_;
null__ : Null__ ;
void__ : Void__ ;
int__ : Int__;
string__ : String__;
bool__ : Bool__;

True__ : 'true'|'True'|'TRUE';
False__ : 'False'|'false'|'FALSE';

Op1 : '*'|'/'|'%';
Op2 : '+'|'-';
Shlr : '<<'|'>>';
RelatOp1 : '<'|'>'|'<='|'>=';
RelatOp2 : '=='|'!=';
LogicOp :'&&'|'||'|'!';

Bool__ : 'bool'; Int__ : 'int'; String__ : 'string'; Null__ : 'null'; Void__ : 'void';
For__ : 'for' ; If__ : 'if' ; While__ : 'while'; Break__ : 'break'; Continue__ : 'continue'; Return__ : 'return' ;
New__ : 'new'; Class__ : 'class'; This__ : 'this';
Else__ : 'else';

Int_ : Digits;
Var : [a-zA-Z_][a-zA-Z0-9_]*;
Digits : ([1-9][0-9]*)|[0];


NL :'\r'? '\n';
String_ : '"' ('\\"'|'\\\\'|'\\n'|'\\t'|'\\r'|.)*?  '"';



//for 修改
//当心问号
//return和for expr in cir当心



