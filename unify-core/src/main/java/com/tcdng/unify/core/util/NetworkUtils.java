/*
 * Copyright 2018-2023 The Code Department.
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
package com.tcdng.unify.core.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Provides network utility methods.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class NetworkUtils {

    private NetworkUtils() {

    }

    public static String constructURL(String baseUrl, String path) throws UnifyException {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        if (!path.startsWith("/")) {
            return baseUrl + "/" + path;
        }

        return baseUrl + path;
    }

    public static String constructURL(String scheme, String host, short port, String context, String path)
            throws UnifyException {
        try {
            URL url = null;
            if (port == 0) {
                url = new URL(scheme, host, context + path);
            } else {
                url = new URL(scheme, host, port, context + path);
            }
            return url.toString();
        } catch (MalformedURLException e) {
            throw new UnifyException(UnifyCoreErrorConstants.NETWORK_OPERATION_ERROR, e);
        }
    }

    public static String getLocalHostIpAddress() throws UnifyException {
        StringBuilder sb = new StringBuilder();
        try {
            String address = InetAddress.getLocalHost().getHostAddress();
            sb.append(address);
        } catch (UnknownHostException e) {
            throw new UnifyException(UnifyCoreErrorConstants.NETWORK_OPERATION_ERROR, e);
        }
        return sb.toString();
    }

    public static String getLocalHostMacAddress() throws UnifyException {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            if (mac != null) {
                for (int i = 0; i < mac.length; i++) {
                    sb.append(String.format("%02X", mac[i]));
                    if (i < (mac.length - 1)) {
                        sb.append('-');
                    }
                }
            }
        } catch (Exception e) {
            throw new UnifyException(UnifyCoreErrorConstants.NETWORK_OPERATION_ERROR, e);
        }
        return sb.toString();
    }
}
