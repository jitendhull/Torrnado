# Proguard rules for TorrentVault
# Keep Hilt
-keep class class_name_of_your_hilt_module { *; }
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep Room
-keep class * extends androidx.room.RoomDatabase

# Keep Retrofit / OkHttp / Gson
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
