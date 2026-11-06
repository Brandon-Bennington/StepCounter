# StepCounter — MDI2 113

Phone and Wear OS companion apps for the MDI2 113 data synchronization assignments.

## Source

This standalone repository adapts the class code from [Leopoldo Flores / StepCounter](https://github.com/Leopoldo-Flores/StepCounter), through instructor commit `7561063` (Session 4). It preserves the earlier MDI2 112 wearable features. The instructor source is credited here; this repository is independently maintained.

## Modules and behavior

- `mobile`: changes a numeric steps goal, sends it to the watch using the Wearable Data Layer, or saves it separately to Cloud Firestore.
- `wear`: listens while the activity is visible, updates Daily Progress and Modify Goals, and removes its listeners when paused.
- `shared`: holds the Firestore collection/document/field constants, model, and read/write helpers used by both apps.

Send to Watch writes an urgent `PutDataMapRequest` containing the goal and a timestamp. Its success callback confirms the local Data Layer write; delivery is verified separately on the paired watch. Save to Firebase writes `dailyGoal` and `updatedAt` to `fitnessData/demo-user`. Firestore listeners on both devices receive cloud changes. Send does not save to Firestore. The last received goal controls the watch display.

## Local setup

1. Open this project in Android Studio and sync Gradle with the bundled JDK.
2. Install Android SDK 37. Both application modules and the shared library compile against SDK 37; minimum SDK is 30.
3. Register Firebase Android package `com.example.stepcounter`. Put the same downloaded `google-services.json` in `mobile/` and `wear/`. The configuration is kept locally and excluded from this public repository. Do not add it to `shared/`.
4. Create Cloud Firestore and configure access to the course demo document. Use only demonstration values; this sample does not implement user authentication.
5. Run the phone app on a Google Play phone emulator and the watch app on a Wear OS emulator. Use Device Manager → Pair Wearable, then finish pairing with the companion app on the phone.
6. Keep the watch app visible. Change the goal on the phone and choose Send to Watch, then independently test Save to Firebase and inspect `fitnessData/demo-user` in Firebase Console.

Build both apps with `bash gradlew :mobile:assembleDebug :wear:assembleDebug`. Runtime verification logs use `PhoneDataLayer`, `WearDataLayer`, `SharedFirebase`, and `SharedFirebaseWear` tags.

## Assistance disclosure

Codex assisted with adapting the instructor code, configuring the local Firebase project, build checks, debugging, and preparation of assignment evidence and report drafts.
