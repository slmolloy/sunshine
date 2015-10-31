#!/usr/bin/env bash

gradle assembleDebug
adb install -r app/build/outputs/apk/app-debug-unaligned.apk
sleep 5
adb shell am start -n com.example.android.sunshine.app/com.example.android.sunshine.app.MainActivity
