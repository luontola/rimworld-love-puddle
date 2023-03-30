# RimWorld Love Puddle Calculator

Solves the problem of who to put in bed with who, without needing a spreadsheet.
See [Francis John's Rimworld Sea Ice Ep 23](https://youtu.be/X2amcS4Isu0?t=4124).

This app can be used at <https://rimworld-love-puddle.luontola.fi>

## Theory

This problem in RimWorld corresponds to finding the [maximum matching](https://en.wikipedia.org/wiki/Matching_(graph_theory)) of an undirected graph. If there are only male-female pairs, then it will be a [bipartite graph](https://en.wikipedia.org/wiki/Bipartite_graph) and finding a matching can be treated as a [network flow](https://en.wikipedia.org/wiki/Flow_network) problem. But RimWorld allows same-sex relationships, so it's not a bipartite graph, but we need to solve the more general problem. The general problem can be solved with the [blossom algorithm](https://en.wikipedia.org/wiki/Blossom_algorithm), but this app doesn't use it, but instead an unproven ad-hoc algorithm.

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
