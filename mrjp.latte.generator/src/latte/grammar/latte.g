grammar latte;

options {
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
    INIT;
    ASS;
    INCR;
    DECR;
    RET;
    COND;
    SWHILE;
    
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
    | sexp
    ;
empty
    : ';'!
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
    : ident
    ;
init
    : ident '=' expr -> ^(INIT ident expr)
    ;
ass
    : ident '=' expr  ';' -> ^(ASS ident expr)
    ;
incr
    : ident '++'  ';' -> ^(INCR ident)
    ;
decr
    : ident '--'  ';' -> ^(DECR ident)
    ;
ret
    : 'return' expr ';' -> ^(RET expr)
    ;
vret
    : 'return' ';'!
    ;
cond
    : 'if' '(' expr ')' stmt ('else' stmt)? -> ^(COND expr stmt+)
    ;
swhile
    : 'while' '(' expr ')' stmt -> ^(SWHILE expr stmt)
    ;
sexp
    : expr ';'!
    ;


/** types */

type
    : TYPE_INT
    | TYPE_STRING
    | TYPE_BOOLEAN
    | TYPE_VOID
    ;
TYPE_INT : 'int' ;
TYPE_STRING : 'string' ;
TYPE_BOOLEAN : 'boolean' ;
TYPE_VOID : 'void' ;


/** expressions */

atom
    : evar
    | elitint
    | 'true' -> TRUE
    | 'false' -> FALSE
    | eapp
    | estring
    | '(' expr ')' -> expr
    ;
evar
    : IDENT
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
