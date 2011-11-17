tree grammar lattetree;

options {
    language = Java;
    tokenVocab = latte;
    ASTLabelType = CommonTree;
}

@header {
    package latte.grammar;
}

program
    : topdef+
    ;
topdef
    : ^(TOP_DEF type ident args* block)
    ;
args
    : ^(ARGS arg+)
    ;
arg
    : ^(ARG type ident)
    ;


/** statements */

block
    : ^(BLOCK stmt*)
    ;
stmt
    : bstmt
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
bstmt
    : block
    ;
decl
    : ^(DECL type item+)
    ;
item
    : noinit
    | init
    ;
noinit
    : ident
    ;
init
    : ^(INIT ident expr)
    ;
ass
    : ^(ASS ident expr)
    ;
incr
    : ^(INCR ident)
    ;
decr
    : ^(DECR ident)
    ;
ret
    : ^(RET expr)
    ;
vret
    : 'return'
    ;
cond
    : ^(COND expr stmt stmt)
    ;
swhile
    : ^(SWHILE expr stmt)
    ;
sexp
    : expr
    ;


/** types */

type
    : TYPE_INT
    | TYPE_STRING
    | TYPE_BOOLEAN
    | TYPE_VOID
    ;
    

/** expressions */


atom
    : IDENT
    | INTEGER
    | 'true'
    | 'false'
    | ^(EAPP ident expr*)
    | STRING 
    ;
    
expr:
    | NEGATION atom
    | '!' atom
    | expr (mulop expr)*
    | expr (addop expr)*
    | expr (relop expr)*
    | expr ('&&' expr)?
    | expr ('||' expr)?
    ;
    
    
/** operators */

addop
    : plus
    | minus
    ;
plus : '+' ;
minus : '-' ;

mulop
    : times
    | div
    | mod
    ;
times : '*' ;
div : '/' ;
mod : '%' ;

relop
    : lth
    | le
    | gth
    | ge
    | equ
    | ne
    ;
lth : '<' ;
le : '<=' ;
gth : '>' ;
ge : '>=' ;
equ : '==' ;
ne : '!=' ;


/** ident */

ident: IDENT ;