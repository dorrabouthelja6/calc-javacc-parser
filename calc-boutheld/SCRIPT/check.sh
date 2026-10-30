#!/bin/sh

# AUTHOR: Michaël PÉRIN, VERIMAG / UGA / INP Polytech Grenoble, decembre 2025

# ROLE:
#   compare the result of CALC with that of bc
#   return 0 if equal, 1 otherwise

# USAGE:
#   $1 = -v (option verbose)
#   $2 = file.calc

# option verbose
VERBOSE=0
if [ "$1" = "-v" ]; then
    VERBOSE=1
    shift  # supprime cet argument
fi

# vérifie qu'il reste un argument
if [ -z "$1" ]; then
    echo "Usage: $0 [-v] file.calc"
    exit 2
fi
FILE="$1"

# == colors
GRAY="\e[90m"
RED="\e[91m"
GREEN="\e[92m"
ORANGE="\e[93m"
BLUE="\e[94m"
PINK="\e[95m"
CYAN="\e[96m"
RESET="\e[0m"
BOLD="\e[99;1m"

# == variables
PROJECT=..
RULES=$PROJECT/SCRIPT/calc2bc.sed
CALC="java -cp $PROJECT/bin/ parser.Parser -f"

# transforme 3,14 en 3.14
LC_NUMERIC=C

# normalise la sortie en supprimant les caractères {\r, \n, espace}
normalize() {
    tr -d '\r\n ' 
}

if [ "$VERBOSE" -eq 1 ]; then
  # affiche le calcul à effectuer
  printf "${GRAY}\n$FILE:\n${RESET}"
  printf "${CYAN}" 
  cat $FILE
  printf "${RESET}"
fi

# évalue les expressions avec bc et CALC

bc_out=$({ sed --file=$RULES "$FILE" | tr -d '\r\n'; printf '\n'; } | bc -q | normalize)

calc_out=$($CALC "$FILE" | normalize)

# Comparaison
if [ "$bc_out" = "$calc_out" ]; then
  printf "\n✅ ${BOLD}$FILE{RESET}"
  exit 0
else
  if [ "$VERBOSE" -eq 1 ]; then
    printf "\n${GRAY} • bc   :${RESET} ${GREEN}$bc_out${RESET}"
    printf "\n${GRAY} • CALC :${RESET} ${ORANGE}$calc_out${RESET}"
  fi
  printf "\n❌ ${BOLD}$FILE${RESET}"
  exit 1
fi
