# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\work\android\sdk/tools/proguard/proguard-android.txt
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
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# 优化时允许访问并修改有修饰符的类和类的成员

-allowaccessmodification

##################### 不混淆 #####################

# 这些类不混淆

-keep public class * extends android.app.Activity

-keep public class * extends android.app.Application

-keep public class * extends android.app.Service

-keep public class * extends android.content.BroadcastReceiver

-keep public class * extends android.content.ContentProvider

-keep public class * extends android.app.backup.BackupAgent

-keep public class * extends android.preference.Preference

-keep public class * extends android.support.v4.app.Fragment

-keep public class * extends android.support.v4.app.DialogFragment

-keep public class * extends com.actionbarsherlock.app.SherlockListFragment

-keep public class * extends com.actionbarsherlock.app.SherlockFragment

-keep public class * extends com.actionbarsherlock.app.SherlockFragmentActivity

-keep public class * extends android.app.Fragment

-keep public class com.android.vending.licensing.ILicensingService

# Native方法不混淆

-keepclasseswithmembernames class * {

native <methods>;

}

# 自定义组件不混淆

-keep public class * extends android.view.View {

public <init>(android.content.Context);

public <init>(android.content.Context, android.util.AttributeSet);

public <init>(android.content.Context, android.util.AttributeSet, int);

public void set*(...);

}

# 自定义控件类和类的成员不混淆(所有指定的类和类成员是要存在)

-keepclasseswithmembers class * {

public <init>(android.content.Context, android.util.AttributeSet);

}