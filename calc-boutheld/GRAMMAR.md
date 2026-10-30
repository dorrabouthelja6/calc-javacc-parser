# Grammaire des calculs reconnus par CALC

##### CALCUL

```Haskell
PARSE ::= CALC . <EOF> 

CALC ::= SEQUENCE . <EVAL> . EXPR 

SEQUENCE ::= (ASSIGN . <SEP>)* 

ASSIGN ::= VARIABLE . <ASSIGN> . EXPR 

VARIABLE ::= <VAR> . DIGIT 
```

##### VALUES

```Haskell
DIGIT ::= <DIGIT>

NAT ::= <DIGIT> | <NAT>

INTEGER ::= <MINUS> . NAT | NAT 
```

##### EXPRESSIONS : partie à modifier / compléter

```Haskell
EXPR ::= EXPRADD
EXPRADD ::= EXPMULT .opt_ADD_EXPRADD

EXPMULT ::= EXPTEXT . opt_MULT_EXPTEXT

EXPTEXT ::= EXPPRE .opt_LOG_EXPPRE

EXPPRE ::= OPPREX . EXPPOST
OPPREX ::= <MINUS> . OPPREX | ε
EXPPOST::= ATOM. SOMEOPPOST
SOMEOPPOST ::= <POST> . SOMEOPPOST | ε
ATOM ::= 
  |VARIABLE
  |INTEGER
  |<"("> . EXPR . <")">
opt_LOG_EXPPRE ::=
  | <LOG> . EXPPRE
  | ε
opt_MULT_EXPTEXT ::=
  | <TIMES> . EXPTEXT
  | ε
opt_ADD_EXPRADD ::=
  | <ADD> . EXPRADD
  | ε

