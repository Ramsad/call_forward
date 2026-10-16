Setup (Android Studio)
1. New project -> Empty Views Activity, Kotlin, package com.example.callforwarder, minSdk 26.
2. Replace the generated .kt files and AndroidManifest.xml with these.
3. Add dependency: implementation("androidx.appcompat:appcompat:1.7.0") (already included in most templates).
4. Run on a real phone with a SIM (emulators can't do calls/USSD).
5. Open the app, save your 3 numbers, grant all permissions.
6. Test: call your phone from another phone and tap one of the buttons in the notification.
