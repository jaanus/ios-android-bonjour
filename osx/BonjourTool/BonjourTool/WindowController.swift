//
//  WindowController.swift
//  BonjourTool
//
//  Created by Jaanus Kase on 08.05.15.
//  Copyright (c) 2015 Jaanus Kase. All rights reserved.
//

import Cocoa
import WebKit



class WindowController: NSWindowController, NSNetServiceBrowserDelegate, NSNetServiceDelegate {

    @IBOutlet var devicesArrayController: NSArrayController!
    let serviceBrowser = NSNetServiceBrowser()
    var netServices: Array<NSNetService> = []
    let webView = WKWebView()
    @IBOutlet weak var webViewContainer: NSView!
    
    override func windowDidLoad() {
        super.windowDidLoad()
    
        serviceBrowser.delegate = self
        serviceBrowser.searchForServicesOfType("_jktest._tcp", inDomain: "")
        // Implement this method to handle any initialization after your window controller's window has been loaded from its nib file.
        
        webView.translatesAutoresizingMaskIntoConstraints = false
        webViewContainer.addSubview(webView)
        
        let horizontalConstraints = NSLayoutConstraint.constraintsWithVisualFormat("H:|-0-[webView]-0-|", options: nil, metrics: nil, views: ["webView": webView])
        let verticalConstraints = NSLayoutConstraint.constraintsWithVisualFormat("V:|-0-[webView]-0-|", options: nil, metrics: nil, views: ["webView": webView])
        
        self.webViewContainer.addConstraints(horizontalConstraints)
        self.webViewContainer.addConstraints(verticalConstraints)
        
        devicesArrayController.addObserver(self, forKeyPath: "selectionIndexes", options: NSKeyValueObservingOptions.Initial, context: nil)
    }
    
    deinit {
        devicesArrayController.removeObserver(self, forKeyPath: "selectionIndexes")
    }
    
    
    
    // MARK: - NSNetServiceBrowserDelegate
    
    func netServiceBrowser(aNetServiceBrowser: NSNetServiceBrowser, didFindService aNetService: NSNetService, moreComing: Bool) {
//        println("service browser found service: \(aNetService). more coming: \(moreComing). service name is \(aNetService.name)")
        
        if !contains(netServices, aNetService) {
            self.willChangeValueForKey("netServices")
            netServices.append(aNetService)
            self.didChangeValueForKey("netServices")
        }
        
        aNetService.delegate = self
        aNetService.resolveWithTimeout(5)
    }
    
    func netServiceBrowser(aNetServiceBrowser: NSNetServiceBrowser, didRemoveService aNetService: NSNetService, moreComing: Bool) {
//        println("service browser removed service: \(aNetService). more coming: \(moreComing)")
        if let i = find(netServices, aNetService) {
            self.willChangeValueForKey("netServices")
            netServices.removeAtIndex(i)
            self.didChangeValueForKey("netServices")
        }
    }
    
    
    // MARK: - NSNetServiceDelegate
    
    func netServiceDidResolveAddress(sender: NSNetService) {
//        println("net service did resolve address: \(sender). name is now \(sender.name)")
        // if resolved and this item is selected in the table, should do the web work
        
        let selectionIndex = devicesArrayController.selectionIndex
        if netServices[selectionIndex] == sender {
            loadContentFromNetService(sender)
        }
    }
    
    func netService(sender: NSNetService, didNotResolve errorDict: [NSObject : AnyObject]) {
        println("service \(sender) did not resolve. error: \(errorDict)")
    }
    
    
    
    // MARK: - KVO
    
    override func observeValueForKeyPath(keyPath: String, ofObject object: AnyObject, change: [NSObject : AnyObject], context: UnsafeMutablePointer<Void>) {
        let selectionIndex = devicesArrayController.selectionIndex
        
        if selectionIndex == NSNotFound {
            self.webViewContainer.hidden = true
        } else {
            self.webViewContainer.hidden = false
            let netService = netServices[selectionIndex]
            if (netService.hostName != nil) {
                // if there is no host, displaying will happen if the host gets resolved
                loadContentFromNetService(netService)
            }
        }
    }
    
    
    
    func loadContentFromNetService(netService: NSNetService) {
        if netService.hostName == nil { return }
        
        let url = NSURL(string: "http://\(netService.hostName!):\(netService.port)/")
        let urlRequest = NSURLRequest(URL: url!)
        webView.loadRequest(urlRequest)
    }
}
