# Empty ClojureScript Project

Something to get quickly started with ClojureScript for a code kata or similar.

## Requirements

- [Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html) version 11 or higher
- [Leiningen](https://leiningen.org/)

### Recommended plugins

- Visual Studio Code:
    - [Calva](https://calva.io/)
    - [Parinfer](https://github.com/oakmac/vscode-parinfer)
- IntelliJ IDEA:
    - [Cursive](https://cursive-ide.com/)

## Useful commands

Install dependencies

    npm install

Run tests when files are changed

    npm run autobuild:karma
    npm run autotest:karma

Start dev server

    npm run start
    open http://localhost:8080/

Build for distribution

    rm -rf public/js
    npm run release
    cp -a public ...

Upgrade dependencies

    lein ancient upgrade :all :check-clojure :no-tests
    npm run outdated
    npm run upgrade
