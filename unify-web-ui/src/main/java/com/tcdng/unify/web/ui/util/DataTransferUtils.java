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

package com.tcdng.unify.web.ui.util;

import com.tcdng.unify.web.ui.DataTransferBlock;
import com.tcdng.unify.web.ui.DataTransferHeader;

/**
 * Data transfer utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class DataTransferUtils {

    private DataTransferUtils() {
        
    }

    public static DataTransferBlock createTransferBlock(String transferId) {
        return DataTransferUtils.createTransferBlock(transferId, null);
    }
    
    public static DataTransferBlock createTransferBlock(String transferId, DataTransferHeader header) {
      DataTransferBlock transferBlock = null;
      String id = transferId;
      int cindex = 0;
      do {
          int iindex = -1;
          int dindex = 0;
          cindex = id.lastIndexOf('.');
          if (cindex > 0) {
              dindex = id.indexOf('d', cindex);
          } else {
              dindex = id.lastIndexOf('d');
          }

          if (dindex > 0) {
              iindex = Integer.parseInt(id.substring(dindex + 1));
              id = id.substring(0, dindex);
          }
          
          transferBlock = new DataTransferBlock(header, id, iindex, transferBlock);
          if (cindex > 0) {
              id = id.substring(0, cindex);
          }
      } while (cindex > 0);
      
      return transferBlock;
    }
}
