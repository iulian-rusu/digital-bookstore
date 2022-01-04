#!/bin/sh

set -ae
. ../.env
set +a

BOOK_LIBRARY_TARGET=target/book-library-1.0.0.jar

if [ -f $BOOK_LIBRARY_TARGET ]; then
  echo "[deploy] - Found package: $BOOK_LIBRARY_TARGET"
else
  echo "[deploy] - No package found, running mvn package first"
  ./mvnw package
fi

java -jar $BOOK_LIBRARY_TARGET