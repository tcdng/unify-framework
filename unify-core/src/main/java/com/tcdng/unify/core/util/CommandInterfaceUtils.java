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
package com.tcdng.unify.core.util;

import java.io.PrintWriter;
import java.net.Socket;

/**
 * Command interface utilities.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public final class CommandInterfaceUtils {

    private CommandInterfaceUtils() {

    }

    public static void sendCommand(String host, int port, String command, String param) throws Exception {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.write(command);
            writer.write(param);
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.close(writer);
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
