/*
 * Copyright 2018-2022 The Code Department.
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

package com.tcdng.unify.core.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.data.ListNumberData;
import com.tcdng.unify.core.data.Listable;

/**
 * Minutes list command.
 * 
 * @author The Code Department
 * @since 1.0
 */
@Component("minuteslist")
public class MinutesListCommand extends AbstractIntegerParamListCommand {

    @Override
    public List<? extends Listable> execute(Locale locale, IntegerParam param) throws UnifyException {
        int minuteJump = 1;
        if (param.isPresent()) {
            minuteJump = param.getValue();
        }
        
        List<Listable> list = new ArrayList<Listable>();
        for (int i = 0; i <= 59; i += minuteJump) {
            list.add(new ListNumberData(i));
        }
        return list;
    }

}
