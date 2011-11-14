grammar latte;

options {
    language = Java;
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
ass.       Stmt ::= Ident "=" Expr  ";" ;
incr.      Stmt ::= Ident "++"  ";" ;
decr.      Stmt ::= Ident "--"  ";" ;
ret.       Stmt ::= "return" Expr ";" ;
vret.      Stmt ::= "return" ";" ;
cond.      Stmt ::= "if" "(" Expr ")" Stmt  ;
condelse.  Stmt ::= "if" "(" Expr ")" Stmt "else" Stmt  ;
while.     Stmt ::= "while" "(" Expr ")" Stmt ;
sexp.      Stmt ::= Expr  ";" ;

    
type
    : 'int' 
    | 'string' 
    | 'boolean'
    ;
    
ident: IDENT ;

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