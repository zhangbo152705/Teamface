# Add itemsArr specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/aaron/document/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any itemsArr specific keep options here:

# If your itemsArr uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-ignorewarnings
-libraryjars libs/zxing.jar