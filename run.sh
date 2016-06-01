echo "Signing with debug key"
jarsigner -keystore ~/.android/debug.keystore -storepass android -keypass android ./bin/LiquidFunPaint-release-unsigned.apk androiddebugkey
echo "Installing apk"
adb install -r ./bin/LiquidFunPaint-release-unsigned.apk
echo "Starting activity"
adb shell monkey -p com.google.fpl.liquidfunpaint -c android.intent.category.LAUNCHER 1
