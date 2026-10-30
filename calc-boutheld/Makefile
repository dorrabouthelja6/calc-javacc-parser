# PREDEFINED VARIABLES

workspace := ..
project := CALC
package := parser
tool := Parser

PROJECT := $(workspace)/$(project)
SOURCE := $(PROJECT)/src/$(package)
CP = $(PROJECT)/bin

main := $(package).$(tool)
EXEC := $(CP)/$(subst .,/,$(main)).class
JAVA_FILES := $(wildcard $(SOURCE)/*.java)

ext := $(shell echo $(project) | tr '[:upper:]' '[:lower:]')
TEST := $(PROJECT)/TEST

# COMMANDS

.PHONY: parser exec demo test check clean

default: $(CP)/
	@clear
	@echo "\033[90m pwd       =\033[0m"  $(shell pwd)
	@echo "\033[90m workspace =\033[0m"  $(shell cd $(workspace) ; pwd)
	@echo "\033[90m PROJECT   =\033[0m"  $(shell cd $(PROJECT) ; pwd)
	@echo "\033[90m CP        =\033[0m"  $(shell cd $(CP) ; pwd)
	@echo "\033[93m SOURCE    =\033[0m"  $(shell cd $(SOURCE) ; pwd)
	@echo "\033[93m ls *.java =\033[0m"  "\033[90m" $(shell cd $(SOURCE) ; ls *.java) "\033[0m"
	@echo "\033[93m main      =\033[0m"  $(main)
	@echo "\033[96m EXEC      =\033[0m"  $(EXEC)
	@echo "\033[94m TEST      =\033[0m"  $(shell cd $(TEST) ; pwd)
	@echo "\033[94m ls *.$(ext) =\033[0m"  $(shell cd $(TEST) ; ls *.$(ext))  

exec:
	@$(MAKE) -silent -C $(SOURCE)/ exec

parser:
	@$(MAKE) -silent -C $(SOURCE)/ parser

demo: 
	@$(MAKE) -silent -C $(SOURCE)/ demo

clean:
	$(MAKE) -silent -C $(SOURCE)/ clean
	$(MAKE) -silent -C $(TEST)/ clean
	$(if $(value CP), rm -fr $(CP)/, $(error CP is not set))

test:
	$(MAKE) -silent -C $(TEST)/ test

check: 
	$(MAKE) -silent -C $(TEST)/ check

# RULES 

$(CP)/ :
	@-mkdir -p $@
