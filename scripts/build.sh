 # Copyright (C) Ocean Code Cloud. 2025-2025 .All Rights Reserved.

#!/usr/bin/env bash
set -euo pipefail
DIR=$(realpath "$(dirname "$0")")
cd "$DIR/../"
WORKDIR=$(pwd)
output_path="$WORKDIR/output"
function build_model() {
    cd "$WORKDIR/$1"
    mvn clean
    mvn install
    if [ -d "./target" ]; then
        cp "target/*.jar $output_path"
    fi
    cd "$WORKDIR"
}

if [ -d "$output_path" ]; then
  rm -rf ./output
fi
mkdir "$output_path"

build_model oceancode-spring-boot-parent
build_model oceancode-core
build_model oceancode-common-spring-boot
build_model oceancode-common-web-spring-boot
build_model oceancode-cloud
build_model oceancode-model
build_model oceancode-test
build_model oceancode-ui-test