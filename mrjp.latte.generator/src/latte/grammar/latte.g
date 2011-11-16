grammar latte;

options {
    language = Java;
    backtrack = true; //TODO: delete this
    
}

@header {
    package latte.grammar;
}

@lexer::header {
    package latte.grammar;
}


/** program */

program
    : topdef+
    ;
topdef
    : type ident '(' arg? ')' block
    ;
arg
    : (type IDENT)(',' type IDENT)*
    ;


/** statements */

block
    : '{' stmt* '}'
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
    | condelse
    | while
    | sexp
    ;
empty
    : ';'
    ;
bstmt
    : block
    ;
decl
    : type (item)(',' item)* ';'
    ;
item
    : noinit
    | init
    ;
noinit
    : ident
    ;
init
    : ident '=' expr
    ;
ass
    : ident '=' expr  ';'
    ;
incr
    : ident '++'  ';'
    ;
decr
    : ident '--'  ';'
    ;
ret
    : 'return' expr ';'
    ;
vret
    : 'return' ';'
    ;
cond
    : 'if' '(' expr ')' stmt
    ;
condelse
    : 'if' '(' expr ')' stmt 'else' stmt
    ;
while
    : 'while' '(' expr ')' stmt
    ;
sexp
    : expr ';'
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

expr6
    : evar
    | elitint
    | elittrue
    | elitfalse
    | eapp
    | estring
    ;
evar
    : IDENT
    ;
elitint
    : INTEGER
    ;
elittrue
    : 'true'
    ;
elitfalse
    : 'false'
    ;
eapp
    : ident '(' ((expr)(',' expr)*)? ')'
    ;
estring
    : STRING 
    ;
    
expr5
    : not
    | neg
    | expr6
    ; 
neg
    : '-' expr6
    ;
not
    : '!' expr6
    ;
    
emul
    : emul (mulop expr5)?
    ;
    
eadd
    : eadd (addop emul)?
    ;
    
erel
    : erel (relop eadd)?
    ;
    
eand
    : erel ('&&' eand)*
    ;
    
eor
    : eand ('||' eor)*
    ;
    
expr
    : eor
    ;


/** operators */

addop
    : plus
    | minus
    ;
plus
    : '+'
    ;
minus
    : '-'
    ;

mulop
    : times
    | div
    | mod
    ;
times
    : '*'
    ;
div
    : '/'
    ;
mod
    : '%'
    ;

relop
    : lth
    | le
    | gth
    | ge
    | equ
    | ne
    ;
lth
    : '<'
    ;
le
    : '<='
    ;
gth
    : '>'
    ;
ge
    : '>=' 
    ;
equ
    : '=='
    ;
ne
    : '!='
    ;


/** ident */

ident: IDENT ;

INTEGER
    : ('0'..'9')*
    ;

STRING
    : '\"'('a'..'z' | 'A'..'Z' | '0'..'9')*'\"'
    ;

IDENT
    : ('a'..'z' | 'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')*
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
