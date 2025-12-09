#!/bin/bash
set -e

EXT_DIR="/home/coder/.local/share/code-server/extensions"

install_ext() {
  if ! code-server --list-extensions | grep -q "$1"; then
    echo "Installing VSCode extension: $1"
    code-server --install-extension "$1" --extensions-dir "$EXT_DIR"
  else
    echo "Extension $1 already installed"
  fi
}

install_ext bmewburn.vscode-intelephense-client
install_ext oracle.oracle-java
install_ext redhat.java
install_ext samuel-weinhardt.vscode-jsp-lang
install_ext vscjava.vscode-gradle
install_ext vscjava.vscode-java-debug
install_ext vscjava.vscode-java-test

code-server --bind-addr 0.0.0.0:8080 --auth none