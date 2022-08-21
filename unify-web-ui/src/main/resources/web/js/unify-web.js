/*
 * Copyright 2018-2022 The Code Department
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Unify Framework Javascript functions.
 * 
 * @author The Code Department
 * @since 1.0
 */
const ux = {};

const UNIFY_SHIFT = 0x0100;
const UNIFY_CTRL = 0x0200;
const UNIFY_ALT = 0x0400;

const UNIFY_RIGHT_BUTTON = 0x02;

const UNIFY_DEFAULT_POPUP_Y_SCALE = 3; // Pop-up Y offset scale

const UNIFY_DEFAULT_POPUP_TIMEOUT = 400; // .4 seconds.
const UNIFY_DELAYEDPOSTING_MIN_DELAY = 250; // .25 seconds.
const UNIFY_BUSY_INDICATOR_DISPLAY_DELAY = 200; // .2 seconds.
const UNIFY_HIDE_USER_HINT_DISPLAY_PERIOD = 3000; // 3 seconds.
const UNIFY_WINDOW_RESIZE_DEBOUNCE_DELAY = 400; // .4 seconds.
const UNIFY_KEY_SEARCH_MAX_GAP = 1000; // 1 second.
const UNIFY_TREEDOUBLECLICK_DELAY = 250; // .25 seconds.
const UNIFY_LASTUSERACT_EFFECT_PERIOD = 180000; // 3 minutes.

const UNIFY_MAX_STRETCHPANEL_DEPTH = 5;

const UNIFY_MINUTES_IN_DAY = 1440;
const UNIFY_MINUTES_IN_HOUR = 60;

const UNIFY_KEY_ESCAPE = '27';
const UNIFY_KEY_UP = '38';
const UNIFY_KEY_DOWN = '40';
const UNIFY_KEY_ENTER = '13';
const UNIFY_KEY_SPACE = '32';
const UNIFY_KEY_BACKSPACE = '8';
const UNIFY_KEY_TAB = '9';
const UNIFY_KEY_DELETE = '46';

const UNIFY_POST_COMMIT_QUEUE = true; // Set to false to switch off commit queuing
const UNIFY_POST_COMMIT_QUEUE_REPEAT_DELAY = 20; // 20 milliseconds
const UNIFY_POST_COMMIT_QUEUE_FIRE_DELAY = 100; // 100 milliseconds
ux.postCommitQueue = [];
ux.postCommitExecuting = false;

ux.docPath = "";
ux.docPopupBaseId = null;
ux.docPopupId = null;
ux.docSysInfoId = null;
ux.docSessionId = null;

ux.popupVisible = false;

ux.submitting = false;
ux.busyIndicator = "";
ux.busyIndicatorTimer;
ux.busyCounter = 0;

ux.cntId = null
ux.cntHintId = null
ux.cntTabCloseId = null;
ux.cntOpenPath = null;
ux.cntSaveIsRemote = false;
ux.cntSavePath = null;
ux.cntSaveList = null;
ux.cntSaveRemoteView = null;
ux.cntScrollY = -1;
ux.remoteView = null;
ux.remoteredirect = [];
ux.hintTimeout=UNIFY_HIDE_USER_HINT_DISPLAY_PERIOD;

ux.shortcuts = [];
ux.pagenamealiases = [];
ux.delayedpanelposting = [];
ux.debouncetime = [];

ux.resizefunctions = {};
ux.pageresets = {};
ux.confirmstore = {};
ux.extensionregistry = {};

ux.lastUserActTime=0;

ux.fnaliases = [];

/** Utilities */
function _id(id) {
	return document.getElementById(id);
}
function _name(name) {
	return document.getElementsByName(name);
}
function _name_0(name) {
	return document.getElementsByName(name)[0];
}
function _enc(value) {
	return encodeURIComponent(value);
}

function _proc(name) {
	var i = name.indexOf('.');
	if (i > 0) {
		return ux.extensionregistry[name.substring(0, i)][name.substring(i + 1)];
	}
	
	return ux.extensionregistry["ux"][name];
}

/** Extensions */
ux.registerExtension = function(extLiteral, extObj) {
	ux.extensionregistry[extLiteral] = extObj;
}

/** Basic * */
ux.setupDocument = function(docPath, docPopupBaseId, docPopupId, docSysInfoId, docSessionId) {
	ux.docPath = docPath;
	ux.docPopupBaseId = docPopupBaseId;
	ux.docPopupId = docPopupId;
	ux.docSysInfoId = docSysInfoId;
	ux.docSessionId = docSessionId;
}

ux.processJSON = function(jsonstring) {
	const fullResp = JSON.parse(jsonstring);
	ux.remoteView = fullResp.remoteView;
	if (fullResp.jsonResp) {
		for (var j = 0; j < fullResp.jsonResp.length; j++) {
			var resp = fullResp.jsonResp[j];
			ux.respHandler[resp.handler](resp);
			if (resp.focusOnWidget) {
				ux.setFocus({wdgid: resp.focusOnWidget});
			}
		}
		ux.cascadeStretch();
	}

	if (ux.cntId) {
		var elem = _id(ux.cntId);
		if (elem) {
			if (fullResp.scrollReset) {
				elem.scrollTop = 0;
			} else if (ux.cntScrollY >= 0) {
				elem.scrollTop = ux.cntScrollY;
			}
		}
	}
	
	ux.remoteView = null;
}

ux.saveContentScroll = function() {
	ux.cntScrollY = -1;
	if (ux.cntId) {
		var elem = _id(ux.cntId);
		if (elem) {
			ux.cntScrollY = elem.scrollTop;
		}
	}
}

/** Event parameters */
ux.newEvPrm = function(rgp) {
	var evp = {};
	if (ux.remoteView) {
		evp.uViewer = ux.remoteView.view;
	}

	if (rgp.pCmdURL) {
		evp.uURL = rgp.pCmdURL;
	}

	return evp;
}

/** Response handlers */
ux.respHandler = {
	commandPostHdl : function(resp) {
		ux.postPath(resp);
	},

	docViewHdl : function(resp) {
		var trg = _id(resp.remoteTarget);
		if (trg) {
			trg.innerHTML = resp.docView.html;
			ux.perform(resp.docView.script);
		}
	},

	downloadHdl : function(resp) {
		window.location.assign(resp.downloadPath);
	},

	forwardHdl : function(resp) {
		window.location.assign(resp.loadDocument);
	},

	firePreConfirmHdl : function(resp) {
		if (resp.fire) {
			if (ux.confirmstore.handler) {
				var handler = ux.confirmstore.handler;
				var normEvt = ux.confirmstore.normEvt;
				normEvt.evp = ux.confirmstore.evp;
				ux.confirmstore = {};
				handler(normEvt);
			}
		}
	},

	hidePopupHdl : function(resp) {
		if (resp.hideSysInfoPopup) {
			var basePanel = null;
			if (ux.docPopupBaseId) {
				basePanel = _id(ux.docPopupBaseId);
			}
			if (basePanel) {
				var targetPanel = _id(ux.docPopupId);

				if (!ux.popupVisible) {
					basePanel.style.display = "none";
				}

				var sysInfoPanel = _id(ux.docSysInfoId);
				sysInfoPanel.style.visibility = "hidden";
				sysInfoPanel.innerHTML = "";
			}
		} else {
			var basePanel = null;
			if (ux.docPopupBaseId) {
				basePanel = _id(ux.docPopupBaseId);
			}
			if (basePanel) {
				basePanel.style.display = "none";
				var targetPanel = _id(ux.docPopupId);
				targetPanel.style.visibility = "hidden";
				targetPanel.innerHTML = "";
				ux.popupVisible = false;
			}
		}
	},

	hintUserHdl : function(resp) {
		if (resp.hintUserHtml) {
			var elem = _id(ux.cntHintId);
			if (elem) {
				elem.innerHTML = resp.hintUserHtml;
				if (ux.closeUserHint) {
					window.clearTimeout(ux.closeUserHint);
				}
				ux.closeUserHint = window.setTimeout("ux.hideUserHint();",
						ux.hintTimeout);
			}
		}
	},

	loadContentHdl : function(resp) {
		ux.callPageResets();
		if (resp.closeRemoteTab) {
			if (ux.cntTabCloseId) {
				ux.fireEvent(_id(ux.cntTabCloseId), "click");
			}
		} else {
			ux.refreshPageGlobals(resp);
			ux.refreshPanels(resp);
			ux.registerRespDebounce(resp);
			if (resp.busyIndicator) {
				ux.busyIndicator = resp.busyIndicator;
			}
			if (resp.scrollToTop) {
				ux.scrollDocToTop();
			}
		}
	},

	openWindowHdl : function(resp) {
		if (resp.attachment) {
			window.location.assign(resp.openWindow);
		} else {
			window.open(resp.openWindow, "_blank",
					"toolbar=0,location=0,menubar=0");
		}
	},

	postHdl : function(resp) {
		ux.postPath(resp);
	},

	refreshMenuHdl : function(resp) {
		ux.refreshPanels(resp);
	},

	refreshPanelHdl : function(resp) {
		ux.refreshPageGlobals(resp);
		ux.refreshPanels(resp);
		ux.registerRespDebounce(resp);
	},

	refreshSectionHdl : function(resp) {
		ux.refreshSection(resp);
		ux.registerRespDebounce(resp);
	},

	showPopupHdl : function(resp) {
		ux.refreshPageGlobals(resp);
		if (resp.showSysInfoPopup) {
			var basePanel = null;
			if (ux.docPopupBaseId) {
				basePanel = _id(ux.docPopupBaseId);
			}
			if (basePanel) {
				if (resp.showSysInfoPopup.html) {
					basePanel.style.display = "block";
					var sysInfoPanel = _id(ux.docSysInfoId);
					sysInfoPanel.style.visibility = "hidden";
					sysInfoPanel.innerHTML = resp.showSysInfoPopup.html;
					ux.centralize(basePanel, sysInfoPanel);
					sysInfoPanel.style.visibility = "visible";
					basePanel.style.display = "block";
					ux.perform(resp.showSysInfoPopup.script);
				}
			}
		} else if (resp.showPopup) {
			var basePanel = null;
			if (ux.docPopupBaseId) {
				basePanel = _id(ux.docPopupBaseId);
			}
			if (basePanel) {
				if (resp.showPopup.html) {
					basePanel.style.display = "block";
					var sysInfoPanel = _id(ux.docSysInfoId);
					sysInfoPanel.style.visibility = "hidden";

					var targetPanel = _id(ux.docPopupId);
					targetPanel.style.visibility = "hidden";
					targetPanel.innerHTML = resp.showPopup.html;
					ux.centralize(basePanel, targetPanel);
					targetPanel.style.visibility = "visible";
					ux.popupVisible = true;
					ux.perform(resp.showPopup.script);
				}
			}
		}
	},

	validationErrorHdl : function(resp) {
		for (var i = 0; i < resp.validationInfo.length; i++) {
			var vInfo = resp.validationInfo[i];
			var elem = _id(vInfo.pId);
			if (elem) {
				if (vInfo.setFocus) {
					elem.focus();
				}

				var brdElem = _id(vInfo.pBrdId);
				if (brdElem) {
					brdElem.style.border = vInfo.borderStyle;
				}
				
				var notifElem = _id(vInfo.pNotfId);
				if (notifElem) {
					if (vInfo.msg) {
						notifElem.innerHTML = vInfo.msg;
						notifElem.style.display = "inline-block";
					} else {
						notifElem.style.display = "none";
					}
				}
			}
		}
	}
}

ux.postPath = function(resp) {
	if (resp.postPath) {
		var path = resp.postPath;
		if (path == "content_open") {
			path = ux.cntOpenPath;
		} else {
			var rpath = ux.remoteredirect[path];
			if (rpath) {
				path = rpath;
			}
		}
		
		var prm = "req_doc=" + _enc(ux.docPath);
		if(resp.target) {
			prm += "&req_trg=" + _enc(resp.target);
		}
		
		var ajaxPrms = ux.ajaxConstructCallParam(path, prm, false, true, false, ux.processJSON);
		ux.ajaxCall(ajaxPrms);
	}
}

ux.refreshPanels = function(resp) {
	if (resp.refreshPanels) {
		for (var i = 0; i < resp.refreshPanels.length; i++) {
			if (resp.refreshPanels[i].html) {
				var trg = _id(resp.refreshPanels[i].target);
				if (trg) {
					trg.innerHTML = resp.refreshPanels[i].html;
				}
			}
		}

		for (var i = 0; i < resp.refreshPanels.length; i++) {
			ux.perform(resp.refreshPanels[i].script);
		}
		
		ux.markNoPushWidgets(resp.noPushWidgets);
	}
}

ux.refreshSection = function(resp) {
	if (resp.section) {
		var trg = _id(resp.section.target);
		if (trg) {
			trg.innerHTML = resp.section.html;
		}

		ux.perform(resp.section.script);
	}
}

ux.perform = function(funcs) {
	if (funcs) {
		for (var i = 0; i < funcs.length; i++) {
			var _func = funcs[i];
			try {
				ux.getfn(_func.fn)(_func.prm);
			} catch(e) {
				console.log("_func = " + JSON.stringify(_func));
				throw e;
			}
		}
	}
}

ux.refreshPageGlobals = function(resp) {
	if (resp.pSaveList) {
		ux.cntSaveList = resp.pSaveList;
	}

	if (resp.clearShortcuts) {
		ux.shortcuts = [];
	}

	ux.setPageNameAliases(resp);
}

ux.registerRespDebounce = function(resp) {
	if (resp.debounceList) {
		ux.registerDebounce(resp.debounceList, resp.debounceClear);
	}
}

ux.setPageNameAliases = function(resp) {
	if (resp.pageNameAliases) {
		for (var i = 0; i < resp.pageNameAliases.length; i++) {
			var pn = resp.pageNameAliases[i].pn;
			var aliases = resp.pageNameAliases[i].aliases;
			ux.pagenamealiases[pn] = aliases;
			// Generate dataless alias 2020-06-01
			if(pn.indexOf(".") < 0) {
				var dIndex = pn.indexOf("d");
				if (dIndex > 0) {
					ux.pagenamealiases[pn.substring(0, dIndex)] = aliases;
				}
			}
			// End generate 2020-06-01
		}
	}
}

/**
 * *************** AJAX ********************
 */
ux.ajaxCreateRequest = function() {
	var uAjaxReq = null;
	try {
		uAjaxReq = new XMLHttpRequest();
	} catch (e) {
		try {
			uAjaxReq = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (ex) {
			try {
				uAjaxReq = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (exx) {
			}
		}
	}
	return uAjaxReq;
}

ux.ajaxCall = function(ajaxPrms) {
	if (ajaxPrms.uSync) {
		if (ux.submitting)
			return;
		else
			ux.submitting = true;
	}

	var uAjaxReq = ux.ajaxCreateRequest();
	if (uAjaxReq == null)
		return;

	if (ajaxPrms.uBusy) {
		ux.busyCounter++;
		ux.prepareBusyIndicator();
	}

	if (ajaxPrms.uIsDebounce) {
		ajaxPrms.uDebounced = ux.effectDebounce();
	}
	
	try {
		ux.saveContentScroll();
		uAjaxReq.open("POST", ajaxPrms.uURL, true);
		if (ajaxPrms.uEncoded) {
			uAjaxReq.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded");
		}
		
		uAjaxReq.onreadystatechange = function() {
			if (uAjaxReq.readyState == 4) {
				if (ajaxPrms.uSync) {
					ux.submitting = false;
				}
				
				if (ajaxPrms.uBusy) {
					if ((--ux.busyCounter) <= 0) {
						ux.hideBusyIndicator();
					}
				}
				
				if (uAjaxReq.status == 200) {
					ajaxPrms.uSuccessFunc(uAjaxReq.responseText);
				} else {
					if (!uAjaxReq.responseText || uAjaxReq.responseText.trim().length == 0) {
						alert("Unable to connect to server.");
					} else {
						alert(uAjaxReq.responseText);
					}
				}
				
				if (ajaxPrms.uIsDebounce) {
					 ux.clearDebounce(ajaxPrms.uDebounced);
				}				
				ux.postCommitExecuting = false;
			}
		};
		
		if (ajaxPrms.uParam) {
			uAjaxReq.send(ajaxPrms.uParam);
		} else {
			uAjaxReq.send();
		}
	} catch (ex) {
		if (ajaxPrms.uIsDebounce) {
			 ux.clearDebounce(ajaxPrms.uDebounced);
		}				
		ux.postCommitExecuting = false;

		if (ajaxPrms.uSync) {
			ux.submitting = false;
		}
		
		if (ajaxPrms.uBusy) {
			if ((--ux.busyCounter) <= 0) {
				ux.hideBusyIndicator();
			}
		}
		
		alert("Unable to connect to \'" + ajaxPrms.uURL + "\', exception = "
				+ ex);
	}
}

ux.ajaxCallWithJSONResp = function(trgObj, evp) {
	if(!evp.uAutoCall) {
		ux.lastUserActTime = new Date().getTime();
	}

	if (evp.uURL) {	
		var uEncoded = true;
		var uPrm;
		if (ux.detectFormElement(trgObj, evp.uRef)) {
			var param = ux.buildFormParams(trgObj, evp); 
			uPrm = param.value;
			uEncoded = false;
		} else {
			var param = ux.buildReqParams(trgObj, evp);
			uPrm = param.value;
		}
		var ajaxPrms = ux.ajaxConstructCallParam(evp.uURL, uPrm, evp.uSync,
				uEncoded, evp.uBusy, ux.processJSON);
		ajaxPrms.uIsDebounce = evp.uIsDebounce;
		ux.ajaxCall(ajaxPrms);
	}
}

ux.ajaxConstructCallParam = function(url, param, sync, encoded, busy,
		successFunc) {
	return {uURL:url, uSync:sync, uBusy:busy, uEncoded:encoded, uParam:param, uSuccessFunc:successFunc};
}

/** Busy indicator */
ux.prepareBusyIndicator = function() {
	if (ux.busyIndicatorTimer) {
		return;
	}
	ux.busyIndicatorTimer = window.setTimeout("ux.showBusyIndicator();",
			UNIFY_BUSY_INDICATOR_DISPLAY_DELAY);
}

ux.showBusyIndicator = function() {
	if (ux.busyIndicator) {
		var busyElem = _id(ux.busyIndicator);
		if (busyElem) {
			busyElem.style.display = "block";
		}
	}
}

ux.hideBusyIndicator = function() {
	if (ux.busyIndicatorTimer) {
		window.clearTimeout(ux.busyIndicatorTimer);
		ux.busyIndicatorTimer = null;
	}
	if (ux.busyIndicator) {
		var busyElem = _id(ux.busyIndicator);
		if (busyElem) {
			busyElem.style.display = "none";
		}
	}
}

ux.scrollDocToTop = function() {
	document.body.scrollTop = document.documentElement.scrollTop = 0;
}

/** ************************ ACTION HANDLERS ********************************* */
ux.forward = function(uEv) {
	var evp = uEv.evp;
	window.location.assign(evp.uURL);
}

ux.submit = function(uEv) {
	var evp = uEv.evp;
	evp.uTrg = uEv.uTrg;
	evp.uSync = true;
	ux.postCommit(evp);
}

ux.post = function(uEv) {
	var evp = uEv.evp;
	evp.uTrg = uEv.uTrg;
	ux.postCommit(uEv.evp);
}

ux.postToPath = function(evp) {
	var ajaxPrms = ux.ajaxConstructCallParam(evp.uPath,
			"req_doc=" + _enc(ux.docPath), false, true, false, ux.processJSON);
	ux.ajaxCall(ajaxPrms);
}

ux.postCommand = function(uEv) {
	var evp = uEv.evp;
	evp.uTrg = uEv.uTrg;
	evp.uURL = evp.uCmdURL;
	evp.uCmd = evp.uTrgPnl + "->" + evp.uTrgCmd;
	if (evp.uRefreshPnls) {
		evp.uPanels = evp.uRefreshPnls;
	}
	ux.postCommit(evp);
}

ux.postCommit = function(evp) {
	evp.uBusy = true;
	if (UNIFY_POST_COMMIT_QUEUE) {
		ux.postCommitQueue.push(evp);
	} else {
		ux.setHiddenValues(evp.uRef, evp.uVal);
		ux.ajaxCallWithJSONResp(evp.uTrg, evp);
	}
}

ux.postCommitProcessor = function() {
   setTimeout(async function() {
	   if (!ux.postCommitExecuting) {
		   const evp = ux.postCommitQueue.pop();
		   if(evp) {
			   ux.postCommitExecuting = true;
			   await ux.sleep(UNIFY_POST_COMMIT_QUEUE_FIRE_DELAY);
			   ux.setHiddenValues(evp.uRef, evp.uVal);
			   ux.ajaxCallWithJSONResp(evp.uTrg, evp);
		   }
	   }

	   ux.postCommitProcessor();
	  }, UNIFY_POST_COMMIT_QUEUE_REPEAT_DELAY);
}

ux.openWindow = function(uEv) {
	var evp = uEv.evp;
	ux.setHiddenValues(evp.uRef, evp.uVal);
	if (evp.uURL) {
		var url = evp.uURL;
		var param = ux.buildReqParams(null, evp);
		if (param.value) {
			url = url + "?" + param.value;
		}
		
		if (evp.uWinName) {
			var winHdl = window.open(url, evp.uWinName);
			if (winHdl) {
				winHdl.focus();
			}
		} else {
			window.open(url, "_blank");
		}
	}
}

ux.download = function(uEv) {
	var evp = uEv.evp;
	window.location.assign(evp.uURL);
}

ux.delegate = function(uEv) {
	if (uEv.evp.uRef) {
		var uRef = uEv.evp.uRef
		if (uRef.delegateFunc) {
			uRef.delegateFunc(uRef.delegateParam);
		}
	}
}

ux.clear = function(uEv) {
	var trgNms = uEv.evp.uRef;
	if (trgNms) {
		for (var i = 0; i < trgNms.length; i++) {
			var pgNm = trgNms[i]
			var elem = _id(pgNm);
			if (elem) {
				if (!elem.disabled && elem.type != "button") {
					if (elem.type == "checkbox") {
						elem.checked = false;
						ux.cbSwitchImg(elem);
					} else if (elem.type == "radio") {
						elem.checked = false;
					} else if (elem.type == "select-multiple") {
						for (var k = 0; k < elem.options.length; k++) {
							elem.options[k].selected = "";
						}
					} else {
						elem.value = "";
					}
					
					// Check for facade 21/08/19
					var elem = _id("fac_" + pgNm);
					if (elem) {
						elem.value = "";
					}
				}
			}
		}
	}
}

ux.disable = function(uEv) {
	var disabled = false;
	var valRef = uEv.evp.uVRef;
	if (valRef) {
		for (var i = 0; i < valRef.length && !disabled; i++) {
			var elem = _id(valRef[i]);
			if (elem) {
				if (!elem.disabled && elem.type != "button") {
					if (elem.type == "checkbox") {
						if (!elem.checked) {
							disabled = true;
							break;
						}
					} else {
						if (elem.value == "") {
							disabled = true;
							break;
						}
					}
				}
			}
		}
	}

	ux.setDisabledById(uEv.evp.uRef, disabled);
}

ux.show = function(uEv) {
	ux.setDisplayModeByNames(uEv.evp.uRef, "block");
}

ux.hide = function(uEv) {
	ux.setDisplayModeByNames(uEv.evp.uRef, "none");
}

ux.setAllChecked = function(uEv) {
	var evp = uEv.evp;
	var trgNms = evp.uRef;
	if (trgNms) {
		var rElem = uEv.uTrg;
		if (evp.uSrcId) {
			rElem = _id(evp.uSrcId);
		}

		for (var i = 0; i < trgNms.length; i++) {
			var elems = _name(trgNms[i]);
			if (elems) {
				for (var j = 0; j < elems.length; j++) {
					var elem = elems[j];
					if (elem.type == "checkbox") {
						elem.setValue(rElem.checked);
					}
				}
			}
		}
	}
}

ux.repositionMenuPopup = function(paramObject) {
	if (paramObject) {
		var clipRect = ux.boundingRect(_id(paramObject.windowId));
		var menuRect = ux.boundingRect(_id(paramObject.menuWinId));
		var popContentRect = ux.boundingRect(_id(paramObject.popupContentId));

		if (paramObject.vertical) {
			// Vertical
			var trgTop = menuRect.top;
			var trgBottom = trgTop + popContentRect.height;
			if (trgBottom > clipRect.bottom) {
				trgTop = trgTop - (trgBottom - clipRect.bottom);
			}

			var popElem = _id(paramObject.popupId);
			popElem.style.top = trgTop + "px";
			popElem.style.left = menuRect.right + "px";
		} else {
			// Horizontal
			var trgLeft = menuRect.left;
			var trgRight = trgLeft + popContentRect.width;
			if (trgRight > clipRect.right) {
				trgLeft = trgLeft - (trgRight - clipRect.right);
			}

			var popElem = _id(paramObject.popupId);
			popElem.style.left = trgLeft + "px";
			popElem.style.top = menuRect.bottom + "px";
		}
	}
}

ux.setFocus = function(evp) {
	var elem = _id(evp.wdgid);
	if (elem) {
		elem.focus();
		ux.setCaretPosition(elem, 10000, 10000);
	}
}

ux.getCheckedPatternValue = function(sel) {
	var val = "";
	if (sel) {
		if (sel._selectIds && sel._labels) {
			var sym = false;
			for (var i = 0; i < sel._selectIds.length; i++) {
				var box = _id(sel._selectIds[i]);
				if (box && box.checked) {
					if (sym) {
						val += ",";
					} else {
						sym = true;
					}
					
					val += ux.decodeHtml(sel._labels[i]);
				}
			}
		}
	}
	
	return val;
}

ux.populateSelectOptions = function(paramObject) {
	if (paramObject) {
		var elem = _id(paramObject.fillPgId);
		if (elem && elem.type == "select-one") {
			elem.options.length = 0;
			if (paramObject.pType == "numberrange") {
				for (j = paramObject.pStart; j <= paramObject.pStop; j++) {
					elem.options[elem.options.length] = new Option(j, j);
				}
				if (paramObject.pDefault) {
					elem.value = paramObject.pDefault;
				}
			}
		}
	}
}

/** Drag and drop popup */
ux.rigDragAndDropPopup = function(rgp) {
	rgp.uTargetPnlId = ux.docPopupId;
	ux.addHdl(_id(rgp.pId), "mousedown", ux.dragDropEngage, rgp);
}

/** Remote document view panel */
ux.loadRemoteDocViewPanel = function(rgp) {
	const evp = {};
	evp.uViewer = rgp.pWinPgNm;
	evp.uURL = rgp.pRemoteURL;
	evp.uLoginId = rgp.pLoginId;
	evp.uUserName = rgp.pUserName;
	evp.uRole = rgp.pRoleCode;
	evp.uBranch = rgp.pBranchCode;
	evp.uGlobal = rgp.pGlobalFlag;
	evp.uColor = rgp.pColorScheme;

	ux.cntSaveRemoteView = {view:evp.uViewer};
	ux.postCommit(evp);
}

/** ******************* CONTAINERS ************************** */
/** Desktop Type 2 */
ux.rigDesktopType2 = function(rgp) {
	var gripToRig = _id(rgp.pGripId);
	if (gripToRig) {
		const evp = {uRigMenu:_id(rgp.pMenuId), uOpen:rgp.pOpen};
		ux.addHdl(gripToRig, "click", ux.collapseGripClickHandler,
				evp);
	}
}

ux.collapseGripClickHandler = function(uEv) {
	var evp = uEv.evp;
	if (evp.uOpen) {
		evp.uRigMenu.className = "menuclosed";
		evp.uOpen = false;
	} else {
		evp.uRigMenu.className = "menuopen";
		evp.uOpen = true;
	}
	// TODO Fire global resize
}

/** Flyout menu */
ux.rigFlyoutMenu = function(rgp) {
	// Setup menu
	var id = rgp.pId;
	var menuWinIds = rgp.pMenuWinId;
	var sliderSections = [];

	if (rgp.pMenuItems) {
		for(var i = 0; i < rgp.pMenuItems.length; i++) {
			const mItem = rgp.pMenuItems[i];
			const evp = {uMain:mItem.main, uOpenPath:mItem.actionPath};
			if (mItem.originPath) {
				ux.remoteredirect[mItem.originPath] = mItem.actionPath;
			}
			
			ux.addHdl(_id(mItem.id), "click", ux.menuOpenPath,
					evp);
		}
	}

	if (rgp.pSelId) {
		var evp = ux.newEvPrm(rgp);
		evp.uSelId = rgp.pSelId;
		evp.uCurSelId = rgp.pCurSelId;
		evp.uCmd = id + "->switchState";
		evp.uPanels = [ id ];
		evp.uRef = [ rgp.pCurSelId ];
		ux.addHdl(_id(rgp.pSelId), "change",
				ux.menuSelectChgHandler, evp);
	}
}

ux.menuOpenPath = function(uEv) {
	var evp = uEv.evp;
	if (!evp.uMain) {
		ux.hidePopup(null);
	}
	
	ux.contentOpen(uEv);
}

ux.menuSelectChgHandler = function(uEv) {
	var evp = uEv.evp;
	var currSelCtrl = _id(evp.uCurSelId);
	if (currSelCtrl) {
		currSelCtrl.value = _id(evp.uSelId).value;
	}
	ux.post(uEv);
}

/** ******************* PANELS ******************* */

/** Content panel */
ux.rigContentPanel = function(rgp) {
	ux.cntId = rgp.pId
	ux.cntHintId = rgp.pHintPanelId;
	ux.cntTabCloseId = rgp.pCloseImgId;
	ux.cntSavePath = rgp.pSavePath;
	ux.cntSaveIsRemote = rgp.pSaveIsRem;

	if (rgp.pImmURL) {
		ux.postToPath({uPath:rgp.pImmURL});
	} else {
		const currIdx = rgp.pCurIdx;
		const menuId = rgp.pMenuId;
		const uId = rgp.pId;
		for(var i = 0; i < rgp.pContent.length; i++) {
			const cnt = rgp.pContent[i];
			if (i == currIdx) {
				if (i > 0) {
					const evp = {uTabPaneId:rgp.pTabPaneId, uMenuId:menuId};
					ux.addHdl(_id(cnt.tabId), "rtclick", ux.contentOpenTabMenu,
							evp);
					ux.contentAttachClose(uId, cnt, "mic_", "CL");
					ux.contentAttachClose(uId, cnt, "mico_", "CLO");
					ux.contentAttachClose(uId, cnt, "mica_", "CLA");
				}
			} else {
				const evp = {uOpenPath:cnt.openPath};
				ux.addHdl(_id(cnt.tabId), "click", ux.contentOpen,
						evp);
			}
			
			if (i > 0) {
				const evp = {uURL:cnt.closePath};
				ux.addHdl(_id(cnt.tabImgId), "click", ux.post,
						evp);
			}
		}
		
	}
}

ux.contentOpenTabMenu = function(uEv) {
	var evp = uEv.evp;
	var loc = ux.getExactPointerCoordinates(uEv);
	// Show menu
	var openPrm = {};
	openPrm.popupId = evp.uMenuId;
	openPrm.relFrameId = evp.uTabPaneId;
	openPrm.stayOpenForMillSec = UNIFY_DEFAULT_POPUP_TIMEOUT;
	openPrm.forceReopen = true;
	openPrm.uTrg = uEv.uTrg;
	openPrm.uLoc = loc;
	ux.doOpenPopup(openPrm);
}

ux.contentOpen  = function(uEv) {
	var evp = uEv.evp;
	var path = evp.uOpenPath;
	evp.uRef = [];
	evp.uViewer = null;
	if (ux.cntSaveList && ux.cntSavePath) {
		ux.cntOpenPath = evp.uOpenPath;
		path = ux.cntSavePath;
		evp.uRef = ux.cntSaveList;
		if (ux.cntSaveIsRemote && ux.cntSaveRemoteView) {
			evp.uViewer = ux.cntSaveRemoteView.view;
		}
	}
	
	evp.uURL = path;
	ux.post(uEv);
}

ux.contentAttachClose = function(uId, cnt, type, mode) {
	const evp = {uSendTrg:mode, uURL:cnt.closePath};
	ux.addHdl(_id(type + uId), "click", ux.post,
			evp);
}

/** Detached panel */
ux.detachObj = null;
ux.rigDetachedPanel = function(rgp) {
	const id = rgp.pId;
	const detp = _id(id);
	detp.orient = rgp.pOrient;
	
	detp.show = function(originId) {
		const origin = _id(originId);
		if (origin && origin.id != this.originId) {
			const orient = this.orient;
			if (orient) {
				if (ux.detachObj) {
					ux.detachObj.hide();
				}
				
				this.style.visibility = "hidden";
				this.style.display = "block";
				const orect = origin.getBoundingClientRect();
				const drect = this.getBoundingClientRect();
				var x = 0;
				var y = 0;
				if ("bottom_left" == orient) {
					x = orect.right - drect.width;
					y = orect.bottom + 1;
				} else if ("bottom_right" == orient) {
					x = orect.left;
					y = orect.bottom + 1;
				} else if ("top_left" == orient) {
					x = orect.right - drect.width;
					y = orect.top - drect.height - 1;
				} else if ("top_right" == orient) {
					x = orect.left;
					y = orect.top - drect.height - 1;
				}
				
				// TODO Shift to stay in display window
				this.style.left = Math.round(x) + "px";
				this.style.top = Math.floor(y) + "px";
				this.style.visibility = "visible";
				
				this.originId = originId;
				ux.detachObj = this;
			}
		}
	};
	
	detp.hide = function() {
		this.style.display = "none";
		this.originId  = null;
		ux.detachObj = null;
	};
}


/** Fixed content panel */
ux.rigFixedContentPanel = function(rgp) {
	ux.cntHintId = rgp.pHintPanelId;
	ux.busyIndicator = rgp.pBusyIndId;
}

/** Split panel */
ux.rigSplitPanel = function(rgp) {
	const evp = {};
	evp.uCtrlId = rgp.pCtrlId;
	evp.uMinorId = rgp.pMinorId;
	evp.uMinorScrId = rgp.pMinorScrId;
	evp.uMajorScrId = rgp.pMajorScrId;
	evp.uMax = rgp.pMax;
	evp.uMin = rgp.pMin;
	evp.uVert = rgp.pVert;
	ux.addHdl(_id(rgp.pCtrlId), "mousedown", ux.splitEngage,
					evp);
	ux.registerResizeFunc(rgp.pId, ux.splitFitContent, evp);
	ux.splitFitContent(evp);
}

ux.splitFitContent = function(evp) {
	var minWElm = _id(evp.uMinorId);
	var minElm = _id(evp.uMinorScrId);
	var majElm = _id(evp.uMajorScrId);
	
	minElm.style.display = "none";
	majElm.style.display = "none";
	
	var dim = ux.boundingRect(minWElm);
	minElm.style.height = dim.height + "px";
	majElm.style.height = dim.height + "px";

	minElm.style.display = "inline-block";
	majElm.style.display = "inline-block";
}

ux.splCtrl = null;
ux.splMin = null;
ux.splMinScr = null;

ux.splitEngage = function(ev) {
	var evp = ev.evp;
	ux.splMinScr = _id(evp.uMinorScrId);
	ux.splMin = _id(evp.uMinorId);
	ux.splMin.rect = ux.boundingRect(ux.splMin);
	ux.splCtrl = _id(evp.uCtrlId);
	ux.splCtrl.max = evp.uMax;
	ux.splCtrl.min = evp.uMin;
	ux.splCtrl.vert = evp.uVert;
	ux.splCtrl.pos = ux.getPointerCoordinates(ev);
	ux.splCtrl.rect = ux.boundingRect(ux.splCtrl);
	ux.addDirectHdl(document, "mouseup", ux.splitDisengage);
	ux.addDirectHdl(document, "mousemove", ux.splitAction);
}

ux.splitDisengage = function(ev) {
	ux.remDirectHdl(document, "mousemove", ux.splitAction);
	ux.remDirectHdl(document, "mouseup", ux.splitDisengage);
}

ux.splitAction = function(ev) {
	var nPos = ux.getPointerCoordinates(ev);
	if (ux.splCtrl.vert) {
		var max = ux.splMin.rect.left + ux.splCtrl.max;
		var min = ux.splMin.rect.left + ux.splCtrl.min;
		var xChg = nPos.x - ux.splCtrl.pos.x;
		var x = ux.splCtrl.rect.left + xChg;
		if (x > max) {
			xChg -= (x - max);
		} else if (x < min) {
			xChg += (min - x);
		}
		ux.splCtrl.style.left = (ux.splCtrl.rect.left + xChg) + "px";
		ux.splMin.style.width = (ux.splMin.rect.width + xChg) + "px";
		ux.splMinScr.style.width = ux.splMin.style.width;
	} else {
		var max = ux.splMin.rect.top + ux.splCtrl.max;
		var min = ux.splMin.rect.top + ux.splCtrl.min;
		var yChg = nPos.y - ux.splCtrl.pos.y;
		var y = ux.splCtrl.rect.top + yChg;
		if (y > max) {
			yChg -= (y - max);
		} else if (y < min) {
			yChg += (min - y);
		}
		ux.splCtrl.style.top = (ux.splCtrl.rect.top + yChg) + "px";
		ux.splMin.style.height = (ux.splMin.rect.height + yChg) + "px";
		ux.splMinScr.style.height = ux.splMin.style.height;
	}
}

/** Stretch panel */
ux.stretchList = [];

ux.rigStretchPanel = function(rgp) {
	var id = rgp.pId;
	ux.stretchPanelResize(rgp);
	ux.registerResizeFunc(id, ux.stretchPanelResize, rgp);
}

ux.stretchPanelResize = function(rgp) {
	var id = rgp.pId;
	var pnlToRig = _id(id);
	var pnlCntToRig = _id(rgp.pContId);

	// Stretch
	pnlCntToRig.style.display = "none";
	ux.stretchArea(pnlToRig, true, true);

	// Fix
	ux.fixArea(pnlToRig, true, true);
	pnlCntToRig.style.display = "block";

	// Test for recalculation
	if (pnlToRig.style.height == "0px" || pnlToRig.style.width == "0px") {
		ux.stretchList.push(rgp);
	}
}

ux.cascadeStretch = function() {
	var j = UNIFY_MAX_STRETCHPANEL_DEPTH;
	while (j > 0 && ux.stretchList.length > 0) {
		var oldList = ux.stretchList;
		ux.stretchList = [];
		for (var i = 0; i < oldList.length; i++) {
			ux.stretchPanelResize(oldList[i]);
		}
		j--;
	}
}

/** Tabbed panel */
ux.rigTabbedPanel = function(rgp) {
	var pgNm = rgp.pId;
	var actTabId = rgp.pActTabId;

	// Post references
	var refList = [ rgp.pSelTabId ];
	if (rgp.pActTabIdList) {
		for (var i = 0; i < rgp.pActTabIdList.length; i++) {
			refList.push(rgp.pActTabIdList[i]);
		}
	}

	// Rig tab click
	if (rgp.pTabIdList) {
		for (var i = 0; i < rgp.pTabIdList.length; i++) {
			var tabPgNm = rgp.pTabIdList[i];
			if (tabPgNm != actTabId) {
				// Apply to inactive only
				var evp = ux.newEvPrm(rgp);
				evp.uCmd = pgNm + "->switchState";
				evp.uPanels = [ rgp.pContId ];
				evp.uSelId = rgp.pSelTabId;
				evp.uNewPgNm = tabPgNm;
				evp.uRef = refList;

				ux.addHdl(_id(rgp.pTabCapIdList[i]), "click",
						ux.tabbedPanelTabClickHandler, evp);
			}
		}
	}
}

ux.tabbedPanelTabClickHandler = function(uEv) {
	var evp = uEv.evp;
	var selCtrl = _id(evp.uSelId);
	if (selCtrl) {
		selCtrl.value = evp.uNewPgNm;
	}
	ux.post(uEv);
}

/** ********************* CONTROLS ********************* */
/** Common */
ux.rigValueAccessor = function(evp) {
	const elem = _id(evp.uId);
	if(elem) {
		elem.setValue = function(val) {
			this.value = val;
		};

		elem.getValue = function() {
			return this.value;
		};
	}
}

/** Accordion */
ux.rigAccordion = function(rgp) {
	var id = rgp.pId;
	var sectionCount = rgp.pSectionCount;
	if (sectionCount > 0) {
		for (var i = 0; i < sectionCount; i++) {
			var evp = ux.newEvPrm(rgp);
			if (!rgp.pCollapsed && i == rgp.pCurrSelIdx) {
				evp.uCmd = id + "->collapseContent";
			} else {
				evp.uCmd = id + "->expandContent";
			}
			evp.uPanels = [ rgp.pContId ];
			evp.uCurrSelCtrlId = rgp.pCurrSelCtrlId;
			evp.uRef = [ rgp.pCurrSelCtrlId ];
			evp.uSelIdx = i;

			ux.addHdl(_id(rgp.pHeaderIdBase + i), "click",
					ux.accordionClickHandler, evp);
		}
	}
}

ux.accordionClickHandler = function(uEv) {
	var evp = uEv.evp;
	var currSelCtrl = _id(evp.uCurrSelCtrlId);
	if (currSelCtrl) {
		currSelCtrl.value = evp.uSelIdx;
	}
	ux.post(uEv);
}

/** AssignmentBox */
ux.rigAssignmentBox = function(rgp) {
	var evPrmSel = ux.newEvPrm(rgp);
	var filterSel1;
	var filterSel2;
	if (rgp.pFilterSel1Id) {
		filterSel1 = _id(rgp.pFilterSel1Id);
		filterSel1.disabled = false;
		evPrmSel.uRef = [ rgp.pFilterSel1Id ];
	}

	if (rgp.pFilterSel2Id) {
		filterSel2 = _id(rgp.pFilterSel2Id);
		filterSel2.disabled = false;
		if (filterSel1) {
			evPrmSel.uRef = [ rgp.pFilterSel1Id, rgp.pFilterSel2Id ];
		} else {
			evPrmSel.uRef = [ rgp.pFilterSel2Id ];
		}
	}

	evPrmSel.uPanels = [ rgp.pContId ];
	ux.addHdl(filterSel1, "change", ux.post, evPrmSel);
	ux.addHdl(filterSel2, "change", ux.post, evPrmSel);

	if (!rgp.pAssnOnly) {
		var assnBoxRigBtns = function(rgp, assnBtnId, assnAllBtnId,
				unassnSelId, assnAll) {
			var unassnSel = _id(unassnSelId);
			var assnBtn = _id(assnBtnId);
			unassnSel.disabled = false;
			assnBtn.disabled = true;
			var evp = ux.newEvPrm(rgp);
			evp.uRef = [ unassnSelId ];
			evp.uPanels = [ rgp.pContId ];
			ux.addHdl(assnBtn, "click", ux.post, evp);
			if (rgp.pEditable) {
				unassnSel.dblevp = evp;
			}
			
			var btnDsbld =  !rgp.pEditable || unassnSel.options.length == 0;
			if (assnAll) {
				var assnAllBtn = _id(assnAllBtnId);
				assnAllBtn.disabled = btnDsbld;
				
				if (!btnDsbld) {
					evp = ux.newEvPrm(rgp);
					evp.uRef = [ unassnSelId ];
					evp.uPanels = [ rgp.pContId ];
					ux.addHdl(assnAllBtn, "click", function(uEv) {
						for (var i = 0; i < unassnSel.options.length; i++) {
							unassnSel.options[i].selected = true;
						}
						ux.post(uEv);
					}, evp);
				}
			}
			
			if (!btnDsbld) {
				evp = {};
				ux.addHdl(unassnSel, "change", function(uEv) {
					assnBtn.disabled = true;
					for (var i = 0; i < unassnSel.options.length; i++) {
						if (unassnSel.options[i].selected) {
							assnBtn.disabled = false;
							break;
						}
					}
				}, evp);
			}
		};

		assnBoxRigBtns(rgp, rgp.pAssnBtnId, rgp.pAssnAllBtnId,
				rgp.pUnassnSelId, rgp.pAssnAll);
		assnBoxRigBtns(rgp, rgp.pUnassnBtnId, rgp.pUnassnAllBtnId,
				rgp.pAssnSelId, rgp.pAssnAll);
	}
}

/** Checkbox */
ux.rigCheckbox = function(rgp) {
	const box = _id(rgp.pId);
	if (box) {
		box._active = rgp.pActive;
		ux.cbWire(box);
	}
}

ux.cbWire = function(box) {
	if (box) {
		if (box._active) {
			box._facId = "fac_" + box.id;
			ux.addHdl(_id(box._facId), "click", ux.cbClick, {uId:box.id});
			ux.addHdl(_id(box._facId), "keydown", ux.cbKeydown, {uId:box.id});
		}

		box.setValue = function(val) {
			this.checked = (val == true);
			ux.cbSwitchImg(this);
		};
		
		box.getValue = function() {
			return this.checked;
		};
	}
}

ux.cbKeydown = function(uEv) {
	if (uEv.uKeyCode == UNIFY_KEY_SPACE) {
		ux.cbClick(uEv);
	}
}

ux.cbClick = function(uEv) {
	const box = _id(uEv.evp.uId);
	if (box) {
		box.setValue(!box.getValue());
		ux.fireEvent(_id(box._facId), "change");
	}
}

ux.cbSwitchImg = function(box) {
	const fac = _id(box._facId);
	if (fac && fac.className) {
		if (box.getValue()) {
			fac.className = fac.className.replace("g_cbb", "g_cba");
			fac.className = fac.className.replace("g_cbd", "g_cbc");
		} else {
			fac.className = fac.className.replace("g_cba", "g_cbb");
			fac.className = fac.className.replace("g_cbc", "g_cbd");
		}
	}
}

/** Checklist */
ux.rigChecklist = function(rgp) {
	const box = _name(rgp.pNm);
	if(box) {
		for(var i = 0; i < box.length; i++) {
			box[i]._active = rgp.pActive;
			ux.cbWire(box[i]);
		}
	}

	const list = _id(rgp.pId);
	if (list) {
		list._box = box;
		list.setValue = function(val) {
			for (var i = 0; i < this._box.length; i++) {
				const box = this._box[i];
				box.setValue(val && val.includes(box.value));
			}
			
			if (list.updateFacade) {
				list.updateFacade(false);
			}
		};
		
		list.getValue = function() {
			const val = [];
			for(var i = 0; i < this._box.length; i++) {
				const box = this._box[i];
				if (box.getValue()) {
					val.push(box.value);
				}
			}
			
			return val;
		};
		
		list.setValue(rgp.pVal);
	}
}

/** Date Field */
ux.rigDateField = function(rgp) {
	const id = rgp.pId;
	const df = _id(id);
	if (df) {
		df._parts = {};
		df._header1 = _id("disp1_" + id);
		df._header2 = _id("disp2_" + id);
		df._calendar = _id("cont_" + id);
		df._monthlist = _id("month_" + id);
		df._yearlist = _id("year_" + id);
		df._format = rgp.pPattern;
		df._padLeft = true;
		df._shortDayNm = rgp.pShortDayNm;
		df._longMonthNm = rgp.pLongMonthNm;
		df._dayClass = rgp.pDayClass;
		df._currClass = rgp.pCurrClass;
		df._todayClass = rgp.pTodayClass;
		df._disableClass = rgp.pDisableClass;
		df._standard = rgp.pType == "standard";
		df._past = rgp.pType == "past";
		df._past_today = rgp.pType == "past_today";
		df._future = rgp.pType == "future";
		df._supportYear = df._standard | df._past | df._past_today | df._future;
		df._pop = rgp.pEnabled;
		
		// Month Select
		if (df._monthlist) {
			var evp = {uId:id};
			ux.addHdl(df._header1, "click", ux.dfMonthClick, evp);
			
			var html = [];
			for (var i = 0; i < df._longMonthNm.length; i++) {
				html.push("<a class=\"norm\" id=\"mn_");
				html.push(id + i);
				html.push("\">");
				html.push(df._longMonthNm[i]);
				html.push("</a>");
			}
			df._monthlist.innerHTML = html.join("");
				
			for (var i = 0; i < df._longMonthNm.length; i++) {
				var elem = _id("mn_" + id + i);
				evp = {uId:id, month:i};
				ux.addHdl(elem, "click", ux.dfMonthSelect, evp);
			}
		}
		
		// Year select
		if (df._yearlist) {
			var evp = {uId:id};
			ux.addHdl(df._header2, "click", ux.dfYearClick, evp);
			
			var currYear = new Date().getFullYear();
			var start_year = df._future ? currYear: currYear - 100;
			var end_year = (df._past || df._past_today) ? currYear: currYear + 50;

			var html = [];
			for (var i = start_year; i <= end_year; i++) {
				html.push("<a class=\"norm\" id=\"yr_");
				html.push(id + i);
				html.push("\">");
				html.push(i);
				html.push("</a>");
			}
			df._header2.start_year = start_year;
			df._header2.end_year = end_year;
			df._yearlist.innerHTML = html.join("");
				
			for (var i = start_year; i <= end_year; i++) {
				elem = _id("yr_" + id + i);
				evp = {uId:id, year:i};
				ux.addHdl(elem, "click", ux.dfYearSelect, evp);
			}
		}
		
		df.hideSelect = function() {
			if (this._monthlist) {
				this._monthlist.style.display = "none";	
			}
			
			if (this._yearlist) {
				this._yearlist.style.display = "none";	
			}
		}

		df.setValue = function(val) {
			this.setDay(val.getDate());
			this.setMonth(val.getMonth());
			this.setYear(val.getFullYear());
			this.setActual(false);
			this.updateCalendar();
		};
		
		df.getValue = function() {
			const val = new Date();
			val.setFullYear(this.getYear());
			val.setMonth(this.getMonth());
			val.setDate(this.getDay());
			return val;
		};

		df.setActual = function(fire) {
			const val = ux.applyPattern(this);
			if(this.value != val) {
				this.value = val;
				this.focus();
				if(fire) {
					ux.fireEvent(this, "change");
				}
			}
		};
		
		df.updateCalendar = function() {
			if (this._pop) {
				var blank = true;
				for(var m in this._parts) {
					blank = false;
					break;
				}
				
				if (blank) {
					const val = new Date();
					this.setDay(val.getDate());
					this.setMonth(val.getMonth());
					this.setYear(val.getFullYear());	
				}

				const month = this._scrollMonth;
				const year = this._scrollYear;

				// Display month year on header
				this._header1.innerHTML = this._longMonthNm[month];
				this._header1.sel = month;
				if (this._supportYear) {
					this._header2.innerHTML = year;
					this._header2.sel = year;
				}

				// Initialize variables and generate calendar HTML
				const selectCheck = new Date(year, month);
				const firstDay = selectCheck.getDay();
				const nextMonth = new Date(year, month + 1);
				nextMonth.setHours(nextMonth.getHours() - 3);
				var daysInMonth = nextMonth.getDate();
				var done = false;
				var rowCount = 0;
				var dayCount = 1;

				var todayDt = new Date();
				var today = todayDt.getDate();
				if (!(month == todayDt.getMonth() && year == todayDt.getFullYear())) {
					today = 0;
				}
				
				const todayCheck = new Date(todayDt.getFullYear(), todayDt.getMonth(), todayDt.getDate());

				var currentDay = this.getDay();
				if (!(month == this.getMonth() && year == this.getYear())) {
					currentDay = 0;
				}

				var calendarHtml = "<table class=\"ctable\">";
				if (this._supportYear) {
					calendarHtml += "<tr>";
					for (var i = 0; i < 7; i++) {
						calendarHtml += "<th>";
						calendarHtml += this._shortDayNm[i];
						calendarHtml += "</th>";
					}
					calendarHtml += "</tr>";
					if (this._past || this._past_today) {
						_id("btnt_" + id).disabled = true;
					}
					while (!done) {
						calendarHtml += "<tr>";
						for (var i = 0; i < 7; i++) {
							calendarHtml += "<td>";
							if ((rowCount == 0) && (i < firstDay)) {
								calendarHtml += "&nbsp;";
							} else {
								if (dayCount >= daysInMonth) {
									done = true;
								}
								if (dayCount <= daysInMonth) {
									var disableDay = (this._future && (selectCheck.getTime() < todayCheck.getTime()))
											|| (this._past && (selectCheck.getTime() >= todayCheck.getTime()))
											|| (this._past_today && (selectCheck.getTime() > todayCheck.getTime()));
									if (disableDay) {
										calendarHtml += "<span class=\"" + this._disableClass
										+ "\">" + dayCount + "</span>";
									} else {
										var dayClass = this._dayClass;
										if (dayCount == currentDay) {
											dayClass = this._currClass;
										}

										if (dayCount == today) {
											dayClass = this._todayClass;
										}
										calendarHtml += "<span class=\"" + dayClass
												+ "\" onclick=\"ux.dfDayHandler('" + this.id
												+ "'," + dayCount + ");\">" + dayCount + "</span>";
									}

									dayCount++;
									selectCheck.setDate(selectCheck.getDate() + 1);
								} else {
									calendarHtml += "&nbsp;";
								}
							}
							calendarHtml += "</td>";
						}
						calendarHtml += "</tr>";
						rowCount++;
					}
				} else {
					while (!done) {
						calendarHtml += "<tr>";
						for (var i = 0; i < 7; i++) {
							calendarHtml += "<td>";
							if (dayCount >= daysInMonth) {
								done = true;
							}
							if (dayCount <= daysInMonth) {
								var dayClass = this._dayClass;
								if (dayCount == currentDay) {
									dayClass = this._currClass;
								}

								calendarHtml += "<span class=\"" + dayClass
										+ "\" onclick=\"ux.dfDayHandler('" + this.id
										+ "'," + dayCount + ");\">" + dayCount + "</span>";
								dayCount++;
							} else {
								calendarHtml += "&nbsp;";
							}
							calendarHtml += "</td>";
						}
						calendarHtml += "</tr>";
						rowCount++;
					}
				}
				calendarHtml += "</table>";
				this._calendar.innerHTML = calendarHtml;
			}
		};

		df.setupScroll = function(scrIdPrefix, target, step) {
			const evp = {uId:this.id, uTarget:target, uStep:step};
			ux.addHdl(_id(scrIdPrefix + this.id), "click",
					ux.dfScrollHandler, evp);
		};
		
		df.setDay = function(val) {
			if (val != undefined) {
				this._parts["day_"] = "" + val;
			}
		};
		
		df.getDay = function() {
			return parseInt(this._parts["day_"]);
		};
		
		df.setMonth = function(val) {
			if (val != undefined) {
				this._scrollMonth =  val;
				this._parts["mon_"] = "" + (val + 1);
			}
		};
		
		df.getMonth = function() {
			return parseInt(this._parts["mon_"]) - 1;
		};
		
		df.setYear = function(val) {
			if (val != undefined) {
				this._scrollYear =  val;
				this._parts["year_"] = "" + val;
			}
		};
		
		df.getYear = function() {
			return parseInt(this._parts["year_"]);
		};
		
		if (df._pop) {
			if (df._supportYear) {
				df.setupScroll("decy_", "year_", -1);
				df.setupScroll("incy_", "year_", 1);
			}
			df.setupScroll("decm_", "mon_", -1);
			df.setupScroll("incm_", "mon_", 1);
			ux.popupWireClear(rgp, "btnc_" + id, [ id ]);
		}

		if (df._supportYear) {
			const evp = {uId:id};
			ux.addHdl(_id("btnt_" + id), "click", ux.dfTodayHandler, evp);
		}

		df.setDay(rgp.pDay);
		df.setMonth(rgp.pMonth);
		df.setYear(rgp.pYear);
		df.setActual(false);
		df.updateCalendar();
	}
}

ux.dfMonthClick = function(uEv) {
	const evp = uEv.evp;
	const id = uEv.evp.uId;
	const mlist = _id("month_" + id);
	const df = _id(id);
	
	const month = df._header1.sel;
	for (var i = 0; i < df._longMonthNm.length; i++) {
		_id("mn_" + id + i).className = i == month ? "sel" :"norm"; 
	}

	df.hideSelect();
	mlist.style.display = "block";
	mlist.scrollTop = month * 25;
}

ux.dfMonthSelect = function(uEv) {
	const evp = uEv.evp;
	const id = uEv.evp.uId;
	const df = _id(id);
	df.hideSelect();

	if (evp.month != df._header1.sel) {
		df.setDay(1);
		df.setMonth(evp.month);
		df.updateCalendar();
	}
}

ux.dfYearClick = function(uEv) {
	const evp = uEv.evp;
	const id = uEv.evp.uId;
	const ylist = _id("year_" + id);
	const df = _id(id);
	
	const year = df._header2.sel;
	for (var i = df._header2.start_year; i <= df._header2.end_year; i++) {
		_id("yr_" + id + i).className = i == year ? "sel" :"norm"; 
	}

	df.hideSelect();
	ylist.style.display = "block";
	ylist.scrollTop = (year - df._header2.start_year) * 25;
}

ux.dfYearSelect = function(uEv) {
	const evp = uEv.evp;
	const id = uEv.evp.uId;
	const df = _id(id);
	df.hideSelect();

	if (evp.year != df._header2.sel) {
		df.setDay(1);
		df.setYear(evp.year);
		df.updateCalendar();
	}
}

ux.dfTodayHandler = function(uEv) {
	const evp = uEv.evp;
	const df = _id(uEv.evp.uId);
	const val = new Date();
	df.setDay(val.getDate());
	df.setMonth(val.getMonth());
	df.setYear(val.getFullYear());	
	df.hideSelect();
	ux.hidePopup(null);
	df.updateCalendar();
	df.setActual(true);
}

ux.dfDayHandler = function(id, dayCount) {
	const df = _id(id);
	df.setDay(dayCount);
	df.setMonth(df._scrollMonth);
	df.setYear(df._scrollYear);		
	df.updateCalendar();
	df.hideSelect();
	ux.hidePopup(null);
	df.setActual(true);
}

ux.dfScrollHandler = function(uEv) {
	const evp = uEv.evp;
	const df = _id(uEv.evp.uId);
	if (evp.uTarget == "mon_") {
		var month = df._scrollMonth + evp.uStep;
		var yearChg = false;
		if (month >= df._longMonthNm.length) {
			month = 0;
			yearChg = true;
		} else if (month < 0) {
			month = df._longMonthNm.length - 1;
			yearChg = true;
		}

		if (yearChg) {
			df._scrollYear = df._scrollYear + evp.uStep;
		}

		df._scrollMonth = month;
	} else {
		df._scrollYear = df._scrollYear + evp.uStep;
	}
	
	df.hideSelect();
	df.updateCalendar();
}

/** Dropdown checklist */
ux.rigDropdownChecklist = function(rgp) {
	const id = rgp.pId;
	const dc = _id(id);
	if (dc) {
		dc._fac = _id(rgp.pFacId);
		dc._selectIds = rgp.pSelectIds;
		dc._keys = rgp.pKeys;
		dc._labels = rgp.pLabels;
		dc._pop = rgp.pEnabled;

		dc.updateFacade = function(fire) {
			const val = ux.getCheckedPatternValue(this);
			if (this._fac.value != val) {
				this._fac.value = val;
				if (fire) {
					ux.fireEvent(this, "change");
				}
			}	
		};
		
		if (rgp.pEnabled) {
			if (rgp.pSelAllId) {
				const box = _id(rgp.pSelAllId);
				box._active = true;
				ux.cbWire(box);

				const evp = {uSrcId:rgp.pSelAllId, uRef:[id]};
				ux.addHdl(_id("fac_" + rgp.pSelAllId), "change", ux.setAllChecked, evp);	
			}
		}

		ux.rigChecklist(rgp);
		dc.setValue(rgp.pVal);
	}
}

ux.dcHidePopup = function(prm) {
	const _item = _id(prm.id);
	if (_item) {
		_item.updateFacade(true);
	}
}

/** Duration Select */
ux.rigDurationSelect = function(rgp) {
	const id = rgp.pId;
	const ds = _id(id);
	if (ds) {
		ds._daySel = _id(rgp.pDaySelId);
		ds._hourSel = _id(rgp.pHourSelId);
		ds._minSel = _id(rgp.pMinSelId);

		ds.setValue = function(val) {
			const days = Math.floor(val / UNIFY_MINUTES_IN_DAY);
			var rem = Math.floor(val % UNIFY_MINUTES_IN_DAY);
			if (this._daySel) {
				this._daySel.setValue(days);
			}
			
			const hours = Math.floor(rem / UNIFY_MINUTES_IN_HOUR);
			rem = Math.floor(rem % UNIFY_MINUTES_IN_HOUR);
			if (this._hourSel) {
				this._hourSel.setValue(hours);
			}

			this._minSel.setValue(rem);
			this.setActual(val, false);
		};
		
		ds.setActual = function(val, fire) {
			if(!this.value || parseInt(this.value) != val) {
				this.value = val;
				if (fire) {
					ux.fireEvent(this, "change");
				}
			}
		};

		ds.getValue = function() {
			return parseInt(this.value);
		};

		const evp = {uId:id};
		if (ds._daySel) {
			ux.addHdl(ds._daySel, "change", ux.dsCalc, evp);
		}
		
		if (ds._hourSel){
			ux.addHdl(ds._hourSel, "change", ux.dsCalc, evp);
		}
		
		ux.addHdl(ds._minSel, "change", ux.dsCalc, evp);
		
		ds.setValue(rgp.pVal);
	}
}

ux.dsCalc = function(uEv) {
	const ds = _id(uEv.evp.uId);
	var total = 0;
	if (ds._daySel){
		total += parseInt(ds._daySel.getValue()) * UNIFY_MINUTES_IN_DAY;
	}
	
	if (ds._hourSel){
		total += parseInt(ds._hourSel.getValue()) * UNIFY_MINUTES_IN_HOUR;
	}
	
	total += parseInt(ds._minSel.getValue());
	ds.setActual(total, true);
}

/** File Attachment */
ux.rigFileAttachment = function(rgp) {
	var id = rgp.pId;
	if (rgp.pEditable) {
		var len = rgp.pLen;
		var fileId = rgp.pFileId;
		var attachId = rgp.pAttchId;
		var viewId = rgp.pViewId;
		var remId = rgp.pRemId;

		for (var i = 0; i < len; i++) {
			var idx = "d" + i;
			var fileElem = _id(fileId + idx)
			var evp = ux.newEvPrm(rgp);
			evp.uPanels = [ rgp.pContId ];
			evp.isUniqueTrg = true;
			ux.addHdl(fileElem, "change", ux.post, evp);

			// Attach
			evp = {fileId:fileElem.id};
			ux.addHdl(_id(attachId + idx), "click",
					ux.attachFileClickHandler, evp);

			// View
			if (rgp.pViewURL) {
				evp = {uURL:rgp.pViewURL, uPanels:[ rgp.pContId ]};
				ux.addHdl(_id(viewId + idx), "click", ux.post, evp);
			} else {
				evp = ux.newEvPrm(rgp);
				evp.uCmd = id + "->view";
				evp.uPanels = [ rgp.pContId ];
				ux.addHdl(_id(viewId + idx), "click", ux.post, evp);
			}

			// Remove
			evp = ux.newEvPrm(rgp);
			evp.uCmd = id + "->detach";
			evp.uPanels = [ rgp.pContId ];
			ux.addHdl(_id(remId + idx), "click", ux.post, evp);
		}
	}
}

/** File Upload View */
ux.rigFileUploadView = function(rgp) {
	var id = rgp.pId;
	if (rgp.pEditable) {
		var len = rgp.pLen;
		var fileId = rgp.pFileId;
		var attachId = rgp.pAttchId;
		var viewId = rgp.pViewId;
		var remId = rgp.pRemId;

		var fileElem = _id(fileId)
//		var evp = ux.newEvPrm(rgp);
//		evp.uPanels = [ rgp.pContId ];
//		evp.isUniqueTrg = true;
//		ux.addHdl(fileElem, "change", ux.post, evp);

		// Attach
		evp = {fileId:fileElem.id};
		ux.addHdl(_id(attachId), "click",
				ux.attachFileClickHandler, evp);

		// View
		if (rgp.pViewURL) {
			evp = {uURL:rgp.pViewURL, uPanels:[ rgp.pContId ]};
			ux.addHdl(_id(viewId), "click", ux.post, evp);
		} else {
			evp = ux.newEvPrm(rgp);
			evp.uCmd = id + "->view";
			evp.uPanels = [ rgp.pContId ];
			ux.addHdl(_id(viewId), "click", ux.post, evp);
		}

		// Remove
		evp = ux.newEvPrm(rgp);
		evp.uCmd = id + "->detach";
		evp.uPanels = [ rgp.pContId ];
		ux.addHdl(_id(remId), "click", ux.post, evp);
	}
}

ux.attachFileClickHandler = function(uEv) {
	_id(uEv.evp.fileId).click();
}

/** File Download */
ux.rigFileDownload = function(rgp) {
	var id = rgp.pId;
	var dBtn = _id(id);
	if (dBtn) {
		var evp = ux.newEvPrm(rgp);
		evp.uCmd = id + "->download";
		ux.addHdl(dBtn, "click", ux.post, evp);
	}
}

/** File Upload */
ux.rigFileUpload = function(rgp) {
	var id = rgp.pId;
	var fileElem = _id(id);
	if (fileElem) {
		var btnElem = _id(rgp.pBtnId);
		var fsDisabled = rgp.pDisabled;
		if (fsDisabled) {
			btnElem.disabled = true;
		} else {
			if (btnElem) {
				const evp = {};
				ux.addHdl(btnElem, "click", function(uEv) {
					fileElem.click();
				}, evp);
			}
		}

		var spanElem = _id(rgp.pSpanId);
		if (spanElem) {
			const evp = {uMaxSize:rgp.pMaxSize, uMaxMsg:rgp.pMaxMsg};
			ux.addHdl(fileElem, "change", function(uEv) {
				var maxLen = 0;
				if (uEv.evp.uMaxSize) {
					maxLen = uEv.evp.uMaxSize;
				}

				var fileListing = "";
				var files = fileElem.files;
				for (var i = 0; i < files.length; i++) {
					if (maxLen > 0 && (files[i].size / 1024) > maxLen) {
						alert(files[i].name + "\n" + uEv.evp.uMaxMsg);
						fileElem.value = null;
						fileListing = " ";
						break;
					}

					fileListing += files[i].name;
					fileListing += " ";
				}

				spanElem.value = fileListing;
			}, evp);
		}

		if (!rgp.pSelect) {
			var btnUpElem = _id(rgp.pUpBtnId);
			btnUpElem.disabled = true;
			if (!rgp.pDisabled) {
				if (rgp.pUploadURL) {
					const evp = {uURL:rgp.pUploadURL, uRef:[ id ], uPanels:[ rgp.pContId ]};
					ux.addHdl(btnUpElem, "click", ux.post, evp);
					ux.addHdl(fileElem, "change", function(uEv) {
						if (fileElem.value) {
							btnUpElem.disabled = false;
						} else {
							btnUpElem.disabled = true;
						}
					}, {});
				}
			}
		}
	}
}

/** Link grid */
ux.rigLinkGrid = function(rgp) {
	if (rgp.categories) {
		for(var i = 0; i < rgp.categories.length; i++) {
			var linkCat = rgp.categories[i];
			for(var j = 0; j < linkCat.links.length; j++) {
				const link = linkCat.links[j];
				const evp = {uURL:linkCat.pURL, uSendTrg:link.pCode};
				ux.addHdl(_id(link.pId), "click", function(uEv) {
					ux.post(uEv);
				}, evp);
			}
		}
	}
}

/** Debit Credit field */
ux.rigDebitCreditField = function(rgp) {
	const evp = {};
	evp.uId = rgp.pId;
	evp.index = rgp.pIndex;
	evp.prefixes = rgp.pPrefixes;
	evp.options = rgp.pOptions;
	evp.hid = _id(rgp.pId);
	evp.fac = _id(rgp.pFacId);
	evp.btn = _id(rgp.pBtnId);

	evp.btn.innerHTML = evp.options[evp.index];
	ux.addHdl(evp.btn, "click", ux.dcfOptionToggle, evp);	
	ux.addHdl(evp.fac, "input", ux.dcfInput, evp);	
}

ux.dcfOptionToggle = function(uEv) {
	const evp = uEv.evp;
	evp.index = evp.index == 0 ? 1 : 0;
	evp.btn.innerHTML = evp.options[evp.index];
	ux.dcfInput(uEv);
	ux.fireEvent(evp.fac, "change");
}

ux.dcfInput = function(uEv) {
	const evp = uEv.evp;
	var val = evp.fac.value;
	if (val) {
		val = evp.prefixes[evp.index] + val;
	}	
	evp.hid.value = val;
}

/** Money field */
ux.rigMoneyField = function(rgp) {
	const id = rgp.pId;
	const mf = _id(id);
	if (mf) {
		mf._norm = rgp.pNormCls;
		mf._sel = rgp.pSelCls;
		mf._selIdx = -2;
		mf._oldSelIdx = -2;
		mf._iCnt = rgp.pICnt;
		mf._selectIds = rgp.pSelectIds;
		mf._keys = rgp.pKeys;
		mf._labels = rgp.pLabels;
		mf._lastKeyHit = Date.now();
		mf._fac = _id(rgp.pFacId);
		mf._frm = _id(rgp.pFrmId);
		mf._list = _id(rgp.pLstId);
		mf._btn = _id(rgp.pBtnId);
		mf._pop = rgp.pEnabled;
		
		mf.setValue = function(val) {
			if (this._keys) {
				const currency = val.currency;
				var k = -1;
				for(var i = 0; i < this._keys.length; i++) {
					if (currency == this._keys[i]) {
						k = i;
						break;
					}
				}
				
				this._fac.value = val.amount;
				this.selectOpt(k, true, false);
			} else {
				this._btn.innerHTML = val.currency;
				this._fac.value = val.amount;
				this.setMoneyVal(false);
			}
		};
		
		mf.getValue = function() {
			return {currency:this._btn.innerHTML, amount:this._fac.value};
		};
		
		mf.selectOpt = function(index, choose, fire) {
			if (this._pop) {
				if(this._oldSelIdx != index) {
					const label = index >= 0 ? _id(this._selectIds[index]) : null;
					const olabel = this._oldSelIdx >= 0 ? _id(this._selectIds[this._oldSelIdx]) : null;
					if(label) {
						label.className = this._sel;
					}
					
					if (olabel && label != olabel) {
						olabel.className = this._norm;
					}
					
					this._oldSelIdx = index;
					if (!choose) {
						ux.listScrollToLabel(this, label);
					}
				}	
			}
			
			if (choose && (this._selIdx != index)) {
				this._btn.innerHTML = index >= 0 ? this._keys[index]:null;
				this._selIdx = index;
				this.setMoneyVal(fire);
			}
		}
		
		mf.setMoneyVal = function(fire) {
			var val = "";
			if (this._fac.value) {
				val = this._btn.innerHTML + " " + this._fac.value;
			}

			if (this.value != val) {
				this.value = val;
				if (fire) {
					ux.fireEvent(this, "change");			
				}
			}
		}
		
		ux.addHdl(mf._fac, "change", ux.mfAmountChange, {uId:id});
		ux.listWirePopFrame(mf, rgp);

		mf.setValue(rgp.pVal);
	}
}


ux.mfAmountChange = function(uEv) {
	_id(uEv.evp.uId).setMoneyVal(true);
}

ux.mfOnShow = function(rgp) {
	ux.setFocus({wdgid: rgp.pFrmId});
}

/** Multi select */
ux.rigMultiSelect = function(rgp) {
	const id = rgp.pId;
	const ms = _id(id);
	if (ms) {
		ms._norm = rgp.pNormCls;
		ms._sel = rgp.pSelCls;
		ms._selectIds = rgp.pSelectIds;
		ms._keys = rgp.pKeys;
		ms._labels = rgp.pLabels;
		ms._lastKeyHit = Date.now();
		ms._frm = _id(rgp.pFrmId);
		ms._list = _id(rgp.pLstId);
		ms._start = -1;
		
		ms.setValue = function(val) {
			for (var i = 0; i < this.options.length; i++) {
				const option = this.options[i];
				const label = _id(this._selectIds[i]);
				option.selected = val && val.includes(option.value);
				label.className = option.selected ? this._sel:this._norm;
			}
		};
		
		ms.getValue = function() {
			const val = [];
			for (var i = 0; i < this.options.length; i++) {
				const option = this.options[i];
				if (option.selected) {
					val.push(option.value);
				}
			}
			
			return val;
		};

		ms.selectOpt = function(index, scroll, fire) {
			const label = _id(this._selectIds[index]);
			label.className = this._sel;
			this.options[index].selected = true;
			if (scroll) {
				ux.listScrollToLabel(this, label);
			}

			if(fire) {
				ux.fireEvent(this, "change");
			}
		};
		
		const evp = {uId:id, uHitHandler:ux.msKeydownHit};
		ux.addHdl(ms._frm, "click", ux.focusOnClick, evp);
		ux.addHdl(ms._frm, "keydown", ux.listSearchKeydown, evp);
		for (var i = 0; i < ms._selectIds.length; i++) {
			const evpi = {uId:id, uIndex:i};
			const label = _id(ms._selectIds[i]);
			label.innerHTML = ms._labels[i];
			ux.addHdl(label, "click", ux.msSelectClick, evpi);
			ux.addHdl(label, "dblclick", ux.msSelectDblClick, evpi);
		}
		
		ms.setValue(rgp.pVal);
	}
}

ux.msKeydownHit = function(ms) {
	if (ms._indexes && ms._indexes.length > 0) {
		const optIndex = ms._indexes[0]
		ux.msUnSelectAllOpt(ms);
		ms.selectOpt(optIndex, true, false);
		ms._start = optIndex;
	}
}

ux.msSelectDblClick = function(uEv) {
	ux.msSelectClick(uEv);
	const evp = uEv.evp;
	const ms = _id(evp.uId);
	if (ms && ms.dblevp) {
		uEv.evp = ms.dblevp;
		ux.post(uEv);
	}
}

ux.msSelectClick = function(uEv) {
	const evp = uEv.evp;
	const ms = _id(evp.uId);

	if (uEv.shiftKey && ms._start >= 0) {
		var start = ms._start;
		var stop = evp.uIndex;
		if (start > stop) {
			start = stop;
			stop = ms._start;
		}

		ux.msUnSelectAllOpt(ms);
		while (start <= stop) {
			ms.selectOpt(start, false, true);
			start++;
		}
	} else {
		if (!uEv.ctrlKey) {
			ux.msUnSelectAllOpt(ms);
		}

		ms.selectOpt(evp.uIndex, false, true);
		ms._start = evp.uIndex;
	}
}

ux.msUnSelectAllOpt = function(ms) {
	for (var i = 0; i < ms._selectIds.length; i++) {
		const option = ms.options[i];
		if (option.selected) {
			_id(ms._selectIds[i]).className = ms._norm;
			option.selected = false;
		}
	}
}

/** Period field */
ux.rigPeriodField = function(rgp) {
	const id = rgp.pId;
	const pf = _id(id);
	if (pf) {
		pf._norm = rgp.pNormCls;
		pf._sel = rgp.pSelCls;
		pf._selIdx = -2;
		pf._oldSelIdx = -2;
		pf._iCnt = rgp.pICnt;
		pf._selectIds = rgp.pSelectIds;
		pf._keys = rgp.pKeys;
		pf._labels = rgp.pLabels;
		pf._lastKeyHit = Date.now();
		pf._fac = _id(rgp.pFacId);
		pf._frm = _id(rgp.pFrmId);
		pf._list = _id(rgp.pLstId);
		pf._btn = _id(rgp.pBtnId);
		pf._unit = null;
		pf._pop = rgp.pEnabled;
		
		pf.setValue = function(val) {
			const unit = val.unit;
			var k = -1;
			for(var i = 0; i < this._keys.length; i++) {
				if (unit == this._keys[i]) {
					k = i;
					break;
				}
			}
			
			this._fac.value = val.magnitude;
			this.selectOpt(k, true, false);
		};
		
		pf.getValue = function() {
			return {unit:this._unit, magnitude:this._fac.value};
		};
		
		pf.selectOpt = function(index, choose, fire) {
			if (this._pop) {
				if(this._oldSelIdx != index) {
					const label = index >= 0 ? _id(this._selectIds[index]) : null;
					const olabel = this._oldSelIdx >= 0 ? _id(this._selectIds[this._oldSelIdx]) : null;
					if (label) {
						label.className = this._sel;
					}
					if (olabel && label != olabel) {
						olabel.className = this._norm;
					}
					
					this._oldSelIdx = index;
					if (!choose) {
						ux.listScrollToLabel(this, label);
					}
				}	
			}
			
			if (choose && (this._selIdx != index)) {
				if (index >= 0) {
					this._unit = this._keys[index];
					this._btn.innerHTML = this._labels[index];
				} else {
					this._unit = null;
					this._btn.innerHTML = null;
				}

				this._selIdx = index;
				this.setPeriodVal(fire);
			}
		}
		
		pf.setPeriodVal = function(fire) {
			var val = "";
			if (this._fac.value) {
				val = this._unit + " " + this._fac.value;
			}

			if (this.value != val) {
				this.value = val;
				if (fire) {
					ux.fireEvent(this, "change");			
				}
			}
		}
		
		ux.addHdl(pf._fac, "change", ux.pfMagnitudeChange, {uId:id});
		ux.listWirePopFrame(pf, rgp);

		pf.setValue(rgp.pVal);
	}
}


ux.pfMagnitudeChange = function(uEv) {
	_id(uEv.evp.uId).setPeriodVal(true);
}

ux.pfOnShow = function(rgp) {
	ux.setFocus({wdgid: rgp.pFrmId});
}

/** Photo Upload */
ux.rigPhotoUpload = function(rgp) {
	if (rgp.pEditable) {
		const fileElem = _id(rgp.pFileId);
		const evp = ux.newEvPrm(rgp);
		evp.uPanels = [ rgp.pContId ];
		ux.addHdl(fileElem, "change", ux.post, evp);
		ux.addHdl(_id(rgp.pImgId), "click", function(uEv) {
			fileElem.click();
		}, {});
	}
}

/** Radio buttons */
ux.rigRadioButtons = function(rgp) {
	const rb = _id(rgp.pId);
	rb._name = rgp.pNm;
	
	rb.setValue = function(val) {
		const btn = _name(this._name);
		for(var i = 0; i < btn.length; i++) {
			btn[i].checked = btn[i].value == val;
		}
	};
	
	rb.getValue = function() {
		const btn = _name(this._name);
		for(var i = 0; i < btn.length; i++) {
			if (btn[i].checked) {
				return btn[i].value;
			}
		}
		
		return null;
	};
	
	rb.setValue(rgp.pVal);
}

/** Search Field */
ux.rigSearchField = function(rgp) {
	const id = rgp.pId;

	// Filter
	const fil = _id(rgp.pFilId);
	if (fil) {
		const evp = ux.newEvPrm(rgp);
		evp.uCmd = id + "->search";
		evp.uIsReqTrg = true;
		ux.addHdl(fil, "input", ux.post, evp);
	}

	ux.sfWireResult(rgp);
	ux.popupWireClear(rgp, rgp.pClrId, [ id, rgp.pFacId ]);
	ux.popupWireCancel(rgp.pCanId);
}

ux.sfWireResult = function(rgp) {
	const id = rgp.pId;
	const sf = _id(id);
	if (sf) {
		sf._selectIds = rgp.pSelectIds;
		sf._keys = rgp.pKeys;
		sf._labels = rgp.pLabels;
		sf._fac = _id(rgp.pFacId);
		
		sf.setValue = function(val) {
			this.updateFacade(val);
			this.setActual(val, false);
		};
			
		sf.getValue = function() {
			return this.value;
		};
		
		sf.updateFacade = function(val) {
			var factxt = "";
			for(var i = 0; i < this._keys.length; i++) {
				if (this._keys[i] == val) {
					factxt = this._labels[i];
					break;
				}
			}
			
			this._fac.value = factxt;
		};
		
		sf.setActual = function(val, fire) {
			if (this.value != val) {
				this.value = val;
				if (fire) {
					ux.fireEvent(this, "change");
				}
			}
		};

		for (var i = 0; i < rgp.pICnt; i++) {
			const evp = {uId:id, uIndex:i};
			const label = _id(sf._selectIds[i]);
			label.innerHTML = sf._labels[i];
			ux.addHdl(label, "click", ux.sfSelect, evp);
		}
		
		sf.setValue(rgp.pVal);
	}
}

ux.sfOnShow = function(rgp) {
	ux.setFocus({wdgid: rgp.pFilId});
}

ux.sfSelect = function(uEv) {
	const sf = _id(uEv.evp.uId);
	ux.hidePopup(null);
	const val = sf._keys[uEv.evp.uIndex];
	sf.updateFacade(val);
	sf.setActual(val, true);
}

/** Options Text Area */
ux.rigOptionsTextArea = function(rgp) {
	const id = rgp.pId;
	const ota = _id(id);
	if (ota) {
		ota._norm = rgp.pNormCls;
		ota._sel = rgp.pSelCls;
		ota._selIdx = -1;
		ota._oldSelIdx = -1;
		ota._iCnt = rgp.pICnt;
		ota._selectIds = rgp.pLabelIds;
		ota._keys = rgp.pKeys;
		ota._labels = rgp.pLabels;
		ota._lastKeyHit = Date.now();
		ota._frm = _id(rgp.pFrmId);
		ota._list = _id(rgp.pLstId);
		ota._pop = rgp.pEnabled;
		
		ota.selectOpt = function(idx, choose, fire) {
			if (this._pop) {
				if(this._oldSelIdx != idx) {
					const label = _id(this._selectIds[idx]);
					const olabel = _id(this._selectIds[this._oldSelIdx]);
					label.className = this._sel;
					if (olabel) {
						olabel.className = this._norm;
					}
					
					this._oldSelIdx = index;
					if (!choose) {
						ux.listScrollToLabel(ota, label);
					}
				}
			}
			
			if (choose) {
				this._selIdx = idx;
				var pos = ux.getCaretPosition(ota);
				var string = this.value;
				var token = "{" + this._keys[idx] + "}";
				var spos = pos.start + token.length;
				string = string.substring(0, pos.start) + token + string.substring(pos.end);
				this.value = string;
				ux.setCaretPosition(ota, spos, spos);
				this.focus();
			}
		};
		
		const evp = {};
		evp.uTrg = ota;
		evp.popupId=rgp.pPopupId;
		evp.frameId=rgp.pId;
		evp.stayOpenForMillSec = 0;
		evp.showHandler = "ux42";
		evp.showParam=rgp.pFrmId;
		ux.addHdl(ota, "keypress", ux.otaTxtKeypress, evp);
		ux.addHdl(ota, "keydown", ux.otaTxtKeydown, evp);	
		if (rgp.pScrEnd) {
			ota.scrollTop = ota.scrollHeight;
		}
		
		ux.listWirePopFrame(ota, rgp);
	}
}

ux.otaTxtKeypress = function(uEv) {

}

ux.otaTxtKeydown = function(uEv) {
	if (uEv.shiftKey && uEv.uKeyCode == UNIFY_KEY_SPACE) {
		ux.doOpenPopup(uEv.evp);
		uEv.uStop();
		return;
	}
	
	const ota = uEv.uTrg;
	var txt = ota.value;
	var pos = ux.getCaretPosition(ota);
	if (pos.start != pos.end) {
		if (uEv.uKeyCode == UNIFY_KEY_BACKSPACE || uEv.uKeyCode == UNIFY_KEY_DELETE) {
			pos.start = ux.otaTokenStart(txt, pos.start);
			pos.end = ux.otaTokenEnd(txt, pos.end);
			ota.value = txt.substring(0, pos.start) + txt.substring(pos.end);
			ux.setCaretPosition(ota, pos.start, pos.start);
			uEv.uStop();
		}
	} else {
		if (uEv.uKeyCode == UNIFY_KEY_BACKSPACE || uEv.uKeyCode == UNIFY_KEY_DELETE) {
			var i = pos.start;
			if (uEv.uKeyCode == UNIFY_KEY_BACKSPACE) {
				i--;
			}

			if (i >= 0) {
				var ch = txt.charAt(i);
				if (ch == '}') {
					pos.start = ux.otaTokenStart(txt, i);
				} else if (ch == '{') {
					pos.start = i;
					pos.end = ux.otaTokenEnd(txt, pos.end);
				} else {
					pos.start = ux.otaTokenStart(txt, i);
					pos.end = ux.otaTokenEnd(txt, pos.end);
				}
				
				if (pos.start != pos.end) {
					ota.value = txt.substring(0, pos.start) + txt.substring(pos.end);
					ux.setCaretPosition(ota, pos.start, pos.start);
					uEv.uStop();
				}
			}
		}
	}
}

ux.otaTokenStart = function(txt, start) {
	var i = start;
	while((--i) >= 0) {
		var ch = txt.charAt(i);
		if(ch == '{') {
			return i;
		} else if (ch == '}') {
			break;
		}
	}
	return start;
}

ux.otaTokenEnd = function(txt, end) {
	i = end;
	while(i < txt.length) {
		var ch = txt.charAt(i++);
		if (ch == '}') {
			return i;
		} else if (ch == '{') {
			break;
		}
	}

	return end;
}


ux.optionsTextAreaOnShow = function(frmId) {
	ux.setFocus({wdgid: frmId});
}

/** Single Select */
ux.rigSingleSelect = function(rgp) {
	const id = rgp.pId;
	const sel = _id(id);
	if (sel) {
		sel._norm = rgp.pNormCls;
		sel._sel = rgp.pSelCls;
		sel._selIdx = -2;
		sel._oldSelIdx = -2;
		sel._isBlankOption = rgp.pIsBlankOption;
		sel._selectIds = rgp.pSelectIds;
		sel._keys = rgp.pKeys;
		sel._labels = rgp.pLabels;
		sel._iCnt = rgp.pICnt;
		sel._lastKeyHit = Date.now();
		sel._fac = _id(rgp.pFacId);
		sel._frm = _id(rgp.pFrmId);
		sel._list = _id(rgp.pLstId);
		sel._blank = _id(rgp.pBlnkId);
		sel._colors = rgp.pColors;
		if (sel._colors) {
			sel._colorbtn = _id(rgp.pSelColId);
		}
		
		sel._pop = rgp.pEnabled;
		
		sel.setValue = function(val) {
			var k = this._isBlankOption ? -1: 0;
			if (val != null && val != undefined) {
				val = "" + val;
				for(var i = 0; i < this._keys.length; i++) {
					if (val == this._keys[i]) {
						k = i;
						break;
					}
				}
			}
			
			this.selectOpt(k, true, false);
		};
		
		sel.getValue = function() {
			return this.value;
		};
		
		sel.selectOpt = function(index, choose, fire) {
			if (this._pop) {
				if(this._oldSelIdx != index) {
					const label = index >= 0 ? _id(this._selectIds[index]) : this._blank;
					const olabel = this._oldSelIdx >= 0 ? _id(this._selectIds[this._oldSelIdx]) : this._blank;
					if (label) {
						label.className = this._sel;
					}
					if (olabel && label != olabel) {
						olabel.className = this._norm;
					}
					
					this._oldSelIdx = index;
					if (!choose) {
						ux.listScrollToLabel(this, label);
					}
				}
			}
			
			if (choose && (this._selIdx != index)) {
				var txt = this._blank ? this._blank.innerHTML:"";
				var val = null;
				if (index >= 0) {
					txt = this._labels[index];
					val = this._keys[index];
				}
				
				if (txt == "&nbsp;") {
					txt = "";
				} else {
					txt = ux.decodeHtml(txt);
				}

				if (this._colors) {
					this._colorbtn.style.backgroundColor = val ? val: "transparent";
				}
				
				this.value = val;
				this._fac.value = txt;
				this._fac.focus();
				this._selIdx = index;
				if (fire) {
					ux.fireEvent(this, "change");			
				}
			}
		};
		
		ux.listWirePopFrame(sel);
		sel.setValue(rgp.pVal);
	}
}

ux.ssOnShow = function(rgp) {
	ux.setFocus({wdgid: rgp.pFrmId});
}


/** Text Area */
ux.rigTextArea = function(rgp) {
	const ta = _id(rgp.pId);
	if (ta) {
		ta.setValue = function(val) {
			this.value = val;
		};
		
		ta.getValue = function() {
			return this.value;
		};
		
		if (rgp.pScrEnd) {
			ta.scrollTop = ta.scrollHeight;
		}
	}
}

/** Table */
ux.rigTable = function(rgp) {
	var id = rgp.pId;
	var tblToRig = _id(id);
	if (!tblToRig) {
		// TODO Show some error
		return;
	}

	if (rgp.pWindowed) {
		// Attach horizontal scroll
		var tblHeader = _id("hdr_" + id);
		var tblBody = _id("bod_" + id);
		ux.addHdl(tblBody, "scroll", function(uEv) {
			tblHeader.style.left = "-" + tblBody.scrollLeft + "px";
		}, rgp);

		// Show window content (Used this approach because of IE and Firefox)
		ux.tableResizeHeight(rgp);
		ux.registerResizeFunc(id, ux.tableResizeHeight, rgp);
	}

	var selectable = rgp.pSelectable && !rgp.pMultiSel;
	if (tblToRig.rows && (selectable || rgp.pShiftable)) {
		tblToRig.uIdxId = rgp.pIdxCtrlId;
		tblToRig.uBaseIdx = rgp.pBaseIdx;
		tblToRig.uSelCls = rgp.pSelClassNm;
		tblToRig.uSelDepList = rgp.pSelDepList;

		if (rgp.pItemCount > 0) {
			var startIndex = 1;
			if (rgp.pWindowed) {
				startIndex = 0;
			}

			for (var i = startIndex; i < tblToRig.rows.length; i++) {
				const tRow = tblToRig.rows[i];
				if (!tblToRig.uFirstRow) {
					tblToRig.uFirstRow = tRow;
				}
				tRow.uIndex = i - startIndex;
				tRow.uClassName = tRow.className;
				const evp = {uRigTbl:tblToRig, uRigRow:tRow};
				ux.addHdl(tRow, "click", ux.tableRowClickHandler,
						evp);
			}
		}

		ux.setDisabledById(tblToRig.uSelDepList, true);
	}

	if (rgp.pPagination) {
		// Navigation
		var leftNavId = "navl_" + id;
		var rightNavId = "navr_" + id;
		var leftNavElem = _id(leftNavId);
		var rightNavElem = _id(rightNavId);
		leftNavElem.disabled = true;
		rightNavElem.disabled = true;
		if (rgp.pItemCount > 0) {
			leftNavElem.disabled = (rgp.pCurrPage <= 0);
			if (!leftNavElem.disabled) {
				ux.tableAttachPageNavClick(leftNavId, rgp.pCurrPage - 1,
						rgp);
			}
			rightNavElem.disabled = (rgp.pCurrPage >= (rgp.pPageCount - 1));
			if (!rightNavElem.disabled) {
				ux.tableAttachPageNavClick(rightNavId, rgp.pCurrPage + 1,
						rgp);
			}
			for (var i = rgp.pNaviStart; i <= rgp.pNaviStop; i++) {
				if (i != rgp.pCurrPage) {
					ux.tableAttachPageNavClick("nav_" + id + i, i, rgp);
				}
			}
		}
		// Items per page
		var evp = ux.newEvPrm(rgp);
		if (rgp.pMultiSel) {
			evp.uRef = [ rgp.pItemPerPgCtrlId, rgp.pSelGrpId ];
		} else {
			evp.uRef = [ rgp.pItemPerPgCtrlId ];
		}
		evp.uPanels = [ rgp.pContId ];
		ux.addHdl(_id(rgp.pItemPerPgCtrlId), "change",
				ux.post, evp);
	}

	if (rgp.pMultiSel) {
		tblToRig.uSelCls = rgp.pSelClassNm;
		tblToRig.uVisibleSel = rgp.pVisibleSel;
		tblToRig.uHiddenSel = rgp.pHiddenSel;
		tblToRig.uMultiSelDepList = rgp.pMultiSelDepList;
		tblToRig.uItemCount = rgp.pItemCount;
		tblToRig.uSelAllId = rgp.pSelAllId;
		tblToRig.uSumColList = rgp.pSumColList;
		tblToRig.uSumSrc = rgp.pSumSrc;
		tblToRig.uSumDepList = rgp.pSumDepList;
		if (rgp.pSumProcList) {
			var procList = rgp.pSumProcList;
			var sumProcs = [];
			for(var i = 0; i < procList.length; i++) {
				sumProcs[i] = _proc(procList[i]);
			}
			tblToRig.uSumProcs = sumProcs;
		}

		// Disable dependencies if required
		ux.setDisabledById(tblToRig.uMultiSelDepList,
				(tblToRig.uVisibleSel + tblToRig.uHiddenSel) <= 0);

		// Rig select/ de-select all
		var selBoxes = _name(rgp.pSelGrpId);
		var rowOffset = 1;
		if (rgp.pWindowed) {
			rowOffset = 0;
		}

		const evp = {uRigTbl:tblToRig};
		tblToRig.uSelBoxes = selBoxes;
		var selAll = _id(rgp.pSelAllId);
		var selAllFac = _id("fac_" + rgp.pSelAllId);
		selAllFac.selAll = selAll;
		ux.addHdl(selAllFac,
				"click", ux.tableSelAllClick, evp);
		
		for (var i = 0; i < selBoxes.length; i++) {
			var selBox = selBoxes[i];
			var tRow = tblToRig.rows[i + rowOffset];
			selBox.uRowClass = tRow.className;
			selBox.uRow = tRow; 
			selBox.uIndex = i;
			
			// Wire handlers
			var selBoxFac = _id("fac_" + selBox.id);
			selBoxFac.selBox = selBox;
			ux.addHdl(selBoxFac, "click", ux.tableMultiSelClick,
					evp);
			if (!rgp.pShiftable) {
				const evpRw = {uRigTbl:tblToRig, uSelBox:selBox};
				ux.addHdl(tRow, "click", ux.tableMultiRowClickHandler,
						evpRw);
			}
			
			// Highlight already selected from back-end
			if (selBox.checked == true) {
				tRow.className = tblToRig.uSelCls;
			}
		}
		
		// Fire summary proc
		ux.tableSummaryProc(tblToRig);
	}
	
	if (rgp.pSortable) {
		if (rgp.pSortColList) {
			for (var i = 0; i < rgp.pSortColList.length; i++) {
				var colInfo = rgp.pSortColList[i];
				const evp = ux.newEvPrm(rgp);
				evp.uCmd = id + "->sort";
				evp.uPanels = [ rgp.pContId ];
				evp.uColIdxId = rgp.pColIdxId;
				evp.uAscendId = rgp.pSortDirId;
				evp.uColIdx = colInfo.idx;
				evp.uAscend = colInfo.ascend;
				evp.uRef = [ rgp.pColIdxId, rgp.pSortDirId, id ];
				var imgId = null;
				if (colInfo.ascend) {
					imgId = colInfo.field + '_' + rgp.pSortAscId;
				} else {
					imgId = colInfo.field + '_' + rgp.pSortDescId;
				}
				ux.addHdl(_id(imgId), "click",
						ux.tableSortClickHandler, evp);
			}
		}
	}

	if (rgp.pShiftable) {
		if (rgp.pItemCount > 0) {
			var evp = null;
			if (rgp.pShiftTopId) {
				evp = ux.getTableShiftParams(rgp, 0);
				ux.addHdlMany(rgp.pShiftTopId, "click",
						ux.tableShiftClickHandler, evp);
			}

			if (rgp.pShiftUpId) {
				evp = ux.getTableShiftParams(rgp, 1);
				ux.addHdlMany(rgp.pShiftUpId, "click",
						ux.tableShiftClickHandler, evp);
			}

			if (rgp.pShiftDownId) {
				evp = ux.getTableShiftParams(rgp, 2);
				ux.addHdlMany(rgp.pShiftDownId, "click",
						ux.tableShiftClickHandler, evp);
			}

			if (rgp.pShiftBottomId) {
				evp = ux.getTableShiftParams(rgp, 3);
				ux.addHdlMany(rgp.pShiftBottomId, "click",
						ux.tableShiftClickHandler, evp);
			}

			if (rgp.pDeleteId) {
				evp = ux.getTableDeleteParams(rgp);
				ux.addHdlMany(rgp.pDeleteId, "click",
						ux.tableDeleteClickHandler, evp);
			}

			var viewIndex = 1 + parseInt(_id(rgp.pIdxCtrlId).value);
			if (rgp.pWindowed) {
				viewIndex--;
			}
			
			ux.fireEvent(tblToRig.rows[viewIndex], "click");
		}
	} else {
		if (selectable && tblToRig.uFirstRow) {
			ux.fireEvent(tblToRig.uFirstRow, "click");
		}
	}
	
	if (rgp.pItemCount <= 0) {
		ux.setDisabledById(rgp.pConDepList, true);
	}
}

ux.tableShiftClickHandler = function(uEv) {
	var evp = uEv.evp;
	var shiftDirCtrl = _id(evp.uShiftDirId);
	if (shiftDirCtrl) {
		shiftDirCtrl.value = evp.uShiftDir;
	}

	var rowElem = ux.findParent(uEv.uTrg, "tr");
	if (rowElem) {
		ux.fireEvent(rowElem, "click");
	}
	
	ux.post(uEv);
}

ux.tableDeleteClickHandler = function(uEv) {
	var rowElem = ux.findParent(uEv.uTrg, "tr");
	if (rowElem) {
		ux.fireEvent(rowElem, "click");
	}
	
	ux.post(uEv);
}

ux.tableSortClickHandler = function(uEv) {
	var evp = uEv.evp;
	var colIdxCtrl = _id(evp.uColIdxId);
	if (colIdxCtrl) {
		colIdxCtrl.value = evp.uColIdx;
	}
	var ascendCtrl = _id(evp.uAscendId);
	if (ascendCtrl) {
		ascendCtrl.value = evp.uAscend;
	}
	ux.post(uEv);
}

ux.tableSelAllClick = function(uEv) {
	var selAllFac = uEv.uTrg;
	if (selAllFac) {
		var selAllBox = selAllFac.selAll;
		var rigTbl = uEv.evp.uRigTbl;
		// Update table values
		if (selAllBox.checked == true) {
			rigTbl.uVisibleSel = rigTbl.uItemCount;
		} else {
			rigTbl.uVisibleSel = 0;
		}
		
		// Update visuals for rows
		var selBoxes = rigTbl.uSelBoxes;
		if (selBoxes) {
			if (selAllBox.checked == true) {
				for (var i = 0; i < selBoxes.length; i++) {
					var selBox = selBoxes[i];
					selBox.checked = selAllBox.checked;
					ux.cbSwitchImg(selBox);
					selBox.uRow.className = rigTbl.uSelCls;
				}
			} else {
				for (var i = 0; i < selBoxes.length; i++) {
					var selBox = selBoxes[i];
					selBox.checked = selAllBox.checked;
					ux.cbSwitchImg(selBox);
					selBox.uRow.className = selBox.uRowClass;
				}
			}
		}
		
		// Update dependencies
		ux.tableDisableMultiSelElements(rigTbl);
		
		// Fire summary
		ux.tableSummaryProc(rigTbl);
	}
}

ux.tableMultiSelClick = function(uEv) {
	var changed = false;
	var selBoxFac = uEv.uTrg;
	if (selBoxFac) {
		var selBox = selBoxFac.selBox;
		var rigTbl = uEv.evp.uRigTbl;
		rigTbl.uLastSelClick = null;
		if (selBox.checked == true) {
			selBox.uRow.className = rigTbl.uSelCls;
			rigTbl.uVisibleSel++;
		} else {
			selBox.uRow.className = selBox.uRowClass;
			rigTbl.uVisibleSel--;
		}

		changed = true;
		ux.tableDisableMultiSelElements(rigTbl);
	}
	
	uEv.uSelClick = true;
	
	if (changed) {
		// Fire summary
		ux.tableSummaryProc(rigTbl);
	}
}

ux.tableMultiRowClickHandler =  function(uEv) {
	if (!uEv.uSelClick) {
		var tRow = uEv.uTrg;
		if (tRow) {
			if (!(uEv.shiftKey && uEv.ctrlKey)) {
				var rigTbl = uEv.evp.uRigTbl;
				var selBox = uEv.evp.uSelBox;
				if (uEv.ctrlKey) {
					ux.tableMultiRowSelect(selBox, rigTbl, false, false);
				} else {
					if (uEv.shiftKey) {
						ux.tableMultiRowSelect(selBox, rigTbl, false, true);
					} else {
						ux.tableMultiRowSelect(selBox, rigTbl, true, false);
					}
				}

				ux.tableDisableMultiSelElements(rigTbl);
			}
		}
	}
}

ux.tableMultiRowSelect =  function(selBox, rigTbl, uncheckOthers, wideSelect) {
	var changed = false;
	if (selBox != rigTbl.uLastSelClick && selBox.checked != true) {
		selBox.checked = true;
		ux.cbSwitchImg(selBox);
		selBox.uRow.className = rigTbl.uSelCls;
		rigTbl.uVisibleSel++;
		
		if (wideSelect && rigTbl.uLastSelClick) {
			var start = selBox.uIndex;
			var end = rigTbl.uLastSelClick.uIndex;
			if (start > end) {
				var temp = start;
				start = end;
				end = temp;
			}
			
			var selBoxes = rigTbl.uSelBoxes;
			for (var i = start; i <= end; i++) {
				var cSelBox = selBoxes[i];
				if (cSelBox.checked != true) {
					cSelBox.checked = true;
					ux.cbSwitchImg(cSelBox);
					cSelBox.uRow.className = rigTbl.uSelCls;
					rigTbl.uVisibleSel++;
				}
			}
		}
		
		rigTbl.uLastSelClick = selBox;
		changed = true;
	}

	if (uncheckOthers) {
		var selBoxes = rigTbl.uSelBoxes;
		for(var i = 0; i < selBoxes.length; i++) {
			var unSelBox = selBoxes[i];
			if (unSelBox.checked == true) {
				if (unSelBox != selBox) {
					unSelBox.checked = false;
					ux.cbSwitchImg(unSelBox);
					unSelBox.uRow.className = unSelBox.uRowClass;
					changed = true;
				}
			}
		}
		
		rigTbl.uVisibleSel = 1;
	}
	
	if (changed) {
		// Fire summary
		ux.tableSummaryProc(rigTbl);
	}
}

ux.tableSummaryProc = function(rigTbl) {
	// No need to perform summary if there are no handlers or targets
	if (!rigTbl.uSumProcs || !rigTbl.uSumDepList) {		
		return;
	}

	var sumProcs = rigTbl.uSumProcs
	var sumDeps = rigTbl.uSumDepList
	var procLen = sumProcs.length;
	if (procLen > sumDeps.length) {
		procLen = sumDeps.length;
	}
	
	if (procLen == 0) {
		return;
	}
	
	// Do summary
	var sumCols = rigTbl.uSumColList;
	var selBoxes = rigTbl.uSelBoxes;
	
	//Initialize
	const summary = {};
	for (var i = 0; i < sumCols.length; i++) {
		summary[sumCols[i].nm] = 0.0;
	}
	
	// Do calculations
	for(var i = 0; i < selBoxes.length; i++) {
		if (selBoxes[i].checked == true) {
			for (var j = 0; j < sumCols.length; j++) {
				var sumCol = sumCols[j];
				// Get cell value and add
				var cVal = 0.0;
				var cell = selBoxes[i].uRow.cells[sumCol.idx];
				if (cell) {
					var cnt = cell.innerHTML;
					if (cnt.charAt(0) == '<') {
						var sIndex = cnt.indexOf(">");
						cnt = cnt.substring(sIndex + 1, cnt.indexOf("<", sIndex));
					}
					cVal = parseFloat(cnt.replace(",", ""));
				}
				summary[sumCol.nm] = cVal + summary[sumCol.nm];
			}
		}
	}

	// Fire summary handlers
	var src =rigTbl.uSumSrc;
	for(var i = 0; i < sumProcs.length; i++) {
		sumProcs[i](sumDeps[i], src, summary);
	}
}

ux.tableResizeHeight = function(rgp) {
	var id = rgp.pId;
	var tblToRig = _id(id);

	// Stretch
	tblToRig.style.display = "none";
	var tblWin = _id("win_" + id);
	var tblBodyCell = _id("bodc_" + id);
	ux.stretchArea(tblBodyCell, false, true);
	ux.stretchArea(tblWin, false, true);

	// Fix
	ux.fixArea(tblWin, false, true);
	ux.fixArea(tblBodyCell, false, true);
	tblToRig.style.display = "table";
}

ux.getTableShiftParams = function(rgp, direction) {
	var evp = ux.newEvPrm(rgp);
	evp.uCmd = rgp.pId + "->shift";
	evp.uPanels = [ rgp.pContId ];
	evp.uIdxId = rgp.pIdxCtrlId;
	evp.uShiftDirId = rgp.pShiftDirId;
	evp.uRef = [ rgp.pId ];
	evp.uShiftDir = direction;
	return evp;
}

ux.getTableDeleteParams = function(rgp) {
	var evp = ux.newEvPrm(rgp);
	evp.uCmd = rgp.pId + "->delete";
	evp.uPanels = [ rgp.pContId ];
	evp.uIdxId = rgp.pIdxCtrlId;
	evp.uRef = [ rgp.pId ];
	return evp;
}

ux.tableDisableMultiSelElements = function(rigTbl) {
	var totalSel = rigTbl.uVisibleSel + rigTbl.uHiddenSel;
	ux.setDisabledById(rigTbl.uMultiSelDepList, totalSel <= 0);

	var selAllElem = _id(rigTbl.uSelAllId);
	if (rigTbl.uVisibleSel <= 0 && selAllElem.checked) {
		selAllElem.checked = false;
		ux.cbSwitchImg(selAllElem);
	}
}

ux.tableAttachPageNavClick = function(id, pageSel, rgp) {
	var evp = ux.newEvPrm(rgp);
	evp.uSelectPage = pageSel;
	if (rgp.pMultiSel) {
		evp.uRef = [ rgp.pCurrPgCtrlId, rgp.pSelGrpId ];
	} else {
		evp.uRef = [ rgp.pCurrPgCtrlId ];
	}
	evp.uPanels = [ rgp.pContId ];
	ux.addHdl(_id(id), "click", ux.tablePageNavClickHandler, evp);
}

ux.tablePageNavClickHandler = function(uEv) {
	var evp = uEv.evp;
	var currPgCtrl = _id(evp.uRef[0]);
	if (currPgCtrl) {
		currPgCtrl.value = evp.uSelectPage;
	}
	ux.post(uEv);
}

ux.tableRowClickHandler = function(uEv) {
	var evp = uEv.evp;
	var rigTbl = evp.uRigTbl;
	var tRow = evp.uRigRow;
	if (rigTbl.uCurrSelRow) {
		var selRow = rigTbl.uCurrSelRow;
		selRow.className = selRow.uClassName;
	}
	rigTbl.uCurrSelRow = tRow;
	tRow.className = rigTbl.uSelCls;
	var tIdxElem = _id(rigTbl.uIdxId);
	if (tIdxElem) {
		tIdxElem.value = rigTbl.uBaseIdx + tRow.uIndex;
	}
	ux.setDisabledById(rigTbl.uSelDepList, false);
}

/** Text Clock */
ux.rigTextClock = function(rgp) {
	var dateElem = _id(rgp.pDateId);
	var timeElem = _id(rgp.pTimeId);
	var showTime = function() {
		var currTime = new Date();
		dateElem.innerHTML = currTime.toDateString();
		timeElem.innerHTML = currTime.toLocaleTimeString();
	};
	showTime();
	window.setInterval(showTime, 1000);
}

/** Time Field */
ux.rigTimeField = function(rgp) {
	const id = rgp.pId;
	const tf = _id(id);
	if (tf) {
		tf._parts = {};
		tf._facId = rgp.pFacId;
		tf._format = rgp.pPattern;
		tf._lists = rgp.pLists;
		tf._padLeft = true;
		tf._clearable = rgp.pClearable;
		tf._pop = rgp.pEnabled;
		
		tf.setValue = function(val) {
			this.setHour24(val.getHours());
			this.setMinute(val.getMinutes());
			this.setSecond(val.getSeconds());
			this.setActual(false);
			this.updateClock();
		};
		
		tf.getValue = function() {
			const val = new Date();
			val.setHours(this.getHour24());
			val.setMinutes(this.getMinute());
			val.setSeconds(this.getSecond());
			return val;
		};

		tf.setActual = function(fire) {
			const val = ux.applyPattern(this);
			if (this.value != val) {
				this.value = val;
				this.focus();
				if (fire) {
					ux.fireEvent(this, "change");
				}
			}
		};
		
		tf.updateClock = function() {
			if (this._pop) {
				var blank = true;
				for(var m in this._parts) {
					blank = false;
					break;
				}
				
				if (blank) {
					const val = new Date();
					this.setHour24(val.getHours());
					this.setMinute(val.getMinutes());
					this.setSecond(val.getSeconds());
				}
				
				for (var i = 0; i < this._format.length; i++) {
					const fmt = this._format[i];
					if (!fmt.flag) {
						const mfac = _id(this._facId + i);
						if (fmt.target == "mer_") {
							mfac.value = this._lists[i].list[this.getMeridiem()];
						} else {
							mfac.value = ux.padLeft(this._parts[fmt.target], '0', fmt.length);
						}
					}
				}
			}
		};
		
		tf.setHour24 = function(val) {
			if (val != undefined) {
				var h12 = 0;
				if (val < 12) {
					h12 = val;
					this._parts["mer_"] = "0";
				} else {
					h12 = val - 12;
					this._parts["mer_"] = "1";
				}
				
				if (h12 == 0) {
					h12 = 12;
				}
				
				this._parts["h12_"] = "" + h12;
				this._parts["h24_"] = "" + val;
			}
		};
		
		tf.getHour24 = function() {
			return parseInt(this._parts["h24_"]);
		};
		
		tf.setHour12 = function(val) {
			if (val != undefined) {
				if (this.getMeridiem() == 0) {
					this._parts["h24_"] = "" + val;
				} else {
					this._parts["h24_"] = "" + (val + 12);
				}
				
				if (val == 0) {
					val = 12;
				}		
				this._parts["h12_"] = "" + val;
			}
		};
		
		tf.getHour12 = function() {
			const val = parseInt(this._parts["h12_"]);
			if (val == 12) {
				return 0;
			}
			
			return val;
		};
		
		tf.setMinute = function(val) {
			if (val != undefined) {
				this._parts["min_"] = "" + val;
			}
		};
		
		tf.getMinute = function() {
			return parseInt(this._parts["min_"]);
		};
		
		tf.setSecond = function(val) {
			if (val != undefined) {
				this._parts["sec_"] = "" + val;
			}
		};
		
		tf.getSecond = function() {
			return parseInt(this._parts["sec_"]);
		};
		
		tf.setMeridiem = function(val) {
			if (val != undefined) {
				if (val == 0) {
					this._parts["h24_"] = this.parts["h12_"];
				} else {
					this._parts["h24_"] = "" + (this.getHour12() + 12);
				}
				
				this._parts["mer_"] = "" + val;
			}
		};
		
		tf.getMeridiem = function() {
			return parseInt(this._parts["mer_"]);
		};
		
		if (tf._pop) {
			ux.addHdl(_id("btns_" + id), "click", ux.tfSetHandler,
					{uId:id});
			ux.popupWireClear(rgp, "btncl_" + id, [ id ]);
			ux.popupWireCancel("btncn_" + id);
			for (var i = 0; i < tf._format.length; i++) {
				const list = tf._lists[i];
				if (list) {
					const evppos = {uId:id, uIndex:i, uStep:1};
					ux.addHdl(_id("btnpos_" + id  + i), "click",
							ux.tfScrollHandler, evppos);

					const evpneg = {uId:id, uIndex:i, uStep:-1};
					ux.addHdl(_id("btnneg_" + id + i), "click",
							ux.tfScrollHandler, evpneg);
				}
			}
		}
		
		tf.setHour24(rgp.pHour);
		tf.setMinute(rgp.pMinute);
		tf.setSecond(rgp.pSecond);
		tf.setActual(false);
		tf.updateClock();
	}
}


ux.tfSetHandler = function(uEv) {
	ux.hidePopup(uEv);
	_id(uEv.evp.uId).setActual(true);
}

ux.tfScrollHandler = function(uEv) {
	const tf = _id(uEv.evp.uId);
	const index = uEv.evp.uIndex;
	const step = uEv.evp.uStep;
	const fmt = tf._format[index];
	const list = tf._lists[index];

	var nextval = parseInt(tf._parts[fmt.target]) + step;
	if (step > 0) {
		if (nextval > list.max) {
			nextval = list.min;
		}
	} else {
		if (nextval < list.min) {
			nextval = list.max;
		}
	}

	if (list.list) {
		tf._parts[fmt.target] = nextval;
	} else {
		tf._parts[fmt.target] = ux.padLeft("" + nextval, '0', fmt.length);
	}
	
	tf.updateClock();
}

/** Tree */
const TREEITEM_CLICK = {code:'TCL', mask:0x0001};
const TREEITEM_RIGHTCLICK = {code:'TRC', mask:0x0002};
const TREEITEM_DBCLICK = {code:'TDC', mask:0x0004};
const TREEITEM_DRAG = {code:'TDG"', mask:0x0008};
const TREEITEM_DROP = {code:'TDP', mask:0x0010};
const MENUITEM_CLICK = {code:'MCL', mask:0x0020};

Object.freeze(TREEITEM_CLICK);
Object.freeze(TREEITEM_RIGHTCLICK);
Object.freeze(TREEITEM_DBCLICK);
Object.freeze(TREEITEM_DRAG);
Object.freeze(TREEITEM_DROP);
Object.freeze(MENUITEM_CLICK);

const TREE_PARENT_FLAG = 0x0001;
const TREE_EXPANDED_FLAG = 0x0002;

ux.treedatmap = {};
ux.srcTreeId = null;
ux.treeDatCreate = function(rgp) {
	var tdat = {};
	tdat.uId = rgp.pId;
	tdat.uPanels = [ rgp.pContId ];
	tdat.uCmd = rgp.pId + "->executeEventPath";
	tdat.uSelItemId = rgp.pSelItemId;
	tdat.uDropTrgItemId = rgp.pDropTrgItemId;
	tdat.uDropSrcId = rgp.pDropSrcId;
	tdat.uDropSrcItemId = rgp.pDropSrcItemId;
	tdat.uEventTypeId = rgp.pEventTypeId;
	tdat.uMenuCodeCtrlId = rgp.pMenuCodeCtrlId;
	tdat.uSel = rgp.pSel;
	tdat.uNorm = rgp.pNorm;
	tdat.uIco = rgp.pIco;
	tdat.uIcod = rgp.pIcod;
	tdat.uLblBase = rgp.pLblBase;
    tdat.uIconBase=rgp.pIconBase;
	tdat.uItemList = rgp.pItemList;
	tdat.uRef = [ rgp.pSelItemId, rgp.pEventTypeId, rgp.pDropTrgItemId, rgp.pDropSrcId, rgp.pDropSrcItemId, rgp.pMenuCodeCtrlId ];
	if(rgp.pEventRef) {
		tdat.uRef = tdat.uRef.concat(rgp.pEventRef);
	}
	tdat.uMenu = rgp.pMenu;
	tdat.uMsMenu = rgp.pMsMenu;
	tdat.uLastSelIdx = -1;
	var oldtdat = ux.treedatmap[tdat.uId];
	if (oldtdat) {
		tdat.uLastSelIdx = oldtdat.uLastSelIdx;
	}

	ux.treedatmap[tdat.uId] = tdat;
	return tdat;
}

ux.getTreeDat = function(evp) {
	return ux.treedatmap[evp.uId];
}

ux.newTreeEvPrm = function(rgp) {
	var evp = ux.newEvPrm(rgp);
	var tdat = ux.treedatmap[rgp.pId]
	evp.uId = tdat.uId;
	evp.uPanels = tdat.uPanels;
	evp.uCmd = tdat.uCmd;
	evp.uRef = tdat.uRef;
	return evp;
}

ux.rigTreeExplorer = function(rgp) {
	var tdat = ux.treeDatCreate(rgp);
	var selList = [];
	
	// Wire menu
	if(rgp.pMenu) {
		var menu = rgp.pMenu;
		for(var i = 0; i < menu.items.length; i++) {
			var menuItem = menu.items[i];
			var evp = ux.newTreeEvPrm(rgp);
			evp.uMenuCode = menuItem.code;
			if (menuItem.pConf) {
				evp.uConfURL = rgp.pConfURL;
				evp.uConf = menuItem.pConf;
				evp.uIconIndex = menuItem.pIconIndex;
			}
			
			ux.addHdl(_id(menuItem.id), "click", ux.treeMenuClickHandler, evp);
		}
	}
	
	// Wire tree items
	var pItemTypeList = rgp.pItemTypeList;
	if (pItemTypeList) {
		// Do setup
		var typeMap = {};
		for(var i = 0; i < pItemTypeList.length; i++) {
			var type = pItemTypeList[i];
			typeMap[type.code] = type;
		}
		
		// Do wire tree items
		if (rgp.pItemList) {
			var pItemList = rgp.pItemList;
			var selObj = _id(rgp.pSelItemId);
			for (var i = 0; i < pItemList.length; i++) {
				var tItem = pItemList[i];
				tItem.treeId = rgp.pId;
				tItem.typeInfo = typeMap[tItem.type];
				tItem.frmId = rgp.pLblBase + tItem.idx;
				if ((tItem.flags & TREE_PARENT_FLAG) > 0) {
					var evp = ux.newEvPrm(rgp);
					evp.uPanels = [ rgp.pContId ];
					evp.uRef = [ rgp.pSelCtrlId ];
					evp.uSelCtrlId = rgp.pSelCtrlId;
					evp.uIdx = tItem.idx;
					if ((tItem.flags & TREE_EXPANDED_FLAG) > 0) {
						evp.uCmd = rgp.pId + "->collapse";
					} else {
						evp.uCmd = rgp.pId + "->expand";
					}
					ux.addHdl(_id(rgp.pCtrlBase + tItem.idx),
							"click", ux.treeCtrlImageClickHandler, evp);
				}

				var evp = ux.newTreeEvPrm(rgp);
				evp.uItemIdx = i;

				var flags = tItem.typeInfo.flags;
				var elm = _id(tItem.frmId);
				if ((flags & TREEITEM_CLICK.mask) > 0) {
					ux.addHdl(elm, "click", ux.treeItemClickHandler, evp);
				}
				
				if ((flags & TREEITEM_DBCLICK.mask) > 0) {
					ux.addHdl(elm, "dblclick", ux.treeItemDbClickHandler, evp);
				}

				if ((flags & TREEITEM_RIGHTCLICK.mask) > 0) {
					evp.uDoMenu = true;
				}
				ux.addHdl(elm, "rtclick", ux.treeItemRightClickHandler, evp);

				if ((flags & TREEITEM_DRAG.mask) > 0) {
					ux.addHdl(elm, "dragstart", ux.treeItemDragStartHandler, evp);
					ux.addHdl(elm, "dragexit", ux.treeItemDragExitHandler, evp);
				}

				if ((flags & TREEITEM_DROP.mask) > 0) {
					ux.addHdl(elm, "dragenter", ux.treeItemDragEnterHandler, evp);
					ux.addHdl(elm, "dragover", ux.treeItemDragOverHandler, evp);
					ux.addHdl(elm, "drop", ux.treeItemDropHandler, evp);
					ux.addHdl(elm, "dragleave", ux.treeItemDragLeaveHandler, evp);
				}

				if (selObj.options[i].selected) {
					selList.push(i);
				}
			}
		}
	}
	
	tdat.selList = selList;
}

ux.treeMenuClickHandler = function(uEv) {
	var tdat = ux.getTreeDat(uEv.evp);
	var elem = _id(tdat.uEventTypeId);
	if(elem) {
		elem.value = MENUITEM_CLICK.code;
	}

	elem = _id(tdat.uMenuCodeCtrlId);
	if(elem) {
		elem.value = uEv.evp.uMenuCode;
	}

	ux.hidePopup(null);
	ux.post(uEv);
}

ux.treeItemClickHandler = function(uEv) {
	ux.treeItemClickEventHandler(uEv, TREEITEM_CLICK.code, true);
}

ux.treeItemDbClickHandler = function(uEv) {
	ux.treeItemClickEventHandler(uEv, TREEITEM_DBCLICK.code, false);
}

ux.treeItemRightClickHandler = function(uEv) {
	ux.treeItemClickEventHandler(uEv, TREEITEM_RIGHTCLICK.code, false);
}

ux.treeItemDragStartHandler = function(uEv) {
	var srctdat = ux.getTreeDat(uEv.evp);
	if (srctdat.timeoutId) {
		window.clearTimeout(srctdat.timeoutId);
		srctdat.timeoutId = null;
	}

	var i = uEv.evp.uItemIdx;
	if (srctdat.selList.includes(i)) {
		// Do multi-select drag if item is part of selected group
		srctdat.dragitems = [];
		for(var j = 0; j < srctdat.selList.length; j++) {
			srctdat.dragitems[j] = srctdat.uItemList[srctdat.selList[j]];
		}
	} else {
		//Do single item drag
		srctdat.dragitems = [srctdat.uItemList[i]];
	}
	
	ux.srcTreeId = srctdat.uId;
	uEv.dataTransfer.setData("srcTreeId", srctdat.uId); //Must do this for mozilla
	uEv.dataTransfer.dropEffect = "move";
}

ux.treeItemDragExitHandler = function(uEv) {
	ux.srcTreeId = null;
}

ux.treeItemDragEnterHandler = function(uEv) {
	if (ux.treeItemAcceptDropLoad(uEv)) {

	}
}

ux.treeItemDragOverHandler = function(uEv) {
	if (ux.treeItemAcceptDropLoad(uEv)) {
		// Show indicator
		var trgtdat = ux.getTreeDat(uEv.evp);
		var trgitem = trgtdat.uItemList[uEv.evp.uItemIdx];
		_id(trgtdat.uIconBase + trgitem.idx).className = trgtdat.uIcod;
		uEv.dataTransfer.dropEffect = "move";
	}
}

ux.treeItemDragLeaveHandler = function(uEv) {
	if (ux.treeItemAcceptDropLoad(uEv)) {
		// Hide indicator
		var trgtdat = ux.getTreeDat(uEv.evp);
		var trgitem = trgtdat.uItemList[uEv.evp.uItemIdx];
		_id(trgtdat.uIconBase + trgitem.idx).className = trgtdat.uIco;
	}
}

ux.treeItemDropHandler = function(uEv) {
	var srctdat = ux.treeItemAcceptDropLoad(uEv);
	if (srctdat) {
		var trgtdat = ux.getTreeDat(uEv.evp);
		var trgitem = trgtdat.uItemList[uEv.evp.uItemIdx];

		// Send command to target
		_id(trgtdat.uEventTypeId).value = TREEITEM_DROP.code;
		_id(trgtdat.uDropTrgItemId).value = trgitem.idx;
		_id(trgtdat.uDropSrcId).value = srctdat.uId;
		var srcIds = ''; var sym = false;
		for(var i = 0; i < srctdat.dragitems.length; i++) {
			if (sym) {
				srcIds += ',';
			} else {
				sym = true;
			}
			srcIds += srctdat.dragitems[i].idx;
		}
		_id(trgtdat.uDropSrcItemId).value = srcIds;
		
		ux.post(uEv);
	}
}

ux.treeItemAcceptDropLoad = function(uEv) {
	var trgtdat = ux.getTreeDat(uEv.evp);
	var trgitem = trgtdat.uItemList[uEv.evp.uItemIdx];
	if ((trgtdat.selList.length > 1) && trgtdat.selList.includes(trgitem.idx)) {
		return null; // Target is part of multi-select. Not allowed.
	}
	
	var accept = trgitem.typeInfo.acceptdrop;
	if (accept && accept.length > 0) {
		var srctdat = ux.treedatmap[ux.srcTreeId];
		if (srctdat && srctdat.dragitems && srctdat.dragitems.length > 0) {
			var lastPidx = -1;
			for(var i = 0; i < srctdat.dragitems.length; i++) {
				var srcitem = srctdat.dragitems[i];
				if (lastPidx >=0 && lastPidx != srcitem.pidx) {
					return null; //Don't accept items with different parents
				}

				if ((srcitem.idx == trgitem.idx) && (srcitem.treeId == trgitem.treeId)) {
					return null; //Same object
				}
				
				if (!accept.includes(srcitem.typeInfo.code)) {
					return null; // Unacceptable type
				}
				
				lastPidx = srcitem.pidx;
			}
			uEv.preventDefault();
			return srctdat;
		}
	}
	return null;
}

ux.treeItemClickEventHandler = function(uEv, eventCode, delay) {
	var tdat = ux.getTreeDat(uEv.evp);
	if (tdat.timeoutId) {
		window.clearTimeout(tdat.timeoutId);
		tdat.timeoutId = null;
	}

	tdat.uEv = uEv;
	tdat.evp = uEv.evp;
	tdat.eventCode = eventCode;
	tdat.uLoc = ux.getExactPointerCoordinates(uEv);
	if (delay) {
		tdat.timeoutId = window.setTimeout("ux.treeItemProcessEvent(\""+ tdat.uId + "\");"
				, UNIFY_TREEDOUBLECLICK_DELAY); 
	} else {
		ux.treeItemProcessEvent(tdat.uId); 
	}
}

ux.treeItemProcessEvent = function(treeId) {
	var tdat = ux.treedatmap[treeId];	
	var evp = tdat.evp;
	var tItem = tdat.uItemList[evp.uItemIdx];
	if (tdat.eventCode == TREEITEM_CLICK.code) {
		if (tdat.uEv.ctrlKey) {
			ux.treeSelectItem(evp, false, true);
		} else if (tdat.uEv.shiftKey) {
			if (tdat.selList.length > 0 && tdat.uLastSelIdx >= 0) {
				ux.treeSelectItemRange(evp, tdat.uLastSelIdx, evp.uItemIdx);
			} else {
				ux.treeSelectItem(evp, true, false);
			}
		} else {
			ux.treeSelectItem(evp, true, false);
			ux.treeSendCommand(tdat);
		}
	} else {
		if (tdat.eventCode == TREEITEM_DBCLICK.code) {
			ux.treeSelectItem(evp, true, false);
			ux.treeSendCommand(tdat);
		} else {
			if (tdat.eventCode == TREEITEM_RIGHTCLICK.code) {
				var selObj = _id(tdat.uSelItemId);
				if (!selObj.options[evp.uItemIdx].selected) {
					ux.treeSelectItem(evp, true, false);
				}
	
				var showMenu = false;
				if (evp.uDoMenu && tdat.uMenu) {
					// Hide all menu items
					var menu = tdat.uMenu;
					ux.setDisplayModeByName(menu.sepId, "none");
					
					var tItem = tdat.uItemList[evp.uItemIdx];
					var actMenu = null;
					if (tdat.selList.length > 1) {
						// Multiple items selected. Do multi-select menu.
						actMenu = tdat.uMsMenu;
					} else {
						// Do selected item menu
						actMenu = tItem.typeInfo.menu;
					}

					// Show menu items
					if (actMenu && actMenu.length > 0) {
						var gIndex = -1;
						for(var i = 0; i < actMenu.length; i++) {
							var mitem = menu.items[actMenu[i]];
							var miElem = _id(mitem.id);
							if (gIndex >= 0 && gIndex != mitem.grpIdx) {
								miElem.className = menu.sepCls;
							} else {
								miElem.className = menu.normCls;
							}
							miElem.style.display = "block";
							gIndex = mitem.grpIdx;
						}
						showMenu = true;
					}

					if (showMenu) {
						// Show menu
						var openPrm = {};
						openPrm.popupId = menu.id;
						openPrm.relFrameId = tItem.frmId;
						openPrm.stayOpenForMillSec = -1;
						openPrm.forceReopen = true;
						openPrm.uTrg = tdat.uEv.uTrg;
						openPrm.uLoc = tdat.uLoc;
						ux.doOpenPopup(openPrm);
					}
				}

				if (!showMenu) {
					ux.hidePopup(null);
				}
			}
		}
	}

	tdat.uEv = null;
	tdat.evp = null;
}

ux.treeCtrlImageClickHandler = function(uEv) {
	var evp = uEv.evp;
	var tSelCtrlElem = _id(evp.uSelCtrlId);
	if (tSelCtrlElem) {
		tSelCtrlElem.value = evp.uIdx;
	}
	ux.post(uEv);
}

ux.treeSelectItem = function(evp, single, toggle) {
	var tdat = ux.getTreeDat(evp);
	var i = evp.uItemIdx;
	tdat.uLastSelIdx = i;
	if (single) {
		ux.treeSelectItemRange(evp, i, i);
	} else{
		var tElem = _id(tdat.uLblBase + tdat.uItemList[i].idx)
		var selObj = _id(tdat.uSelItemId);
		if (toggle) {
			if(selObj.options[i].selected) {
				ux.treeSelect(evp, tElem, selObj, i, false);
			} else {
				ux.treeSelect(evp, tElem, selObj, i, true);
			}
		} else{
			ux.treeSelect(evp, tElem, selObj, i, true);
		}
	}
}

ux.treeSelectItemRange = function(evp, start, end) {
	if (start > end) {
		var temp = end;
		end = start;
		start = temp;
	}

	var tdat = ux.getTreeDat(evp);
	var selObj = _id(tdat.uSelItemId);
	for(var i = 0; i < tdat.uItemList.length; i++) {
		var tElem = _id(tdat.uLblBase + tdat.uItemList[i].idx)
		if(i >= start && i <= end) {
			ux.treeSelect(evp, tElem, selObj, i, true);
		} else {
			ux.treeSelect(evp, tElem, selObj, i, false);
		}
	}
}

ux.treeSendCommand = function(tdat) {
	var evp = tdat.evp;
	var elem = _id(tdat.uEventTypeId);
	if(elem) {
		elem.value = tdat.eventCode;
	}

	var uEv = tdat.uEv;
	uEv.evp = evp;
	ux.post(uEv);
}

ux.treeSelect = function(evp, tElem, selObj, i, select) {
	var tdat = ux.getTreeDat(evp);
	var j = tdat.selList.indexOf(i);
	if (j >= 0) {
		if(!select) {
			tdat.selList = tdat.selList.splice(j + 1, 1);
		}
	} else if (select) {
		tdat.selList.push(i);
	}

	if (select) {
		tElem.className = tdat.uSel;
	} else {
		tElem.className = tdat.uNorm;
	}
	selObj.options[i].selected = select;
}

/** ************************* PARAMETERS ********************************** */

ux.buildFormParams = function(trgObj, evp) {
	var param = {};
	param.value = new FormData();
	param.isForm = true;
	ux.buildObjParams(trgObj, evp, param);
	return param;
}

ux.buildReqParams = function(trgObj, evp) {
	var param = {};
	param.value = "morsic=" + new Date().getTime();
	param.isForm = false;
	ux.buildObjParams(trgObj, evp, param);
	return param;
}

ux.buildObjParams = function(trgObj, evp, param) {
	var builtNames = []; // Added to prevent double build of a parameter.
	// Expanded component references and page aliases can cause double builds
	if (evp.uRef) {
		for (var i = 0; i < evp.uRef.length; i++) {
			ux.buildNameParams(evp.uRef[i], builtNames, param);
		}
	}

	var pb = param.value;
	var isForm = param.isForm;
	if (evp.uLoginId) {
		if (isForm) {
			pb.append("req_uid", evp.uLoginId);
			pb.append("req_unm", evp.uUserName);
			if (evp.uRole) {
				pb.append("req_rcd", evp.uRole); 
			}
			if (evp.uBranch) {
				pb.append("req_bcd", evp.uBranch);
			}
			if (evp.uGlobal) {
				pb.append("req_gac", evp.uGlobal);
			}
			if (evp.uColor) {
				pb.append("req_csm", evp.uColor);
			}
		} else {
			pb += ("&req_uid=" + _enc(evp.uLoginId));
			pb += ("&req_unm=" + _enc(evp.uUserName));
			if (evp.uRole) {
				pb += ("&req_rcd=" + _enc(evp.uRole));
			}
			if (evp.uBranch) {
				pb += ("&req_bcd=" + _enc(evp.uBranch));
			}
			if (evp.uGlobal) {
				pb += ("&req_gac=" + _enc(evp.uGlobal));
			}
			if (evp.uColor) {
				pb += ("&req_csm=" + _enc(evp.uColor));
			}
		}
	}

	if (evp.uConfMsg) {
		if (isForm) {
			pb.append("req_cmsg", evp.uConfMsg);
			pb.append("req_cmsgicon", evp.uIconIndex);
		} else {
			pb += ("&req_cmsg=" + _enc(evp.uConfMsg));
			pb += ("&req_cmsgicon=" + _enc(evp.uIconIndex));
		}
	}

	if (evp.uConfPrm) {
		if (isForm) {
			pb.append("req_cprm", evp.uConfPrm);
		} else {
			pb += ("&req_cprm=" + _enc(evp.uConfPrm));
		}
	}

	if (evp.uSendTrg) {
		if (isForm) {
			pb.append("req_trg", evp.uSendTrg);
		} else {
			pb += ("&req_trg=" + _enc(evp.uSendTrg));
		}
	}
	
	if (evp.uId && evp.uId.startsWith("row_")) {
		trgObj = ux.findParent(trgObj, "tr");
	}
	
	if (trgObj) {
		if (evp.isUniqueTrg) {
			ux.extractObjParams(trgObj, param);
		} else {
			ux.buildNameParams(trgObj.id, builtNames, param)
		}

		var _val;
		if (trgObj.id) {
			var hiddenElem = _id("trg_" + trgObj.id);
			if (hiddenElem) {
				_val = hiddenElem.value;
			}
		} else if (trgObj.dispIdx) {
			_val = trgObj.dispIdx;
		}

		if (evp.uReqTrg) {
			_val = evp.uReqTrg;
		} else if (evp.uIsReqTrg) {
			_val = trgObj.value;
		}
		
		if (_val !== undefined) {
			if (isForm) {
				pb.append("req_trg", _val);
			} else {
				pb += ("&req_trg=" + _enc(_val));
			}
		}
	}

	if (isForm) {
		if (evp.uViewer) {
			pb.append("req_rv", evp.uViewer);
			pb.append("req_rsi", ux.docSessionId);
		} else {
			pb.append("req_doc", ux.docPath);
			pb.append("req_win", window.name);
		}
		if (evp.uValidateAct) {
			pb.append("req_va", evp.uValidateAct);
		}
		if (evp.uCmd) {
			pb.append("req_cmd", evp.uCmd);
		}
		if (evp.uCmdTag) {
			pb.append("req_cmdtag", evp.uCmdTag);
		}
		if (evp.uId) {
			pb.append("req_wid", evp.uId);
		}
		if (evp.uPanels) {
			for (var i = 0; i < evp.uPanels.length; i++) {
				pb.append("req_rsh", evp.uPanels[i]);
			}
		}
	} else {
		if (evp.uViewer) {
			pb += ("&req_rv=" + _enc(evp.uViewer));
			pb += ("&req_rsi=" + _enc(ux.docSessionId));
		} else {
			pb += ("&req_doc=" + _enc(ux.docPath));
			pb += ("&req_win=" + _enc(window.name));
		}
		if (evp.uValidateAct) {
			pb += ("&req_va=" + _enc(evp.uValidateAct));
		}
		if (evp.uCmd) {
			pb += ("&req_cmd=" + _enc(evp.uCmd));
		}
		if (evp.uCmdTag) {
			pb += ("&req_cmdtag=" + _enc(evp.uCmdTag));
		}
		if (evp.uId) {
			pb += ("&req_wid=" + _enc(evp.uId));
		}
		if (evp.uPanels) {
			for (var i = 0; i < evp.uPanels.length; i++) {
				pb += ("&req_rsh=" + _enc(evp.uPanels[i]));
			}
		}
	}
	
	param.value = pb;
}

ux.buildNameParams = function(name, builtNames, param) {
	var namesToBuild = [];
	ux.buildNames(namesToBuild, name);

	for (var i = 0; i < namesToBuild.length; i++) {
		var id = namesToBuild[i];
		if (id) {
			if (builtNames[id]) {
				continue;
			}

			builtNames[id] = true;
			ux.extractObjParams(_id(id), param);
		}
	}
}

ux.extractObjParams = function(elem, param) {
	if (elem && !elem.disabled && !elem._nopush && elem.type != "button") {
		var trnId = elem.id;
		if (elem.type == "hidden") {
			var pblank = false;
			if(elem.value == "pushc_") {
				pblank = true;
				var cElems = _name(trnId);
				for(var i = 0; i < cElems.length; i++) {
					if (cElems[i].checked) {
						ux.appendParam(trnId, cElems[i].value, param);
						pblank = false;
					}
				}
			} else if(elem.value == "pushr_") {
				pblank = true;
				var rElems = _name(trnId);
				for(var i = 0; i < rElems.length; i++) {
					if (rElems[i].checked) {
						ux.appendParam(trnId, rElems[i].value, param);
						pblank = false;
						break;
					}
				}
			} else if(elem.value == "pushg_") {
				var gElems = _name(trnId);
				for(var i = 0; i < gElems.length; i++) {
					ux.extractObjParams(gElems[i], param);
				}
			} else {
				ux.appendParam(trnId, elem.value, param);
			}
			
			if (pblank) {
				ux.appendParam(trnId, "", param);
			}
		} else if (elem.type == "checkbox") {
			ux.appendParam(trnId, elem.checked, param);
		} else if (elem.type == "select-multiple") {
			for (var i = 0; i < elem.options.length; i++) {
				if (elem.options[i].selected) {
					ux.appendParam(trnId, elem.options[i].value, param);
				}
			}
		} else if (elem.type == "file") {
			if (elem.value) {
				var files = elem.files;
				for (var i = 0; i < files.length; i++) {
					param.value.append(trnId, files[i],
							files[i].name);
				}
			}
		} else {
			if (elem.value != undefined) {
				ux.appendParam(trnId, elem.value, param);
			}
		}
	}
}

ux.appendParam = function(id, value, param) {
	if (param.isForm) {
		param.value.append(id, value);
	} else {
		param.value += "&" + id + "="
				+ _enc(value);
	}
}

ux.buildNames = function(resultNames, name) {
	var aliasNames = ux.pagenamealiases[name];
	if (aliasNames) {
		for (var i = 0; i < aliasNames.length; i++) {
			ux.buildNames(resultNames, aliasNames[i]);
		}
	} else {
		resultNames.push(name);
	}
}

ux.detectFormElement = function(trgObj, ids) {
	if (trgObj && trgObj.type == "file")
		return true;
	if (ids) {
		for (var i = 0; i < ids.length; i++) {
			var id = ids[i];
			var elem = _id(id);
			if (elem && elem.type == "file") {
				return true;
			}

			var allNames = ux.pagenamealiases[id];
			if (allNames) {
				for (var j = 0; j < allNames.length; j++) {
					elem = _id(allNames[j]);
					if (elem && elem.type == "file") {
						return true;
					}
				}
			}
		}
	}
	return false;
}

ux.setDisabledById = function(ids, disabled) {
	if (ids) {
		for (var i = 0; i < ids.length; i++) {
			var id = ids[i];
			var elem = _id(id);
			if (elem) {
				// 27/08/19 Enable only if disabled by this function
				if (elem.disabled != disabled) {
					if(elem.disabled) {
						if(elem.localDisable) {
							elem.disabled = false;
						}
					} else {
						elem.localDisable = true;
						elem.disabled = true;
					}
				}
			}

			var grpElems = _name(id);
			if (grpElems) {
				for(var j = 0; j < grpElems.length; j++) {
					grpElems[j].disabled = disabled;
				}
			}
			
			var aliases = ux.pagenamealiases[id];
			if (aliases) {
				ux.setDisabledById(aliases, disabled);
			}
		}
	}
}

ux.setDisplayModeByNames = function(names, mode) {
	if (names) {
		for (var i = 0; i < names.length; i++) {
			ux.setDisplayModeByName(names[i], mode);
		}
	}
}

ux.setDisplayModeByName = function(name, mode) {
	var elems = _name(name);
	for (var j = 0; j < elems.length; j++) {
		elems[j].style.display = mode;
	}
}

ux.markNoPushWidgets = function(noPushList) {
	if (noPushList) {
		for (var i = 0; i < noPushList.length; i++) {
			var elem = _id(noPushList[i]);
			if (elem) {
				elem._nopush = true;
			}
		}
	}
}

/** ************************** MISCELLANEOUS ****************** */
/** Lists */
ux.listWirePopFrame = function(sel) {
	if (sel._pop) {
		var evp = {};
		evp.uId = sel.id;
		evp.uHitHandler = ux.listKeydownHit;
		evp.uEnterHandler = ux.listKeydownEnter;
		ux.addHdl(sel._frm, "click", ux.focusOnClick, evp);
		ux.addHdl(sel._frm, "keydown", ux.listSearchKeydown, evp);
		
		if (sel._blank) {
			ux.addHdl(sel._blank, "click", ux.listSelectClick, {uId:sel.id, uIndex:-1});
		}
		
		for (var i = 0; i < sel._iCnt; i++) {
			const label = _id(sel._selectIds[i])
			if (label) {
				const pref = sel._colors ? "<span class=\"sscol\" style=\"background-color:" + sel._keys[i] + ";\"></span>" : "";
				label.innerHTML =  pref + sel._labels[i];
				ux.addHdl(label, "click", ux.listSelectClick, {uId:sel.id, uIndex:i});
			}
		}
	}
}

ux.listKeydownHit = function(sel) {
	sel.selectOpt(sel._indexes[0], false, false);
}

ux.listKeydownEnter = function(sel) {
	sel.selectOpt(sel._oldSelIdx, true, true);
	ux.hidePopup(null);
}

ux.listKeydownSkip = function(sel, up) {
	var i = sel._oldSelIdx;
	if (up) {
		i--;
	} else {
		i++;
	}
	
	if(i >= 0 && i < sel._iCnt) {
		sel.selectOpt(i, false, false);
	}	
}

ux.listSelectClick = function(uEv) {
	const sel = _id(uEv.evp.uId);
	sel.selectOpt(uEv.evp.uIndex, true, true);
	ux.hidePopup(null);
}

ux.listScrollToLabel = function(sel, aElem) {
	var aH = ux.boundingHeight(aElem);
	var fH = ux.boundingHeight(sel._frm);
	var lH = ux.boundingHeight(sel._list);
	if(aH.top < fH.top) {
		sel._frm.scrollTop = aH.top - lH.top;
	} else {
		if (aH.bottom > fH.bottom) {
			sel._frm.scrollTop = aH.bottom - (lH.top + fH.height);
		}
	}
}

ux.listSearchKeydown = function(uEv) {
	const sel = _id(uEv.evp.uId);
	var evp = uEv.evp;
	if (uEv.uChar) {
		const gap = Date.now() - sel._lastKeyHit;
		if (gap > UNIFY_KEY_SEARCH_MAX_GAP) {
			sel._schIdx = 0;
			sel._indexes = null;
		}

		ux.listSearchLabel(sel, uEv.uChar);
		if (sel._indexes && sel._indexes.length > 0) {
			evp.uHitHandler(sel);
		}
	
		sel._lastKeyHit = Date.now(); 
	} else {
		if(uEv.uKeyCode == UNIFY_KEY_UP) {
			ux.listKeydownSkip(sel, true);
			uEv.uStop();
		} else if(uEv.uKeyCode == UNIFY_KEY_DOWN) {
			ux.listKeydownSkip(sel, false);
			uEv.uStop();
		} else if (uEv.uKeyCode == UNIFY_KEY_ENTER || (uEv.uKey && "ENTER" == uEv.uKey.toUpperCase())) {
			if (evp.uEnterHandler) {
				evp.uEnterHandler(sel);
				uEv.uStop();
			}
		}
	}
}

ux.listSearchLabel = function(sel, char) {
	const newIndexes = [];
	var schIdx = sel._schIdx;
	const labels = sel._labels;
	const indexes = sel._indexes;
	var mChar = char.toUpperCase();
	if(!indexes) {
		for(var i = 0;  i < labels.length; i++) {
			var label = labels[i];
			if (schIdx < label.length && label.charAt(schIdx) == mChar) {
				newIndexes.push(i);
			}
		}
	} else {
		for(var i = 0; i < indexes.length; i++) {
			var actI = indexes[i];
			var label = labels[actI]
			if (schIdx < label.length && label.charAt(schIdx) == mChar) {
				newIndexes.push(actI);
			}
		}
	}
	
	sel._schIdx++;
	sel._indexes = newIndexes;
}

/** Delayed post */
ux.setDelayedPanelPost = function(delayedPostPrm) {
	var pgNm = delayedPostPrm.pId;
	var evp = ux.newEvPrm(delayedPostPrm);
	evp.uURL = delayedPostPrm.pURL;
	evp.uCmd = pgNm + "->switchState";
	evp.uPanels = [ pgNm ];
	evp.uAutoCall = true;
	evp.uOnUserAct = delayedPostPrm.pOnUserAct;

	var delayPrd = delayedPostPrm.pPeriodMilliSec;
	if (delayPrd < UNIFY_DELAYEDPOSTING_MIN_DELAY) {
		delayPrd = UNIFY_DELAYEDPOSTING_MIN_DELAY;
	}
	evp.uDelayMillSec = delayPrd;
	ux.delayedpanelposting[pgNm] = evp;
	
	window.setTimeout("ux.fireDelayedPost(\"" + pgNm + "\");", evp.uDelayMillSec);
}

ux.fireDelayedPost = function(pgNm) {
	var evp = ux.delayedpanelposting[pgNm];
	if (evp) {
		if (evp.uOnUserAct) {
			if (ux.lastUserActTime > 0) {
				// Check if delayed post depends on last user activity
				var fromLastActTime = new Date().getTime() - ux.lastUserActTime;
				if (fromLastActTime >= UNIFY_LASTUSERACT_EFFECT_PERIOD) {
					if (_id(pgNm)) {
						// Postpone by delaying post again
						window.setTimeout("ux.fireDelayedPost(\"" + pgNm + "\");", evp.uDelayMillSec);
					} else {
						ux.delayedpanelposting[pgNm] = null;
					}
					return;
				}
			} else {
				ux.lastUserActTime = new Date().getTime()
			}
		}

		ux.delayedpanelposting[pgNm] = null;
		if (_id(pgNm)) {
			ux.ajaxCallWithJSONResp(null, evp);
		}
	}
}

/** Debounce */
ux.registerDebounce = function(pgNmlist, clear) {
	if (clear) {
		ux.debouncetime = [];
	}
	
	if(pgNmlist) {
		var timestamp = new Date().getTime();
		for (var i = 0; i < pgNmlist.length; i++) {
			ux.debouncetime[pgNmlist[i]] = timestamp;
		}
	}
}

ux.effectDebounce = function() {
	var debounced = [];
	for(var pgNm in ux.debouncetime) {
		var elem = _id(pgNm);
		if (elem && !elem.disabled) {
			elem.disabled = true;
			debounced[pgNm] = ux.debouncetime[pgNm];
		}
	}
	
	return debounced;
}

ux.clearDebounce = function(debounced) {
	if (debounced) {
		for(var pgNm in debounced) {
			if(debounced[pgNm] == ux.debouncetime[pgNm]) {
				var elem = _id(pgNm);
				if (elem) {
					elem.disabled = false;
				}				
			} 
		}
	}
}

/** Translation */
ux.centralize = function(baseElem, elem) {
	var x = Math.floor((baseElem.offsetWidth - elem.offsetWidth) / 2);
	if (x < 0)
		elem.style.left = "0px";
	else
		elem.style.left = x + "px";

	var y = Math.floor((baseElem.offsetHeight - elem.offsetHeight) / UNIFY_DEFAULT_POPUP_Y_SCALE);
	if (y < 0)
		elem.style.top = "0px";
	else
		elem.style.top = y + "px";
}

/** Scaling */
ux.stretchArea = function(elem, isWidth, isHeight) {
	if (isWidth) {
		elem.style.width = "100%";
	}

	if (isHeight) {
		elem.style.height = "100%";
	}
}

ux.fixArea = function(elem, isWidth, isHeight) {
	var elemRect = ux.boundingRect(elem);
	if (isWidth) {
		elem.style.width = (elemRect.width - 4) + "px";
	}

	if (isHeight) {
		elem.style.height = elemRect.height + "px";
	}
}

ux.boundingRect = function(elem) {
	var rect = elem.getBoundingClientRect();
	var _left = Math.round(rect.left);
	var _top = Math.round(rect.top);
	var _right = Math.round(rect.right);
	var _bottom = Math.round(rect.bottom);
	var _width = _right - _left;
	var _height = _bottom - _top;
	return {
		offsetLeft: elem.offsetLeft,
		offsetTop: elem.offsetTop,
		left : _left,
		top : _top,
		right : _right,
		bottom : _bottom,
		width : _width,
		height : _height
	};
}

ux.boundingHeight = function(elem) {
	var rect = elem.getBoundingClientRect();
	var _top = Math.round(rect.top);
	var _bottom = Math.round(rect.bottom);
	var _height = _bottom - _top;
	return {
		offsetTop: elem.offsetTop,
		top : _top,
		bottom : _bottom,
		height : _height
	};
}

ux.boundingWidth = function(elem) {
	var rect = elem.getBoundingClientRect();
	var _left = Math.round(rect.left);
	var _right = Math.round(rect.right);
	var _width = _right - _left;
	return {
		offsetLeft: elem.offsetLeft,
		left : _left,
		right : _right,
		width : _width,
	};
}

/** ************************** FORMATTED TEXT ****************** */
/** Text formatting */
ux.setTextRegexFormatting = function(prm) {
	var evp = {};
	if (prm.pCase) {
		evp.sTextCase = prm.pCase.toLowerCase();
	}

	if (prm.pRegex) {
		evp.sFormatRegex = (!prm.pRegex || prm.pRegex === "") ? null: new RegExp(prm.pRegex);
	}

	var elem = _id(prm.pId);
	if (elem) {
		if (evp.sFormatRegex) {
			ux.addHdl(elem, "paste", ux.textInputPaste,
					evp);
		}
		
		ux.addHdl(elem, "keypress", ux.textInputKeypress,
				evp);
		ux.addHdl(elem,  "keydown", ux.textInputKeydown,
				evp);
		if (prm.pCase) {
			ux.addHdl(elem,  "keyup", ux.textInputKeyup,
					evp);
		}
	}
}

ux.textInputPaste = function(uEv) {
	var evp = uEv.evp;
    const clipboard = uEv.clipboardData || window.clipboardData;
    var pasted = clipboard ? clipboard.getData('Text') : "";
	var _rejex = evp.sFormatRegex;
	if (_rejex && !_rejex.test(pasted)) {
		uEv.uStop();
		return;
	}
}

ux.textInputKeypress = function(uEv) {

}

ux.textInputKeyup = function(uEv) {
	var trgObj = uEv.uTrg;
	var evp = uEv.evp;
	if (!trgObj.readOnly) {
		var pos = ux.getCaretPosition(trgObj);
		var string = trgObj.value;
		if (evp.sTextCase) {
			if ("upper" == evp.sTextCase) {
				trgObj.value = string.toUpperCase();
			} else if ("camel" == evp.sTextCase) {
				const baseArr = string.split(" ")
				const res = [];
				for (var i = 0; i < baseArr.length; i++) {
					var str = baseArr[i];
					res.push(str.charAt(0).toUpperCase() + str.slice(1));
				}
				trgObj.value = res.join(" ");
			} else {
				trgObj.value = string.toLowerCase();
			}
			
			ux.setCaretPosition(trgObj, pos.start, pos.start);
			return;
		}		
	}
}

ux.textInputKeydown = function(uEv) {
	var trgObj = uEv.uTrg;
	var evp = uEv.evp;

	if (uEv.uChar && !trgObj.readOnly) {
		if (uEv.ctrlKey && ((uEv.uChar == 'v') ||(uEv.uChar == 'V'))) {
			return;
		}
		
		var pos = ux.getCaretPosition(trgObj);
		var string = trgObj.value;
		string = string.substring(0, pos.start) + uEv.uChar + string.substring(pos.end);

		var _rejex = evp.sFormatRegex;
		if (_rejex && !_rejex.test(string)) {
			uEv.uStop();
			return;
		}
	}
}

ux.getCaretPosition = function(trgObj) {
	if (document.selection) {
		trgObj.focus();
		var txtRange = document.selection.createRange();
		var selLen = txtRange.text.length;
		txtRange.moveStart('character', -trgObj.value.length);
		return {'start': txtRange.text.length - selLen, 'end': txtRange.text.length };
	} else if (trgObj.selectionStart || trgObj.selectionStart == '0') {
		return {'start': trgObj.selectionStart, 'end': trgObj.selectionEnd };
	}

	return {'start': 0, 'end': 0};
}

const caretSupport = ["text", "search", "URL", "tel", "password"];
ux.setCaretPosition = function(trgObj, start, end) {	
	if(trgObj.setSelectionRange) {
		if (trgObj.type && caretSupport.indexOf(trgObj.type) < 0) {
			return;
		}

		trgObj.focus();
		trgObj.setSelectionRange(start, end);
	} else if (trgObj.createTextRange) {
		var txtRange = trgObj.createTextRange();
		txtRange.collapse(true);
		txtRange.moveStart('character', start);
		txtRange.moveEnd('character', end);
		txtRange.select();
	}	
}

/** Text validation */
ux.setTextActionValidation = function(name, validation, validationRefArray,
		passMessage, failMessage, required) {
	var evp = {};
	evp.uURL = validation;
	evp.uRef = validationRefArray;
	evp.sPassMessage = passMessage;
	evp.sFailMessage = failMessage;
	evp.sRequired = required;
	ux.addHdlMany(name, "blur", ux.textValidationOnBlurHandler,
			evp);
}

ux.setTextRegexValidation = function(name, validation, validationRefArray,
		passMessage, failMessage, required) {
	var evp = {};
	evp.sValidationRegex = validationRegex;
	evp.sPassMessage = passMessage;
	evp.sFailMessage = failMessage;
	evp.sRequired = required;
	ux.addHdlMany(name, "blur", ux.textValidationOnBlurHandler,
			evp);
}

ux.setTextJSValidation = function(name, validation, validationRefArray,
		passMessage, failMessage, required) {
	var evp = {};
	evp.sValidationJS = validationJS;
	evp.uRef = validationRefArray;
	evp.sPassMessage = passMessage;
	evp.sFailMessage = failMessage;
	evp.sRequired = required;
	ux.addHdlMany(name, "blur", ux.textValidationOnBlurHandler,
			evp);
}

ux.textValidationOnBlurHandler = function(uEv) {
	var evp = uEv.evp;
	var trgObj = uEv.uTrg;

	trgObj.sError = null;
	if (evp.sRequired && evp.value == "") {
		trgObj.sError = "required";
	}

	if (trgObj.sError == null) {
		var validationRegex = null;
		if (evp.sValidationRegex) {
			validationRegex = (!evp.sValidationRegex || evp.sValidationRegex === "") ? null: new RegExp(evp.sValidationRegex);
		}

		if (validationRegex) {
			if (!validationRegex.test(trgObj.value)) {
				trgObj.sError = "invalid";
			}
		}

		if (trgObj.sError == null) {
			if (evp.sValidationJS) {
				if (!evp.sValidationJS(trgObj, evp.uRef)) {
					trgObj.sError = "invalid";
				}
			}
		}

		if (trgObj.sError == null) {
			if (evp.uURL) {
				ux.ajaxCallWithJSONResp(trgObj, evp);
				return;
			}
		}
	}
}

ux.padLeft = function(text, ch, length) {
	while (text.length < length) {
		text = ch + text;
	}
	return text;
}

ux.padRight = function(text, ch, length) {
	while (text.length < length) {
		text = text + ch;
	}
	return text;
}

/** Mouse */
ux.actRightClick = false;
ux.wireRightClickHandler = function(evp, handler) {
	evp.uRightHandler = handler;
	return ux.onRightClickHandler;
}

ux.onRightClickHandler = function(uEv) {
	if (uEv.mButton == UNIFY_RIGHT_BUTTON) {
		ux.actRightClick = true;
		uEv.evp.uRightHandler(uEv);
	}
}

/** Keys */
ux.wireSpecialKeyHandler = function(evp, handler, key, keyCode) {
	evp.uSpecialKey = key.toUpperCase();
	evp.uSpecialKeyCode = keyCode;
	evp.uSpecialKeyHandler = handler;
	return ux.onSpecialKeyHandler;
}

ux.onSpecialKeyHandler = function(uEv) {
	if (uEv.uKey) {
		if (uEv.uKey.toUpperCase() == uEv.evp.uSpecialKey) {
			uEv.evp.uSpecialKeyHandler(uEv);
		}
	} else {
		if (uEv.uKeyCode == uEv.evp.uSpecialKeyCode) {
			uEv.evp.uSpecialKeyHandler(uEv);
		}
	}
}

ux.setShortcut = function(evp) {
	ux.shortcuts[evp.uShortcut] = evp;
}

ux.setOnEvent = function(evp) {
	const eventName = evp.uEvnt;
	if (evp.uPushSrc && evp.uRef && !evp.uRef.includes(evp.uId)) {
		evp.uRef.push(evp.uId);
	}
	
	var elem = _id(evp.uId);
	const _fn = ux.getfn(evp.uFunc);
	if (elem) {
		if (elem.value == "pushr_") {
			ux.addHdlMany(evp.uId, eventName, _fn,
					evp);
		} else {
			ux.addHdl(elem, eventName, _fn,
					evp);
			if (evp.uFire) {
				ux.fireEvent(elem, eventName);
			}
		}
	} else {
		ux.addHdlMany(evp.uId, eventName, _fn,
				evp);
	}
}

ux.popupWireClear = function(rgp, btnId, trgArr) {
	var clearBtn = _id(btnId);
	if (clearBtn) {
		if (rgp.pClearable) {
			const evp = {uRef:trgArr};
			ux.addHdl(clearBtn, "click", function(uEv) {
				ux.clear(uEv);
				ux.hidePopup(uEv);
			}, evp);
		} else {
			clearBtn.disabled = true;
		}
	}
}

ux.popupWireCancel = function(btnId) {
	var cancelBtn = _id(btnId);
	if (cancelBtn) {
		ux.addHdl(cancelBtn, "click", ux.hidePopup, {});
	}
}

ux.applyPattern = function(df) {
	var val = "";
	if (df._parts && df._format) {
		for (var i = 0; i < df._format.length; i++) {
			const fmt = df._format[i];
			if (fmt.flag) {
				val += fmt.target;
			} else {
				var dat = df._parts[fmt.target];
				if (dat != undefined) {
					if(df._lists && df._lists[i].list) {
						dat = df._lists[i].list[parseInt(dat)];
					}

					if (df._padLeft) {
						val += ux.padLeft(dat, '0', fmt.length);
					} else {
						val += dat;
					}
				} else {
					return "";
				}
			}
		}
		
	}
	
	return val;
}

/** Set hidden values */
ux.setHiddenValues = function(references, hiddenValues) {
	if (references && hiddenValues) {
		for (var i = 0; i < references.length; i++) {
			var elem = _name_0(references[i]);
			if (elem && (elem.type == "hidden")) {
				elem.value = hiddenValues[i];
			}
		}
	}
}

/** Document functions and event handlers */
ux.init = function() {
	ux.resizeTimeout = null;
	// Set document keydown handler
	ux.addHdl(document, "keydown", ux.documentKeydownHandler,
					{});
	
	// Register self as extension
	ux.registerExtension("ux", ux);
	
	// Override window menu context
	window.oncontextmenu = function (uEv) {
		if (ux.actRightClick) {
			ux.actRightClick = false;
			return false;
		}
		
		ux.hidePopup(null);
	    return true; // Do default
	}

	if (UNIFY_POST_COMMIT_QUEUE) {
		ux.postCommitProcessor();
	}
	
	//Perform
    ux.setfn(ux.forward, "ux01");
    ux.setfn(ux.submit, "ux02"); 
    ux.setfn(ux.post, "ux03");   
    ux.setfn(ux.postToPath, "ux04");   
    ux.setfn(ux.postCommand, "ux05");   
    ux.setfn(ux.openWindow, "ux06");
    ux.setfn(ux.download, "ux07");
    ux.setfn(ux.clear, "ux08");
    ux.setfn(ux.disable, "ux09");
    ux.setfn(ux.show, "ux0a");
    ux.setfn(ux.hide, "ux0b");
    ux.setfn(ux.delegate, "ux0c");
    ux.setfn(ux.setAllChecked, "ux0d");
    ux.setfn(ux.populateSelectOptions, "ux0e");
    ux.setfn(ux.openPopup, "ux0f");  
    ux.setfn(ux.hidePopup, "ux10");
    ux.setfn(ux.repositionMenuPopup, "ux11");
    ux.setfn(ux.setFocus, "ux12");
    ux.setfn(ux.rigAssignmentBox, "ux13");  
    ux.setfn(ux.rigCheckbox, "ux14");  
    ux.setfn(ux.rigChecklist, "ux15");  
    ux.setfn(ux.rigDateField, "ux16");  
    ux.setfn(ux.rigDebitCreditField, "ux17");  
    ux.setfn(ux.rigDropdownChecklist, "ux18");  
    ux.setfn(ux.dcHidePopup, "ux19"); 
    ux.setfn(ux.rigDurationSelect, "ux1a");  
    ux.setfn(ux.rigFileAttachment, "ux1b");  
    ux.setfn(ux.rigFileDownload, "ux1c");  
    ux.setfn(ux.rigFileUploadView, "ux1d");  
    ux.setfn(ux.rigFileUpload, "ux1e");  
    ux.setfn(ux.rigDragAndDropPopup, "ux1f");  
    ux.setfn(ux.rigLinkGrid, "ux20");  
    ux.setfn(ux.rigMoneyField, "ux21");  
    ux.setfn(ux.mfOnShow, "ux22"); 
    ux.setfn(ux.rigMultiSelect, "ux23");  
    ux.setfn(ux.rigOptionsTextArea, "ux24");  
    ux.setfn(ux.rigPeriodField, "ux25");  
    ux.setfn(ux.pfOnShow, "ux26");
    ux.setfn(ux.rigPhotoUpload, "ux27");  
    ux.setfn(ux.rigRadioButtons, "ux28");  
    ux.setfn(ux.rigSearchField, "ux29");  
    ux.setfn(ux.sfWireResult, "ux2a");  
    ux.setfn(ux.sfOnShow, "ux2b"); 
    ux.setfn(ux.rigSingleSelect, "ux2c");  
    ux.setfn(ux.ssOnShow, "ux2d");
    ux.setfn(ux.rigTable, "ux2e");  
    ux.setfn(ux.rigTextArea, "ux2f");  
    ux.setfn(ux.rigTextClock, "ux30");  
    ux.setfn(ux.setTextRegexFormatting, "ux31");  
    ux.setfn(ux.rigTimeField, "ux32");  
    ux.setfn(ux.rigDragAndDropPopup, "ux33");  
    ux.setfn(ux.rigTreeExplorer, "ux34");  
    ux.setfn(ux.rigDesktopType2, "ux35");  
    ux.setfn(ux.rigAccordion, "ux36");  
    ux.setfn(ux.rigContentPanel, "ux37");  
    ux.setfn(ux.rigDetachedPanel, "ux38");  
    ux.setfn(ux.rigFixedContentPanel, "ux39");  
    ux.setfn(ux.loadRemoteDocViewPanel, "ux3a");  
    ux.setfn(ux.rigSplitPanel, "ux3b");  
    ux.setfn(ux.rigStretchPanel, "ux3c");  
    ux.setfn(ux.rigTabbedPanel, "ux3d");  
    ux.setfn(ux.rigValueAccessor, "ux3e");
    ux.setfn(ux.setShortcut, "ux3f");
    ux.setfn(ux.setOnEvent, "ux40"); 
    ux.setfn(ux.setDelayedPanelPost, "ux41");
    ux.setfn(ux.optionsTextAreaOnShow, "ux42");
}

ux.setfn = function(fn, id) {
	ux.fnaliases[id] = fn;
}

ux.getfn = function(id) {
	return ux.fnaliases[id];
}

ux.setHintTimeout = function(millisec) {
	ux.hintTimeout = millisec;
}

ux.documentKeydownHandler = function(uEv) {
	// Hide popup on tab
	if (uEv.uKeyCode == UNIFY_KEY_TAB) {
		ux.hidePopup(null);
		return;
	}
	
	if (uEv.uKeyCode == UNIFY_KEY_BACKSPACE) { // Stop general backspace except for particular elements
		var stopBackspace = true;
		var elem = uEv.uTrg;
		if (elem.type == "text" || elem.type == "password"
				|| elem.type == "textarea") {
			stopBackspace = elem.readOnly || elem.disabled;
		}
		if (stopBackspace) {
			uEv.uStop();
		}
	}

	var evp = ux.shortcuts[uEv.uShortKeyCode];
	if (evp) {
		if (_id(evp.uId)) { // Containing panel must be visible for shortcut
			uEv.evp = evp;
			ux.getfn(evp.uFunc)(uEv);
			uEv.uStop();
		}
	}
}


/** DOM functions */
ux.decodeHtml = function(html) {
    var elem = document.createElement("textarea");
    elem.innerHTML = html;
    return elem.value;	
}

const UNESCAPE_MAP = {
		  '&amp;' : '&',
		  '&lt;' : '<',
		  '&gt;' : '>',
		  '&quot;' : '"',
		  '&#039;' : "'"
		};

ux.unescape = function (str) {
  return str ? str.replace(/&lt;|&gt;|&quot;|&#039;|&amp;/g , function(k) { return UNESCAPE_MAP[k]; }): "";
}

ux.findParent = function(domObject, tagName) {
	if (domObject) {
		while (domObject = domObject.parentNode) {
			if (domObject.tagName.toLowerCase() == tagName.toLowerCase()) {
				return domObject;
			}
		}
	}
	
	return null;
}

/** Drag and drop */
ux.dragElem = null;
ux.dragElemPos = {
	x : 0,
	y : 0
};
ux.dragPointerPos = {
	x : 0,
	y : 0
};

ux.dragDropEngage = function(ev) {
	var evp = ev.evp;
	ux.dragElem = _id(evp.uTargetPnlId);
	ux.dragElemPos = {
		x : parseInt(ux.dragElem.style.left),
		y : parseInt(ux.dragElem.style.top)
	};
	ux.dragPointerPos = ux.getPointerCoordinates(ev);
	ux.addDirectHdl(document, "mouseup", ux.dragDropDisengage);
	ux.addDirectHdl(document, "mousemove", ux.dragDropAction);
}

ux.dragDropDisengage = function(ev) {
	ux.remDirectHdl(document, "mousemove", ux.dragDropAction);
	ux.remDirectHdl(document, "mouseup", ux.dragDropDisengage);
}

ux.dragDropAction = function(ev) {
	var newPointerPos = ux.getPointerCoordinates(ev);
	var x = ux.dragElemPos.x + newPointerPos.x - ux.dragPointerPos.x
	var y = ux.dragElemPos.y + newPointerPos.y - ux.dragPointerPos.y

	// Restrict to view port
	var viewRect = ux.getWindowRect();
	var dragElemRect = ux.boundingRect(ux.dragElem);

	var xFar = x + dragElemRect.width;
	if (xFar > viewRect.right)
		x -= (xFar - viewRect.right);
	if (x < viewRect.left)
		x = viewRect.left;

	var yFar = y + dragElemRect.height;
	if (yFar > viewRect.bottom)
		y -= (yFar - viewRect.bottom);
	if (y < viewRect.top)
		y = viewRect.top;

	ux.dragElem.style.left = x + "px";
	ux.dragElem.style.top = y + "px";
}

/** Coordinates and sizes */
ux.getWindowRect = function() {
	var w = window.innerWidth || document.documentElement.clientWidth
			|| document.body.clientWidth
			|| document.documentElement.offsetWidth
			|| document.body.offsetWidth;

	var h = window.innerHeight || document.documentElement.clientHeight
			|| document.body.clientHeight
			|| document.documentElement.offsetHeight
			|| document.body.offsetHeight;
	return {
		left : 0,
		top : 0,
		right : w,
		bottom : h
	};
}

ux.getPointerCoordinates = function(ev) {
	if (ev.pageX || ev.pageY) {
		return {
			x : ev.pageX,
			y : ev.pageY
		};
	}
	return {
		x : ev.clientX + document.body.scrollLeft - document.body.clientLeft,
		y : ev.clientY + document.body.scrollTop - document.body.clientTop
	};
}

ux.getExactPointerCoordinates = function(ev) {
	if (ev.pageX || ev.pageY) {
		return {
			x : ev.pageX,
			y : ev.pageY
		};
	}
	return {
		x : ev.clientX,
		y : ev.clientY
	};
}

ux.getRelPointerCoordinates = function(ev) {
	var uLoc = ux.getExactPointerCoordinates(ev);
	var tCoord = ux.getElementPosition(ev.uTrg);
	return {
		x : uLoc.x = tCoord.x,
		y : uLoc.y = tCoord.y
	};
}


ux.getElementPosition = function getPosition(el) {
	  var xp = 0;
	  var yp = 0;

	  while (el) {
	    xp += (el.offsetLeft - el.scrollLeft + el.clientLeft);
	    yp += (el.offsetTop - el.scrollTop + el.clientTop);
	    el = el.offsetParent;
	  }

	  return {
	    x: xp,
	    y: yp
	  };
}  

/** Low-level event functions */
ux.addHdlMany = function(name, eventName, handler, evp) {
	var elems = _name(name);
	for (var i = 0; i < elems.length; i++) {
		ux.addHdl(elems[i], eventName, handler, evp);
	}
}

ux.addHdl = function(domObject, eventName, handler, evp) {
	if ("enter" == eventName) {
		eventName = "keydown";
		handler = ux.wireSpecialKeyHandler(evp, handler, "Enter", UNIFY_KEY_ENTER);
	} else if ("rtclick" == eventName) {
		eventName = "mouseup";
		handler = ux.wireRightClickHandler(evp, handler);
	}

	if (domObject) {
		if (domObject.addEventListener) {
			domObject.addEventListener(eventName, function(event) {
				ux.handleOrConfirmRedirect(event, handler, evp);
			}, false); // DOM Level 2. false = Bubble, true = Capture

		} else if (domObject.attachEvent) {
			domObject.attachEvent("on" + eventName, function(event) {
				ux.handleOrConfirmRedirect(event, handler, evp);
			}); // Explorer
		}
	}
}

ux.handleOrConfirmRedirect = function(event, handler, evp) {
	if (evp.uConf) {
		// Store action
		ux.confirmstore.handler = handler;
		ux.confirmstore.normEvt = ux.normaliseEvent(event, evp);
		ux.confirmstore.evp = evp;

		// Execute confirmation redirect
		var evPrmConf = {};
		evPrmConf.uURL = evp.uConfURL;
		evPrmConf.uConfMsg = evp.uConf;
		evPrmConf.uIconIndex = evp.uIconIndex;
		evPrmConf.uViewer = evp.uViewer;
		var hiddenElem = _id(ux.confirmstore.normEvt.uTrg.id + "_a");
		if (hiddenElem) {
			evPrmConf.uConfPrm = hiddenElem.value;
		}
		ux.hidePopup(null);
		ux.postCommit(evPrmConf);
	} else {
		handler(ux.normaliseEvent(event, evp));
	}
}

ux.addDirectHdl = function(domObject, eventName, handler) {
	if (document.addEventListener) {
		domObject.addEventListener(eventName, handler, false); // DOM Level 2.
	} else if (document.attachEvent) {
		domObject.attachEvent("on" + eventName, handler); // Explorer
	}
}

ux.remDirectHdl = function(domObject, eventName, handler) {
	if (document.removeEventListener) {
		domObject.removeEventListener(eventName, handler, false);
	} else {
		domObject.detachEvent("on" + eventName, handler);
	}
}

ux.normaliseEvent = function(event, evp) {
	event = event || window.event;
	if (event.stopPropagation) {
		event.uStop = function() {
			this.stopPropagation();
			this.preventDefault();
		};
	} else {
		event.uStop = function() {
			this.cancelBubble = true;
			this.returnValue = false;
		};
	}

	if (event.srcElement) {
		event.uTrg = event.srcElement;
	} else {
		event.uTrg = event.target;
	}

	if (event.key) {
		event.uKey = event.key;
		if (event.key.length == 1) {
			event.uChar = event.key;
		}
	}

	event.uKeyCode = event.keyCode;
	event.uShortKeyCode = event.uKeyCode;
	if (event.shiftKey) {
		event.uShortKeyCode |= UNIFY_SHIFT;
	}
	if (event.ctrlKey) {
		event.uShortKeyCode |= UNIFY_CTRL;
	}
	if (event.altKey) {
		event.uShortKeyCode |= UNIFY_ALT;
	}

	if (!event.uChar) {
		if (ux.isPrintable(event.uKeyCode)) {
			event.uChar = String.fromCharCode(event.uKeyCode);
		}
	}

	if((event.which && event.which == 3) || (event.button && event.button == 2)) {
		event.mButton = UNIFY_RIGHT_BUTTON;
	}
	
	event.evp = evp;
	return event;
}

ux.isPrintable = function(keyCode) {
	if (keyCode) {
		return (keyCode > 47 && keyCode < 58) || (keyCode > 64 && keyCode < 91)
				|| (keyCode > 95 && keyCode < 112)
				|| (keyCode > 218 && keyCode < 223)
				|| (keyCode > 185 && keyCode < 193) || keyCode == 32;
	}
	return false;
}

ux.fireEvent = function(domObject, eventName) {
	if (document.createEvent) {
		var event = document.createEvent("HTMLEvents");
		event.initEvent(eventName, true, true);
		return !domObject.dispatchEvent(event);
	} else {
		var event = document.createEventObject();
		return domObject.fireEvent("on" + eventName, event);
	}
}


ux.focusOnClick = function(uEv) {
	uEv.uTrg.focus();
}


/** Pop-up */
ux.popupTimeout = UNIFY_DEFAULT_POPUP_TIMEOUT;
ux.popupNewOpen = false;
ux.popupStayOpen = false;
ux.openPrm = undefined;
ux.popupOrigin = undefined;
ux.popCurr = undefined;

ux.hideUserHint = function() {
	var elem = _id(ux.cntHintId);
	if (elem) {
		elem.innerHTML = "";
	}
}

ux.openPopup = function(uEv) {
	var openPrm = uEv.evp.uRef;
	openPrm.uTrg = uEv.uTrg;
	ux.doOpenPopup(openPrm);
}

ux.doOpenPopup = function(openPrm) {
	if (openPrm) {
		var oldPCurr = ux.popCurr;
		var newPCurr = _id(openPrm.popupId);

		ux.hidePopup(null);
		ux.cancelClosePopupTimer();
		ux.popupStayOpen = openPrm.stayOpenForMillSec <= 0;
		if (ux.popupStayOpen == true) {
			ux.popupTimeout = UNIFY_DEFAULT_POPUP_TIMEOUT;
			if (!openPrm.forceReopen) {
				if (oldPCurr == newPCurr) {
					return;
				}
			}
		} else {
			ux.popupTimeout = openPrm.stayOpenForMillSec;
		}

		ux.popupOrigin = openPrm.uTrg;
		ux.popCurr = newPCurr;
		ux.popupNewOpen = true;
		if (!(ux.popCurr.eventsSet == true)) {
			ux.addHdl(ux.popCurr, "mouseover",
					ux.cancelClosePopupTimer, {});
			if (ux.popupStayOpen != true) {
				ux.addHdl(openPrm.uTrg, "mouseout",
						ux.startClosePopupTimer, {});
				ux.addHdl(ux.popCurr, "mouseout",
						ux.startClosePopupTimer, {});
			}

			ux.popCurr.eventsSet = true;
		}

		if (openPrm.frameId) {
			ux.popCurr.style.top= '0px';
			ux.popCurr.style.left= '0px';
			ux.popCurr.style.visibility = 'hidden';
			ux.popCurr.style.display = 'block';
			
			var frameRect = ux.boundingRect(_id(openPrm.frameId));
			var y = frameRect.bottom;
			var h = ux.boundingHeight(ux.popCurr).height;
			if ((y + h) > window.innerHeight) {
				y = frameRect.top - h;
			}

			y = y - frameRect.top;
			
			ux.popCurr.style.top = y + 'px';
			ux.popCurr.style.left = '0px';
			ux.popCurr.style.minWidth = frameRect.width + 'px';
		} else if (openPrm.relFrameId) {
			ux.popCurr.style.top= '0px';
			ux.popCurr.style.left= '0px';
			ux.popCurr.style.visibility = 'hidden';
			ux.popCurr.style.display = 'block';
			
			var frameRect = ux.boundingRect(_id(openPrm.relFrameId));
			var x = frameRect.left;
			var y = frameRect.bottom;
			if (openPrm.uLoc) {
				x = openPrm.uLoc.x;
				y = openPrm.uLoc.y;
			}
			
			ux.popCurr.style.left = x + 'px';
			ux.popCurr.style.top = y + 'px';
			ux.popCurr.style.minWidth = frameRect.width + 'px';
		}

		ux.popCurr.style.visibility = 'visible';

		ux.openPrm = openPrm;
		if (openPrm.showHandler) {
			ux.getfn(openPrm.showHandler)(openPrm.showParam);
		}
	}
}

ux.isPopupVisible = function(uEv) {
	if (ux.popCurr) {
		return true;
	}
	
	return false;
}

ux.hidePopup = function(uEv) {
	if (ux.popCurr) {
		var openPrm = ux.openPrm;
		if (openPrm.frameId) {
			ux.popCurr.style.display = 'none';
		}
		
		ux.popCurr.style.visibility = 'hidden';
		ux.popCurr = null;
		if (openPrm && openPrm.hideHandler) {
			ux.getfn(openPrm.hideHandler)(openPrm.hideParam);
		}
	}
}

ux.startClosePopupTimer = function() {
	if (ux.closePopupTimer) {
		window.clearTimeout(ux.closePopupTimer);
	}
	ux.closePopupTimer = window.setTimeout(ux.hidePopup, ux.popupTimeout);
}

ux.cancelClosePopupTimer = function() {
	if (ux.closePopupTimer) {
		window.clearTimeout(ux.closePopupTimer);
		ux.closePopupTimer = null;
	}
}

// Hide popup when click-out
ux.documentHidePopup = function(uEv) {
	if (ux.popCurr) {
		var elem = uEv.uTrg;
		while (elem) {
			if (elem == ux.popupOrigin || elem == ux.popCurr) {
				return;
			}
			elem = elem.parentElement;
		}

		ux.hidePopup(uEv);
	}
	
	if (ux.detachObj) {
		const orig = _id(ux.detachObj.originId);
		var elem = uEv.uTrg;
		while (elem) {
			if (elem == orig || elem == ux.detachObj) {
				return;
			}
			elem = elem.parentElement;
		}

		ux.detachObj.hide();
	}
}

ux.addHdl(document, "click", ux.documentHidePopup, {});

/** Delays */
ux.sleep = function (mill) {
	  return new Promise(resolve => setTimeout(resolve, mill));
}

/** Page resets */
ux.registerPageReset = function(id, resetFunc) {
	ux.pageresets[id] = resetFunc;
}

ux.callPageResets = function() {
	for (var id in ux.pageresets) {
		try {
			ux.pageresets[id]();
		} catch(e) {
			//console.log(e.message);
		}
	}
}

/** On window resize function */
ux.registerResizeFunc = function(id, resizeFunc, resizePrm) {
	const resizeInfo = {};
	resizeInfo.resizeFunc = resizeFunc;
	resizeInfo.resizePrm = resizePrm;
	ux.resizefunctions[id] = resizeInfo;
}

ux.callResizeFuncs = function() {
	for (var id in ux.resizefunctions) {
		if (ux.resizefunctions.hasOwnProperty(id)) {
			if (_id(id)) {
				var resizeInfo = ux.resizefunctions[id];
				if (resizeInfo) {
					resizeInfo.resizeFunc(resizeInfo.resizePrm);
				}
			}
		}
	}
}

ux.addHdl(window, "resize", function() {
	window.clearTimeout(ux.resizeTimeout); // Debounce resize call
	ux.resizeTimeout = window.setTimeout(ux.callResizeFuncs,
			UNIFY_WINDOW_RESIZE_DEBOUNCE_DELAY);
}, {});

/** Initialization */
ux.init();

/** Types */
// Transformation
const DEFAULT_TRANSFORMATION_STEP_RATE = 20;
const LINEAR_TRANSLATION = 0;

function Transformation(element, stepRate) {
	var thisInst = this;
	this.queue = [];
	this.intervalId = undefined;
	this.element = element;
	this.stepRate = stepRate;
	if (!this.stepRate) {
		this.stepRate = DEFAULT_TRANSFORMATION_STEP_RATE;
	}

	this.linear = function(startX, stopX, startY, stopY, rate) {
		var steps = Math.floor(rate / this.stepRate);
		if (rate % this.stepRate)
			steps++;

		var stepX = Math.round((stopX - startX) / steps);
		var stepY = Math.round((stopY - startY) / steps);

		this.set(startX, startY);
		this.queue.push({
			type : LINEAR_TRANSLATION,
			currentX : startX,
			stopX : stopX,
			stepX : stepX,
			currentY : startY,
			stopY : stopY,
			stepY : stepY
		});

		if (!this.intervalId) {
			this.intervalId = setInterval(function() {
				thisInst.step();
			}, this.stepRate);
		}
	}

	this.set = function(x, y) {
		this.element.style.left = x + "px";
		this.element.style.top = y + "px";
	}

	this.step = function() {
		var transObj = this.queue[0];
		if (transObj.type == LINEAR_TRANSLATION) {
			var x = transObj.currentX + transObj.stepX;
			var y = transObj.currentY + transObj.stepY;

			var completedX = transObj.stepX == 0
					|| (transObj.stepX > 0 && x >= transObj.stopX)
					|| (transObj.stepX < 0 && x <= transObj.stopX);
			if (completedX)
				x = transObj.stopX;

			var completedY = transObj.stepY == 0
					|| (transObj.stepY > 0 && y >= transObj.stopY)
					|| (transObj.stepY < 0 && y <= transObj.stopY);
			if (completedY)
				y = transObj.stopY;

			if (completedX && completedY) {
				this.queue.shift();
			} else {
				transObj.currentX = x;
				transObj.currentY = y;
			}
		}

		if (this.queue.length == 0) {
			clearInterval(this.intervalId);
			this.intervalId = undefined;
		}

		this.set(x, y);
	}
}
