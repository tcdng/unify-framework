/*
 * Copyright 2018-2019 The Code Department.
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
package com.tcdng.unify.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.tcdng.unify.core.annotation.Component;

/**
 * Unify command interface.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("unify-commandinterface")
public class UnifyCommandInterface extends AbstractUnifyContainerInterface {

    private ListenerThread listenerThread;

    private int listeningPort;

    @Override
    public int getPort() {
        return listeningPort;
    }

    @Override
    protected void onStartServicingRequests() throws UnifyException {
        if (listenerThread == null) {
            try {
                listenerThread = new ListenerThread();
                listenerThread.start();
            } catch (IOException e) {
                throwOperationErrorException(e);
            }
        }
    }

    @Override
    protected void onStopServicingRequests() throws UnifyException {
        if (listenerThread != null) {
            listenerThread.stopListener();
            listenerThread = null;
        }
    }

    @Override
    protected void onInitialize() throws UnifyException {
        listeningPort = getContainerSetting(short.class, UnifyCorePropertyConstants.APPLICATION_COMMAND_PORT,
                UnifyContainer.DEFAULT_COMMAND_PORT);

    }

    @Override
    protected void onTerminate() throws UnifyException {

    }

    private class ListenerThread extends Thread {

        private ServerSocket serverSocket;

        private boolean runFlag;

        public ListenerThread() throws IOException {
            serverSocket = new ServerSocket(listeningPort);
        }

        public void stopListener() {
            runFlag = false;
        }

        @Override
        public void run() {
            runFlag = true;
            while (runFlag) {
                try {
                    Socket socket = serverSocket.accept();
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String cmd = br.readLine();
                    String param = br.readLine();
                    sendCommand(cmd, param);
                } catch (Exception e) {
                    logError(e);
                }
            }
        }

    }
}
