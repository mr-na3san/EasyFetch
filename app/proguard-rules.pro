-keep class com.easyfetch.app.data.model.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn kotlinx.serialization.**
-keep,includedescriptorclasses class com.easyfetch.app.**$$serializer { *; }
-keepclassmembers class com.easyfetch.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.easyfetch.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
