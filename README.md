# Drones CV Algo
This repository is for app developers in Android Studio environment. The app receive RTSP stream and casting opencv and native custom functions on the frames.

# OPTION 1: Setup the native libraries

Step 1: Download OpenCV Android Library Go to the OpenCV Android Sourceforge page and download the latest OpenCV Android library and extract it to the android folder.

Step 2: Import OpenCV Module
Click on File -> New -> Import Module.
Browse to the folder where you extracted the OpenCV Android library zip file contents (sdk/java).
after that click ok, next and finish.

Step 3: Fixing Gradle Sync Errors
Browse to OpenCV library module and open its build.gradle file.
change compileSdkVersion and targetSdkVersion to the app build.gradle versions. After changing the version click on sync.

Step 4: Add the OpenCV Dependency
click on File -> Project Structure.
nevigate to  module, click on the Dependencies tab click on the app module You should see a green plus button on the far right of the dialog, click on it and select Module dependency. Select the OpenCV library module and click on OK.

Step 5: Add Native Libraries
On your file explorer, navigate to the folder where you extracted the content of the OpenCV Open the sdk folder and then the native folder. copy the content of the libs folder (4 files: x86, x86_64 etc.).
right click on the app, in the project view, choose new/folder/jni folder and change it's name to jniLibs.
then paste the 4 files there.

Step 6: Add Required Permissions
Add permissions in the AndroidManifest.xml file, if necessary.
E.G: <uses-permission android:name="android.permission.CAMERA"/>

* To compile the Native code from the android studio ide, make use of the cmakelist as is in the repository.

# Usage
One can use the ready to use function in the java scope via import, or implement a cpp native code in the native-lib scope.

# OPTION 2: import JAVACV to GRADLE

    implementation 'org.bytedeco:javacv:+'
    implementation 'org.bytedeco.javacpp-presets:opencv:3.0.0-1.1:android-x86'
    implementation 'org.bytedeco.javacpp-presets:ffmpeg:2.8.1-1.1:android-x86'
    implementation 'org.bytedeco.javacpp-presets:opencv:3.0.0-1.1:android-arm'
    implementation 'org.bytedeco.javacpp-presets:ffmpeg:2.8.1-1.1:android-arm'

# Add FFMPEG-Nobile to GRADLE
add the following line in the dependancy list:
implementation 'com.arthenica:mobile-ffmpeg-full-gpl:4.3.1.LTS'

# Usage
The usage of the FFMPEG in this project is only in the java scope, via FFMpeg.execute.
see application and api guide here:https://ffmpeg.org/







