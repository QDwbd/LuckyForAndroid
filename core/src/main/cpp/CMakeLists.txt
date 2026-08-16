cmake_minimum_required(VERSION 3.0)

project(clash-bridge C)

# 仓库根目录路径(主仓库根目录)，用于获取 Git 版本信息
get_filename_component(GIT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../.." ABSOLUTE)

# 获取Git提交哈希（短7位）
execute_process(
  COMMAND git log -1 --format=%H
  WORKING_DIRECTORY ${GIT_ROOT}
  OUTPUT_VARIABLE COMMIT_HASH_FULL
  OUTPUT_STRIP_TRAILING_WHITESPACE
)
string(SUBSTRING "${COMMIT_HASH_FULL}" 0 7 COMMIT_HASH)
message(STATUS "git hash= ${COMMIT_HASH}")

# 获取当前分支名称
execute_process(
  COMMAND git rev-parse --abbrev-ref HEAD
  WORKING_DIRECTORY ${GIT_ROOT}
  OUTPUT_VARIABLE CURRENT_BRANCH
  OUTPUT_STRIP_TRAILING_WHITESPACE
)
message(STATUS "git current branch = ${CURRENT_BRANCH}")

# 获取编译时间
string(TIMESTAMP COMPILE_TIME "%y%m%d")
message(STATUS "compile time = ${COMPILE_TIME}")

# 生成版本信息
set(GIT_VERSION "${CURRENT_BRANCH}_${COMMIT_HASH}_${COMPILE_TIME}")
message(STATUS "version info = ${GIT_VERSION}")

# 去除空格
string(REGEX REPLACE "[ ]+" "" GIT_VERSION "${GIT_VERSION}")

# 生成 version.h 文件
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/version.h.in ${CMAKE_CURRENT_SOURCE_DIR}/version.h @ONLY)

# 下面是你原本的构建配置，不变
set(CMAKE_POSITION_INDEPENDENT_CODE on)
set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -O3")

set(GO_OUTPUT_BASE ${GO_OUTPUT}/${FLAVOR_NAME})

if ("${CMAKE_BUILD_TYPE}" STREQUAL "Debug")
    set(GO_OUTPUT_BASE "${GO_OUTPUT_BASE}Debug")
elseif ("${CMAKE_BUILD_TYPE}" STREQUAL "Release")
    set(GO_OUTPUT_BASE "${GO_OUTPUT_BASE}Release")
elseif ("${CMAKE_BUILD_TYPE}" STREQUAL "RelWithDebInfo")
    set(GO_OUTPUT_BASE "${GO_OUTPUT_BASE}Release")
else ()
    message(FATAL_ERROR "Unknown build type ${CMAKE_BUILD_TYPE}")
endif ()

include_directories("${GO_OUTPUT_BASE}/${CMAKE_ANDROID_ARCH_ABI}")
include_directories("${GO_SOURCE}")

link_directories("${GO_OUTPUT_BASE}/${CMAKE_ANDROID_ARCH_ABI}")

add_library(bridge SHARED main.c jni_helper.c bridge_helper.c)
target_link_libraries(bridge log clash)
