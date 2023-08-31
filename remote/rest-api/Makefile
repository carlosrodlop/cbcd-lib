MKFILE := $(abspath $(dir $(lastword $(MAKEFILE_LIST))))
PARENT_MKFILE   := $(HOME)/.Makefile

include $(PARENT_MKFILE)

#https://docs.cloudbees.com/docs/cloudbees-cd-api/latest/flow-api/rest-apiabout

.PHONY: goto_online_docs
goto_online_docs: ## Open API REST online documentation
goto_online_docs:
	source ../.env && \
		open $$CBCD_URL/rest/doc/v1.0/

.PHONY: get_projects
get_project: ## Get project pass as env variable
get_project:
	source ../.env && \
		bash cdcb-rest-get.sh projects/$$PROJECT_NAME

.PHONY: delete_projects
delete_project: ## Delete project pass as env variable
delete_project:
	source ../.env && \
		bash cdcb-rest-delete.sh projects/$$PROJECT_NAME