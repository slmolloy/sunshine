#!/usr/bin/env bash

# Cool intents triggerable from command line

# Start up maps app for a specific zip code
adb shell am start -a android.intent.action.VIEW -d "geo:0,0?q=97006"
