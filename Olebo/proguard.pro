-dontwarn javax.annotation.**
-keep class org.sqlite.** { *; }
-keep class org.jetbrains.exposed.** { *; }
-keep class org.slf4j.simple.** { *; }

# Kotlin serialization looks up the generated serializer classes through a function on companion
# objects. The companions are looked up reflectively so we need to explicitly keep these functions.
-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
# If a companion has the serializer function, keep the companion field on the original type so that
# the reflective lookup succeeds.
-if class **.*$Companion {
  kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
}

-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keep class com.jthemedetecor.** { *; }
-keep class jdk.internal.access.** { *; }
-keep class oshi.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keepnames @kotlin.Metadata class com.******.domain.entities.**
-keep class com.******.domain.entities.** { *; }
-keepclassmembers class com.*****.domain.entities.** { *; }