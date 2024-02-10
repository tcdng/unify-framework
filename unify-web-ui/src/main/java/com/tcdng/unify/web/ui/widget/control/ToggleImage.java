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
package com.tcdng.unify.web.ui.widget.control;

import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.web.ui.widget.AbstractTargetControl;

/**
 * Graphical indicator that shows a toggle state. The toggle state is the
 * boolean value of the widget's binded property.
 *
 * @author The Code Department
 * @since 1.0
 */
@Component("ui-toggleimage")
@UplAttributes({ @UplAttribute(name = "onImgSrc", type = String.class, defaultVal = "$t{images/toggleon.png}"),
        @UplAttribute(name = "offImgSrc", type = String.class, defaultVal = "$t{images/toggleoff.png}") })
public class ToggleImage extends AbstractTargetControl {

}
