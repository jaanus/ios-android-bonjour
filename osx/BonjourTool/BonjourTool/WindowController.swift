//
//  WindowController.swift
//  BonjourTool
//
//  Created by Jaanus Kase on 08.05.15.
//  Copyright (c) 2015 Jaanus Kase. All rights reserved.
//

import Cocoa

class WindowController: NSWindowController, NSNetServiceBrowserDelegate, NSNetServiceDelegate {

    let serviceBrowser = NSNetServiceBrowser()
    var netServices: Array<NSNetService> = []
    
    override func windowDidLoad() {
        super.windowDidLoad()
    
        serviceBrowser.delegate = self
        serviceBrowser.searchForServicesOfType("_jktest._tcp", inDomain: "")
        // Implement this method to handle any initialization after your window controller's window has been loaded from its nib file.
    }
    
    
    
    // MARK: - NSNetServiceBrowserDelegate
    
    func netServiceBrowser(aNetServiceBrowser: NSNetServiceBrowser, didFindService aNetService: NSNetService, moreComing: Bool) {
        println("service browser found service: \(aNetService). more coming: \(moreComing)")
        
        if !contains(netServices, aNetService) {
            netServices.append(aNetService)
        }
        
        aNetService.delegate = self
        aNetService.resolveWithTimeout(5)
    }
    
    func netServiceBrowser(aNetServiceBrowser: NSNetServiceBrowser, didRemoveService aNetService: NSNetService, moreComing: Bool) {
        println("service browser removed service: \(aNetService). more coming: \(moreComing)")
        if let i = find(netServices, aNetService) {
            netServices.removeAtIndex(i)
        }
    }
    
    
    // MARK: - NSNetServiceDelegate
    
    func netServiceDidResolveAddress(sender: NSNetService) {
        println("net service did resolve address: \(sender)")
    }
    
    func netService(sender: NSNetService, didNotResolve errorDict: [NSObject : AnyObject]) {
        println("service \(sender) did not resolve. error: \(errorDict)")
    }    
}
