﻿
// UpdateHandler.as
//
// Handles checking for a newer version of the simulation
// online, and what to do if that newer version exists.
//
// Author: Jonathan Olson

class UpdateHandler {
	// the latest version information detected from the server
	// TODO: rename these to match _level0 fields
	public var versionMajor : Number;
	public var versionMinor : Number;
	public var versionDev : Number;
	public var versionRevision : Number;
	public var simTimestamp : Number;
	public var simAskLaterDays : Number;
	
	public var installerRecommend : Boolean;
	public var installerTimestamp : Number;
	public var installerAskLaterDays : Number;
	
	// whether the "Check for updates now" button was clicked
	// (manual check for updates)
	public var manual : Boolean;
	
	public var common : FlashCommon;
	
	public var receivedSimResponse : Boolean;
	public var receivedInstallationResponse : Boolean;
	
	// shorthand debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function UpdateHandler() {
		debug("UpdateHandler initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// set to true if the user is manually checking for updates
		// in this case, we should give them a response if they are
		// running the current version
		manual = false;
		
		receivedSimResponse = false;
		receivedInstallationResponse = false;
		
		// make this object accessible from _level0.updateHandler
		// should only be one copy of UpdateHandler (singleton-like)
		_level0.updateHandler = this;
		common.updateHandler = this;
		
		// make sure the user allows us to check for updates!
		if(common.preferences.areUpdatesAllowed()) {
			// OLD
			//checkUpdates();
			
			// check for both sim and installation
			sendStartupQuery(startupQueryString(true, true));
		} else {
			debug("UpdateHandler: not checking for updates (Preferences.areUpdatesAllowed() = false)\n");
		}
		
	}
	
	/*
	public function checkUpdates() : Void {
		// make sure we can access phet.colorado.edu and all files under that domain
		// this is more of a sanity-check than anything else, this should be included
		// under FlashCommon.as
		System.security.allowDomain("phet.colorado.edu");
		
		// create XML that will be filled in with the response
		var xml : XML = new XML();
		
		// make sure that whitespace isn't treated as nodes! (DO NOT REMOVE THIS)
		xml.ignoreWhite = true;
		
		// function that is called when the XML is either loaded or fails somehow
		xml.onLoad = function(success : Boolean) {
			if(success) {
				_level0.debug("UpdateHandler: reply successfully received\n");
				_level0.debug(String(xml) + "\n");
				
				var hand : UpdateHandler = _level0.updateHandler;
				
				var simVersionInfo : XMLNode = xml.childNodes[0];
				var attributes : Object = simVersionInfo.attributes;
				
				hand.versionRevision = parseInt(attributes['revision']);
				hand.simTimestamp = parseInt(attributes['timestamp']);
				hand.installerTimestamp = parseInt(attributes['installer_timestamp']);
				hand.parseVersionInfo(attributes['version']);
				
				hand.debug("   latest: " + hand.common.zeroPadVersion(hand.versionMajor, hand.versionMinor, hand.versionDev) + " (" + String(hand.versionRevision) + ")\n");
				
				var latestSkipped : Array = _level0.preferences.getLatestSkippedUpdate();
				
				if(hand.versionRevision == _level0.common.getVersionRevision()) {
					// running the latest version
					_level0.debug("UpdateHandler: running latest version\n");
					
					// if the user clicked "Check for Updates Now", inform the user that no
					// update is available
					if(hand.manual) {
						hand.updatesNotAvailable();
					}
				} else if(hand.versionRevision < _level0.common.getVersionRevision()) {
					_level0.debug("WARNING UpdateHandler: running a more recent version than on the production website.\n");
				} else if(hand.versionMajor == undefined || hand.versionMinor == undefined) {
					_level0.debug("WARNING UpdateHandler: received undefined version information!\n");
				} else if(!(hand.manual) && (hand.versionMajor < latestSkipped[0] || (hand.versionMajor == latestSkipped[0] && hand.versionMinor <= latestSkipped[1]))) {
					// user did not click "Check for Updates Now" AND the new version <= latest skipped version
					_level0.debug("UpdateHandler: used selected to skip this update\n");
				} else if(!(hand.manual) && _level0.preferences.askLaterElapsed() < 1000 * 60 * 60 * 24) {
					_level0.debug("UpdateHandler: used selected ask later, time elapsed = " + String(_level0.preferences.askLaterElapsed()) + "\n");
				} else if(hand.common.fromFullInstallation() && hand.simTimestamp + 1800 > hand.installerTimestamp) {
					// installer was deployed before (or just around) the time the sim was deployed
					_level0.debug("UpdateHandler: installer might not contain the most recent sim\n");
				} else {
					hand.simUpdatesAvailable(hand.versionMajor, hand.versionMinor, hand.versionDev);
				}
				
			} else {
				_level0.debug("WARNING: UpdateHandler: Failure to obtain latest version information\n");
			}
		}
		
		/////////////////////////////////////////////
		// TODO needs to be changed to the path of the version script for each simulation
		// most likely will be http://phet.colorado.edu/simulations/sim-version-info.php?project=BLAH&sim=BLAH
		//xml.load("http://phet.colorado.edu/jolson/deploy/sims/fake-sim-version-info.php?project=" + common.getSimProject() + "&sim=" + common.getSimName());
		//xml.load("http://localhost/jolson/deploy/sims/fake-sim-version-info.php?project=" + _level0.simName + "&sim=" + _level0.simName);
		xml.load("http://phet.colorado.edu/simulations/sim-version-info.php?project=" + _level0.simName + "&sim=" + _level0.simName);
	}
	*/
	
	
	
	public function startupQueryString(checkSim : Boolean, checkInstallation : Boolean) : String {
		// if user isn't querying anything, return undefined
		if(!(checkSim || (checkInstallation && common.fromFullInstallation()))) {
			return undefined;
		}
		
		var str = "<?xml version=\"1.0\"?>";
		str += "<sim_startup_query>";
		
		if(checkSim) {
			str += "<sim_version project=\"" + common.getSimProject() + "\" sim=\"" + common.getSimName() + "\" />";
		}
		
		if(checkInstallation && common.fromFullInstallation()) {
			str += "<phet_installer_update timestamp_seconds=\"" + String(common.getInstallationTimestamp()) + "\" />";
		}
		
		str += "</sim_startup_query>";
		
		_level0.debug("UpdateHandler (2): Startup query:\n" + str + "\n");
		
		return str;
	}
	
	public function sendStartupQuery(query : String) : Void {
		
		if(query === undefined) {
			// must not be querying for anything, don't do anything
			return;
		}
		
		// make sure we can access phet.colorado.edu and all files under that domain
		// this is more of a sanity-check than anything else, this should be included
		// under FlashCommon.as
		System.security.allowDomain("phet.colorado.edu");
		
		// create XML that will be filled in with the response
		var xml : XML = new XML();
		
		// make sure that whitespace isn't treated as nodes! (DO NOT REMOVE THIS)
		xml.ignoreWhite = true;
		
		// function that is called when the XML is either loaded or fails somehow
		xml.onLoad = function(success : Boolean) {
			if(success) {
				_level0.debug("UpdateHandler (2): reply successfully received\n");
				_level0.debug(String(xml) + "\n");
				
				// TODO: remove after DEVELOPMENT
				_level0.debugXML = xml;
				
				
				
				var children : Array = xml.childNodes[0].childNodes; // children of sim_startup_query_response
				
				var hand : UpdateHandler = _level0.updateHandler;
				
				hand.receivedSimResponse = false;
				hand.receivedInstallationResponse = false;
				
				for(var idx in children) {
					var child = children[idx];
					var atts : Object = child.attributes;
					if(child.nodeName == "sim_version_response") {
						_level0.debug("UpdateHandler (2): received sim_version_response\n");
						// sanity checks
						if(atts["project"] != hand.common.getSimProject()) {
							_level0.debug("WARNING UpdateHandler (2): Project does not match\n");
						}
						if(atts["sim"] != hand.common.getSimName()) {
							_level0.debug("WARNING UpdateHandler (2): Name does not match\n");
						}
						
						hand.receivedSimResponse = true;
						
						hand.versionMajor = parseInt(atts["version_major"]);
						hand.versionMinor = parseInt(atts["version_minor"]);
						hand.versionDev = parseInt(atts["version_dev"]);
						hand.versionRevision = parseInt(atts["version_revision"]);
						hand.simTimestamp = parseInt(atts["version_timestamp"]);
						hand.simAskLaterDays = parseInt(atts["ask_me_later_duration_days"]);
						
						hand.debug("   latest: " + hand.common.zeroPadVersion(hand.versionMajor, hand.versionMinor, hand.versionDev) + " (" + String(hand.versionRevision) + ")\n");
						
					} else if(child.nodeName == "phet_installer_update_response") {
						_level0.debug("UpdateHandler (2): received phet_installer_update_response\n");
						
						hand.receivedInstallationResponse = true;
						
						hand.installerRecommend = (atts["recommend_update"] == "true");
						hand.installerTimestamp = parseInt(atts["timestamp_seconds"]);
						hand.installerAskLaterDays = parseInt(atts["ask_me_later_duration_days"]);
					} else {
						_level0.debug("WARNING UpdateHandler (2): unknown child: " + child.nodeName + "\n");
						_level0.debugLastUnknownChild = child;
					}
				}
				
				hand.handleResponse();
				
			} else {
				_level0.debug("WARNING: UpdateHandler (2): Failure to obtain latest version information\n");
			}
		}
		
		// send the request, wait for the response to load
		xml.load("http://localhost/jolson/deploy/fake-sim-startup-query.php?request=" + escape(query));
	}
	
	public function manualCheckSim() : Void {
		debug("UpdateHandler: checking manually for sim");
		manual = true;
		sendStartupQuery(startupQueryString(true, false));
	}
	
	public function manualCheckInstallation() : Void {
		debug("UpdateHandler: checking manually for installation");
		manual = true;
		sendStartupQuery(startupQueryString(false, true));
	}
	
	public function handleResponse() : Void {
		debug("UpdateHandler: handleResponse()\n");
		if(receivedInstallationResponse && common.fromFullInstallation()) {
			receivedInstallationResponse = false;
			
			if(installerRecommend) {
				if(!manual && common.preferences.installationAskLaterElapsed() < 0) {
					_level0.debug("UpdateHandler: used selected ask later, installation time elapsed = " + String(common.preferences.installationAskLaterElapsed()) + "\n");
				} else {
					installationUpdatesAvailable(installerTimestamp, installerAskLaterDays);
				}
			} else {
				if(manual) {
					_level0.preferencesDialog.updatesInstallationButton.setText(common.strings.get("NoUpdatesAvailable", "No Updates Available"));
					_level0.preferencesDialog.updatesInstallationButton.setEnabled(false);
				}
				
				// run this again to handle whether sim response was received
				handleResponse();
			}
		} else if(receivedSimResponse) {
			receivedSimResponse = false;
			
			var latestSkipped : Array = common.preferences.getLatestSkippedUpdate();
			
			if(versionRevision == common.getVersionRevision()) {
				// running the latest version
				_level0.debug("UpdateHandler: running latest version\n");
				
				// if the user clicked "Check for Updates Now", inform the user that no
				// update is available
				if(manual) {
					_level0.preferencesDialog.updatesSimButton.setText(common.strings.get("NoUpdatesAvailable", "No Updates Available"));
					_level0.preferencesDialog.updatesSimButton.setEnabled(false);
				}
			} else if(versionRevision < common.getVersionRevision()) {
				_level0.debug("WARNING UpdateHandler: running a more recent version than on the production website.\n");
			} else if(versionMajor == undefined || versionMinor == undefined) {
				_level0.debug("WARNING UpdateHandler: received undefined version information!\n");
			} else if(!manual && (versionMajor < latestSkipped[0] || (versionMajor == latestSkipped[0] && versionMinor <= latestSkipped[1]))) {
				// user did not click "Check for Updates Now" AND the new version <= latest skipped version
				_level0.debug("UpdateHandler: used selected to skip this update\n");
			} else if(!manual && common.preferences.askLaterElapsed() < 0) {
				_level0.debug("UpdateHandler: used selected ask later, sim time elapsed = " + String(common.preferences.askLaterElapsed()) + "\n");
			} else if(common.fromFullInstallation() && simTimestamp + 1800 > installerTimestamp) {
				// TODO: what happens if installerTimestamp is not set? muahaha!
				// installer was deployed before (or just around) the time the sim was deployed
				_level0.debug("UpdateHandler: installer might not contain the most recent sim\n");
			} else {
				simUpdatesAvailable(versionMajor, versionMinor, versionDev, simAskLaterDays);
			}
			
		}
		
		/*
		
		// only called if an update is manually checked for
		_level0.preferencesDialog.updatesSimButton.setText(common.strings.get("NoUpdatesAvailable", "No Updates Available"));
		_level0.preferencesDialog.updatesSimButton.setEnabled(false);
				manual
								hand.receivedSimResponse = true;
						
						hand.versionMajor = parseInt(atts["version_major"]);
						hand.versionMinor = parseInt(atts["version_minor"]);
						hand.versionDev = parseInt(atts["version_dev"]);
						hand.versionRevision = parseInt(atts["version_revision"]);
						hand.simTimestamp = parseInt(atts["version_timestamp"]);
						hand.simAskLaterDays = parseInt(atts["ask_me_later_duration_days"]);
						

						hand.receivedInstallationResponse = true;
						
						hand.installerRecommend = (atts["recommend_update"] == "true");
						hand.installerTimestamp = parseInt(atts["timestamp_seconds"]);
						hand.installerAskLaterDays = parseInt(atts["ask_me_later_duration_days"]);
						
				var latestSkipped : Array = _level0.preferences.getLatestSkippedUpdate();
				
				if(hand.versionRevision == _level0.common.getVersionRevision()) {
					// running the latest version
					_level0.debug("UpdateHandler: running latest version\n");
					
					// if the user clicked "Check for Updates Now", inform the user that no
					// update is available
					if(hand.manual) {
						hand.updatesNotAvailable();
					}
				} else if(hand.versionRevision < _level0.common.getVersionRevision()) {
					_level0.debug("WARNING UpdateHandler: running a more recent version than on the production website.\n");
				} else if(hand.versionMajor == undefined || hand.versionMinor == undefined) {
					_level0.debug("WARNING UpdateHandler: received undefined version information!\n");
				} else if(!(hand.manual) && (hand.versionMajor < latestSkipped[0] || (hand.versionMajor == latestSkipped[0] && hand.versionMinor <= latestSkipped[1]))) {
					// user did not click "Check for Updates Now" AND the new version <= latest skipped version
					_level0.debug("UpdateHandler: used selected to skip this update\n");
				} else if(!(hand.manual) && _level0.preferences.askLaterElapsed() < 1000 * 60 * 60 * 24) {
					_level0.debug("UpdateHandler: used selected ask later, time elapsed = " + String(_level0.preferences.askLaterElapsed()) + "\n");
				} else if(hand.common.fromFullInstallation() && hand.simTimestamp + 1800 > hand.installerTimestamp) {
					// installer was deployed before (or just around) the time the sim was deployed
					_level0.debug("UpdateHandler: installer might not contain the most recent sim\n");
				} else {
					hand.simUpdatesAvailable(hand.versionMajor, hand.versionMinor, hand.versionDev);
				}
		*/
		
	}
	
	
	// called if a newer version is available online
	public function simUpdatesAvailable(versionMajor : Number, versionMinor : Number, versionDev : Number, simAskLaterDays : Number) : Void {
		debug("UpdateHandler: Sim Updates Available (dialog)!\n");
		
		if(_level0.updateSimWindow) {
			// update window exists, just show it
			debug("Showing dialog again\n");
			_level0.updateSimWindow.show();
		} else {
			// update window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.updateSimDialog = new UpdateSimDialog(versionMajor, versionMinor, versionDev, simAskLaterDays);
		}
	}
	
	// called if a newer version is available online
	public function installationUpdatesAvailable(installerTimestamp : Number, installerAskLaterDays : Number) : Void {
		debug("UpdateHandler: Installation Updates Available (dialog)!\n");
		
		if(_level0.updateInstallationWindow) {
			// update window exists, just show it
			debug("Showing dialog again\n");
			_level0.updateInstallationWindow.show();
		} else {
			// update window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.updateInstallationDialog = new UpdateInstallationDialog(installerTimestamp, installerAskLaterDays);
		}
	}
	
	
}

