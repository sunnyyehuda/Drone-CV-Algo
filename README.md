# SLAM-on-Android

In this project, we receive a video stream from the drone to the smartphone and manipulate it Using computer vision libraries.
Eventually we apply a SLAM algorithm on the frames in the smartphone app.

# Brief work summary
  Firstly, in order to use the video stream codecs and grabbers we make use of FFMpeg grabbers with the matching codecs.
We grabe the stream from the drone using the IPCameraFrameGrabber from the FFmpeg library. And after some manipulations and
convertion we get the mat objects from the frames

  Secondly, we apply some methods from the OpenCV library. To get the OpenCV library work with our android app, we used the
precompiled version of javacv, javacpp and the original OpenCV library.

# Prerequisites
-   javacv:1.5.3
-   javacpp-presets:opencv:2.4.11-0.11:android-x86
-   javacpp-presets:ffmpeg:2.8.1-1.1:android-x86
-   javacpp-presets:opencv:2.4.11-0.11:android-arm
-   javacpp-presets:ffmpeg:2.8.1-1.1:android-arm
-   javacpp-presets:openblas:0.2.19-1.3:android-x86
-   javacpp-presets:openblas:0.2.19-1.3:android-arm
-   openCVLibrary24136

# Build Enviroment
# javacpp

The build process for the modules of javacpp-presets usually depends on the same version of the javacpp module. To insure
that you have the latest matching version of both, please execute the following before starting the build in the 
javacpp-presets directory or one of its subdirectories:
$ git clone https://github.com/bytedeco/javacpp.git --branch <tag>
$ git clone https://github.com/bytedeco/javacpp-presets.git --branch <tag>
$ cd javacpp
$ mvn clean install

To produce native libraries for Android, we basically only need to install the JDK and the NDK, which is available for Linux,
Mac OS X, and Windows. However, the build scripts of some libraries only run correctly under Linux, so we recommend using a
recent distribution of Linux (such as Fedora or Ubuntu) as build environment for Android.

Download the latest supported version of the NDK, which is r18b at the time of this writing and features Clang with libc++
instead of GCC with libstdc++, among other things.
Note: OpenBLAS requires a Fortran compiler to build the optional LAPACK library. For this, download the gcc-arm-linux-
x86_64.tar.bz2 and gcc-x86-linux-x86_64.tar.bz2 toolchains with Fortran compiler and follow the instructions from
https://github.com/buffer51/android-gfortran to deploy the toolchains into the NDK folder.
Install the NDK under, for example, ~/Android/android-ndk, where the build scripts will look for it by default.
Finally, make sure to have installed at least OpenJDK and Maven as per the instructions of your distribution.
Run the "Prerequisites for all platforms" tasks inside the shell.
After which the following command can be used to start the build inside the javacpp-presets directory or one of its
subdirectories:
$ mvn clean install -Djavacpp.platform=android-xxx -Djavacpp.platform.root=/path/to/android-ndk/
where android-xxx is either android-arm, android-arm64, android-x86, or android-x86_64.

# OpenCV
Follow this guide : https://android.jlelse.eu/a-beginners-guide-to-setting-up-opencv-android-library-on-android-studio-19794e220f3c




  


