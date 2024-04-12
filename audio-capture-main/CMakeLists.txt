cmake_minimum_required(VERSION 3.23)
project(audio_capture)

set(CMAKE_CXX_STANDARD 20)

add_executable(audio_capture ApplicationLoopback.cpp LoopbackCapture.h LoopbackCapture.cpp Common.h Queue.cpp Queue.h CaptureLibrary.cpp CaptureLibrary.h)
add_library(LoopbackCapture SHARED LoopbackCapture.h LoopbackCapture.cpp Common.h Queue.cpp Queue.h CaptureLibrary.cpp CaptureLibrary.h)

target_include_directories(audio_capture PUBLIC ${CMAKE_SOURCE_DIR}/deps/wil/include)
target_include_directories(LoopbackCapture PUBLIC ${CMAKE_SOURCE_DIR}/deps/wil/include)

target_link_libraries(audio_capture mmdevapi.lib Mfplat.lib Mfuuid.lib)
target_link_libraries(LoopbackCapture mmdevapi.lib Mfplat.lib Mfuuid.lib)