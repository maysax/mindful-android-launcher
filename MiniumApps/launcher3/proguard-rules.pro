# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Shahab\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#}
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn org.apache.commons.codec.binary.Base64
-dontwarn org.scribe.services.DatatypeConverterEncoder
-dontwarn butterknife.internal.**

-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
-dontwarn butterknife.Views$InjectViewProcessor
-dontwarn com.gc.materialdesign.views.**

-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**
-keep class javamail.** {*;}
-keep class javax.mail.** {*;}
-keep class javax.activation.** {*;}
-keep class com.sun.mail.dsn.** {*;}
-keep class com.sun.mail.handlers.** {*;}
-keep class com.sun.mail.smtp.** {*;}
-keep class com.sun.mail.util.** {*;}
-keep class mailcap.** {*;}
-keep class mimetypes.** {*;}
-keep class myjava.awt.datatransfer.** {*;}
-keep class org.apache.harmony.awt.** {*;}
-keep class org.apache.harmony.misc.** {*;}

-keepclassmembers class * extends org.greenrobot.**greendao.**AbstractDao
-dontwarn org.greenrobot.greendao.database.**

-dontwarn org.xmlpull.v1.**
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn okio.**
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

