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
package com.tcdng.unify.web.ui.widget;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.ui.UnifyWebUIErrorConstants;
import com.tcdng.unify.web.ui.WebUIApplicationComponents;

/**
 * Default widget command manager implementation.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component(WebUIApplicationComponents.APPLICATION_UICOMMANDMANAGER)
public class WidgetCommandManagerImpl extends AbstractUnifyComponent implements WidgetCommandManager {

    private FactoryMap<Class<? extends Widget>, UICommandInfo> uiCommandInfoMap;

    public WidgetCommandManagerImpl() {
        uiCommandInfoMap = new FactoryMap<Class<? extends Widget>, UICommandInfo>() {

            @Override
            protected UICommandInfo create(Class<? extends Widget> widgetClass, Object... params) throws Exception {
                UICommandInfo uiCommandInfo = new UICommandInfo();
                Method[] methods = widgetClass.getMethods();
                for (Method method : methods) {
                    Action ca = method.getAnnotation(Action.class);
                    if (ca != null) {
                        if (isCommandSignature(method)) {
                            uiCommandInfo.addCommandMethod(method);
                        } else {
                            throw new UnifyException(UnifyWebUIErrorConstants.WIDGET_INVALID_COMMAND_HANDLER_SIGNATURE,
                                    widgetClass, method.getName());
                        }
                    } else {
                        // Check if method has an command method signature and super class has similar.
                        // In other words, check inheritance of @Action from super class.
                        // This implies that if a command method overrides a super command method, we
                        // don't have to apply the @Action annotation
                        if (isCommandSignature(method) && isSuperCommandMethod(widgetClass, method.getName())) {
                            uiCommandInfo.addCommandMethod(method);
                        }
                    }
                }
                return uiCommandInfo;
            }
        };
    }

    @Override
    public void executeCommand(Widget widget, String command) throws UnifyException {
        try {
            Method method = uiCommandInfoMap.get(widget.getClass()).getCommandMethod(command);
            if (method == null) {
                throw new UnifyException(UnifyWebUIErrorConstants.WIDGET_UNKNOWN_COMMANDHANDLER, widget.getClass(),
                        command);
            }
            method.invoke(widget);
        } catch (UnifyException e) {
            throw e;
        } catch (Exception e) {
            throwOperationErrorException(e);
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class UICommandInfo {

        private Map<String, Method> commandMethods;

        public UICommandInfo() {
            commandMethods = new HashMap<String, Method>();
        }

        public void addCommandMethod(Method method) {
            commandMethods.put(method.getName(), method);
        }

        public boolean isCommandMethod(String name) throws UnifyException {
            return commandMethods.containsKey(name);
        }

        public Method getCommandMethod(String name) throws UnifyException {
            return commandMethods.get(name);
        }
    }

    private boolean isCommandSignature(Method method) throws UnifyException {
        return void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0
                && method.getExceptionTypes().length == 1
                && UnifyException.class.isAssignableFrom(method.getExceptionTypes()[0]);
    }

    @SuppressWarnings("unchecked")
    private boolean isSuperCommandMethod(Class<? extends Widget> widgetClass, String methodName) throws UnifyException {
        Class<?> clazz = widgetClass;
        while ((clazz = clazz.getSuperclass()) != null && Widget.class.isAssignableFrom(clazz)) {
            if (uiCommandInfoMap.get((Class<? extends Widget>) clazz).isCommandMethod(methodName)) {
                return true;
            }
        }

        return false;
    }
}
