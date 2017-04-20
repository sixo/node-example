cmake_minimum_required(VERSION 3.4.1)

file(GLOB app_src_files
       "src/main/cpp/*.cpp" )

add_library( native-lib SHARED ${app_src_files} )


###########################################################
# Node.js part starts here

# Replace ${CMAKE_SOURCE_DIR}/src/main/.." with the path to
# your Node.js includes
include_directories(SYSTEM ${CMAKE_SOURCE_DIR}/src/main/include/node )

add_library( nodejs-lib SHARED IMPORTED)

# Replace "${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}" with
# the path to your libnode.so shared lib
set_target_properties( nodejs-lib
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libnode.so )

# If your libnode.so is not placed into jniLibs directory, you might need to copy
# it manually like this
#file(COPY $ENV{VENDOR}/nodejs/lib/${ANDROID_ABI}/libnode.so
#    DESTINATION ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
###########################################################


find_library( log-lib log )
target_link_libraries( native-lib ${log-lib} nodejs-lib )
