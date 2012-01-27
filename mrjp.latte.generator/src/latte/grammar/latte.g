grammar latte;

options {
    backtrack=true;
    language = Java;
    output=AST;
    ASTLabelType=CommonTree;
}

tokens {
    TOP_DEF;
    ARGS;
    ARG;
    BLOCK;
    DECL;
    NOINIT;
    INIT;
    ASS;
    INCR;
    DECR;
    RET;
    RETV;
    COND;
    SWHILE;
    SFOR;
    EMPTY;
    
    EAPP;
    EMUL;
    EADD;
    EREL;
    EAND;
    EOR;
    
    NEGATION;
    NOT;
    TRUE;
    FALSE;
    VAR_IDENT;
    
    TYPE_INT;
    TYPE_STRING;
    TYPE_BOOLEAN;
    TYPE_VOID;
    
    ARRTYPE;
    NEWARR;
    SUBSCRIB;
    
    ATTRIBUTE;
}

@header {
    package latte.grammar;
}

@lexer::header {
    package latte.grammar;
}

/** program */

program
    : topdef+ EOF!
    ;
topdef
    : type ident '(' args? ')' block -> ^(TOP_DEF type ident args* block)
    ;
args
    : (arg)(',' arg)* -> ^(ARGS arg+)
    ;
arg
    : type ident -> ^(ARG type ident)
    ;


/** statements */

block
    : lc='{' stmt* '}' -> ^(BLOCK[$lc,"BLOCK"] stmt*)
    ;
stmt
    : empty
    | bstmt
    | decl
    | ass
    | incr
    | decr
    | ret
    | vret
    | cond
    | swhile
    | sfor
    | sexp
    ;
empty
    : lc=';'  -> ^(EMPTY[$lc,"EMPTY"])
    ;
bstmt
    : block
    ;
decl
    : type (item)(',' item)* ';' -> ^(DECL type item+)
    ;
item
    : noinit
    | init
    ;
noinit
    : ident -> ^(NOINIT ident)
    ;
init
    : ident lc='=' expr -> ^(INIT[$lc, "INIT"] ident expr)
    ;
ass
    : assignable lc='=' expr  ';' -> ^(ASS[$lc, "ASS"] assignable expr)
    ;
incr
    : ident lc='++'  ';' -> ^(INCR[$lc, "INCR"] ident)
    ;
decr
    : ident lc='--'  ';' -> ^(DECR[$lc, "DECR"] ident)
    ;
ret
    : lc='return' expr ';' -> ^(RET[$lc,"RET"] expr)
    ;
vret
    : lc='return' ';' -> ^(RETV[$lc,"RETV"])
    ;
cond
    : lc='if' '(' expr ')' stmt ('else' stmt)? -> ^(COND[$lc, "COND"] expr stmt+)
    ;
swhile
    : lc='while' '(' expr ')' stmt -> ^(SWHILE[$lc, "SWHILE"] expr stmt)
    ;
sfor
    : lc='for' '(' type2 noinit ':' ident ')' stmt -> ^(SFOR[$lc, "SFOR"] ^(DECL type2 noinit) ident stmt)
    ;
sexp
    : expr ';'!
    ;


/** types */

type
    : type2
    | arrtype
    ;

type2
    : 'int' -> TYPE_INT
    | 'string' -> TYPE_STRING
    | 'boolean' -> TYPE_BOOLEAN
    | 'void' -> TYPE_VOID
    ;
    
arrtype
    : type2 '[]' ->  ^(ARRTYPE type2)
    ;


/** expressions */

atom
    : newarr
    | assignable
    | attribute
    | elitint
    | lc='true' -> TRUE[$lc,"TRUE"]
    | lc='false' -> FALSE[$lc,"FALSE"]
    | eapp
    | estring
    | '(' expr ')' -> expr
    ;
assignable
    : subscrib
    | evar
    ;
subscrib
    : evar '[' expr ']' -> ^(SUBSCRIB evar expr)
    ;
attribute
    : evar '.' ident -> ^(ATTRIBUTE evar ident)
    ;
newarr
    : 'new' type2 '[' expr ']' -> ^(NEWARR type2 expr)
    ;
evar
    : ident -> ^(VAR_IDENT ident)
    ;
elitint
    : INTEGER
    ;
eapp
    : ident '(' ((expr)(',' expr)*)? ')' -> ^(EAPP ident expr*)
    ;
estring
    : STRING 
    ;

unary
    : not
    | neg
    | atom
    ; 
neg
    : '-' atom -> ^(NEGATION atom)
    ;
not
    : '!' atom -> ^(NOT atom)
    ;
    
emul
    : unary (mulop^ unary)*
    ;
    
eadd
    : emul (addop^ emul)*
    ;
    
erel
    : eadd (relop^ eadd)*
    ;
    
eand
    : erel ('&&'^ eand)?
    ;
    
eor
    : eand ('||'^ eor)?
    ;
    
expr
    : eor
    ;


/** operators */

OP_OR : '||' ;
OP_AND : '&&' ; 

addop
    : OP_PLUS
    | OP_MINUS
    ;
OP_PLUS : '+' ;
OP_MINUS : '-' ;

mulop
    : OP_TIMES
    | OP_DIV
    | OP_MOD
    ;
OP_TIMES : '*' ;
OP_DIV : '/' ;
OP_MOD: '%' ;

relop
    : OP_LTH
    | OP_LE
    | OP_GTH
    | OP_GE
    | OP_EQU
    | OP_NE
    ;
OP_LTH : '<' ;
OP_LE : '<=' ;
OP_GTH : '>' ;
OP_GE : '>=' ;
OP_EQU : '==' ;
OP_NE : '!=' ;


/** ident */

ident: IDENT ;

fragment LETTER : ('a'..'z' | 'A'..'Z') ; 
fragment DIGIT : ('0'..'9'); 

INTEGER
    : DIGIT*
    ;

STRING
    :   '"' 
        ( .
        )*
        '"'
    ;

IDENT
    : LETTER (LETTER | DIGIT)*
    ;

WHITESPACE
    : (' ' | '\t' | '\r' | '\n') { $channel = HIDDEN; }
    ;


/** comments */

COMMENT
    : '/*' .* '*/' { $channel=HIDDEN; }
    ;

LINE_COMMENT
    : ('//'|'#') ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN; }
    ;
