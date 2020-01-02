/*
 * Copyright 2018-2020 The Code Department.
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
package com.tcdng.unify.core.system;

import java.util.List;
import java.util.Map;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.business.BusinessService;
import com.tcdng.unify.core.data.Input;
import com.tcdng.unify.core.data.Inputs;
import com.tcdng.unify.core.system.entities.ParameterDef;

/**
 * Used to manage parameter definitions and parameter instantiations.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface ParameterService extends BusinessService {

    /**
     * Defines parameters for type
     * 
     * @param paramTypeName
     *            the definition name
     * @param type
     *            the type name
     * @throws UnifyException
     *             if an error occurs
     */
    void defineParameters(String paramTypeName, Class<?> type) throws UnifyException;

    /**
     * Defines parameters. Expected to create new entries if none exists.
     * 
     * @param paramTypeName
     *            the definition name
     * @param parameterList
     *            the parameter definition list
     * @throws UnifyException
     *             if an error occurs
     */
    void defineParameters(String paramTypeName, List<ParameterDef> parameterList) throws UnifyException;

    /**
     * Finds parameter definitions for a parameterized type instance by name.
     * 
     * @param paramTypeName
     *            the definition name
     * @return list of parameter definitions
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, ParameterDef> findParameterDefinitionsByName(String paramTypeName) throws UnifyException;

    /**
     * Returns a new list of inputs as defined for a parameter type.
     * 
     * @param paramTypeName
     *            the definition name
     * @return list of inputs
     * @throws UnifyException
     *             if an error occurs
     */
    List<Input<?>> fetchInputList(String paramTypeName) throws UnifyException;

    /**
     * Finds normalized parameter inputs
     * 
     * @param paramTypeName
     *            the parameter definition name
     * @param instTypeName
     *            the instance type name
     * @param instId
     *            the type instance ID
     * @return normalized parameter inputs
     * @throws UnifyException
     *             if an error occurs
     */
    Inputs fetchNormalizedInputs(String paramTypeName, String instTypeName, Long instId) throws UnifyException;

    /**
     * Finds parameter type values
     * 
     * @param paramTypeName
     *            the parameter definition name
     * @param instTypeName
     *            the instance type name
     * @param instId
     *            the type instance ID
     * @return parameter type values
     * @throws UnifyException
     *             if an error occurs
     */
    Map<String, Object> findParameterTypeValues(String paramTypeName, String instTypeName, Long instId)
            throws UnifyException;

    /**
     * Updates parameter values for a definition instance.
     * 
     * @param paramTypeName
     *            the definition name
     * @param instTypeName
     *            the instance type name
     * @param instId
     *            the instance ID
     * @param inputs
     *            the values to set
     * @throws UnifyException
     *             if an error occurs
     */
    void updateParameterValues(String paramTypeName, String instTypeName, Long instId, Inputs inputs)
            throws UnifyException;

    /**
     * Deletes parameter values for a definition instance.
     * 
     * @param paramTypeName
     *            the definition name
     * @param instTypeName
     *            the instance type name
     * @param instId
     *            the instance ID
     * @throws UnifyException
     *             if an error occurs
     */
    void deleteParameterValues(String paramTypeName, String instTypeName, Long instId) throws UnifyException;
}
