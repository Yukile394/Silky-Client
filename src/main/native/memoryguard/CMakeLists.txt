cmake_minimum_required(VERSION 3.24)
project(SilkyMemoryGuard LANGUAGES CXX)

if(APPLE)
    message(FATAL_ERROR "macOS is not a supported Silky Memory Guard target")
endif()

find_package(JNI REQUIRED)

add_library(silky_memory_guard SHARED memory_guard.cpp)
target_include_directories(silky_memory_guard PRIVATE ${JNI_INCLUDE_DIRS})
target_compile_features(silky_memory_guard PRIVATE cxx_std_17)

if(WIN32)
    target_compile_definitions(silky_memory_guard PRIVATE WIN32_LEAN_AND_MEAN NOMINMAX)
    target_link_libraries(silky_memory_guard PRIVATE advapi32)
elseif(UNIX AND CMAKE_SYSTEM_NAME STREQUAL "Linux")
    target_compile_definitions(silky_memory_guard PRIVATE _GNU_SOURCE)
else()
    message(FATAL_ERROR "Silky Memory Guard supports only Windows and Linux")
endif()
