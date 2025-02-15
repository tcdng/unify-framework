/*
 * Copyright 2018-2024 The Code Department.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.unify.web.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.constant.MimeType;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;
import com.tcdng.unify.web.Action;
import com.tcdng.unify.web.Controller;
import com.tcdng.unify.web.ControllerFinder;
import com.tcdng.unify.web.ControllerPathParts;
import com.tcdng.unify.web.PathInfoRepository;
import com.tcdng.unify.web.UnifyWebPropertyConstants;
import com.tcdng.unify.web.annotation.RequestParameter;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.constant.ReservedPageControllerConstants;
import com.tcdng.unify.web.constant.ResultMappingConstants;
import com.tcdng.unify.web.constant.UnifyWebRequestAttributeConstants;
import com.tcdng.unify.web.ui.widget.Page;
import com.tcdng.unify.web.ui.widget.PageManager;
import com.tcdng.unify.web.ui.widget.PropertyInfo;

/**
 * Default implementation of application UI controller utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(WebUIApplicationComponents.APPLICATION_UICONTROLLERUTIL)
public class UIControllerUtilImpl extends AbstractUnifyComponent implements UIControllerUtil {

    @Configurable
    private PageManager pageManager;

    @Configurable
    private PageRequestContextUtil pageRequestContextUtil;

    @Configurable
    private PathInfoRepository pathInfoRepository;

    @Configurable
    private ControllerFinder controllerFinder;

    private Map<String, Result> defaultResultMap;

    @SuppressWarnings("rawtypes")
    private FactoryMap<Class<? extends PageController>, PageControllerActionInfo> pageControllerActionInfoMap;

    private FactoryMap<String, PageControllerInfo> pageControllerInfoMap;

    private FactoryMap<String, ResourceControllerInfo> resourceControllerInfoMap;

    private PageControllerResponse hintUserResponse;

    private PageControllerResponse refreshMenuResponse;

    private Map<String, String> additionalResponseHeaders;

    private String commonUtilitiesControllerName;

    private boolean hideErrorTrace;

    private boolean cspNonce;

    @SuppressWarnings("rawtypes")
    public UIControllerUtilImpl() {
        pageControllerActionInfoMap = new FactoryMap<Class<? extends PageController>, PageControllerActionInfo>()
            {

                @Override
                protected PageControllerActionInfo create(Class<? extends PageController> clazz, Object... params)
                        throws Exception {
                    PageControllerActionInfo pageControllerActionInfo = new PageControllerActionInfo();
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        com.tcdng.unify.web.annotation.Action aa = method
                                .getAnnotation(com.tcdng.unify.web.annotation.Action.class);
                        if (aa != null) {
                            if (isActionHandlerSignature(method)) {
                                pageControllerActionInfo.addActionMethod(method);
                            } else {
                                throw new UnifyException(
                                        UnifyWebUIErrorConstants.CONTROLLER_INVALID_ACTION_HANDLER_SIGNATURE,
                                        clazz.getName(), method.getName());
                            }
                        } else {
                            // Check if method has an action handler method signature and super class has
                            // similar.
                            // In other words, check inheritance of @Action from super class.
                            // This implies that if a action handler method overrides a super action handler
                            // method, we
                            // don't have to apply the @Action annotation
                            if (isActionHandlerSignature(method) && isSuperCommandMethod(clazz, method.getName())) {
                                pageControllerActionInfo.addActionMethod(method);
                            }
                        }
                    }

                    return pageControllerActionInfo;
                }

            };

        pageControllerInfoMap = new FactoryMap<String, PageControllerInfo>()
            {
                @Override
                protected PageControllerInfo create(String controllerName, Object... params) throws Exception {
                    return createPageControllerInfo(controllerName);
                }
            };

        resourceControllerInfoMap = new FactoryMap<String, ResourceControllerInfo>()
            {
                @Override
                protected ResourceControllerInfo create(String controllerName, Object... params) throws Exception {
                    return createResourceControllerInfo(controllerName);
                }
            };
    }

    @Override
    public boolean isCSPNonce() throws UnifyException {
        return cspNonce;
    }

    @Override
    public boolean isHideErrorTrace() throws UnifyException {
        return hideErrorTrace;
    }

    @Override
    public Map<String, String> getAdditionalResponseHeaders() throws UnifyException {
        return additionalResponseHeaders;
    }

    @Override
    public PageControllerInfo getPageControllerInfo(String controllerName) throws UnifyException {
        return pageControllerInfoMap.get(controllerName);
    }

    @Override
    public void updatePageControllerInfo(String controllerName, String standalonePanelName) throws UnifyException {
        PageControllerInfo pbbi = getPageControllerInfo(controllerName);
        Map<String, PropertyInfo> bindings = pageManager.getStandalonePanelPropertyBindings(standalonePanelName);
        pbbi.addBindings(standalonePanelName, bindings);
    }

    @Override
    public ResourceControllerInfo getResourceControllerInfo(String controllerName) throws UnifyException {
        return resourceControllerInfoMap.get(controllerName);
    }

    @Override 
    public String executePageController(String fullActionPath) throws UnifyException {
        Page currentPage = pageRequestContextUtil.getRequestPage();
        try {
            ControllerPathParts targetPathParts = pathInfoRepository.getControllerPathParts(fullActionPath);
            PageController<?> targetPageController = (PageController<?>) controllerFinder
                    .findController(targetPathParts);
            loadRequestPage(targetPathParts);
            return targetPageController.executePageCall(targetPathParts.getActionName());
        } finally {
            pageRequestContextUtil.setRequestPage(currentPage); // Restore original page
        }
    }

    @Override
    public void populatePageBean(String controllerName, String property, Object value) throws UnifyException {
        Page currentPage = pageRequestContextUtil.getRequestPage();
        try {
            ControllerPathParts targetPathParts = pathInfoRepository.getControllerPathParts(controllerName);
            controllerFinder.findController(targetPathParts); // Force target page to be created in session if necessary
            Page targetPage = loadRequestPage(targetPathParts);
            DataUtils.setNestedBeanProperty(targetPage.getPageBean(), property, value);
        } finally {
            pageRequestContextUtil.setRequestPage(currentPage); // Restore original page
        }
    }

    @Override
    public String getCommonUtilitiesControllerName() {
        return commonUtilitiesControllerName;
    }

    @Override
    public Page loadRequestPage(ControllerPathParts controllerPathParts) throws UnifyException {
		final String pageId = pageManager.getCurrentRequestPageId(controllerPathParts);
        Page page = (Page) getSessionContext().getAttribute(pageId);
        pageRequestContextUtil.setRequestPage(page);
        return page;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onInitialize() throws UnifyException {
        commonUtilitiesControllerName = getContainerSetting(String.class,
                UnifyWebPropertyConstants.APPLICATION_COMMON_UTILITIES,
                ReservedPageControllerConstants.COMMONUTILITIES);

        // Default result mappings
        defaultResultMap = new HashMap<String, Result>();
        Locale defaultLocale = Locale.getDefault();

        hintUserResponse = (PageControllerResponse) getUplComponent(defaultLocale, "!hintuserresponse", false);

        refreshMenuResponse = (PageControllerResponse) getUplComponent(defaultLocale, "!refreshmenuresponse", false);

        defaultResultMap.put(ResultMappingConstants.NONE,
                new Result(new PageControllerResponse[] { hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.INDEX, new Result(MimeType.TEXT_HTML, new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!loaddocumentresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.OPEN,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!loadcontentresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!showpopupresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.RELOAD,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!reloadcontentresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.SAVE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!postresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.CLOSE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!unloadcontentresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.CLOSE_WINDOW,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!closewindowresponse", false)}));

        defaultResultMap.put(ResultMappingConstants.REMOTE_VIEW, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!docviewresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.POST_RESPONSE,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale,
                                "!postresponse pathRequestAttribute:$s{"
                                        + UnifyWebRequestAttributeConstants.COMMAND_POSTRESPONSE_PATH + "}",
                                false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.HINT_USER,
                new Result(new PageControllerResponse[] { hintUserResponse }));

        defaultResultMap.put(ResultMappingConstants.SHOW_POPUP, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!showpopupresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.SHOW_DYNAMIC_POPUP, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!showpopupresponse popup:$s{dynPopup}", false) }));

        defaultResultMap.put(ResultMappingConstants.HIDE_POPUP,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        hintUserResponse, refreshMenuResponse }));

        defaultResultMap.put(ResultMappingConstants.HIDE_POPUP_FIRE_CONFIRM,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        hintUserResponse, refreshMenuResponse,
                        (PageControllerResponse) getUplComponent(defaultLocale, "!firepreconfirmresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.COMMAND,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!commandpostresponse", false),
                        hintUserResponse }));

        defaultResultMap.put(ResultMappingConstants.SHOW_ATTACHMENT, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!fileattachmentresponse", false),
                hintUserResponse}));

        defaultResultMap.put(ResultMappingConstants.DOWNLOAD_FILE, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!filedownloadresponse", false),
                hintUserResponse}));

        defaultResultMap.put(ResultMappingConstants.DOWNLOAD_FILE_HIDE_POPUP,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!filedownloadresponse", false),
                        hintUserResponse}));

        defaultResultMap.put(ResultMappingConstants.REFRESH_SHOW_POPUP,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!showpopupresponse", false),
                        hintUserResponse}));

        defaultResultMap.put(ResultMappingConstants.REFRESH_HIDE_POPUP,
                new Result(new PageControllerResponse[] {
                        (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false),
                        (PageControllerResponse) getUplComponent(defaultLocale, "!hidepopupresponse", false),
                        hintUserResponse}));

        defaultResultMap.put(ResultMappingConstants.REFRESH_PANELS, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!refreshpanelresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.REFRESH_SECTION, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!refreshsectionresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.AUTO_REFRESH, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!autorefreshresponse", false) }));

        defaultResultMap.put(ResultMappingConstants.VALIDATION_ERROR, new Result(new PageControllerResponse[] {
                (PageControllerResponse) getUplComponent(defaultLocale, "!validationerrorresponse", false) }));
        defaultResultMap = Collections.unmodifiableMap(defaultResultMap);

        hideErrorTrace = getContainerSetting(boolean.class, UnifyWebPropertyConstants.APPLICATION_WEB_HIDE_ERRORTRACE);
        cspNonce = getContainerSetting(boolean.class, UnifyWebPropertyConstants.APPLICATION_WEB_CSP_NONCE); 
        additionalResponseHeaders = new HashMap<String, String>();
        List<String> headers = DataUtils.convert(ArrayList.class, String.class,
                getContainerSetting(Object.class, UnifyWebPropertyConstants.APPLICATION_WEB_RESPONSE_HEADER));
        if (headers != null) {
            for (String header : headers) {
                int index = header.indexOf('=');
                if (index > 0) {
                    additionalResponseHeaders.put(header.substring(0, index), header.substring(index + 1));
                }
            }
        }

        additionalResponseHeaders = Collections.unmodifiableMap(additionalResponseHeaders);
    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class PageControllerActionInfo {

        private Map<String, Method> actionMethods;

        public PageControllerActionInfo() {
            actionMethods = new HashMap<String, Method>();
        }

        public void addActionMethod(Method method) {
            actionMethods.put(method.getName(), method);
        }

        public boolean isActionMethod(String name) {
            return actionMethods.containsKey(name);
        }

        public Collection<Method> getActionMethods() {
            return actionMethods.values();
        }
    }

    private boolean isActionHandlerSignature(Method method) throws UnifyException {
        return String.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0
                && method.getExceptionTypes().length == 1
                && UnifyException.class.isAssignableFrom(method.getExceptionTypes()[0]);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean isSuperCommandMethod(Class<? extends PageController> pageControllerClass, String methodName)
            throws UnifyException {
        Class<?> clazz = pageControllerClass;
        while ((clazz = clazz.getSuperclass()) != null && PageController.class.isAssignableFrom(clazz)) {
            if (pageControllerActionInfoMap.get((Class<? extends PageController<?>>) clazz)
                    .isActionMethod(methodName)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private PageControllerInfo createPageControllerInfo(String controllerName) throws UnifyException {
		// Process action handlers
		Map<String, Action> actionByNameMap = new HashMap<String, Action>();
		Class<? extends PageController> typeClass = getComponentType(PageController.class, controllerName);
		for (Method method : pageControllerActionInfoMap.get(typeClass).getActionMethods()) {
			actionByNameMap.put("/" + method.getName(), new Action(method));
		}

		// Process result mappings
		Map<String, Result> resultByNameMap = new ConcurrentHashMap<String, Result>();
		List<Class<?>> classList = ReflectUtils.getClassHierachyList(typeClass);
		for (Class<?> clazz : classList) {
			// Grab results definition by super class hierarchy with subclass
			// definitions overriding those in superclass
			ResultMappings rsa = clazz.getAnnotation(ResultMappings.class);
			if (rsa != null) {
				for (com.tcdng.unify.web.annotation.ResultMapping ra : rsa.value()) {
					String[] descriptors = ra.response();

					List<PageControllerResponse> responses = new ArrayList<PageControllerResponse>();
					Locale locale = Locale.getDefault();
					for (String descriptor : descriptors) {
						if (!"!hintuserresponse".equals(descriptor) && !"!refreshmenuresponse".equals(descriptor)) {
							responses.add((PageControllerResponse) getUplComponent(locale, descriptor, false));
						}
					}

					if (ra.type().isApplicationJson()) {
						// Add implicit JSON responses
						responses.add(hintUserResponse);
						responses.add(refreshMenuResponse);
					}

					// Set result object
					resultByNameMap.put(ra.name(), new Result(ra.type(),
							DataUtils.toArray(PageControllerResponse.class, responses), ra.reload()));
				}
			}
		}

		// Resolve category
		List<String> categoryList = DataUtils.convert(ArrayList.class, String.class,
				getContainerSetting(Object.class, UnifyCorePropertyConstants.APPLICATION_LAYOUT));
		UnifyConfigUtils.resolveConfigurationOverrides(resultByNameMap, categoryList);

		resultByNameMap.putAll(defaultResultMap); // Set result mappings that can
		// not be overridden

		// Set page name bindings
		Map<String, PropertyInfo> pageNamePropertyBindingMap = new HashMap<String, PropertyInfo>();
		pageNamePropertyBindingMap.putAll(pageManager.getStandalonePanelPropertyBindings(controllerName));
		setIdRequestParameterBindings(typeClass, pageNamePropertyBindingMap);

		return new PageControllerInfo(controllerName, actionByNameMap, resultByNameMap, pageNamePropertyBindingMap);
    }

    private ResourceControllerInfo createResourceControllerInfo(String controllerName) throws UnifyException {
        Class<? extends PageResourceController> resourceControllerClass = getComponentType(PageResourceController.class,
                controllerName);
        Map<String, PropertyInfo> propertyBindingMap = new HashMap<String, PropertyInfo>();
        setIdRequestParameterBindings(resourceControllerClass, propertyBindingMap);
        return new ResourceControllerInfo(controllerName, Collections.emptyMap(), propertyBindingMap);
    }

    private void setIdRequestParameterBindings(Class<? extends Controller> controllerClass,
            Map<String, PropertyInfo> propertyBindingMap) throws UnifyException {
        // Request parameter bindings
        List<Class<?>> classHeirachyList = ReflectUtils.getClassHierachyList(controllerClass);
        for (Class<?> clazz : classHeirachyList) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(RequestParameter.class)) {
                    String fieldName = field.getName();
                    propertyBindingMap.put(pageManager.getPageName(field.getName()),
                            new PropertyInfo(fieldName, fieldName, fieldName, false));
                }
            }
        }
    }
}
