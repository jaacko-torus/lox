cmake_minimum_required(VERSION 3.21)
project(clox C)

set(CMAKE_C_STANDARD 17)

add_executable(clox src/main.c src/common.h src/chunk.c src/chunk.h src/memory.c src/memory.h src/debug.c src/debug.h src/value.c src/value.h src/vm.c src/vm.h src/compiler.c src/compiler.h src/scanner.c src/scanner.h src/object.c src/object.h src/table.c src/table.h)
