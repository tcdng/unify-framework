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
package com.tcdng.unify.web;

import java.io.IOException;
import java.io.Writer;

import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.web.constant.ClientRequestType;
import com.tcdng.unify.web.constant.HttpResponseConstants;
import com.tcdng.unify.web.constant.Secured;
import com.tcdng.unify.web.data.Response;

/**
 * Convenient base class for HTTP CRUD controllers.
 * 
 * @author The Code Department
 * @since 1.0
 */
public abstract class AbstractHttpCRUDController extends AbstractHttpClientController {

	private final String contentType;

	private Response noResourceIdResponse;

	public AbstractHttpCRUDController(String contentType) {
		super(Secured.FALSE);
		this.contentType = contentType;
	}

	@Override
	public final void process(ClientRequest request, ClientResponse response) throws UnifyException {
		Response _response = null;
		try {
			String _contentType = request.getRequestHeaders().getHeader("Content-Type");
			if (contentType.equals(_contentType)) {
				final ClientRequestType clientRequestType = request.getType();
				final ControllerPathParts parts = request.getRequestPathParts().getControllerPathParts();
				final String resource = parts.getActionName();
				final String basePath = parts.getControllerName();
				HttpCRUDControllerProcessor _processor = processor(basePath, resource);
				switch (clientRequestType) {
				case DELETE:
					if (_processor.isSupportDelete()) {
						_response = parts.isWithResourceId()
								? _processor.delete(request.getRequestHeaders(), request.getParameters(),
										parts.getResourceId())
								: getNoResourceIdResponse();
					}
					break;
				case GET:
					if (_processor.isSupportRead()) {
						if (parts.isWithOperation()) {
							if ("count".equals(parts.getOperation())) {
								_response = _processor.count(request.getRequestHeaders(), request.getParameters());
							}
						} else {
							_response = _processor.read(request.getRequestHeaders(), request.getParameters(),
									parts.getResourceId());
						}
					}
					break;
				case POST:
					if (_processor.isSupportCreate()) {
						_response = _processor.create(request.getRequestHeaders(), request.getParameters(),
								request.getText());
					}
					break;
				case PUT:
				case PATCH:
					if (_processor.isSupportUpdate()) {
						_response = parts.isWithResourceId()
								? _processor.update(request.getRequestHeaders(), request.getParameters(),
										request.getText(), parts.getResourceId())
								: getNoResourceIdResponse();
					}
					break;
				case HEAD:
				case OPTIONS:
				case TRACE:
				default:
					break;
				}

				if (_response == null) {
					_response = getErrorResponse(HttpResponseConstants.METHOD_NOT_ALLOWED, "Method not allowed.",
							"Request method \'" + clientRequestType + "\' not supported.");
				}
			} else {
				_response = getErrorResponse(HttpResponseConstants.NOT_ACCEPTABLE, "Content not acceptable.",
						"Request content type \'" + _contentType + "\' not acceptable.");
			}
		} catch (Exception e) {
			logError(e);
			_response = getErrorResponse(HttpResponseConstants.INTERNAL_SERVER_ERROR, "Internal server error.",
					e.getMessage());
		} finally {
			response.setStatus(_response.getStatus());
			response.setContentType(contentType);
			if (!response.isOutUsed()) {
				Writer writer = response.getWriter();
				try {
					writer.write(_response.getResponseBody());
				} catch (IOException e) {
					logError(e);
					throwOperationErrorException(e);
				}
			}

			response.close();
		}
	}

	@Override
	public final void ensureContextResources(ControllerPathParts controllerPathParts) throws UnifyException {

	}

	/**
	 * Gets Http CRUD controller processor.
	 * 
	 * @param basePath the base path
	 * @param resource the resource
	 * @return the processor
	 * @throws UnifyException if an error occurs
	 */
	protected abstract HttpCRUDControllerProcessor processor(String basePath, String resource) throws UnifyException;

	/**
	 * Gets response from supplied error parameters.
	 * 
	 * @param status    the status
	 * @param errorText the error text
	 * @param errorMsg  the error message
	 * @return the error response
	 * @throws UnifyException if an error occurs
	 */
	protected abstract Response getErrorResponse(int status, String errorText, String errorMsg) throws UnifyException;

	private Response getNoResourceIdResponse() throws UnifyException {
		if (noResourceIdResponse == null) {
			synchronized (this) {
				if (noResourceIdResponse == null) {
					noResourceIdResponse = getErrorResponse(HttpResponseConstants.BAD_REQUEST, "Missing resource ID.",
							"Resource ID not present as path parameter.");
				}
			}
		}

		return noResourceIdResponse;
	}
}
