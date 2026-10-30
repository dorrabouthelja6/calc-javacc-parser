#BEGIN

##### LICENCE, AUTHOR, INSTITUTION, DATE

* GPL
* Michaël PÉRIN, 
* VERIMAG / Univ. Grenoble-Alpes / INP Polytech Grenbole
* December 2025


# Un évaluateur d'expressions arithmétiques

L'objectif du TP est de réaliser un équivalent de l'outil `bc` (_basic calculator_) qui fonctionne en ligne de commande et effectue des calculs arithmétiques simples.

### Déroulé du TP

> __Un parser est difficile à mettre au point__

* il est difficile à déboguer avec les outils standard de debug du fait des appels récursifs en cascade
* pour faire la mise au point, on trace les appels aux non-terminaux et on affiche les tokens reconnus par la partie lexer ; d'où les appels à la classe `Tracing`.

Il faut donc procéder pas à pas pour maîtriser et valider chaque extension du parser.
Je vous conseille donc de procéder en plusieurs étapes.

#### étape 1 : obtenir un parser minimal qui compile

Je vous fournis un parser qui ne reconnaît pas grand chose mais qui fonctionne.

#### étape 2* : complétez les non-terminaux

Certains non-terminaux (par exemple, `EXPR0`) ne sont pas complètement définis.
Répétez le procédé suivant :
  * On réfléchit à la grammaire (sur papier, pas directement dans le code `.jj.md`)
  * On ajoute __une__ alternative manquante et les actions correspondantes 
  * On teste avant d'ajouter une autre alternative. Pour cela ajoutez vos tests dans le répertoire `TEST/` 

> __IMPORTANT__ Vos tests font partie du rendu du projet. 

#### étape 3 : corrigez la grammaire et le parser en procédant par étapes

Vous constaterez que l'évaluation de l'expression `3 * 2 + 5` retourne `30` au lieu de `11`
car la priorité des opérateurs n'est pas prise en compte.

Vous devez modifier la grammaire des expressions pour tenir compte de la priorité des opérateurs 
`*, %, /` par rapport aux opérateurs `+, -`. 


## _Parser_ en JavaCC 

Un _parser_ en JavaCC se compose de 3 parties :

1. Une classe java `Parser`
2. Un lexer défini par une collection d'expressions régulières
3. Un analyseur syntaxique (_parser_ en anglais) défini par une collection de fonctions de reconnaissance


## JAVACC OPTIONS

```Java
options {
  STATIC = false;
}
```
## PARTIE JAVA du parser

```Java
PARSER_BEGIN(Parser)
```

### CLASS `Parser`


```Java
package parser;

import java.io.BufferedReader;
import java.io.FileReader;

public class Parser {
```

#### CONSTRUCTEUR

Le constructeur de la classe `Parser` sera généré automatiquement par `javacc` : il prend en argument un flot de caractères qui sera passé au `lexer` qui à son tour fournira un flot de tokens aux fonctions de _parsing_.

#### CHAMPS

On aura besoin d'un tableau `memory` servira à stocker les valeurs des variables entières `$0` à `$9` du langage CALC.

```Java
  static int memory[];

  static {
    memory = new int[10];
  }
  static int fact(int n) {
    if (n < 0)
        throw new IllegalArgumentException("n doit être >= 0");

    int res = 1;
    for (int i = 2; i <= n; i++) {
        res *= i;
    }
    return res;
}
static double ln(int n) {
    if (n <= 0)
        throw new IllegalArgumentException("n doit être > 0 pour ln(n)");
    return Math.log(n); // retourne un double
}

 
```

#### MÉTHODES / FONCTIONS

* Une grande partie des méthodes de la classe `Parser` sont des _parsers_ qui correspondent aux non-terminaux de la grammaire. Elles sont générées 
automatiquement par `javacc` à partir de la description des non-terminaux (voir ci-dessous).

* On peut compléter la classe `Parser` avec des méthodes écrites directement par le développeur que l'outil `javacc` conserve, sans les modifier.

##### La fonction `main`

* l'option `-e` évalue le calcul donné en ligne de commande : 
  `Parser -e "41+2"`
* l'option `-f` évalue le calcul donnée dans une fichier : `Parser -f test0.calc`

* l'option `-v` active le mode _verbose_ où le parser affiche les noms terminaux par lesquels il passe lors de son analyse de texte. 


* `main` appelle la méthode `parse()` qui se charge d'effectuer simultanément la reconnaissance et l'évaluation de l'expression parsée.

```Java
  public static void main(String[] args) throws Exception {
    int result = 314159;
    Parser parser = null;
    boolean verbosity = false;
 
    for(int i = 0 ; i < args.length ; i++){    
      if (args[i].equals("-v"))
        verbosity = true ;

      if (args[i].equals("-e") && i+1 < args.length) {
        String input_string = args[i+1];
        parser = new Parser(new java.io.StringReader(input_string));
      }

      if (args[i].equals("-f") && i+1 < args.length) {
        String path_file = args[i+1];
        parser = new Parser(new BufferedReader(new FileReader(path_file)));
      }
    }

    if (parser == null)
      throw new IllegalArgumentException("missing arguments");

    Tracing.enable(verbosity);
    result = parser.parse();
    System.out.print(String.format("\n%d\n", result)); 
  }
```

##### La fonction `int horner(String digits)`

* prend en entrée une séquence de digits sous la forme d'une `String`
* retourne l'entier correspondant
* applique le schéma de Horner pour construire efficacement l'entier
* `horner("00123") == 123`

```Java
  public static int horner(String digits){
    if (digits.isEmpty())
      throw new IllegalArgumentException("Parser.horner: empty string");
    int r = 0 ;
    for(int i = 0 ; i < digits.length() ; i++)
      r = 10 * r + (digits.charAt(i) - '0') ;
    return r;
  }
```

```Java
} // end of class Parser

PARSER_END(Parser) // end of the Java part
```


## PARTIE LEXER

Le lexer définit des reconnaisseurs sous la forme d'expressions régulières (RegExp en abbrégée et en anglais) qui sont traduites en automates.

* les mots à ignorer et 
* les lexèmes (_Tokens_ en anglais) 

Les déclarations de reconnaisseurs

* doivent respecter la syntaxe suivante  `<` _Catégorie de token_ `:` _RegExp_`> {` _action Java optionnelle_ `}` 
* sont précédées du mot-clef `SKIP` ou `TOKEN`


##### Fonctionnement

Le `lexer` fait tourner les automates dans l'ordre de déclaration.

* Chaque fois qu'un automate reconnaît un mot, le `lexer` génère un object `Token` contenant 
  * un champ contenant la catégorie du _token_
  * un champ `String image` : le mot reconnu qu'on peut récupérer au moyen de la méthode `toString()`


##### SKIP

Déclarations des mots à ignorer

```Java
SKIP: 
{ " " | "\n" }
```

##### TOKEN

* Il est possible mais pas nécessaire de déclarer un automate pour les mots utilisés une seule fois dans la grammaire tels que `$`, `:=`, `;`, `(`, `)`, `eval`.

* On le fait pour `$`, `:=`, `;` et `eval` afin de pouvoir changer facilement la notation si on le souhaite en `#`, `=`, `,` et `return`.


```Java
TOKEN:
{
  < DIGIT: ["0"-"9"] > { Tracing.token("DIGIT", image.toString()); } 
}

TOKEN:
{ 
  < NAT: (<DIGIT>)+ > { Tracing.token("NAT", image.toString()); } 
}

TOKEN:
{ 
  < ADD: "+" |"-" > { Tracing.token("ADD", image.toString()); } 
}
TOKEN:
{ 
  < TIMES: "*" |"/" > { Tracing.token("TIMES", image.toString()); } 
}
TOKEN:
{ 
  < LOG: "mod" |"ln" > { Tracing.token("LOG", image.toString()); } 
}
TOKEN:
{ 
  < POST: "²" |"³"|"!" > { Tracing.token("POST", image.toString()); } 
}

TOKEN:
{
  < VAR: "$" > { Tracing.token("VAR", image.toString()); }
}

TOKEN:
{
  < ASSIGN: ":=" > { Tracing.token("ASSIGN", image.toString()); }
}

TOKEN:
{
  < SEP: ";" > { Tracing.token("SEP", image.toString()); }
}

TOKEN:
{
  < EVAL: "eval" > { Tracing.token("EVAL", image.toString()); }
}
```


## PARTIE PARSER = Grammar + Actions Java

##### Germe de la grammaire = Non-Terminal PARSE = méthode `parse()` 

```Haskell
PARSE ::= CALC . <EOF> 
``` 

La méthode `int parse()`

* lit le flot de caractères issu du fichier jusqu'à rencontrer le token `<EOF>` prédéfini, acronyme de End Of File (en anglais) et indiquant la fin du fichier.
* retourne la valeur entière qui résulte de l'évaluation de l'expression parsée

```Java
int parse() ::= 
DECL{ 
  // déclarations des variables
  int depth = 0;
  Tracing.call(depth, "parse"); // <-- affichage avec une indentation
  int i; 
}
RULE{ 
  // production/reconnaissance /reconnaissance du non-terminal PARSE
  i = CALC(depth+1) // <-- appel du non-terminal CALC avec indentation
  <EOF>
  { // actions java
    Tracing.returns(0, "parse", String.format("%d",i)); // <-- affichage du résultat
    return i; 
  }
}
```

##### Non-Terminal DIGIT = méthode `DIGIT(int depth)`

```Haskell
DIGIT ::= <DIGIT>
```

Ne pas confondre la méthode `DIGIT` et le token `<DIGIT>`

La méthode `int DIGIT(int depth)`

* prend en paramètre un entier `depth` qui correspond à la profondeur de l'appel
* `depth` est utilisé pour afficher l'appel avec une indentation correspondante
* consomme un token `<DIGIT>`, le convertit en `String` puis en entier
* si le token n'est pas un entier &in; [0..9], 
  * la fonction ne doit __surtout pas__ rendre une valeur entière : 
    ce qui serait une source de bug, c'est à dire une erreur silencieuce 
    qui ne se détecte pas immédiatement.
  * on préferera provoquer un erreur explicite en levant une exception.

```Java
int DIGIT(int depth) ::=
DECL{ 
  Tracing.call(depth, "DIGIT"); 
  Token token;
}
RULE{
  token = <DIGIT> 
  { // action java
    switch( token.toString() ){
      case "0" : return 0;
      case "1" : return 1; 
      case "2" : return 2; 
      case "3" : return 3;
      case "4" : return 4; 
      case "5" : return 5;
      case "6" : return 6; 
      case "7" : return 7; 
      case "8" : return 8;
      case "9" : return 9 ;
      default: throw new IllegalStateException("unknown digit");
    } 
  }
}
```


##### Non-Terminal NAT = méthode `NAT(int depth)`

```Haskell
NAT ::= <DIGIT> | <NAT>
```

Ne pas confondre la méthode `NAT` et le token `<NAT>`

La méthode `int NAT(int depth)`

* consomme un token `<NAT>` dans le flot de tokens
* le convertit en chaîne de caractères
* puis convertit la chaîne obtenue (formée de digits) en un entier naturel

```Java
int NAT(int depth) ::= 
DECL{ 
  Tracing.call(depth,"NAT"); 
  Token token;  
  int n;
}
RULE{ 
  ( 
    n = DIGIT(depth+1)
  | 
    token = <NAT> { n = horner(token.toString()); }
  )
  { // action java commune aux deux alternatives
    Tracing.returns(depth, "NAT", String.format("%d", n));
    return n; 
  }
}
```


##### Non-Terminal INTEGER = méthode `INTEGER(int depth)`

```Haskell 
INTEGER ::= <MINUS> . NAT | NAT 
```

* Le parseur `INTEGER` réussit lorsque
  * soit le flot de tokens contient un token `<MINUS>` suivi d'un préfixe reconnu par le parseur `NAT` 
  * soit le flot de tokens contient un préfixe reconnu par le non-terminal `NAT`
* il retourne un entier signé


```Java
int INTEGER(int depth) ::= 
DECL{
  Tracing.call(depth,"INTEGER");
  int n,i;  
}
RULE{
  // [1]
  n = NAT(depth+1)

  { // action java
    Tracing.returns(depth, "INTEGER[1]", String.format("%d",n));
    return n ;
  }
}
```


##### Non-Terminal EXPR = méthode `EXPR(int depth)`

```Haskell 
EXPR ::= EXPRADD
```

La méthode `int EXPR(int depth)`

* reconnaît une expression de niveau 0 suivie 
  éventuellement d'un opérateur binaire et d'une expression
* retourne la valeur de l'expression complète 

```Java
int EXPR(int depth) ::=
DECL{
  Tracing.call(depth,"EXPR");
  int l;
}
RULE{
  l = EXPRADD(depth+1)
  {
    Tracing.returns(depth, "EXPR", String.format("%d",l));
    return l ;
  }
}
```

##### Non-Terminal EXPRADD = méthode `EXPRADD(int depth)`

```Haskell 
EXPRADD ::= EXPMULT .OPT_ADD_EXPRADD
```

La méthode `int EXPRADD(int depth)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int EXPRADD(int depth) ::=
DECL{
  Tracing.call(depth, "EXPRADD");
  int i;
}
RULE{ 
  i = EXPMULT(depth+1)
  i = opt_ADD_EXPRADD(depth+1,i)
    {
    Tracing.returns(depth, "EXPRADD", String.format("%d",i));
    return i ;
  }
}
```

##### Non-Terminal EXPRMULT = méthode `EXPRMULT(int depth)`

```Haskell 
EXPRMULT ::= EXPTEXT .OPT_MULT_EXPTEXT
```

La méthode `int EXPRMULT(int depth)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int EXPMULT(int depth) ::=
DECL{
  Tracing.call(depth, "EXPMULT");
  int i;
}
RULE{ 
  i = EXPTEXT(depth+1)
  i = opt_MULT_EXPTEXT(depth+1,i)
    {
    Tracing.returns(depth, "EXPMULT", String.format("%d",i));
    return i ;
  }
}
```
##### Non-Terminal EXPRTEXT = méthode `EXPTEXT(int depth)`

```Haskell 
EXPTEXT ::= EXPPRE .opt_LOG_EXPPRE
```

La méthode `int EXPTEXT(int depth)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int EXPTEXT(int depth) ::=
DECL{
  Tracing.call(depth, "EXPTEXT");
  int i;
}
RULE{ 
  i = EXPPRE(depth+1)
  i = opt_LOG_EXPPRE(depth+1,i)
    {
    Tracing.returns(depth, "EXPTEXT", String.format("%d",i));
    return i ;
  }
}
```
##### Non-Terminal EXPPRE = méthode `EXPPRE(int depth)`

```Haskell 
EXPPRE ::= OPPREX . EXPPOST
```

La méthode `int EXPPRE(int depth)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int EXPPRE(int depth) ::=
DECL{
  Tracing.call(depth, "EXPPRE");
  int i;
}
RULE{ 
  i = OPPREX(depth+1)
  i = EXPPOST(depth+1,i)
    {
    Tracing.returns(depth, "EXPPRE", String.format("%d",i));
    return i ;
  }
}
```

##### Non-Terminal OPPREX = méthode `OPPREX(int depth)`

```Haskell 
OPPREX ::= <MINUS> . OPPREX | ε
```

La méthode `int OPPREX(int depth)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int OPPREX(int depth) ::=
DECL{
  Tracing.call(depth,"OPPREX");
  int n;
  Token op;  
}
RULE{
  // [1]
  op =<ADD>
  {
    if (!op.toString().equals("-")){
      throw new ParseException();
    }
  }
  n = OPPREX(depth+1)
  {
    Tracing.returns(depth, "OPPREX[1]", String.format("%d",-n));
    return -n;
  }


| // <-- alternative

  // [ε]
    {
    Tracing.returns(depth, "OPPREX[ε]", "0");
    return 0;
  }
}
```
##### Non-Terminal EXPPOST = méthode `EXPPOST(int depth , int i)`

```Haskell 
EXPPOST::= ATOM. SOMEOPPOST
```

La méthode `int EXPPOST(int depth , int i)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int EXPPOST(int depth , int i) ::=
DECL{
  Tracing.call(depth, "EXPPOST");
}
RULE{ 
  i = ATOM(depth+1)
  i = SOMEOPPOST(depth+1,i)
    {
    Tracing.returns(depth, "EXPPOST", String.format("%d",i));
    return i ;
  }
}
```

##### Non-Terminal SOMEOPPOST = méthode `SOMEOPPOST(int depth , int r)`

```Haskell 
SOMEOPPOST ::= <POST> . SOMEOPPOST | ε
```

La méthode `int SOMEOPPOST(int depth , int r)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int SOMEOPPOST(int depth , int i) ::=
DECL{
  Tracing.call(depth,"SOMEOPPOST");
  Token t; 
}
RULE{
  // [1]
  t = <POST>
  {
    switch(t.toString()){
      case "!"  : i = fact(i) ; break ;
      case "²" : i = i * i ; break ;
      case "³" : i = i * i * i; break ;
      default: throw new IllegalStateException("unknown binary operator") ;
    }
  }
  i = SOMEOPPOST(depth+1,i)

  {
    Tracing.returns(depth, "SOMEOPPOST[1]", String.format("%d",i));
    return i;
  }
| // <-- alternative

  // [ε]
    {
       Tracing.returns(depth, "SOMEOPPOST[ε]", String.format("%d", i));
    return i;
  }
}
```

##### Non-Terminal ATOM = méthode `ATOM(int depth)`

```Haskell 
ATOM ::= 
  |VARIABLE
  |INTEGER
  |<"("> . EXPR . <")">
```

La méthode `int ATOM(int depth)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int ATOM(int depth) ::=
DECL{
  Tracing.call(depth, "ATOM");
  int i, index;

}
RULE{
  // [1]
   "("
  i = EXPR(depth+1)
  ")"

  { // action java
    Tracing.returns(depth, "ATOM[1]", String.format("%d",i));
    return i;
  }

| // <-- alternative

  // [2]
  index = VARIABLE(depth+1)

  { // action java
    Tracing.returns(depth, "ATOM[2]", String.format("%d",memory[index]));
    return memory[index];
  }
| // <-- alternative

  // [3]
   i = INTEGER(depth+1)

  { // action java
    Tracing.returns(depth, "ATOM[3]", String.format("%d",i));
    return i;
  }

}
```

##### Non-Terminal opt_LOG_EXPPRE = méthode `opt_LOG_EXPPRE(int depth , int l)`

```Haskell 
opt_LOG_EXPPRE ::=
  | <LOG> . EXPPRE
  | ε
```

La méthode `int opt_LOG_EXPPRE(int depth, int l)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int opt_LOG_EXPPRE(int depth, int l) ::=
DECL{
  Tracing.call(depth,"opt_LOG_EXPPRE");
  Token t;
  int i, r; 
}
RULE{
  // [1]
  t = <LOG>
  r = EXPPRE(depth+1)
  {
    switch(t.toString()){
      case "mod"  : i = l%r; break ;
      case "ln" : i = (int)(ln(l)/ln(r)) ; break ;
      default: throw new IllegalStateException("unknown binary operator") ;
    }
  }
  {
    Tracing.returns(depth, "opt_LOG_EXPPRE [1]", String.format("%d",i));
    return i;
  }
| // <-- alternative

  // [ε]
    {
      Tracing.returns(depth, "opt_LOG_EXPPRE[ε]", String.format("%d", l));
    return l;
  }
}
```

##### Non-Terminal opt_MULT_EXPTEXT = méthode `opt_MULT_EXPTEXT(int depth)`

```Haskell 
opt_MULT_EXPTEXT ::=
  | <TIMES> . EXPTEXT
  | ε
```

La méthode `int opt_MULT_EXPTEXT(int depth , int r)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int opt_MULT_EXPTEXT(int depth , int l) ::=
DECL{
  Tracing.call(depth,"opt_MULT_EXPTEXT");
  Token t; 
  int i, r;
}
RULE{
  // [1]
  t = <TIMES>
  r = EXPTEXT(depth+1)
  {
    switch(t.toString()){
      case "*"  : i =l*r ; break ;
      case "/" : i =l/r ; break ;
      default: throw new IllegalStateException("unknown binary operator") ;
    }
  }
  {
    Tracing.returns(depth, "opt_MULT_EXPTEXT[1]", String.format("%d",i));
    return i;
  }
| // <-- alternative

  // [ε]
    {
       Tracing.returns(depth, "opt_MULT_EXPTEXT[ε]", String.format("%d", l));
    return l;
  }
}
```

##### Non-Terminal opt_ADD_EXPRADD = méthode `opt_ADD_EXPRADD(int depth , int r)`

```Haskell 
opt_ADD_EXPRADD ::=
  | <ADD> . EXPRADD
  | ε
```

La méthode `int opt_ADD_EXPRADD(int depth , int r)`

* reconnaît une expression de niveau 0
* retourne la valeur de l'expression

```Java
int opt_ADD_EXPRADD(int depth,int l) ::=
DECL{
  Tracing.call(depth,"opt_ADD_EXPRADD");
  Token t; 
  int i,r;
}
RULE{
  // [1]
  t = <ADD>
  r = EXPRADD(depth+1)
  {
    switch(t.toString()){
      case "+"  : i =l+r ; break ;
      case "-" : i =l-r ; break ;
      default: throw new IllegalStateException("unknown binary operator") ;
    }
  }

  {
    Tracing.returns(depth, "opt_ADD_EXPRADD[1]", String.format("%d",i));
    return i;
  }
| // <-- alternative

  // [ε]
    {
       Tracing.returns(depth, "opt_ADD_EXPRADD[ε]", String.format("%d", l));
    return l;
  }
}
```



##### Non-Terminal VARIABLE = méthode `VARIABLE(int depth)`

```Haskell
VARIABLE ::= <VAR> . DIGIT 
```

La méthode `int VARIABLE(int depth)`

* reconnaît un nom de variable
* retourne __le numéro__ de variable

```Java
int VARIABLE(int depth) ::= 
DECL{ 
  Tracing.call(depth,"VARIABLE");
  int d;
}
RULE{ 
  <VAR>
  d = DIGIT(depth+1)
  {
       Tracing.returns(depth, "VARIABLE", String.format("%d",d));
    return d ;  
  }
}
```


##### Non-Terminal ASSIGN = méthode `ASSIGN(int depth)`

```Haskell 
ASSIGN ::= VARIABLE . <ASSIGN> . EXPR 
```

Ne pas confondre la méthode `ASSIGN` avec le token `<ASSIGN>`

La méthode `ASSIGN(int depth)`

* reconnaît une affectation de variable
* met à jour la valeur de la variable dans le tableau memory
* __ne retourne rien__

###### Remarque 1

On pourrait faire un autre choix : 
Les langages impératifs tels que `C, Java` choississent de retourner la valeur affectée.

Ainsi une instruction d'affectation __est__ une valeur ; et on peut écrire en `C, Java` : `x=y=z=0;` qui est évaluée comme 
`x=(y=(z=0))` où `z=0` vaut `0` et donc s'évalue en `x=(y=0)` où `y=0` vaut `0` et finalement s'évalue en `x=0` mettant ainsi les 3 variables à `0`.  

Pour simplifier, on n'adopte pas la convention choisie par `C, Java`, mais vous pourrez tenter de le faire en fin de TP si ça vous chante.

```Java
void ASSIGN(int depth) ::= 
DECL{
  Tracing.call(depth,"ASSIGN");
  int var;
  int val;
}
RULE{ 
  var =VARIABLE(depth+1)
  <ASSIGN>
  val = EXPR(depth+1)
   /*ε*/ 
   { 
    memory[var] = val;
    Tracing.returns(depth, "ASSIGN", String.format("$%d=%d",var ,val));
   }
} 
```


##### Non-Terminal SEQUENCE = méthode `SEQUENCE(int depth)`

```Haskell
SEQUENCE ::= (ASSIGN . <SEP>)* 
```

* utilise l'étoile de Kleene définie en JavaCC
* reconnaît une suite (éventuellement vide) d'affectations
* __ne retourne rien__
  

```Java
void SEQUENCE(int depth) ::=
DECL{
  Tracing.call(depth,"SEQUENCE");
}
RULE{
  (
    ASSIGN(depth+1)
    <SEP>
    { } // <-- actions java
  )*  // <-- Kleene star
}
```


##### Non-Terminal CALC = méthode `CALC(int depth)`

```Haskell 
CALC ::= SEQUENCE . <EVAL> . EXPR 
```

La méthode `CALC(int depth)`

* reconnaît une séquence d'affectations suivie d'une expression
* retourne le résultat de l'évaluation de l'expression parsée 

```Java
int CALC(int depth) ::=
DECL{ 
  Tracing.call(depth,"CALC");
  int i;
}
RULE{ // FIXME: non conforme à la grammaire
  SEQUENCE(depth+1)
  <EVAL>
  i = EXPR(depth+1)   
  { 
    Tracing.returns(depth, "CALC", String.format("%d",i));
    return i; 
  }
}
```

#END