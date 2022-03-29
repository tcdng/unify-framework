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

package com.tcdng.unify.web.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * HTTP response.
 * 
 * @author The Code Department
 * @since 1.0
 */
public interface HttpResponse {
    
    void setHeader(String key, String val);

    void setContentType(String contentType);

    void setCharacterEncoding(String charset);

    OutputStream getOutputStream() throws IOException;
    
    Writer getWriter() throws IOException;
    
    void setStatus(int status);
    
    void setStatusOk();
    
    void setStatusForbidden();

    void setCookie(String name, String val);

    void setCookie(String name, String val, int maxAge);

    void setCookie(String domain, String path, String name, String val, int maxAge);
    
}
