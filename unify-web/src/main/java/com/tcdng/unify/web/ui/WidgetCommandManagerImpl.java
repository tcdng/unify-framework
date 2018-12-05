/*
 * Copyright 2014 The Code Department
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.web.UnifyWebErrorConstants;
import com.tcdng.unify.web.WebApplicationComponents;
import com.tcdng.unify.web.annotation.Action;

/**
 * Default widget command manager implementation.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(WebApplicationComponents.APPLICATION_UICOMMANDMANAGER)
public class WidgetCommandManagerImpl extends AbstractUnifyComponent implements WidgetCommandManager {

	private FactoryMap<Class<? extends Widget>, UICommandInfo> uiCommandInfoMap;

	public WidgetCommandManagerImpl() {
		uiCommandInfoMap = new FactoryMap<Class<? extends Widget>, UICommandInfo>() {

			@Override
			protected UICommandInfo create(Class<? extends Widget> key, Object... params) throws Exception {
				UICommandInfo uiCommandInfo = new UICommandInfo();
				Method[] methods = key.getMethods();
				for (Method method : methods) {
					Action ca = method.getAnnotation(Action.class);
					if (ca != null) {
						if (void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 0) {
							uiCommandInfo.addCommandMethod(method.getName(), method);
						} else {
							throw new UnifyException(UnifyWebErrorConstants.WIDGET_INVALID_COMMAND_HANDLER_SIGNATURE,
									key, method.getName());
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
				throw new UnifyException(UnifyWebErrorConstants.WIDGET_UNKNOWN_COMMANDHANDLER, widget.getClass(),
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

		public void addCommandMethod(String name, Method method) {
			commandMethods.put(name, method);
		}

		public Method getCommandMethod(String name) throws UnifyException {
			return commandMethods.get(name);
		}
	}
}
