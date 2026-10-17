# Study Points – Android project

Contains the app (app/src/main/assets/index.html), the WebView shell and the app-blocking service.

## Easiest: build the APK online (no installation)
1. Create a free GitHub account, make a new repository, upload ALL files of this folder (keep the .github folder).
2. Open the repository, Actions tab, "Build APK", Run workflow.
3. After about 3 minutes open the finished run and download "StudyPoints-apk" (zip), extract app-debug.apk.

## Or with Android Studio
File > Open this folder, wait for Gradle sync, then Build > Build Bundle(s) / APK(s) > Build APK(s).

## Install on the phone
Copy app-debug.apk to the phone, open it, allow "install unknown apps" when asked.
Then: Settings tab in the app > Allow app blocking > turn on "Study Points" in Accessibility.

To update the app later: replace app/src/main/assets/index.html with the new HTML and build again.
