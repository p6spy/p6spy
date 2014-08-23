#!/bin/bash
set -e

# outputs "." in every $1 secs
function dots {
    while : ; do
        echo -n "."
        sleep $1
    done
}

# wraps cmd passed as argument with dots
function wrapWithDots() {
    # print dots in a bg
    dots 60 &
    pid=$!
    # run wrapped cmd
    eval $(printf "%q " "$@")
    # kill dots
    kill -9 $pid 2>/dev/null
}
