# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript com.sil.interface
# class:
#-keepclassmembers class fqcn.of.javascript.com.sil.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
## BEGIN RETROFIT
-dontwarn okio.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keeppackagenames
## END RETROFIT
-keep public class * {
    public protected *;
}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep public class sil.com.retrofit.** { *; }
-keep public class sil.com.retrofit.model.** { *; }
-keep public class sil.com.model.** { *; }
-keep  class com.sil.ucubesdk.** { *; }

# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers enum * { *; }
#-keep class com.google.gson.stream.** { *; }
