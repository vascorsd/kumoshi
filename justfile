set shell := ["bash", "-c", "-x"]

# tools bins:
SBT       := 'sbt -v --supershell=never --batch'


### begin TASKS ###
clean-compile: clean build

# supply a specific TARGET to not build everything
build TARGET='all':
    @if [ "{{TARGET}}" == "all" ]; then \
        echo 'Building all targets...'; \
        {{SBT}} compile; \
    else \
        echo 'Building only specified target...'; \
        {{SBT}} "{{TARGET}} / compile"; \
    fi

# supply MODE=all really clean the workspace as if fresh git clone
clean MODE='':
    @echo 'Removing build related artifacts...'
    @rm --recursive --force --verbose build out target .target

    @if [ "{{MODE}}" == "all" ]; then \
        echo 'Removing extra stuff from workspace...'; \
        rm --recursive --force --verbose .bloop .idea .idea_modules .bsp .metals; \
    fi

run APP:
    {{SBT}} "{{APP}} / run"

### end TASKS ###


### begin SOURCE CHANGING ###
format TARGET='all':
    @if [ "{{TARGET}}" == "all" ]; then \
        {{SBT}}; \
    else \
        {{SBT}} ; \
    fi

### end SOURCE CHANGING ###


### begin CHECKING / LINTING ###
check-format TARGET='all':

check-lint:

check-updates:
    {{SBT}}

### end CHECKING / LINTING ###
