#!/bin/bash

echo "Downloading first abstract file"

axel -n 16 "http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-abstract.xml"

for  i in $(seq 1 1 23)
do
  echo "http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-abstract$i.xml"
  axel -n 16 "http://dumps.wikimedia.org/enwiki/latest/enwiki-latest-abstract$i.xml"
done
