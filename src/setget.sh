#!/bin/bash

arrMethods=(
"Left:i + 1:0:i+1:1"
"Right:i + 1:GRID_SIZE - 1:i + 1:GRID_SIZE - 2"
"Up:0:i + 1:1:i + 1"
"Down:GRID_SIZE - 1:i + 1:GRID_SIZE - 2:i + 1"
)

singleMethods=(
"Upleft:0:0:1:1"
"Upright:0:GRID_SIZE - 1:1:GRID_SIZE - 2"
"Downleft:GRID_SIZE - 1: 0: GRID_SIZE - 2:1"
"Downright:GRID_SIZE - 1:GRID_SIZE - 1:GRID_SIZE - 2: GRID_SIZE - 2"
)

for var in "${arrMethods[@]}"; do
  while IFS=':' read -ra arr; do
    nm="${arr[0]}"
    rs="${arr[1]}"
    cs="${arr[2]}"
    rg="${arr[3]}"
    cg="${arr[4]}"

    echo -e "public void set${nm}(boolean[] data) {
   for (int i = 0; i < GRID_SIZE - 2; i++) {
      grid[${rs}][${cs}] = data[i];
   }
}\n"

    echo -e "public boolean[] get${nm}() {
   boolean[] data = new boolean[GRID_SIZE - 2];
   for (int i = 0; i < data.length; i++) {
      data[i] = grid[${rg}][${cg}];
   }
   return data;
}\n"

  done <<< "$var"
done

for var in "${singleMethods[@]}"; do
  while IFS=':' read -ra arr; do
    nm="${arr[0]}"
    rs="${arr[1]}"
    cs="${arr[2]}"
    rg="${arr[3]}"
    cg="${arr[4]}"

    echo -e "
public void set${nm}(boolean data) {
   grid[${rs}][${cs}] = data;
}\n"

    echo -e "
public boolean get${nm}() {
   return grid[${rg}][${cg}];
}\n"

  done <<< "$var"
done
