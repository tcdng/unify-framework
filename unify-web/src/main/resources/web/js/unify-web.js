/*
 * Copyright 2014 The Code Department
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
 * Unify Framework Javascript.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
var ux = {};

var UNIFY_SHIFT = 0x0100;
var UNIFY_CTRL = 0x0200;
var UNIFY_ALT = 0x0400;

var UNIFY_RIGHT_BUTTON = 0x02;

var UNIFY_DEFAULT_POPUP_Y_SCALE = 3; // Pop-up Y offset scale

var UNIFY_DEFAULT_POPUP_TIMEOUT = 400; // .4 seconds.
var UNIFY_DELAYEDPOSTING_MIN_DELAY = 250; // .25 seconds.
var UNIFY_BUSY_INDICATOR_DISPLAY_DELAY = 200; // .2 seconds.
var UNIFY_HIDE_USER_HINT_DISPLAY_PERIOD = 3000; // 3 seconds.
var UNIFY_WINDOW_RESIZE_DEBOUNCE_DELAY = 400; // .4 seconds.
var UNIFY_KEY_SEARCH_MAX_GAP = 1000; // 1 second.
var UNIFY_TREEDOUBLECLICK_DELAY = 250; // .25 seconds.
var UNIFY_LASTUSERACT_EFFECT_PERIOD = 180000; // 3 minutes.

var UNIFY_MAX_STRETCHPANEL_DEPTH = 5;

ux.docPath = "";
ux.docPopupBaseId = null;
ux.docPopupId = null;
ux.docSysInfoId = null;

ux.popupVisible = false;

ux.submitting = false;
ux.busyIndicator = "";
ux.busyIndicatorTimer;
ux.busyCounter = 0;

ux.cntHintId = null
ux.cntTabCloseId = null;
ux.cntOpenPath = null;
ux.cntSavePath = null;
ux.cntSaveList = null;
ux.cntSaveRemoteView = null;
ux.remoteView = null;

ux.shortcuts = [];
ux.pagenamealiases = [];
ux.delayedpanelposting = [];
ux.resizefunctions = {};
ux.remoteviewsessions = [];

ux.confirmStore = {};

ux.lastUserActTime=0;

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

/** Basic * */
ux.setupDocument = function(docPath, docPopupBaseId, docPopupId, docSysInfoId) {
	ux.docPath = docPath;
	ux.docPopupBaseId = docPopupBaseId;
	ux.docPopupId = docPopupId;
	ux.docSysInfoId = docSysInfoId;
}

ux.processJSON = function(jsonstring) {
	var jsonEval = eval("(" + jsonstring + ")");
	ux.remoteView = jsonEval.remoteView;
	if (ux.remoteView) {
		ux.remoteviewsessions[ux.remoteView.view] = ux.remoteView.sessionID;
	}

	if (jsonEval.jsonResp) {
		for (var j = 0; j < jsonEval.jsonResp.length; j++) {
			var resp = jsonEval.jsonResp[j];
			ux.respHandler[resp.handler](resp);
		}
		ux.cascadeStretch();
	}

	ux.remoteView = null;
}

/** Event parameters */
ux.newEvPrm = function(rgp) {
	var evp = {};
	if (ux.remoteView) {
		evp.uSessionID = ux.remoteView.sessionID;
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
			eval(resp.docView.script);
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
			if (ux.confirmStore.handler) {
				var handler = ux.confirmStore.handler;
				var normEvt = ux.confirmStore.normEvt;
				normEvt.evp = ux.confirmStore.evp;
				ux.confirmStore = {};
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
						UNIFY_HIDE_USER_HINT_DISPLAY_PERIOD);
			}
		}
	},

	loadContentHdl : function(resp) {
		if (resp.closeRemoteTab) {
			if (ux.cntTabCloseId) {
				ux.fireEvent(_id(ux.cntTabCloseId), "click", true);
			}
		} else {
			ux.refreshPageGlobals(resp);
			ux.refreshPanels(resp);
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
	},

	refreshSectionHdl : function(resp) {
		ux.refreshSection(resp);
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
					if (resp.showSysInfoPopup.script) {
						eval(resp.showSysInfoPopup.script);
					}
				}
			}
		} else {
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

					if (resp.showPopup.script) {
						eval(resp.showPopup.script);
					}
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
		}
		
		var ajaxPrms = ux.ajaxConstructCallParam(path, "req_doc="
				+ _enc(ux.docPath), false, true, false, ux.processJSON);
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
			if (resp.refreshPanels[i].script) {
				eval(resp.refreshPanels[i].script);
			}
		}
	}
}

ux.refreshSection = function(resp) {
	if (resp.section) {
		if (resp.section.html) {
			var trg = _id(resp.section.target);
			if (trg) {
				trg.innerHTML = resp.section.html;
			}
		}

		if (resp.section.script) {
			eval(resp.section.script);
		}
	}
}

ux.refreshPageGlobals = function(resp) {
	if (resp.pSaveList) {
		ux.cntSaveList = resp.pSaveList;
	}

	ux.cntSaveRemoteView = ux.remoteView;

	if (resp.clearShortcuts) {
		ux.shortcuts = [];
	}

	ux.setPageNameAliases(resp);
}

ux.setPageNameAliases = function(resp) {
	if (resp.pageNameAliases) {
		for (var i = 0; i < resp.pageNameAliases.length; i++) {
			ux.pagenamealiases[resp.pageNameAliases[i].pn] = resp.pageNameAliases[i].aliases;
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

	try {
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
					alert(uAjaxReq.responseText);
				}
			}
		};
		if (ajaxPrms.uParam)
			uAjaxReq.send(ajaxPrms.uParam);
		else
			uAjaxReq.send();
	} catch (ex) {
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
		ux.ajaxCall(ajaxPrms);
	}
}

ux.ajaxConstructCallParam = function(url, param, sync, encoded, busy,
		successFunc) {
	var ajaxPrms = {};
	ajaxPrms.uURL = url;
	ajaxPrms.uSync = sync;
	ajaxPrms.uBusy = busy;
	ajaxPrms.uEncoded = encoded;
	ajaxPrms.uParam = param;
	ajaxPrms.uSuccessFunc = successFunc;
	return ajaxPrms;
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

ux.postToPath = function(url) {
	var ajaxPrms = ux.ajaxConstructCallParam(url,
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
	ux.setHiddenValues(evp.uRef, evp.uVal);
	ux.ajaxCallWithJSONResp(evp.uTrg, evp);
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
		window.open(url, "_blank");
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
			var elem = _id(trgNms[i]);
			if (elem) {
				if (!elem.disabled && elem.type != "button") {
					if (elem.type == "checkbox" || elem.type == "radio") {
						elem.checked = false;
					} else if (elem.type == "select-multiple") {
						for (var k = 0; k < elem.options.length; k++) {
							elem.options[k].selected = "";
						}
					} else {
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
	var refElem = uEv.uTrg;
	if (refElem.type == "checkbox") {
		var trgNms = uEv.evp.uRef;
		if (trgNms) {
			for (var i = 0; i < trgNms.length; i++) {
				var elems = _name(trgNms[i]);
				if (elems) {
					for (var j = 0; j < elems.length; j++) {
						var elem = elems[j];
						if (elem.type == "checkbox") {
							elem.checked = refElem.checked;
						}
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

ux.setFocus = function(id) {
	var elem = _id(id);
	if (elem) {
		elem.focus();
	}
}

ux.setCheckedPatternValue = function(prm) {
	if (prm) {
		var patternValue = "";
		if (prm.chkIds && prm.fillValues) {
			var chkIds = prm.chkIds;
			var fillValues = prm.fillValues;
			var appendSym = false;
			for (var i = 0; i < chkIds.length; i++) {
				var elem = _id(chkIds[i]);
				if (elem && elem.checked) {
					if (appendSym) {
						patternValue += ",";
					} else {
						appendSym = true;
					}
					patternValue += fillValues[i];
				}
			}
		}

		var elem = _id(prm.fillId);
		var oldValue = elem.value;
		elem.value = patternValue;
		if (patternValue != oldValue) {
			ux.fireEvent(elem, "change", true);
		}
	}
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
	var actElem = _id(rgp.pId);
	rgp.uTargetPnlId = ux.docPopupId;
	ux.attachEventHandler(actElem, "mousedown", ux.dragDropEngage, rgp);
}

/** Remote document view panel */
ux.loadRemoteDocViewPanel = function(rgp) {
	var evp = {};
	evp.uViewer = rgp.pWinPgNm;
	evp.uSessionID = ux.remoteviewsessions[evp.uViewer];
	evp.uURL = rgp.pRemoteURL;
	evp.uLoginId = rgp.pRemoteLoginId;
	evp.uUserName = rgp.pRemoteUserName;
	evp.uRole = rgp.pRemoteRoleCode;
	evp.uBranch = rgp.pRemoteBranchCode;
	evp.uGlobal = rgp.pRemoteGlobalFlag;
	ux.postCommit(evp);
}

/** ******************* CONTAINERS ************************** */
/** Desktop Type 2 */
ux.rigDesktopType2 = function(rgp) {
	var gripToRig = _id(rgp.pGripId);
	if (gripToRig) {
		var evp = {};
		evp.uRigMenu = _id(rgp.pMenuId);
		evp.uOpen = rgp.pOpen;
		ux.attachEventHandler(gripToRig, "click", ux.collapseGripClickHandler,
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
			var mItem = rgp.pMenuItems[i];
			var evp = {};
			evp.uMain = mItem.main;
			evp.uOpenPath = mItem.actionPath;
			ux.attachEventHandler(_id(mItem.id), "click", ux.menuOpenPath,
					evp);
		}
	}
	
	if (rgp.pVertical) {
		var sliderHeight = 0;
		var maxSliderWidth = 0;
		for (var i = 0; i < menuWinIds.length; i++) {
			var rect = ux.boundingRect(_id(menuWinIds[i]));
			sliderSections[i] = sliderHeight;
			sliderHeight += rect.height;

			var sliderWidth = rect.width;
			if (maxSliderWidth < sliderWidth) {
				maxSliderWidth = sliderWidth;
			}
		}

		var slider = _id(rgp.pSliderId);
		var navMenuRect = ux.boundingRect(_id(rgp.pNavId));
		slider.style.height = navMenuRect.height + "px";
		slider.style.visibility = "visible";

		// Attach event handlers to scroll buttons if slider length is longer
		// than slider window
		var upBtn = _id(rgp.pBackBtnId);
		var downBtn = _id(rgp.pForwardBtnId);
		var rect = ux.boundingRect(_id(rgp.pSliderWinId));
		if (rect.height < sliderHeight) {
			var evp = {};
			evp.rate = rgp.pRate;
			evp.sliderIndex = 0;
			evp.sliderHeight = sliderHeight;
			evp.sliderSections = sliderSections;
			evp.sliderWinId = rgp.pSliderWinId;
			evp.menuCount = menuWinIds.length;
			evp.sliderTranslation = new Transformation(slider,
					rgp.pStepRate);
			ux.attachEventHandler(upBtn, "click",
					ux.menuSliderUpBtnClickHandler, evp);
			ux.attachEventHandler(downBtn, "click",
					ux.menuSliderDownBtnClickHandler, evp);
		} else {
			upBtn.style.display = "none";
			downBtn.style.display = "none";
		}
	} else {
		var sliderWidth = 0;
		var maxSliderHeight = 0;
		for (var i = 0; i < menuWinIds.length; i++) {
			var rect = ux.boundingRect(_id(menuWinIds[i]));
			sliderSections[i] = sliderWidth;
			sliderWidth += rect.width;

			var sliderHeight = rect.height;
			if (maxSliderHeight < sliderHeight) {
				maxSliderHeight = sliderHeight;
			}
		}

		var slider = _id(rgp.pSliderId);
		var navMenuRect = ux.boundingRect(_id(rgp.pNavId));
		slider.style.width = navMenuRect.width + "px";
		slider.style.visibility = "visible";

		if (maxSliderHeight > 0) {
			var menuBody = _id(rgp.pId);
			maxSliderHeight += rgp.pSliderGap;
			menuBody.style.height = maxSliderHeight + "px";
		}

		// Attach event handlers to scroll buttons if slider length is longer
		// than slider window
		var leftBtn = _id(rgp.pBackBtnId);
		var rightBtn = _id(rgp.pForwardBtnId);
		var rect = ux.boundingRect(_id(rgp.pSliderWinId));
		if (rect.width < sliderWidth) {
			var evp = {};
			evp.rate = rgp.pRate;
			evp.sliderIndex = 0;
			evp.sliderWidth = sliderWidth;
			evp.sliderSections = sliderSections;
			evp.sliderWinId = rgp.pSliderWinId;
			evp.menuCount = menuWinIds.length;
			evp.sliderTranslation = new Transformation(slider,
					rgp.pStepRate);
			ux.attachEventHandler(leftBtn, "click",
					ux.menuSliderLeftBtnClickHandler, evp);
			ux.attachEventHandler(rightBtn, "click",
					ux.menuSliderRightBtnClickHandler, evp);
		} else {
			leftBtn.style.display = "none";
			rightBtn.style.display = "none";
		}
	}

	if (rgp.pSelId) {
		var evp = ux.newEvPrm(rgp);
		evp.uSelId = rgp.pSelId;
		evp.uCurSelId = rgp.pCurSelId;
		evp.uCmd = id + "->switchState";
		evp.uPanels = [ id ];
		evp.uRef = [ rgp.pCurSelId ];
		ux.attachEventHandler(_id(rgp.pSelId), "change",
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

ux.menuSliderUpBtnClickHandler = function(uEv) {
	var evp = uEv.evp;
	if (evp.sliderIndex > 0) {
		evp.sliderTranslation.linear(0, 0,
				-evp.sliderSections[evp.sliderIndex],
				-evp.sliderSections[--evp.sliderIndex], evp.rate);
	}
}

ux.menuSliderDownBtnClickHandler = function(uEv) {
	var evp = uEv.evp;

	// Shift only if slider section is hidden
	var rect = ux.boundingRect(_id(evp.sliderWinId));
	var sliderWinHeight = rect.height;
	if (sliderWinHeight < (evp.sliderHeight - evp.sliderSections[evp.sliderIndex])) {
		if (evp.sliderIndex < (evp.menuCount - 1)) {
			evp.sliderTranslation.linear(0, 0,
					-evp.sliderSections[evp.sliderIndex],
					-evp.sliderSections[++evp.sliderIndex], evp.rate);
		}
	}
}

ux.menuSliderLeftBtnClickHandler = function(uEv) {
	var evp = uEv.evp;
	if (evp.sliderIndex > 0) {
		evp.sliderTranslation.linear(
				-evp.sliderSections[evp.sliderIndex],
				-evp.sliderSections[--evp.sliderIndex], 0, 0, evp.rate);
	}
}

ux.menuSliderRightBtnClickHandler = function(uEv) {
	var evp = uEv.evp;

	// Shift only if slider section is hidden
	var rect = ux.boundingRect(_id(evp.sliderWinId));
	var sliderWinWidth = rect.width;
	if (sliderWinWidth < (evp.sliderWidth - evp.sliderSections[evp.sliderIndex])) {
		if (evp.sliderIndex < (evp.menuCount - 1)) {
			evp.sliderTranslation.linear(
					-evp.sliderSections[evp.sliderIndex],
					-evp.sliderSections[++evp.sliderIndex], 0, 0,
					evp.rate);
		}
	}
}

/** ******************* PANELS ******************* */

/** Content panel */
ux.rigContentPanel = function(rgp) {
	ux.cntHintId = rgp.pHintPanelId;
	ux.cntTabCloseId = rgp.pCloseImgId;
	ux.cntSavePath = rgp.pSavePath;

	if (rgp.pImmURL) {
		ux.postToPath(rgp.pImmURL);
	} else {
		var currIdx = rgp.pCurIdx;
		for(var i = 0; i < rgp.pContent.length; i++) {
			var cnt = rgp.pContent[i];
			if (i != currIdx) {
				var evp = {};
				evp.uOpenPath = cnt.openPath;
				ux.attachEventHandler(_id(cnt.tabId), "click", ux.contentOpen,
						evp);
			}
			
			if (i > 0) {
				var evp = {};
				evp.uURL = cnt.closePath;
				ux.attachEventHandler(_id(cnt.tabImgId), "click", ux.post,
						evp);
			}
		}
		
	}
}

ux.contentOpen  = function(uEv) {
	var evp = uEv.evp;
	var path = evp.uOpenPath;
	evp.uRef = [];
	evp.uViewer = null;
	evp.uSessionID = null;
	if (ux.cntSaveList && ux.cntSavePath) {
		ux.cntOpenPath = evp.uOpenPath;
		path = ux.cntSavePath;
		evp.uRef = ux.cntSaveList;
		if (ux.cntSaveRemoteView) {
			evp.uSessionID = ux.cntSaveRemoteView.sessionID;
			evp.uViewer = ux.cntSaveRemoteView.view;
		}
	}
	
	evp.uURL = path;
	ux.post(uEv);
}

/** Fixed content panel */
ux.rigFixedContentPanel = function(rgp) {
	ux.cntHintId = rgp.pHintPanelId;
	ux.busyIndicator = rgp.pBusyIndId;
}

/** Split panel */
ux.rigSplitPanel = function(rgp) {
	var evp = {};
	evp.uCtrlId = rgp.pCtrlId;
	evp.uMinorId = rgp.pMinorId;
	evp.uMinorScrId = rgp.pMinorScrId;
	evp.uMajorScrId = rgp.pMajorScrId;
	evp.uMax = rgp.pMax;
	evp.uMin = rgp.pMin;
	evp.uVert = rgp.pVert;
	ux.attachEventHandler(_id(rgp.pCtrlId), "mousedown", ux.splitEngage,
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
	ux.attachDirectEventHandler(document, "mouseup", ux.splitDisengage);
	ux.attachDirectEventHandler(document, "mousemove", ux.splitAction);
}

ux.splitDisengage = function(ev) {
	ux.detachDirectEventHandler(document, "mousemove", ux.splitAction);
	ux.detachDirectEventHandler(document, "mouseup", ux.splitDisengage);
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

				ux.attachEventHandler(_id(rgp.pTabCapIdList[i]), "click",
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

			ux.attachEventHandler(_id(rgp.pHeaderIdBase + i), "click",
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
	ux.attachEventHandler(filterSel1, "change", ux.post, evPrmSel);
	ux.attachEventHandler(filterSel2, "change", ux.post, evPrmSel);

	var assnBoxRigBtns = function(rgp, assnBtnId, assnAllBtnId,
			unassnSelId) {
		var unassnSel = _id(unassnSelId);
		var assnBtn = _id(assnBtnId);
		var assnAllBtn = _id(assnAllBtnId);
		unassnSel.disabled = false;
		assnBtn.disabled = true;
		assnAllBtn.disabled = !rgp.pEditable
				|| unassnSel.options.length == 0;
		var evp = ux.newEvPrm(rgp);
		evp.uRef = [ unassnSelId ];
		evp.uPanels = [ rgp.pContId ];
		ux.attachEventHandler(assnBtn, "click", ux.post, evp);

		if (!assnAllBtn.disabled) {
			evp = ux.newEvPrm(rgp);
			evp.uRef = [ unassnSelId ];
			evp.uPanels = [ rgp.pContId ];
			ux.attachEventHandler(assnAllBtn, "click", function(uEv) {
				for (var i = 0; i < unassnSel.options.length; i++) {
					unassnSel.options[i].selected = true;
				}
				ux.post(uEv);
			}, evp);

			evp = {};
			ux.attachEventHandler(unassnSel, "change", function(uEv) {
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
			rgp.pUnassnSelId);
	assnBoxRigBtns(rgp, rgp.pUnassnBtnId, rgp.pUnassnAllBtnId,
			rgp.pAssnSelId);
}

/** Date Field */
ux.rigDateField = function(rgp) {
	var id = rgp.pId;
	var df = _id(id);
	df.uRigPrm = rgp;
	
	ux.dateSetupScroll(rgp, "decy_", "year_", -1);
	ux.dateSetupScroll(rgp, "incy_", "year_", 1);
	ux.dateSetupScroll(rgp, "decm_", "mon_", -1);
	ux.dateSetupScroll(rgp, "incm_", "mon_", 1);

	// Setup 'Clear' button
	ux.popupWireClear(rgp, "btnc_" + id, [ id ]);

	var evp = {};
	evp.uId = id;
	evp.uShortDayNm = rgp.pShortDayNm;
	evp.uLongMonthNm = rgp.pLongMonthNm;
	evp.uDayClass = rgp.pDayClass;
	evp.uCurrClass = rgp.pCurrClass;
	evp.uTodayClass = rgp.pTodayClass;
	df.evp = evp;
	ux.dateSetCurrent(id);

	// Setup 'Today' button
	ux.attachEventHandler(_id("btnt_" + id), "click",
			ux.dateTodayClickHandler, evp);

	// Populate calendar
	ux.datePopulateCalendar(evp);
}

ux.dateTodayClickHandler = function(uEv) {
	var id = uEv.evp.uId;
	var today = new Date();
	_id("day_" + id).value = today.getDate();
	_id("mon_" + id).value = today.getMonth() + 1;
	_id("year_" + id).value = today.getFullYear();
	ux.dateSetCurrent(id);
	ux.setPatternValue(_id(id).uRigPrm);
	ux.hidePopup(null);
	ux.datePopulateCalendar(uEv.evp);
}

ux.dateSetCurrent = function(id) {
	var df = _id(id);
	df.uDay = parseInt(_id("day_" + id).value);
	df.uMonth = parseInt(_id("mon_" + id).value) - 1;
	df.uYear = parseInt(_id("year_" + id).value);
}

ux.dateSetupScroll = function(rgp, scrIdPrefix, valueIdPrefix, step) {
	var id = rgp.pId;
	var evp = {};
	evp.uId = id;
	evp.uShortDayNm = rgp.pShortDayNm;
	evp.uLongMonthNm = rgp.pLongMonthNm;
	evp.uDayClass = rgp.pDayClass;
	evp.uCurrClass = rgp.pCurrClass;
	evp.uTodayClass = rgp.pTodayClass;
	evp.uValueIdPrefix = valueIdPrefix;
	evp.uStep = step;
	ux.attachEventHandler(_id(scrIdPrefix + id), "click",
			ux.dateScrollHandler, evp);
}

ux.dateScrollHandler = function(uEv) {
	var evp = uEv.evp;
	var elem = _id(evp.uValueIdPrefix + evp.uId);
	if (evp.uValueIdPrefix == "mon_") {
		var month = parseInt(elem.value);
		var yearChg = false;
		if (evp.uStep > 0) {
			if (month >= evp.uLongMonthNm.length) {
				elem.value = 0;
				yearChg = true;
			}
		} else {
			if (month <= 1) {
				elem.value = evp.uLongMonthNm.length + 1;
				yearChg = true;
			}
		}

		if (yearChg) {
			var yearElem = _id("year_" + evp.uId);
			yearElem.value = parseInt(yearElem.value) + evp.uStep;
		}
	}
	elem.value = parseInt(elem.value) + evp.uStep;
	ux.datePopulateCalendar(evp);
}

ux.datePopulateCalendar = function(evp) {
	var id = evp.uId;
	var month = parseInt(_id("mon_" + id).value) - 1;
	var year = parseInt(_id("year_" + id).value);

	// Display month year on header
	var displayElem = _id("disp_" + id);
	displayElem.innerHTML = evp.uLongMonthNm[month] + "&nbsp;" + year;

	// Initialize variables and generate calendar HTML
	var firstDay = new Date(year, month, 1).getDay();
	var nextMonth = new Date(year, month + 1, 1);
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

	var df = _id(id);
	var currentDay = df.uDay;
	if (!(month == df.uMonth && year == df.uYear)) {
		currentDay = 0;
	}

	var calendarHtml = "<table>";
	calendarHtml += "<tr>";
	for (var i = 0; i < 7; i++) {
		calendarHtml += "<th>";
		calendarHtml += evp.uShortDayNm[i];
		calendarHtml += "</th>";
	}
	calendarHtml += "</tr>";
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
					var dayClass = evp.uDayClass;
					if (dayCount == currentDay) {
						dayClass = evp.uCurrClass;
					}

					if (dayCount == today) {
						dayClass = evp.uTodayClass;
					}
					calendarHtml += "<span class=\"" + dayClass
							+ "\" onclick=\"ux.dateCalendarDayClick('" + id
							+ "'," + dayCount + ");\">" + dayCount + "</span>";
					dayCount++;
				} else {
					calendarHtml += "&nbsp;";
				}
			}
			calendarHtml += "</td>";
		}
		calendarHtml += "</tr>";
		rowCount++;
	}
	calendarHtml += "</table>";
	_id("cont_" + id).innerHTML = calendarHtml;
}

ux.dateCalendarDayClick = function(id, dayCount) {
	_id("day_" + id).value = dayCount;
	ux.dateSetCurrent(id);
	ux.setPatternValue(_id(id).uRigPrm);
	ux.hidePopup(null);
	ux.datePopulateCalendar(_id(id).evp);
}

/** FileAttachment */
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
			ux.attachEventHandler(fileElem, "change", ux.post, evp);

			// Attach
			evp = {};
			evp.fileId = fileElem.id;
			ux.attachEventHandler(_id(attachId + idx), "click",
					ux.attachFileClickHandler, evp);

			// View
			if (rgp.pViewURL) {
				evp = {};
				evp.uURL = rgp.pViewURL;
				evp.uPanels = [ rgp.pContId ];
				ux.attachEventHandler(_id(viewId + idx), "click", ux.post, evp);
			} else {
				evp = ux.newEvPrm(rgp);
				evp.uCmd = id + "->view";
				evp.uPanels = [ rgp.pContId ];
				ux.attachEventHandler(_id(viewId + idx), "click", ux.post, evp);
			}

			// Remove
			evp = ux.newEvPrm(rgp);
			evp.uCmd = id + "->detach";
			evp.uPanels = [ rgp.pContId ];
			ux.attachEventHandler(_id(remId + idx), "click", ux.post, evp);
		}
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
		ux.attachEventHandler(dBtn, "click", ux.post, evp);
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
				var evp = {};
				ux.attachEventHandler(btnElem, "click", function(uEv) {
					fileElem.click();
				}, evp);
			}
		}

		var spanElem = _id(rgp.pSpanId);
		if (spanElem) {
			var evp = {};
			evp.uMaxSize = rgp.pMaxSize;
			evp.uMaxMsg = rgp.pMaxMsg;
			ux.attachEventHandler(fileElem, "change", function(uEv) {
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
					var evp = {};
					evp.uURL = rgp.pUploadURL;
					evp.uRef = [ id ];
					evp.uPanels = [ rgp.pContId ];
					ux.attachEventHandler(btnUpElem, "click", ux.post, evp);

					evp = {};
					ux.attachEventHandler(fileElem, "change", function(uEv) {
						if (fileElem.value) {
							btnUpElem.disabled = false;
						} else {
							btnUpElem.disabled = true;
						}
					}, evp);
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
				var link = linkCat.links[j];
				var evp = {};
				evp.uURL = linkCat.pURL;
				evp.uSendTrg = link.pCode;
				ux.attachEventHandler(_id(link.pId), "click", function(uEv) {
					ux.post(uEv);
				}, evp);
			}
		}
	}
}

/** Money field */
ux.rigMoneyField = function(rgp) {
	var mfCom = {};
	mfCom.uId = rgp.pId;
	mfCom.uNorm = rgp.pNormCls;
	mfCom.uSel = rgp.pSelCls;
	mfCom.uSelIdx = rgp.pKeyIdx;
	mfCom.uOldSelIdx = rgp.pKeyIdx;
	mfCom.uICnt = rgp.pICnt;
	mfCom.uKeys = rgp.pKeys;
	mfCom.uOptIds = rgp.pLabelIds;
	mfCom.uLabels = ux.extractLabels(rgp.pLabelIds, true);
	mfCom.uLastKeyHit = Date.now();
	mfCom.uHidObj = _id(rgp.pId);
	mfCom.uFacObj = _id(rgp.pFacId);
	mfCom.uFrmObj = _id(rgp.pFrmId);
	mfCom.uListObj = _id(rgp.pLstId);
	mfCom.uBtnObj = _id(rgp.pBtnId);
	mfCom.uSelHandler = ux.mfSelectOpt;
	mfCom.uFire = true;
	
	// Wire facade
	var evp = {};
	evp.uCom = mfCom;
	ux.attachEventHandler(mfCom.uFacObj, "change", ux.mfAmountChange, evp);
	
	// Wire section
	ux.listWirePopFrame(mfCom, rgp);
}

ux.mfSelectOpt = function(mfCom, index, select) {
	var aElem = null;
	var aVal = null;
	if (index >= 0) {
		aElem = _id(mfCom.uOptIds[index]);
		aVal = mfCom.uKeys[index];
	}

	if(mfCom.uOldSelIdx != index) {
		var oldElem = null;
		if (mfCom.uOldSelIdx >= 0) {
			oldElem = _id(mfCom.uOptIds[mfCom.uOldSelIdx]);
		}
		
		aElem.className = mfCom.uSel;
		if (oldElem) {
			oldElem.className = mfCom.uNorm;
		}

		mfCom.uOldSelIdx = index;
		
		if (!select) {
			// Scroll to element
			ux.listScrollLabel(mfCom, aElem);
		}
	}
	
	if (select) {
		if (mfCom.uSelIdx != index) {
			mfCom.uBtnObj.innerHTML = aVal;
			mfCom.uSelIdx = index;
			ux.mfSetMoneyVal(mfCom);
		}
	}
}


ux.mfAmountChange = function(uEv) {
	var evp = uEv.evp;
	ux.mfSetMoneyVal(evp.uCom);
}

ux.mfSetMoneyVal = function(mfCom) {
	if (mfCom.uFacObj.value) {
		mfCom.uHidObj.value = mfCom.uBtnObj.innerHTML + " " + mfCom.uFacObj.value;
	} else {
		mfCom.uHidObj.value = "";
	}

	if (mfCom.uFire) {
		ux.fireEvent(mfCom.uHidObj, "change", true);			
	}
}

ux.moneyFieldOnShow = function(rgp) {
	ux.setFocus(rgp.pFrmId);
}

/** Multi select */
ux.rigMultiSelect = function(rgp) {
	var id = rgp.pId;
	var msCom = {};
	msCom.uId = id;
	msCom.uNorm = rgp.pNormCls;
	msCom.uSel = rgp.pSelCls;
	msCom.uOptIds = rgp.pLabelIds;
	msCom.uLabels = ux.extractLabels(rgp.pLabelIds, true);
	msCom.uLastKeyHit = Date.now();
	msCom.uSelObj = _id(id);
	msCom.uFrmObj = _id(rgp.pFrmId);
	msCom.uListObj = _id(rgp.pLstId);
	msCom.uStart = -1;
	
	// Wire section
	var evp = {};
	evp.uCom = msCom;
	evp.uHitHandler = ux.msKeydownHit;
	ux.attachEventHandler(msCom.uFrmObj, "click", ux.focusOnClick, evp);
	ux.attachEventHandler(msCom.uFrmObj, "keydown", ux.listSearchKeydown, evp);

	// Select
	for (var i = 0; i < rgp.pICnt; i++) {
		evp = {};
		evp.uIndex = i;
		evp.uCom = msCom;
		var aElem = _id(rgp.pLabelIds[i]);
		if (aElem) {
			ux.attachEventHandler(aElem, "click", ux.msSelectClick, evp);
		}
	}
}

ux.msKeydownHit = function(msCom) {
	if (msCom.uIndexes && msCom.uIndexes.length > 0) {
		var optIndex = msCom.uIndexes[0]
		ux.msUnSelectAllOpt(msCom);
		ux.msSelectOpt(msCom, optIndex, true);
		msCom.uStart = optIndex;
	}
}

ux.msSelectClick = function(uEv) {
	var evp = uEv.evp;
	var msCom = evp.uCom;

	if (uEv.shiftKey && msCom.uStart >= 0) {
		var start = msCom.uStart;
		var stop = evp.uIndex;
		if (start > stop) {
			start = stop;
			stop = msCom.uStart;
		}

		ux.msUnSelectAllOpt(msCom);
		while (start <= stop) {
			ux.msSelectOpt(msCom, start, false);
			start++;
		}
	} else {
		if (!uEv.ctrlKey) {
			ux.msUnSelectAllOpt(msCom);
		}

		ux.msSelectOpt(msCom, evp.uIndex, false);
		msCom.uStart = evp.uIndex;
	}
}

ux.msSelectOpt = function(msCom, index, scroll) {
	var aElem = _id(msCom.uOptIds[index]);
	aElem.className = msCom.uSel;
	msCom.uSelObj.options[index].selected = true;
	
	if (scroll) {
		ux.listScrollLabel(msCom, aElem);
	}

	ux.fireEvent(msCom.uSelObj, "change", true);
}

ux.msUnSelectAllOpt = function(msCom) {
	var length = msCom.uOptIds.length;
	var selObj = msCom.uSelObj;
	for (var i = 0; i < length; i++) {
		if (selObj.options[i].selected) {
			var aElem = _id(msCom.uOptIds[i]);
			if (aElem) {
				aElem.className = msCom.uNorm;
			}
			selObj.options[i].selected = false;
		}
	}
}

/** Photo Upload */
ux.rigPhotoUpload = function(rgp) {
	if (rgp.pEditable) {
		var fileElem = _id(rgp.pFileId);
		var evp = ux.newEvPrm(rgp);
		evp.uPanels = [ rgp.pContId ];
		ux.attachEventHandler(fileElem, "change", ux.post, evp);

		var imgElem = _id(rgp.pImgId);
		evp = {};
		ux.attachEventHandler(imgElem, "click", function(uEv) {
			fileElem.click();
		}, evp);
	}
}

/** Search Field */
ux.rigSearchField = function(rgp) {
	var id = rgp.pId;

	// Filter
	var fElem = _id(rgp.pFilId);
	if (fElem) {
		var evp = ux.newEvPrm(rgp);
		evp.uCmd = id + "->search";
		evp.uReqTrg = true;
		ux.attachEventHandler(fElem, "enter", ux.post, evp);
	}

	// Result
	ux.searchWireResult(rgp);

	// Clear button
	ux.popupWireClear(rgp, rgp.pClrId, [ id, rgp.pFacId ]);

	// Cancel button
	ux.popupWireCancel(rgp.pCanId);
}

ux.searchWireResult = function(rgp) {
	// Select
	for (var i = 0; i < rgp.pICnt; i++) {
		var evp = {};
		evp.uId = rgp.pId;
		evp.uFacId = rgp.pFacId;
		evp.uKey = rgp.pKeys[i];
		var aElem = _id(rgp.pLabelIds[i]);
		if (aElem) {
			ux.attachEventHandler(aElem, "click", ux.searchSelect, evp);
		}
	}
}

ux.searchOnShow = function(rgp) {
	// Focus on filter input
	ux.setFocus(rgp.pFilId);
}

ux.searchSelect = function(uEv) {
	var evp = uEv.evp;
	var hElem = _id(evp.uId);
	if (hElem) {
		hElem.value = evp.uKey;
		ux.fireEvent(hElem, "change", true);
	}

	var tElem = _id(evp.uFacId);
	if (tElem) {
		var selOpt = uEv.uTrg;
		tElem.value = selOpt.innerHTML;
	}

	ux.hidePopup(null);
}

/** Options Text Area */
ux.rigOptionsTextArea = function(rgp) {
	var otaCom = {};
	otaCom.uId = rgp.pId;
	otaCom.uNorm = rgp.pNormCls;
	otaCom.uSel = rgp.pSelCls;
	otaCom.uSelIdx = 0;
	otaCom.uOldSelIdx = 0;
	otaCom.uICnt = rgp.pICnt;
	otaCom.uKeys = rgp.pKeys;
	otaCom.uOptIds = rgp.pLabelIds;
	otaCom.uLabels = ux.extractLabels(rgp.pLabelIds, true);
	otaCom.uLastKeyHit = Date.now();
	otaCom.uFrmObj = _id(rgp.pFrmId);
	otaCom.uListObj = _id(rgp.pLstId);
	otaCom.uSelHandler = ux.otaSelectOpt;
	
	// Init
	var elem = _id(rgp.pId);
	if (elem) {
		var evp = {};
		evp.uTrg = elem;
		evp.popupId=rgp.pPopupId;
		evp.frameId=rgp.pId;
		evp.stayOpenForMillSec = 0;
		evp.showHandler = ux.optionsTextAreaOnShow;
		evp.showParam=rgp.pFrmId;
		ux.attachEventHandler(elem, "keypress", ux.otaTxtKeypress,
				evp);
		ux.attachEventHandler(elem,  "keydown", ux.otaTxtKeydown,
				evp);
		
		if (rgp.pScrEnd) {
			elem.scrollTop = elem.scrollHeight;
		}
	}
	
	// Wire section
	ux.listWirePopFrame(otaCom, rgp);
}

ux.otaTxtKeypress = function(uEv) {

}

ux.otaTxtKeydown = function(uEv) {
	if (uEv.shiftKey && uEv.uKeyCode == '32') {
		ux.doOpenPopup(uEv.evp);
		uEv.uStop();
		return;
	}
	
	var elem = uEv.uTrg;
	var txt = elem.value;
	var pos = ux.getCaretPosition(elem);
	if (pos.start != pos.end) {
		if (uEv.uKeyCode == '8' || uEv.uKeyCode == '46') {
			pos.start = ux.otaTokenStart(txt, pos.start);
			pos.end = ux.otaTokenEnd(txt, pos.end);
			elem.value = txt.substring(0, pos.start) + txt.substring(pos.end);
			ux.setCaretPosition(elem, pos.start, pos.start);
			uEv.uStop();
		}
	} else {
		if (uEv.uKeyCode == '8' || uEv.uKeyCode == '46') {
			var i = pos.start;
			if (uEv.uKeyCode == '8') {
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
					elem.value = txt.substring(0, pos.start) + txt.substring(pos.end);
					ux.setCaretPosition(elem, pos.start, pos.start);
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

ux.otaSelectOpt = function(otaCom, idx, sel) {
	var aElem = _id(otaCom.uOptIds[idx]);
	var aVal = otaCom.uKeys[idx];

	if(otaCom.uOldSelIdx != idx) {
		var oldElem = _id(otaCom.uOptIds[otaCom.uOldSelIdx]);
		aElem.className = otaCom.uSel;
		if (oldElem) {
			oldElem.className = otaCom.uNorm;
		}
		otaCom.uOldSelIdx = idx;
		
		if (!sel) {
			ux.listScrollLabel(otaCom, aElem);
		}
	}

	if (sel) {
		otaCom.uSelIdx = idx;
		var txtElem = _id(otaCom.uId);
		var pos = ux.getCaretPosition(txtElem);
		var string = txtElem.value;
		var token = "{" + aVal + "}";
		var spos = pos.start + token.length;
		string = string.substring(0, pos.start) + token + string.substring(pos.end);
		txtElem.value = string;
		ux.setCaretPosition(txtElem, spos, spos);
		txtElem.focus();
	}
}


ux.optionsTextAreaOnShow = function(frmId) {
	ux.setFocus(frmId);
}

/** Single Select */
ux.rigSingleSelect = function(rgp) {
	var ssCom = {};
	ssCom.uId = rgp.pId;
	ssCom.uNorm = rgp.pNormCls;
	ssCom.uSel = rgp.pSelCls;
	ssCom.uSelIdx = rgp.pKeyIdx;
	ssCom.uOldSelIdx = rgp.pKeyIdx;
	ssCom.uIsBlank = rgp.pIsBlank;
	ssCom.uICnt = rgp.pICnt;
	ssCom.uKeys = rgp.pKeys;
	ssCom.uOptIds = rgp.pLabelIds;
	ssCom.uLabels = ux.extractLabels(rgp.pLabelIds, true);
	ssCom.uLastKeyHit = Date.now();
	ssCom.uHidObj = _id(rgp.pId);
	ssCom.uFacObj = _id(rgp.pFacId);
	ssCom.uFrmObj = _id(rgp.pFrmId);
	ssCom.uListObj = _id(rgp.pLstId);
	ssCom.uBlankObj = _id(rgp.pBlnkId);
	ssCom.uSelHandler = ux.ssSelectOpt;
	
	// Init
	if (ssCom.uSelIdx < 0 && !ssCom.uIsBlank && ssCom.uICnt > 0) {
		ux.ssSelectOpt(ssCom, 0, true);
	}
	ssCom.uFire = true;
	
	// Wire section
	ux.listWirePopFrame(ssCom, rgp);
}

ux.ssSelectOpt = function(ssCom, index, select) {
	var aElem = ssCom.uBlankObj;
	var aVal = null;
	if (index >= 0) {
		aElem = _id(ssCom.uOptIds[index]);
		aVal = ssCom.uKeys[index];
	}

	if(ssCom.uOldSelIdx != index) {
		var oldElem = ssCom.uBlankObj;
		if (ssCom.uOldSelIdx >= 0) {
			oldElem = _id(ssCom.uOptIds[ssCom.uOldSelIdx]);
		}
		
		aElem.className = ssCom.uSel;
		if (oldElem) {
			oldElem.className = ssCom.uNorm;
		}
		ssCom.uOldSelIdx = index;
		
		if (!select) {
			ux.listScrollLabel(ssCom, aElem);
		}
	}
	
	if (select) {
		if (ssCom.uSelIdx != index) {
			var txt = aElem.innerHTML;
			if (txt == "&nbsp;") {
				txt = "";
			}

			ssCom.uFacObj.value = txt;
			ssCom.uHidObj.value = aVal;
			ssCom.uSelIdx = index;

			if (ssCom.uFire) {
				ux.fireEvent(ssCom.uHidObj, "change", true);			
			}
		}
	}
}


ux.singleSelectOnShow = function(rgp) {
	ux.setFocus(rgp.pFrmId);
}


/** Text Area */
ux.rigTextArea = function(rgp) {
	var elem = _id(rgp.pId);
	if (elem && rgp.pScrEnd) {
		elem.scrollTop = elem.scrollHeight;
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
		ux.attachEventHandler(tblBody, "scroll", function(uEv) {
			tblHeader.style.left = "-" + tblBody.scrollLeft + "px";
		}, rgp);

		// Show window content (Used this approach because of IE and Firefox)
		ux.tableResizeHeight(rgp);
		ux.registerResizeFunc(id, ux.tableResizeHeight, rgp);
	}

	if (tblToRig.rows && rgp.pSelectable) {
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
				var evp = {};
				var tRow = tblToRig.rows[i];
				if (!tblToRig.uFirstRow) {
					tblToRig.uFirstRow = tRow;
				}
				tRow.uIndex = i - startIndex;
				tRow.uClassName = tRow.className;
				evp.uRigTbl = tblToRig;
				evp.uRigRow = tRow;
				ux.attachEventHandler(tRow, "click", ux.tableRowClickHandler,
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
		ux.attachEventHandler(_id(rgp.pItemPerPgCtrlId), "change",
				ux.post, evp);
	}

	if (rgp.pMultiSel) {
		tblToRig.uVisibleSel = rgp.pVisibleSel;
		tblToRig.uHiddenSel = rgp.pHiddenSel;
		tblToRig.uMultiSelDepList = rgp.pMultiSelDepList;
		tblToRig.uItemCount = rgp.pItemCount;
		tblToRig.uSelAllId = rgp.pSelAllId;

		ux.setDisabledById(tblToRig.uMultiSelDepList,
				(tblToRig.uVisibleSel + tblToRig.uHiddenSel) <= 0);

		var evp = {};
		evp.uRigTbl = tblToRig;
		ux.attachEventHandler(_id(rgp.pSelAllId), "click",
				ux.tableSelAllClick, evp);

		evp = {};
		evp.uRef = [ rgp.pSelGrpId ];
		ux.attachEventHandler(_id(rgp.pSelAllId),
				"click", ux.setAllChecked, evp);

		var selBoxes = _name(rgp.pSelGrpId);
		for (var i = 0; i < selBoxes.length; i++) {
			evp = {};
			evp.uRigTbl = tblToRig;
			ux.attachEventHandler(selBoxes[i], "click", ux.tableMultiSelClick,
					evp);
		}
	}

	if (rgp.pSortable) {
		if (rgp.pSortColList) {
			for (var i = 0; i < rgp.pSortColList.length; i++) {
				var colInfo = rgp.pSortColList[i];
				var evp = ux.newEvPrm(rgp);
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
				ux.attachEventHandler(_id(imgId), "click",
						ux.tableSortClickHandler, evp);
			}
		}
	}

	if (rgp.pShiftable) {
		if (rgp.pItemCount > 0) {
			var evp = ux.getTableShiftParams(rgp, 0);
			ux.multipleAttachEventHandler(rgp.pShiftTopId, "click",
					ux.tableShiftClickHandler, evp);

			evp = ux.getTableShiftParams(rgp, 1);
			ux.multipleAttachEventHandler(rgp.pShiftUpId, "click",
					ux.tableShiftClickHandler, evp);

			evp = ux.getTableShiftParams(rgp, 2);
			ux.multipleAttachEventHandler(rgp.pShiftDownId, "click",
					ux.tableShiftClickHandler, evp);

			evp = ux.getTableShiftParams(rgp, 3);
			ux.multipleAttachEventHandler(rgp.pShiftBottomId, "click",
					ux.tableShiftClickHandler, evp);

			var viewIndex = 1 + parseInt(_id(rgp.pIdxCtrlId).value);
			if (rgp.pWindowed) {
				viewIndex--;
			}
			ux.fireEvent(tblToRig.rows[viewIndex], "click", true);
		}
	} else {
		if (rgp.pSelectable) {
			if (tblToRig.uFirstRow) {
				ux.fireEvent(tblToRig.uFirstRow, "click", true);
			}
		}
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
		ux.fireEvent(rowElem, "click", true);
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
	var selBox = uEv.uTrg;
	if (selBox.type == "checkbox") {
		var evp = uEv.evp;
		var rigTbl = evp.uRigTbl;
		if (selBox.checked == true) {
			rigTbl.uVisibleSel = rigTbl.uItemCount;
		} else {
			rigTbl.uVisibleSel = 0;
		}
		ux.tableDisableMultiSelElements(rigTbl);
	}
}

ux.tableMultiSelClick = function(uEv) {
	var selBox = uEv.uTrg;
	if (selBox.type == "checkbox") {
		var evp = uEv.evp;
		var rigTbl = evp.uRigTbl;
		if (selBox.checked == true) {
			rigTbl.uVisibleSel++;
		} else {
			rigTbl.uVisibleSel--;
		}
		ux.tableDisableMultiSelElements(rigTbl);
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

ux.tableDisableMultiSelElements = function(rigTbl) {
	var totalSel = rigTbl.uVisibleSel + rigTbl.uHiddenSel;
	ux.setDisabledById(rigTbl.uMultiSelDepList, totalSel <= 0);

	var selAllElem = _id(rigTbl.uSelAllId);
	if (rigTbl.uVisibleSel <= 0 && selAllElem.checked) {
		selAllElem.checked = false;
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
	ux.attachEventHandler(_id(id), "click", ux.tablePageNavClickHandler, evp);
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
	var id = rgp.pId;

	// Setup 'Set' button
	var evp = {};
	evp.uRigPrm = rgp;
	ux.attachEventHandler(_id("btns_" + id), "click", ux.timeSetBtnHandler,
			evp);

	// Setup 'Clear' button
	ux.popupWireClear(rgp, "btncl_" + id, [ id ]);

	// Setup 'Cancel' button
	ux.popupWireCancel("btncn_" + id);

	// Setup scroll buttons
	for (var i = 0; i < rgp.pFormat.length; i++) {
		var format = rgp.pFormat[i];
		var len = rgp.pPattern[i].length;
		if (format) {
			var target = "timi_" + id + i;
			var evp = {};
			evp.uRigPrm = rgp;
			evp.uTarget = target;
			if (format.list) {
				evp.uAltTarget = "h_" + target;
			}
			evp.uStep = 1;
			evp.uLen = len;
			evp.uFormat = format;
			ux.attachEventHandler(_id("btnat_" + id  + i), "click",
					ux.timeScrollHandler, evp);

			evp = {};
			evp.uRigPrm = rgp;
			evp.uTarget = target;
			if (format.list) {
				evp.uAltTarget = "h_" + target;
			}
			evp.uStep = -1;
			evp.uLen = len;
			evp.uFormat = format;
			ux.attachEventHandler(_id("btnst_" + id + i), "click",
					ux.timeScrollHandler, evp);
		}
	}
}

ux.timeSetBtnHandler = function(uEv) {
	ux.setPatternValue(uEv.evp.uRigPrm);
	ux.hidePopup(uEv);
}

ux.timeScrollHandler = function(uEv) {
	var evp = uEv.evp;
	var elem = _id(evp.uTarget);
	if (evp.uAltTarget) {
		elem = _id(evp.uAltTarget);
	}

	var nextValue = parseInt(elem.value) + evp.uStep;
	var format = evp.uFormat;
	if (evp.uStep > 0) {
		if (nextValue > format.max) {
			nextValue = format.min;
		}
	} else {
		if (nextValue < format.min) {
			nextValue = format.max;
		}
	}

	elem.value = ux.padLeft(nextValue + '', '0', evp.uLen);
	if (evp.uAltTarget) {
		elem = _id(evp.uTarget);
		elem.value = format.list[nextValue];
	}
}

/** Tree */
var TREE_CLICK = 0;
var TREE_DBLCLICK = 1;
var TREE_RTCLICK = 2;
var TREE_MENUCLICK = 3;

var treeDataMap = {};

ux.rigTreeExplorer = function(rgp) {
	var id = rgp.pId;
	var selList = [];
	if (rgp.pItemList) {
		var pItemList = rgp.pItemList;
		var selObj = _id(rgp.pSelItemId);
		var evpEq = {};
		evpEq.uPanels = [ rgp.pContId ];
		evpEq.uRef = [ rgp.pSelItemId, rgp.pEventTypeId ];
		if(rgp.pEventRef) {
			evpEq.uRef = evpEq.uRef.concat(rgp.pEventRef);
		}

		for (var i = 0; i < pItemList.length; i++) {
			var itemInfo = pItemList[i];
			if (itemInfo.parent) {
				var evp = ux.newEvPrm(rgp);
				evp.uPanels = [ rgp.pContId ];
				evp.uRef = [ rgp.pSelCtrlId ];
				evp.uSelCtrlId = rgp.pSelCtrlId;
				evp.uIdx = itemInfo.idx;
				if (itemInfo.expanded) {
					evp.uCmd = id + "->collapse";
				} else {
					evp.uCmd = id + "->expand";
				}
				ux.attachEventHandler(_id(rgp.pCtrlBase + itemInfo.idx),
						"click", ux.treeCtrlImageClickHandler, evp);
			}

			var tElem = _id(rgp.pLblBase + itemInfo.idx);
			var evp = ux.treeNewEvPrm(rgp, evpEq, pItemList, i, TREE_CLICK);
			ux.attachEventHandler(tElem, "click", ux.treeDelayItemClickHandler, evp);

			evp = ux.treeNewEvPrm(rgp, evpEq, pItemList, i, TREE_DBLCLICK);
			ux.attachEventHandler(tElem, "dblclick", ux.treeItemClickHandler, evp);
				
			evp = ux.treeNewEvPrm(rgp, evpEq, pItemList, i, TREE_RTCLICK);
			ux.attachEventHandler(tElem, "rtclick", ux.treeItemClickHandler, evp);
			
			if (selObj.options[i].selected) {
				selList.push(i);
			}
		}
	}
	
	if(rgp.pMenu) {
		ux.rigTreeExplorerMenu(rgp, rgp.pMenu);
	}
	
	if(rgp.pMenus) {
		for(var i = 0; i < rgp.pMenus.length; i++) {
			ux.rigTreeExplorerMenu(rgp, rgp.pMenus[i]);
		}
	}
	
	var treeData = {};
	treeData.selList = selList;
	treeData.lastSelIdx = -1;
	treeDataMap[id] = treeData;
	
	ux.disableWinContextMenu();
}

ux.rigTreeExplorerMenu = function(rgp, menu) {
	var evpEq = {};
	evpEq.uPanels = [ rgp.pContId ];
	evpEq.uRef = [ rgp.pSelItemId, rgp.pEventTypeId, rgp.pMenuCodeCtrlId ];
	if(rgp.pEventRef) {
		evpEq.uRef = evpEq.uRef.concat(rgp.pEventRef);
	}

	for(var i = 0; i < menu.items.length; i++) {
		var menuItem = menu.items[i];
		var evp = ux.treeMenuEvPrm(rgp, evpEq, menuItem, TREE_MENUCLICK);
		ux.attachEventHandler(_id(menuItem.id), "click", ux.treeMenuClickHandler, evp);
	}
}

ux.treeMenuEvPrm = function(rgp, evpEq, menuItem, eventCodeIdx) {
	var evp = ux.newEvPrm(rgp);
	evp.uPanels = evpEq.uPanels;
	evp.uRef = evpEq.uRef;
	evp.uEventTypeId = rgp.pEventTypeId;
	evp.uMenuCodeCtrlId = rgp.pMenuCodeCtrlId;
	evp.uEvCode = rgp.pEventCode[eventCodeIdx];
	evp.uMenuCode = menuItem.code;
	evp.uCmd = rgp.pId + "->executeEventPath";
	return evp;
}

ux.treeMenuClickHandler = function(uEv) {
	var evp = uEv.evp;
	var tElem = _id(evp.uEventTypeId);
	if(tElem) {
		tElem.value = evp.uEvCode;
	}

	var mElem = _id(evp.uMenuCodeCtrlId);
	if(mElem) {
		mElem.value = evp.uMenuCode;
	}

	ux.hidePopup(null);
	ux.post(uEv);
}

ux.treeNewEvPrm = function(rgp, evpEq, pItemList, visualIdx, eventCodeIdx) {
	var evp = ux.newEvPrm(rgp);
	evp.uPanels = evpEq.uPanels;
	evp.uRef = evpEq.uRef;
	evp.uSelItemId = rgp.pSelItemId;
	evp.uEventTypeId = rgp.pEventTypeId;
	evp.uSel = rgp.pSel;
	evp.uNorm = rgp.pNorm;
	evp.uVisualIdx = visualIdx;
	evp.uItemList = pItemList;
	evp.uLblBase = rgp.pLblBase;
	evp.uFrameId = rgp.pLblBase + pItemList[visualIdx].idx;
	evp.uEvCodeIdx = eventCodeIdx;
	evp.uEvCode = rgp.pEventCode[eventCodeIdx];
	if (rgp.pMenu) {
		evp.uPopupId = rgp.pMenu.popupId;
	}
	
	evp.uId = rgp.pId;
	evp.uCmd = rgp.pId + "->executeEventPath";
	return evp;
}

ux.treeCtrlImageClickHandler = function(uEv) {
	var evp = uEv.evp;
	var tSelCtrlElem = _id(evp.uSelCtrlId);
	if (tSelCtrlElem) {
		tSelCtrlElem.value = evp.uIdx;
	}

	ux.post(uEv);
}

ux.treeDelayItemClickHandler = function(uEv) {
	var treeData = treeDataMap[uEv.evp.uId];
	if (treeData.timeoutId) {
		window.clearTimeout(treeData.timeoutId);
	}

	treeData.uEv = uEv;
	treeData.evp = uEv.evp;
	treeData.evp.mCoord = ux.getExactPointerCoordinates(uEv);
	treeData.timeoutId = window.setTimeout("ux.treeItemEventHandler(\""+ uEv.evp.uId + "\");"
			, UNIFY_TREEDOUBLECLICK_DELAY); 
}

ux.treeItemClickHandler = function(uEv) {
	var treeData = treeDataMap[uEv.evp.uId];
	if (treeData.timeoutId) {
		window.clearTimeout(treeData.timeoutId);
		treeData.timeoutId = null;
	}

	treeData.uEv = uEv;
	treeData.evp = uEv.evp;
	treeData.evp.mCoord = ux.getExactPointerCoordinates(uEv);
	ux.treeItemEventHandler(uEv.evp.uId); 
}

ux.treeItemEventHandler = function(treeId) {
	var treeData = treeDataMap[treeId];	
	var evp = treeData.evp;
	var itemInfo = evp.uItemList[evp.uVisualIdx];
	
	if (evp.uEvCodeIdx == TREE_CLICK) {
		if (treeData.uEv.ctrlKey) {
			ux.treeSelectItem(evp, false, true);
		} else if (treeData.uEv.shiftKey) {
			if (treeData.selList.length > 0 && treeData.lastSelIdx >= 0) {
				ux.treeSelectItemRange(evp, treeData.lastSelIdx, evp.uVisualIdx);
			} else {
				ux.treeSelectItem(evp, true, false);
			}
		} else {
			ux.treeSelectItem(evp, true, false);
			if(itemInfo.pClick) {
				ux.treeSendCommand(treeData);
			}
		}
	} else if (evp.uEvCodeIdx == TREE_DBLCLICK) {
		ux.treeSelectItem(evp, true, false);
		if(itemInfo.pDblClick) {
			ux.treeSendCommand(treeData);
		}

	} else if (evp.uEvCodeIdx == TREE_RTCLICK) {
		var selObj = _id(evp.uSelItemId);
		if (!selObj.options[evp.uVisualIdx].selected) {
			ux.treeSelectItem(evp, true, false);
		}

		var showMenu = false;
		var popupId = itemInfo.popupId;
		for (var j = 0; j < treeData.selList.length; j++) {
			var i = treeData.selList[j];
			if (popupId != evp.uItemList[i].popupId) {
				popupId = evp.uPopupId;
				showMenu = true;
				break;
			}
		}
		
		if (showMenu || itemInfo.pRtClick) {
			if (popupId) {
				var openPrm = {};
				openPrm.popupId = popupId;
				openPrm.relFrameId = evp.uFrameId;
				openPrm.stayOpenForMillSec = -1;
				openPrm.forceReopen = true;
				openPrm.uTrg = treeData.uEv.uTrg;
				openPrm.mCoord = evp.mCoord;
				ux.doOpenPopup(openPrm);
			}
		}
	}

	treeData.uEv = null;
	treeData.evp = null;
}

ux.treeSelectItem = function(evp, single, toggle) {
	var i = evp.uVisualIdx;
	treeDataMap[evp.uId].lastSelIdx = i;
	if (single) {
		ux.treeSelectItemRange(evp, i, i);
	} else{
		var tElem = _id(evp.uLblBase + evp.uItemList[i].idx)
		var selObj = _id(evp.uSelItemId);
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

	var selObj = _id(evp.uSelItemId);
	for(var i = 0; i < evp.uItemList.length; i++) {
		var tElem = _id(evp.uLblBase + evp.uItemList[i].idx)
		if(i >= start && i <= end) {
			ux.treeSelect(evp, tElem, selObj, i, true);
		} else {
			ux.treeSelect(evp, tElem, selObj, i, false);
		}
	}
}

ux.treeSendCommand = function(treeData) {
	var evp = treeData.evp;
	var tTypeElem = _id(evp.uEventTypeId);
	if(tTypeElem) {
		tTypeElem.value = evp.uEvCode;
	}

	var uEv = treeData.uEv;
	uEv.evp = evp;
	ux.post(uEv);
}

ux.treeSelect = function(evp, tElem, selObj, i, select) {
	var treeData = treeDataMap[evp.uId];
	var j = treeData.selList.indexOf(i);
	if (j >= 0) {
		if(!select) {
			treeData.selList = treeData.selList.splice(j + 1, 1);
		}
	} else if (select) {
		treeData.selList.push(i);
	}

	if (select) {
		tElem.className = evp.uSel;
	} else {
		tElem.className = evp.uNorm;
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
		} else {
			pb += ("&req_uid=" + _enc(evp.uLoginId));
			pb += ("&req_unm=" + _enc(evp.uUserName));
			if (evp.uRole) {
				param.value += ("&req_rcd=" + _enc(evp.uRole));
			}
			if (evp.uBranch) {
				param.value += ("&req_bcd=" + _enc(evp.uBranch));
			}
			if (evp.uGlobal) {
				param.value += ("&req_gac=" + _enc(evp.uGlobal));
			}
		}
	}

	if (evp.uConfMsg) {
		if (isForm) {
			pb.append("req_cmsg", evp.uConfMsg);
		} else {
			pb += ("&req_cmsg=" + _enc(evp.uConfMsg));
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
	
	if (trgObj) {
		if (evp.isUniqueTrg) {
			ux.extractObjParams(trgObj, param);
		} else {
			ux.buildNameParams(trgObj.id, builtNames, param)
		}

		if (trgObj.id) {
			// Used for sending target value for target control
			var hiddenElem = _id("trg_" + trgObj.id);
			if (hiddenElem) {
				if (isForm) {
					pb.append("req_trg", hiddenElem.value);
				} else {
					pb += ("&req_trg=" + _enc(hiddenElem.value));
				}
			}
		}

		if (evp.uReqTrg) {
			if (isForm) {
				pb.append("req_trg", trgObj.value);
			} else {
				pb += ("&req_trg=" + _enc(trgObj.value));
			}
		}
	}

	if (isForm) {
		if (evp.uViewer) {
			pb.append("req_rv", evp.uViewer);
			if (evp.uSessionID) {
				pb.append("req_rsi", evp.uSessionID);
			} else if (ux.remoteviewsessions[evp.uViewer]) {
				pb.append("req_rsi", ux.remoteviewsessions[evp.uViewer]);
			}
		} else {
			pb.append("req_doc", ux.docPath);
		}
		if (evp.uValidateAct) {
			pb.append("req_va", evp.uValidateAct);
		}
		if (evp.uCmd) {
			pb.append("req_cmd", evp.uCmd);
		}
		if (evp.uPanels) {
			for (var i = 0; i < evp.uPanels.length; i++) {
				pb.append("req_rsh", evp.uPanels[i]);
			}
		}
	} else {
		if (evp.uViewer) {
			pb += ("&req_rv=" + _enc(evp.uViewer));
			if (evp.uSessionID) {
				pb += ("&req_rsi=" + _enc(evp.uSessionID));
			} else if (ux.remoteviewsessions[evp.uViewer]) {
				pb += ("&req_rsi=" + _enc(ux.remoteviewsessions[evp.uViewer]));
			}
		} else {
			pb += ("&req_doc=" + _enc(ux.docPath));
		}
		if (evp.uValidateAct) {
			pb += ("&req_va=" + _enc(evp.uValidateAct));
		}
		if (evp.uCmd) {
			pb += ("&req_cmd=" + _enc(evp.uCmd));
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
	if (elem && !elem.disabled && elem.type != "button") {
		var transferId = elem.id;
		if (elem.type == "hidden") {
			if(elem.value == "pushc_") {
				var cElems = _name(transferId);
				for(var i = 0; i < cElems.length; i++) {
					if (cElems[i].checked) {
						ux.appendParam(transferId, cElems[i].value, param);
					}
				}
			} else if(elem.value == "pushr_") {
				var rElems = _name(transferId);
				for(var i = 0; i < rElems.length; i++) {
					if (rElems[i].checked) {
						ux.appendParam(transferId, rElems[i].value, param);
						break;
					}
				}
			} else if(elem.value == "pushg_") {
				var gElems = _name(transferId);
				for(var i = 0; i < gElems.length; i++) {
					ux.extractObjParams(gElems[i], param);
				}
			} else {
				ux.appendParam(transferId, elem.value, param);
			}
		} else if (elem.type == "checkbox") {
			ux.appendParam(transferId, elem.checked, param);
		} else if (elem.type == "select-multiple") {
			for (var i = 0; i < elem.options.length; i++) {
				if (elem.options[i].selected) {
					ux.appendParam(transferId, elem.options[i].value, param);
				}
			}
		} else if (elem.type == "file") {
			if (elem.value) {
				var files = elem.files;
				for (var i = 0; i < files.length; i++) {
					param.value.append(transferId, files[i],
							files[i].name);
				}
			}
		} else {
			if (elem.value != undefined) {
				ux.appendParam(transferId, elem.value, param);
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
				elem.disabled = disabled;
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
			var elems = _name(names[i]);
			for (var j = 0; j < elems.length; j++) {
				elems[j].style.display = mode;
			}
		}
	}
}

/** ************************** MISCELLANEOUS ****************** */
/** Popup text fields */
ux.textPopupFitFacade = function(prm) {
	var brdElem = _id(prm.pBrdId);
	var btnElem = _id(prm.pBtnId);
	if (brdElem && btnElem) {
		var w1 = brdElem.clientWidth;
		var w2 = btnElem.clientWidth;
		if (w1 == 0) {
			w1 = parseInt(window.getComputedStyle(brdElem, null).getPropertyValue("width"));
			w2 = parseInt(window.getComputedStyle(btnElem, null).getPropertyValue("width"));
		}

		var facElem = _id(prm.pFacId);
		if (facElem) {
			facElem.style.width = (w1 - w2 - 4) + "px";
		}
	}
};

/** Lists */
ux.listWirePopFrame = function(sCom, rgp) {
	var evp = {};
	evp.uCom = sCom;
	evp.uHitHandler = ux.listKeydownHit;
	evp.uEnterHandler = ux.listKeydownEnter;
	ux.attachEventHandler(sCom.uFrmObj, "click", ux.focusOnClick, evp);
	ux.attachEventHandler(sCom.uFrmObj, "keydown", ux.listSearchKeydown, evp);
	
	if (sCom.uBlankObj) {
		evp = {};
		evp.uIndex = -1;
		evp.uCom = sCom;
		ux.attachEventHandler(sCom.uBlankObj, "click", ux.listSelectClick, evp);
	}
	
	for (var i = 0; i < rgp.pICnt; i++) {
		evp = {};
		evp.uIndex = i;
		evp.uCom = sCom;
		var aElem = _id(rgp.pLabelIds[i]);
		if (aElem) {
			ux.attachEventHandler(aElem, "click", ux.listSelectClick, evp);
		}
	}
}

ux.listKeydownHit = function(sCom) {
	sCom.uSelHandler(sCom, sCom.uIndexes[0], false);
}

ux.listKeydownEnter = function(sCom) {
	/*
	 * if(sCom.uIndexes && sCom.uIndexes.length > 0) { sCom.uSelHandler(sCom,
	 * sCom.uIndexes[0], true); } else { sCom.uSelHandler(sCom, sCom.uOldSelIdx,
	 * true); }
	 */
	sCom.uSelHandler(sCom, sCom.uOldSelIdx, true);
	ux.hidePopup(null);
}

ux.listKeydownSkip = function(sCom, up) {
	var i = sCom.uOldSelIdx;
	if (up) {
		i--;
	} else {
		i++;
	}
	
	if(i >= 0 && i < sCom.uICnt) {
		sCom.uSelHandler(sCom, i, false);
	}	
}

ux.listSelectClick = function(uEv) {
	var evp = uEv.evp;
	var sCom = evp.uCom;
	sCom.uSelHandler(sCom, evp.uIndex, true);
	ux.hidePopup(null);
}

ux.listScrollLabel = function(sCom, aElem) {
	var aH = ux.boundingHeight(aElem);
	var fH = ux.boundingHeight(sCom.uFrmObj);
	var lH = ux.boundingHeight(sCom.uListObj);
	if(aH.top < fH.top) {
		sCom.uFrmObj.scrollTop = aH.top - lH.top;
	} else {
		if (aH.bottom > fH.bottom) {
			sCom.uFrmObj.scrollTop = aH.bottom - (lH.top + fH.height);
		}
	}
}

ux.listSearchKeydown = function(uEv) {
	var evp = uEv.evp;
	var sCom = evp.uCom;
	if (uEv.uChar) {
		var gap = Date.now() - sCom.uLastKeyHit;
		
		if (gap > UNIFY_KEY_SEARCH_MAX_GAP) {
			sCom.uSchIdx = 0;
			sCom.uIndexes = null;
		}

		ux.listSearchLabel(sCom, uEv.uChar);
		if (sCom.uIndexes && sCom.uIndexes.length > 0) {
			evp.uHitHandler(sCom);
		}
	
		sCom.uLastKeyHit = Date.now(); 
	} else {
		if(uEv.uKeyCode == '38') {
			ux.listKeydownSkip(sCom, true);
			uEv.uStop();
		} else if(uEv.uKeyCode == '40') {
			ux.listKeydownSkip(sCom, false);
			uEv.uStop();
		} else if (uEv.uKeyCode == 13 || (uEv.uKey && "ENTER" == uEv.uKey.toUpperCase())) {
			if (evp.uEnterHandler) {
				evp.uEnterHandler(sCom);
				uEv.uStop();
			}
		}
	}
}

ux.listSearchLabel = function(sCom, char) {
	var newIndexes = [];
	var schIdx = sCom.uSchIdx;
	var labels = sCom.uLabels;
	var indexes = sCom.uIndexes;
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
	
	sCom.uSchIdx++;
	sCom.uIndexes = newIndexes;
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
ux.setTextRegexFormatting = function(id, formatRegex, textCase) {
	var evp = {};
	if (textCase) {
		evp.sTextCase = textCase.toLowerCase();
	}

	if (formatRegex) {
		evp.sFormatRegex = eval(formatRegex);
	}

	var elem = _id(id);
	if (elem) {
		ux.attachEventHandler(elem, "keypress", ux.textInputKeypress,
				evp);
		ux.attachEventHandler(elem,  "keydown", ux.textInputKeydown,
				evp);
	}
}

ux.textInputKeypress = function(uEv) {

}

ux.textInputKeydown = function(uEv) {
	var trgObj = uEv.uTrg;
	var evp = uEv.evp;

	if (uEv.uChar && !trgObj.readOnly) {
		var pos = ux.getCaretPosition(trgObj);
		var string = trgObj.value;
		string = string.substring(0, pos.start) + uEv.uChar + string.substring(pos.end);

		var formatRegex = evp.sFormatRegex;
		if (formatRegex && !formatRegex.test(string)) {
			uEv.uStop();
			return;
		}

		if (evp.sTextCase) {
			if ("upper" == evp.sTextCase) {
				trgObj.value = string.toUpperCase();
			} else {
				trgObj.value = string.toLowerCase();
			}
			
			var spos = pos.start + 1;
			ux.setCaretPosition(trgObj, spos, spos);
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

ux.setCaretPosition = function(trgObj, start, end) {
	if(trgObj.setSelectionRange) {
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
	ux.multipleAttachEventHandler(name, "blur", ux.textValidationOnBlurHandler,
			evp);
}

ux.setTextRegexValidation = function(name, validation, validationRefArray,
		passMessage, failMessage, required) {
	var evp = {};
	evp.sValidationRegex = validationRegex;
	evp.sPassMessage = passMessage;
	evp.sFailMessage = failMessage;
	evp.sRequired = required;
	ux.multipleAttachEventHandler(name, "blur", ux.textValidationOnBlurHandler,
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
	ux.multipleAttachEventHandler(name, "blur", ux.textValidationOnBlurHandler,
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
			validationRegex = eval(evp.sValidationRegex);
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
ux.wireRightClickHandler = function(evp, handler) {
	evp.uRightHandler = handler;
	return ux.onRightClickHandler;
}

ux.onRightClickHandler = function(uEv) {
	if (uEv.mButton == UNIFY_RIGHT_BUTTON) {
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

ux.setShortcut = function(shortcut, evp) {
	ux.shortcuts[shortcut] = evp;
}

ux.setOnEvent = function(evp) {
	var elem = _id(evp.uId);
	if (elem) {
		var eventName = evp.uEvnt;
		if (elem.value == "pushr_") {
			ux.multipleAttachEventHandler(evp.uId, eventName, evp.uFunc,
					evp);
		} else {
			ux.attachEventHandler(elem, eventName, evp.uFunc,
					evp);
			if (evp.uFire) {
				ux.fireEvent(elem, eventName, true);
			}
		}
	}
}

ux.extractLabels = function(trgIdArr, upperCase) {
	var labels = [];
	if (trgIdArr) {
		for (var i = 0; i < trgIdArr.length; i++) {
			var elem = _id(trgIdArr[i]);
			if (elem) {
				if(upperCase) {
					labels.push(elem.innerHTML.toUpperCase());
				} else {
					labels.push(elem.innerHTML);
				}
			} else {
				labels.push("");
			}
		}
	}
	
	return labels;
}

ux.popupWireClear = function(rgp, btnId, trgArr) {
	var clearBtn = _id(btnId);
	if (rgp.pClearable) {
		var evp = {};
		evp.uRef = trgArr;
		ux.attachEventHandler(clearBtn, "click", function(uEv) {
			ux.clear(uEv);
			ux.hidePopup(uEv);
		}, evp);
	} else {
		clearBtn.disabled = true;
	}
}

ux.popupWireCancel = function(btnId) {
	var cancelBtn = _id(btnId);
	var evp = {};
	ux.attachEventHandler(cancelBtn, "click", ux.hidePopup, evp);
}

ux.setPatternValue = function(rgp) {
	var patternArr = rgp.pPattern;
	if (patternArr) {
		var fullValue = '';
		for (var i = 0; i < patternArr.length; i++) {
			var pattern = patternArr[i];
			if (pattern.flag == true) {
				fullValue += pattern.target;
			} else {
				if (rgp.pPadLeft) {
					fullValue += ux.padLeft(_id(pattern.target).value, '0',
							pattern.length);
				} else {
					fullValue += _id(pattern.target).value;
				}
			}
		}

		var elem = _id(rgp.pId);
		elem.value = fullValue;
		ux.fireEvent(elem, "change", true);
	}
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
var menuContextOff = false;

ux.init = function() {
	ux.resizeTimeout = null;
	var evp = {};
	ux.attachEventHandler(document, "keydown", ux.documentKeydownHandler,
					evp);
}

ux.disableWinContextMenu = function() {
	if (!menuContextOff) {
		window.oncontextmenu = function () {
		    return false; // Cancel default menu
		}

		menuContextOff = true;
	}
}

ux.documentKeydownHandler = function(uEv) {
	if (uEv.uKeyCode == 8) {
		var preventKeypress = true;
		var elem = uEv.uTrg;
		if (elem.type == "text" || elem.type == "password"
				|| elem.type == "textarea") {
			preventKeypress = elem.readOnly || elem.disabled;
		}
		if (preventKeypress) {
			uEv.uStop();
		}
	}

	var evp = ux.shortcuts[uEv.uShortKeyCode];
	if (evp) {
		if (_id(evp.uId)) { // Containing panel must be visible for shortcut
			uEv.evp = evp;
			evp.uFunc(uEv);
			uEv.uStop();
		}
	}
}


/** DOM search functions */
ux.findParent = function(domObject, tagName) {
	while (domObject = domObject.parentNode) {
		if (domObject.tagName.toLowerCase() == tagName.toLowerCase()) {
			return domObject;
		}
	}
	return null;
}

/** Drag and drop */
var dragElem = null;
var dragElemPos = {
	x : 0,
	y : 0
};
var dragPointerPos = {
	x : 0,
	y : 0
};

ux.dragDropEngage = function(ev) {
	var evp = ev.evp;
	dragElem = _id(evp.uTargetPnlId);
	dragElemPos = {
		x : parseInt(dragElem.style.left),
		y : parseInt(dragElem.style.top)
	};
	dragPointerPos = ux.getPointerCoordinates(ev);
	ux.attachDirectEventHandler(document, "mouseup", ux.dragDropDisengage);
	ux.attachDirectEventHandler(document, "mousemove", ux.dragDropAction);
}

ux.dragDropDisengage = function(ev) {
	ux.detachDirectEventHandler(document, "mousemove", ux.dragDropAction);
	ux.detachDirectEventHandler(document, "mouseup", ux.dragDropDisengage);
}

ux.dragDropAction = function(ev) {
	var newPointerPos = ux.getPointerCoordinates(ev);
	var x = dragElemPos.x + newPointerPos.x - dragPointerPos.x
	var y = dragElemPos.y + newPointerPos.y - dragPointerPos.y

	// Restrict to view port
	var viewRect = ux.getWindowRect();
	var dragElemRect = ux.boundingRect(dragElem);

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

	dragElem.style.left = x + "px";
	dragElem.style.top = y + "px";
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
	var mCoord = ux.getExactPointerCoordinates(ev);
	var tCoord = ux.getElementPosition(ev.uTrg);
	return {
		x : mCoord.x = tCoord.x,
		y : mCoord.y = tCoord.y
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
ux.multipleAttachEventHandler = function(name, eventName, handler, evp) {
	var elems = _name(name);
	for (var i = 0; i < elems.length; i++) {
		ux.attachEventHandler(elems[i], eventName, handler, evp);
	}
}

ux.attachEventHandler = function(domObject, eventName, handler, evp) {
	if ("enter" == eventName) {
		eventName = "keydown";
		handler = ux.wireSpecialKeyHandler(evp, handler, "Enter", 13);
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
		ux.confirmStore.handler = handler;
		ux.confirmStore.normEvt = ux.normaliseEvent(event, evp);
		ux.confirmStore.evp = evp;

		// Execute confirmation redirect
		var evPrmConf = {};
		evPrmConf.uURL = evp.uConfURL;
		evPrmConf.uConfMsg = evp.uConf;
		evPrmConf.uViewer = evp.uViewer;
		var hiddenElem = _id(ux.confirmStore.normEvt.uTrg.id + "_a");
		if (hiddenElem) {
			evPrmConf.uConfPrm = hiddenElem.value;
		}
		ux.postCommit(evPrmConf);
	} else {
		// Handle now
		handler(ux.normaliseEvent(event, evp));
	}
}

ux.attachDirectEventHandler = function(domObject, eventName, handler) {
	if (document.addEventListener) {
		domObject.addEventListener(eventName, handler, false); // DOM Level 2.
		// false =
		// Bubble, true
		// = Capture
	} else if (document.attachEvent) {
		domObject.attachEvent("on" + eventName, handler); // Explorer
	}
}

ux.detachDirectEventHandler = function(domObject, eventName, handler) {
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

ux.fireEvent = function(domObject, eventName, bubbling) {
	if (document.createEvent) {
		var event = document.createEvent("HTMLEvents");
		event.initEvent(eventName, bubbling, true);
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
			ux.attachEventHandler(ux.popCurr, "mouseover",
					ux.cancelClosePopupTimer, {});
			if (ux.popupStayOpen != true) {
				ux.attachEventHandler(openPrm.uTrg, "mouseout",
						ux.startClosePopupTimer, {});
				ux.attachEventHandler(ux.popCurr, "mouseout",
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
			if (openPrm.mCoord) {
				x = openPrm.mCoord.x;
				y = openPrm.mCoord.y;
			}
			
			ux.popCurr.style.left = x + 'px';
			ux.popCurr.style.top = y + 'px';
			ux.popCurr.style.minWidth = frameRect.width + 'px';
		}

		ux.popCurr.style.visibility = 'visible';

		ux.openPrm = openPrm;
		if (openPrm.showHandler) {
			openPrm.showHandler(openPrm.showParam);
		}
	}
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
			openPrm.hideHandler(openPrm.hideParam);
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
	if (ux.popupStayOpen) {
		if (ux.popupNewOpen) {
			ux.popupNewOpen = false;
			return;
		}
	}

	var elem = uEv.uTrg;
	while (elem) {
		// Do not hide. Exit if element clicked is original source element or
		// popup window
		if (elem == ux.popupOrigin || elem == ux.popCurr) {
			return;
		}
		elem = elem.parentElement;
	}

	ux.hidePopup(uEv);
}

ux.attachEventHandler(document, "click", ux.documentHidePopup, {});

/** On window resize function */
ux.registerResizeFunc = function(id, resizeFunc, resizePrm) {
	var resizeInfo = {};
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

ux.attachEventHandler(window, "resize", function() {
	window.clearTimeout(ux.resizeTimeout); // Debounce resize call
	ux.resizeTimeout = window.setTimeout(ux.callResizeFuncs,
			UNIFY_WINDOW_RESIZE_DEBOUNCE_DELAY);
}, {});

/** Initialisation */
ux.init();

/** Types */
// Transformation
var DEFAULT_TRANSFORMATION_STEP_RATE = 20;
var LINEAR_TRANSLATION = 0;

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
