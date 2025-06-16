#!/bin/bash

root=icon
sizes=( 16 32 64 128 256)

for s in ${sizes[@]}
do
	echo "$root$s.png"
	flatpak run org.inkscape.Inkscape -w $s -h $s $root.svg -o $root$s.png
done
