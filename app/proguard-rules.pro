# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Toothpick Proguard rules
# from(https://github.com/stephanenicolas/toothpick/blob/master/smoothie/proguard-rules.txt)

# Note that if we could use kapt to generate registries, possible to get rid of this
-keepattributes *Annotation*
# Do not obfuscate classes with Injected Constructors
-keepclasseswithmembernames class * { @javax.inject.Inject <init>(...); }
-keepnames @toothpick.InjectConstructor class *
# Do not obfuscate classes with Injected Fields
-keepclasseswithmembernames class * { @javax.inject.Inject <fields>; }
# Do not obfuscate classes with Injected Methods
-keepclasseswithmembernames class * { @javax.inject.Inject <methods>; }
# Do not obfuscate classes with Inject delegates
-keepclasseswithmembernames class * { toothpick.ktp.delegate.* *; }
-keep class javax.inject.**
-keep class javax.annotation.**
-keep class **__Factory { *; }
-keep class **__MemberInjector { *; }
-keepclassmembers class * {
	@javax.inject.Inject <init>(...);
	@javax.inject.Inject <init>();
	@javax.inject.Inject <fields>;
	public <init>(...);
    toothpick.ktp.delegate.* *;
}
-keep class toothpick.** { *; }

-keep @javax.inject.Singleton class *
# You need to keep your custom scopes too, e.g.
# -keepnames @foo.bar.ActivityScope class *

-keepnames @ch.bretscherhochstrasser.cleanme.annotation.ApplicationScope class *