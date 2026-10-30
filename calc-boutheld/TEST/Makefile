# MAKEFILE FOR Testing Parser
#
# Michaël PÉRIN
# VERIMAG / UGA / INP Polytech Grenoble 
# decembre 2025

workspace = ../..
project := CALC
package := parser
tool := Parser

# VARIABLES

main := $(package).$(tool)

# == JAVA

PROJECT := $(workspace)/$(project)
CP := $(PROJECT)/bin
EXEC := $(CP)/$(package)/$(tool).class


JAVA := java -cp $(CP)

# == SCRIPT

SCRIPT := $(PROJECT)/SCRIPT

# == TEST

ext := $(shell echo $(project) | tr '[:upper:]' '[:lower:]')
TEST := $(wildcard *.$(ext))

# COMMANDS

default:
	@clear
	@echo "\033[90m pwd       =\033[0m"  $(shell pwd)
	@echo "\033[90m workspace =\033[0m"  $(shell cd $(workspace) ; pwd)
	@echo "\033[90m PROJECT   =\033[0m"  $(shell cd $(PROJECT) ; pwd)
	@echo "\033[90m CP        =\033[0m"  $(shell cd $(CP) ; pwd)
	@echo "\033[93m main      =\033[0m"  $(main)
	@echo "\033[96m EXEC      =\033[0m"  $(shell ls $(EXEC))
	@echo "\033[94m ext       =\033[0m"  .$(ext)
	@echo "\033[94m TEST      =\033[0m"  $(TEST)

test: $(TEST:%=%.tested)

check: $(TEST:%=%.checked)


clean:

# RULES

%.tested: %
	$(JAVA) $(main) -v -f $<

%.checked: % 
	@-$(SCRIPT)/check.sh -v $<
#	@-$(SCRIPT)/check.sh $<
