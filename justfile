set shell := ["bash", "-c", "-x"]

# tools bins:
SBT       := 'sbt -v -d --supershell=never'
BLOOP     := 'bloop'
COURSIER  := 'coursier'
SCALAFMT  := 'scalafmt-jvm'
SCALAFIX  := 'scalafix-jvm'

clean-compile: clean reload build

reload:
    # generate bloop files
    {{SBT}} bloopInstall

# supply a specific TARGET to not build everything
build TARGET='all':
    @if [ "{{TARGET}}" == "all" ]; then \
        echo 'Building all targets...'; \
        {{BLOOP}} compile kumoshi; \
    else \
        echo 'Building only specified target...'; \
        {{BLOOP}} compile {{TARGET}}; \
    fi

# supply MODE=all really clean the workspace as if fresh git clone
clean MODE='':
    @echo 'Removing build related artifacts...'
    @rm --recursive --force --verbose build out target .target

    @if [ "{{MODE}}" == "all" ]; then \
        echo 'Removing extra stuff from workspace...'; \
        rm --recursive --force --verbose .bloop .idea .idea_modules .bsp .metals; \
    fi

run APP: build
    {{BLOOP}} run {{APP}}
