//
//  AppDelegate.swift
//  BonjourTool
//
//  Created by Jaanus Kase on 08.05.15.
//  Copyright (c) 2015 Jaanus Kase. All rights reserved.
//

import Cocoa

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {

    let windowController = WindowController(windowNibName: "WindowController")

    func applicationDidFinishLaunching(aNotification: NSNotification) {
        // Insert code here to initialize your application
        windowController.showWindow(self)
    }

    func applicationWillTerminate(aNotification: NSNotification) {
        // Insert code here to tear down your application
    }

    func applicationShouldHandleReopen(sender: NSApplication, hasVisibleWindows flag: Bool) -> Bool {
        windowController.showWindow(self)
        return false
    }
}

