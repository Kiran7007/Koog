# Koog Agent Framework
-keep class ai.koog.** { *; }

# Kotlinx Serialization
-keepattributes Signature
-keepattributes *Annotation*

# OkHttp / Netty (used by Koog internally)
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn io.netty.**