#!/bin/bash
set -euxo pipefail

rm -rf public/js
npm run release

git worktree add -f --no-checkout gh-pages gh-pages
cp -a public/* gh-pages

(
    cd gh-pages
    echo rimworld-love-puddle.luontola.fi > CNAME
    git add -A .
    git commit -m "Update site"
    git push
)

rm -rf gh-pages
