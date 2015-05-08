# ios-android-bonjour

This project is a simple implementation on Bonjour advertisers on several platforms, and a Mac tool to communicate with them.

[`osx`](osx) contains the Xcode project for a iOS Bonjour provider and a Mac tool to talk to the providers. [`android`](android) is an Android provider. Both providers advertise themselves as `_jktest._tcp`. They both contain a small embedded webserver that serves a “hello world” page at the site root.
