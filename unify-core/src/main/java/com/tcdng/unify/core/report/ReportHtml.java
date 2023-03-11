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
package com.tcdng.unify.core.report;

import com.tcdng.unify.core.constant.PageSizeType;
import com.tcdng.unify.core.util.StringUtils;

/**
 * Report HTML.
 * 
 * @author The Code Department
 * @since 1.0
 */
public class ReportHtml {

    private String name;

    private String html;

    private String resourceBaseUri;

    private ReportHtml(String name, String html, String resourceBaseUri) {
    	this.name = name;
        this.html = html;
        this.resourceBaseUri = resourceBaseUri;
    }

    public String getName() {
		return name;
	}

	public String getHtml() {
        return html;
    }
	
	public String getResourceBaseUri() {
		return resourceBaseUri;
	}

	public static Builder newBuilder(ReportPageProperties pageProperties, String name) {
		return new Builder(pageProperties, name);
	}
	
	public static class Builder {

		private ReportPageProperties pageProperties;
		
	    private String name;
	    
	    private String completeHtml;

	    private String style;

	    private String bodyContent;
	    
	    public Builder(ReportPageProperties pageProperties, String name) {
	    	this.pageProperties = pageProperties;
	    	this.name = name;
	    }
	    
	    public Builder completeHtml(String completeHtml) {
	    	this.completeHtml = completeHtml;
	    	return this;
	    }
	    
	    public Builder style(String style) {
	    	this.style = style;
	    	return this;
	    }
	    
	    public Builder bodyContent(String bodyContent) {
	    	this.bodyContent = bodyContent;
	    	return this;
	    }
	    
	    public ReportHtml build() {
	    	if (!StringUtils.isBlank(bodyContent)) {
	    		StringBuilder sb = new StringBuilder();
	    		sb.append("<html>");
	    		sb.append("<head>");
	    		sb.append("<style>");
	    		sb.append("@page {");
	    		// Size
	    		sb.append("size: ");
	    		PageSizeType size = pageProperties.getSize();
	    		if (size.isCustom()) {
		    		sb.append(pageProperties.getPageWidth()).append("px ");
		    		sb.append(pageProperties.getPageHeight()).append("px;");
	    		} else {
		    		sb.append(size.code());
		    		if (pageProperties.isLandscape()) {
			    		sb.append(" landscape;");
		    		} else {
			    		sb.append(" portrait;");
		    		}
	    		}
	    		
	    		//Margin
	    		if (pageProperties.getMarginTop() > 0) {
		    		sb.append("margin-top: ").append(pageProperties.getMarginTop()).append("px;");
	    		}
	    		
	    		if (pageProperties.getMarginRight() > 0) {
		    		sb.append("margin-right: ").append(pageProperties.getMarginRight()).append("px;");
	    		}
	    		
	    		if (pageProperties.getMarginBottom() > 0) {
		    		sb.append("margin-bottom: ").append(pageProperties.getMarginBottom()).append("px;");
	    		}
	    		
	    		if (pageProperties.getMarginLeft() > 0) {
		    		sb.append("margin-left: ").append(pageProperties.getMarginLeft()).append("px;");
	    		}
	    		
	    		sb.append("}");	    		
	    		if (!StringUtils.isBlank(style)) {
		    		sb.append(style);
	    		}

	    		sb.append("</style>");
	    		sb.append("</head>");
	    		sb.append("<body>");
	    		sb.append(bodyContent);
	    		sb.append("</body>");
	    		sb.append("</html>");	    		
	    		return new ReportHtml(name, sb.toString(), pageProperties.getResourceBaseUri());
	    	} else if (!StringUtils.isBlank(completeHtml)) {
	    		return new ReportHtml(name, completeHtml, pageProperties.getResourceBaseUri());
	    	}
	    	
	    	throw new IllegalArgumentException("You must provide either complete html or body content.");
	    }
	}
}
