cmake_minimum_required(VERSION 3.4.1)

file(GLOB app_src_files
       "src/main/cpp/*.cpp" )

add_library( native-lib SHARED ${app_src_files} )


###########################################################
# Node.js part starts here

# Replace $ENV{VENDOR}/nodejs/.." with the path to
# your Node.js includes
include_directories(SYSTEM $ENV{VENDOR}/nodejs/include )
include_directories(SYSTEM $ENV{VENDOR}/nodejs/include/v8_include )

add_library( nodejs-lib SHARED IMPORTED)

# Replace "$ENV{VENDOR}/nodejs/lib/${ANDROID_ABI}/libnode.so" with
# the path to your libnode.so shared lib
set_target_properties( nodejs-lib
                      PROPERTIES IMPORTED_LOCATION
                      $ENV{VENDOR}/nodejs/lib/${ANDROID_ABI}/libnode.so )

# It still seems to be necessary to manually copy the external shared lib
# to the proper Android Studio project directory
file(COPY $ENV{VENDOR}/nodejs/lib/${ANDROID_ABI}/libnode.so
    DESTINATION ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
###########################################################


find_library( log-lib log )
target_link_libraries( native-lib ${log-lib} nodejs-lib )
