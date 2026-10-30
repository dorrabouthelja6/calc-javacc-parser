

# Installation de `JavaCC`

* Rendez-vous sur le site officiel de [JavaCC](https://javacc.github.io/javacc/) : https://javacc.github.io/javacc/

* Suivez les instructiosn de la partie « __Use JavaCC from the command line__ »

---

# Sujet du TP 

## Récupération du sujet de TP sur le dépôt `gricad`

Dans le répertoire qui correspond à un `workspace` `Eclipse`, faîtes

`git clone git@gricad-gitlab.univ-grenoble-alpes.fr:michael_perin_private_project/aeg_2025/`_«gricad_login»_`/calc.git CALC`

Vous obtenez un répertoire `CALC/` qui contient la branche `master` d'un projet java 

## Création d'une branche de travail nommée d'après votre _«gricad login»_

> La branche `main/master` étant protégée, elle ne vous sert qu'à récupérer le projet.
> Par conséquent, vous devrez travailler et _push_ une branche que vous créez.

Commencez par créer une copie de `master` dans une branche nommée d'après votre _«gricad login»_.

1. `git branch` _«gricad login»_
2. `git switch` _«gricad login»_
3. `git push --set-upstream origin` _«gricad login»_

## Test de l'installation

* `make` sans commande devrait vous afficher les informations suivantes avec un chemin `?` approprié

```shell
  pwd       = ?/CALC
  workspace = ?
  PROJECT   = ?/CALC
  CP        = ?/CALC/bin
  SOURCE    = ?/CALC/src/parser
  ls *.java = Tracing.java 
  main      = parser.Parser
  EXEC      = ../CALC/bin/parser/Parser.class
  TEST      = ?/CALC/TEST
  ls *.calc = test00.calc test01.calc test02.calc test03.calc
```

* `make demo` devrait vous afficher la trace d'exécution qui suit.
  * si c'est le cas, vous pouvez __commencer à travailler en important le projet java dans Eclipse__
  * si ce n'est pas le cas, c'est que 
    * soit `JavaCC` n'est pas correctement installé
    * soit la structure de vos répertoires n'est pas la bonne

```Haskell
Java Compiler Compiler Version 7.0.12 (Parser Generator)
(type "javacc" with no arguments for help)
Reading from file Parser.jj . . .
File "TokenMgrError.java" does not exist.  Will create one.
File "ParseException.java" does not exist.  Will create one.
File "Token.java" does not exist.  Will create one.
File "SimpleCharStream.java" does not exist.  Will create one.
Parser generated successfully.
eval 1 + 2
parse
| CALC <EVAL:"eval">
| | EXPR
| | | EXPR0
| | | | INTEGER <DIGIT:"1">
| | | | | NAT
| | | | | | DIGIT
| | | | | ↳NAT: 1
| | | | ↳INTEGER[2]: 1
| | | ↳EXPR0[1]: 1
| | | Opt_BINOP_EXPR <BINOP:"+">
| | | | BINOP
| | | | ↳BINOP: +
| | | | EXPR
| | | | | EXPR0
| | | | | | INTEGER <DIGIT:"2">
| | | | | | | NAT
| | | | | | | | DIGIT
| | | | | | | ↳NAT: 2
| | | | | | ↳INTEGER[2]: 2
| | | | | ↳EXPR0[1]: 2
| | | | | Opt_BINOP_EXPR
| | | | | ↳Opt_BINOP_EXPR[2]: 2
| | | | ↳EXPR: 2
| | | ↳Opt_BINOP_EXPR[1]: 3
| | ↳EXPR: 3
| ↳CALC: 3
↳parse: 3
3
```


---

# TP À RENDRE au plus tard à 23h59 la veille du DS n°3 d'A&G 

À cette date je ferai un dernier `update` pour récupérer vos modifications avant de commencer à noter vos TP.

Vous devez avoir fait un `push` de votre branche nommée d'après votre _«gricad login»_, après avoir fait un `commit` des fichiers :

* du répertoire `TEST/`
* [GRAMMAR.md](GRAMMAR.md)
* [Parser.jj.md](src/parser/Parser.jj.md)