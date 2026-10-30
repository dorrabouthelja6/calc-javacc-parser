# MAKEFILE FOR Parser.jj.md
#
# Michaël PÉRIN
# VERIMAG / UGA / INP Polytech Grenoble 
# decembre 2025

workspace = ../../..
project := CALC

# == DO NOT MODIFY WHAT FOLLOWS ==

package := parser
tool := Parser

# == JAVA

PROJECT := $(workspace)/$(project)
SOURCE := $(PROJECT)/src/$(package)
CP := $(PROJECT)/bin

main := $(package).$(tool)
EXEC := $(CP)/$(subst .,/,$(main)).class

JAVA_FILES := $(wildcard *.java)

JAVAC := javac -cp $(CP) -d $(CP) 

# == JAVACC

GENERATED_BY_JAVACC := Parse*.java Token*.java SimpleCharStream.java
JAVACC := javacc 

# == SCRIPT

SCRIPT := $(PROJECT)/SCRIPT

# == TEST

TEST := $(PROJECT)/TEST

# COMMANDS

.PHONY: check exec parser clean

default: $(CP)/
	@clear
	@echo "\033[90m pwd       =\033[0m"  $(shell pwd)
	@echo "\033[90m workspace =\033[0m"  $(shell cd $(workspace) ; pwd)
	@echo "\033[90m PROJECT   =\033[0m"  $(shell cd $(PROJECT) ; pwd)
	@echo "\033[90m CP        =\033[0m"  $(shell cd $(CP) ; pwd)
	@echo "\033[93m main      =\033[0m"  $(main)
	@echo "\033[93m SOURCE    =\033[0m"  $(shell cd $(SOURCE) ; pwd)
	@echo "\033[93m ls *.java =\033[0m"  "\033[90m" $(shell cd $(SOURCE) ; ls *.java) "\033[0m"
	@echo "\033[96m EXEC      =\033[0m"  $(EXEC)
	@echo "\033[96m SCRIPT    =\033[0m"  $(shell cd $(SCRIPT) ; ls)

demo: $(CP)/ $(EXEC)
	@cat $(TEST)/test00.calc
	@java -cp $(CP) $(main) -v -f $(TEST)/test00.calc

exec: $(CP)/ $(EXEC)

parser: $(CP)/ Parser.java

clean_jj: Parser.jj.md
	@rm -f Parser.jj

clean_javacc:
	@rm -f $(GENERATED_BY_JAVACC) 

clean: clean_jj clean_javacc

# RULES

$(CP)/:
	@-mkdir -p $@

$(EXEC): Parser.java $(JAVA_FILES)
	@$(JAVAC) $(JAVA_FILES) $(GENERATED_BY_JAVACC)

Parser.java: Parser.jj Parser.jj.md
	@$(JAVACC) $<
	@make clean_jj

Parser.jj: Parser.jj.md	
	@sed -f $(SCRIPT)/lit2src.sed $< > $@