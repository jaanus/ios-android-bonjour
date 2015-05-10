# ios-android-bonjour

This project is a simple implementation on Bonjour advertisers on several platforms, and a Mac tool to communicate with them.

[`osx`](osx) contains the Xcode project for a iOS Bonjour provider and a Mac tool to talk to the providers. [`android`](android) is an Android provider. Both providers advertise themselves as `_jktest._tcp`. They both contain a small embedded webserver that serves a “hello world” page at the site root.

## OSX installation

The Xcode project uses Cocoapods. Run `pod install` in the project folder to set everything up.

## Android installation

Everything should run out of the box. Just open the project in Android Studio and run it. Since the emulator network setup is weird, this might only work when running on a real Android device with a proper network stack.
