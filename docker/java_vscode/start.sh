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

install_ext redhat.java@1.54.0
install_ext vscjava.vscode-gradle@3.17.2
install_ext vscjava.vscode-java-debug
install_ext vscjava.vscode-java-test@0.44.0
install_ext samuel-weinhardt.vscode-jsp-lang

code-server --bind-addr 0.0.0.0:8080 --auth none