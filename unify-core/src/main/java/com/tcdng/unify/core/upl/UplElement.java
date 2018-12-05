/*
 * Copyright 2014 The Code Department
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
package com.tcdng.unify.core.upl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyException;

/**
 * Represents a UPL element.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public class UplElement implements UplElementAttributes {

	private Map<String, Object> uplAttributes;

	private Map<String, UplElement> childElements;

	private Map<String, UplAttributeInfo> attributeExtension;

	private Map<String, List<String>> referencedLongNames;

	private Set<String> shallowReferencedLongNames;

	private Set<String> deepReferencedLongNames;

	private UplElement parentElement;

	private String qualifiedName;

	private String elementType;

	private String id;

	private String longName;

	private String shortName;

	private String source;

	private String key;

	private int lineNumber;

	private int uplType;

	public UplElement() {
		this.uplType = UplTypeConstants.DOCUMENT;
		this.uplAttributes = new HashMap<String, Object>();
	}

	public UplElement(int uplType, String source, int lineNumber, String qualifiedName, String elementType, String id) {
		this.uplType = uplType;
		this.source = source;
		this.lineNumber = lineNumber;
		this.qualifiedName = qualifiedName;
		this.elementType = elementType;
		this.id = id;
		this.uplAttributes = new HashMap<String, Object>();
	}

	public UplElement getParentElement() {
		return parentElement;
	}

	public void setParentElement(UplElement parentElement) {
		this.parentElement = parentElement;
	}

	public String getSource() {
		return source;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public String getElementType() {
		return elementType;
	}

	@Override
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int getUplType() {
		return uplType;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public String getParentLongName() {
		if (this.parentElement != null) {
			return this.parentElement.getLongName();
		}
		return null;
	}

	@Override
	public String getComponentName() {
		return this.elementType;
	}

	public boolean isAttribute(String name) {
		return this.uplAttributes.containsKey(name);
	}

	@Override
	public Set<String> getAttributeNames() {
		return this.uplAttributes.keySet();
	}

	@Override
	public Set<UplElementAttributes> getChildElements() {
		if (this.childElements != null) {
			return new HashSet<UplElementAttributes>(this.childElements.values());
		}
		return Collections.emptySet();
	}

	@Override
	public List<String> getShallowReferencedLongNames(String attribute) throws UnifyException {
		if (this.referencedLongNames != null) {
			List<String> list = this.referencedLongNames.get(attribute);
			if (list != null) {
				return list;
			}
		}
		return Collections.emptyList();
	}

	@Override
	public Set<String> getShallowReferencedLongNames() throws UnifyException {
		if (this.shallowReferencedLongNames != null) {
			return this.shallowReferencedLongNames;
		}
		return Collections.emptySet();
	}

	@Override
	public Set<String> getDeepReferencedLongNames() throws UnifyException {
		if (this.deepReferencedLongNames != null) {
			return this.deepReferencedLongNames;
		}
		return Collections.emptySet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttributeValue(Class<T> clazz, String name) throws UnifyException {
		if (!this.uplAttributes.containsKey(name)) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_COMPONENT_ATTRIBUTE_UNKNOWN, this.elementType, name);
		}
		return (T) this.uplAttributes.get(name);
	}

	public void finalizeReferences() throws UnifyException {
		// References
		for (Map.Entry<String, Object> entry : uplAttributes.entrySet()) {
			Object attrValue = entry.getValue();
			if (attrValue instanceof UplElementReferences) {
				this.finalizeReferences(entry.getKey(), ((UplElementReferences) attrValue));
			} else if (attrValue instanceof UplElementReferences[]) {
				for (UplElementReferences uplRef : (UplElementReferences[]) attrValue) {
					this.finalizeReferences(entry.getKey(), uplRef);
				}
			}
		}

		// Child
		if (this.childElements != null) {
			if (this.deepReferencedLongNames == null) {
				this.shallowReferencedLongNames = new HashSet<String>();
				this.deepReferencedLongNames = new HashSet<String>();
			}

			for (UplElement uplElement : this.childElements.values()) {
				String longName = uplElement.getLongName();
				this.shallowReferencedLongNames.add(longName);
				this.deepReferencedLongNames.add(longName);

				uplElement.finalizeReferences();
				this.deepReferencedLongNames.addAll(uplElement.getDeepReferencedLongNames());
			}
		}
	}

	private void finalizeReferences(String attrName, UplElementReferences uplRefs) {
		this.addFinalizedReferences(attrName, uplRefs.getLongNames());
	}

	private void addFinalizedReferences(String attrName, List<String> list) {
		if (this.referencedLongNames == null) {
			this.referencedLongNames = new HashMap<String, List<String>>();
		}

		List<String> oldList = this.referencedLongNames.get(attrName);
		if (oldList == null) {
			this.referencedLongNames.put(attrName, list);
		} else {
			oldList.addAll(list);
		}

		if (this.deepReferencedLongNames == null) {
			this.shallowReferencedLongNames = new HashSet<String>();
			this.deepReferencedLongNames = new HashSet<String>();
		}
		this.shallowReferencedLongNames.addAll(list);
		this.deepReferencedLongNames.addAll(list);
	}

	public Object getAttributeValue(String name) {
		return this.uplAttributes.get(name);
	}

	public void setAttributeValue(String name, Object value) {
		this.uplAttributes.put(name, value);
	}

	public boolean isChildElement(String id) {
		if (this.childElements != null) {
			return this.childElements.containsKey(id);
		}
		return false;
	}

	public Set<String> getChildIds() {
		if (this.childElements != null) {
			return this.childElements.keySet();
		}
		return Collections.emptySet();
	}

	public UplElement getChildElement(String id) {
		if (this.childElements != null) {
			return this.childElements.get(id);
		}
		return null;
	}

	public void merge(UplElement uplElement) throws UnifyException {
		if (this.qualifiedName == null) {
			this.qualifiedName = uplElement.getQualifiedName();
		}
		if (this.elementType == null) {
			this.elementType = uplElement.getElementType();
		}

		if (this.source == null) {
			this.source = uplElement.getSource();
		}

		for (String attribute : uplElement.getAttributeNames()) {
			if (!this.uplAttributes.containsKey(attribute)) {
				this.uplAttributes.put(attribute, uplElement.getAttributeValue(attribute));
			}
		}

		if (uplElement.attributeExtension != null) {
			for (String attribute : uplElement.attributeExtension.keySet()) {
				this.extendAttributes(attribute, uplElement.attributeExtension.get(attribute));
			}
		}

		for (String id : uplElement.getChildIds()) {
			this.addChildElement(uplElement.getChildElement(id), true);
		}
	}

	public void addChildElement(UplElement uplElement, boolean merge) throws UnifyException {
		if (merge) {
			if (this.isChildElement(uplElement.getId())) {
				return;
			}
		} else {
			if (this.isChildElement(uplElement.getId())) {
				throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_ID_DUPLICATE, uplElement.getSource(),
						uplElement.getLineNumber(), uplElement.getId());
			}
		}

		uplElement.setParentElement(this);

		if (this.childElements == null) {
			this.childElements = new HashMap<String, UplElement>();
		}
		this.childElements.put(uplElement.getId(), uplElement);
	}

	public boolean isAttributeExtension(String attribute) {
		if (this.attributeExtension != null) {
			return this.attributeExtension.containsKey(attribute);
		}
		return false;
	}

	public void extendAttributes(String newAttribute, UplAttributeInfo uplAttributeInfo) {
		if (this.attributeExtension == null) {
			this.attributeExtension = new HashMap<String, UplAttributeInfo>();
		}
		this.attributeExtension.put(newAttribute, uplAttributeInfo);
	}

	public UplAttributeInfo getAttributeExtension(String attribute) {
		if (this.attributeExtension != null) {
			return this.attributeExtension.get(attribute);
		}
		return null;
	}

	public String getReferenceLongName(String referenceId) throws UnifyException {
		String longName = null;
		if (this.isChildElement(referenceId)) {
			longName = this.getChildElement(referenceId).getLongName();
		}

		if (longName == null) {
			UplElement topParentElement = this.getParentElement();
			if (topParentElement != null) {
				UplElement topParentChildElement = topParentElement.getChildElement(referenceId);
				if (topParentChildElement != null) {
					longName = topParentChildElement.getLongName();
				} else {
					// Dig deeper using assuming deep reference (NOT long name!)
					int index = referenceId.indexOf('.');
					if (index > 0) {
						String childReferenceId = referenceId.substring(0, index);
						topParentChildElement = topParentElement.getChildElement(childReferenceId);
						if (topParentChildElement != null) {
							longName = topParentChildElement.getReferenceLongName(referenceId.substring(index + 1));
						}
					}
				}
			}
		}

		if (longName == null) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_CHILD_WITH_ELEMENT_ID_NOTFOUND, this.getId(),
					referenceId);
		}
		return longName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(elementType = ").append(this.elementType).append(';');
		sb.append("qualifiedName = ").append(this.qualifiedName).append(';');
		sb.append("id = ").append(this.id).append(';');
		sb.append("source = ").append(this.source).append(';');
		sb.append("lineNumber = ").append(this.lineNumber).append(';');
		sb.append("\nlongName = ").append(this.longName).append(';');
		sb.append("\nuplAttributes = ").append(this.uplAttributes).append(';');
		sb.append("\nattributeExtension = ").append(this.attributeExtension).append(';');
		sb.append("\nchildElements = ").append(this.childElements).append(";)\n");
		return sb.toString();
	}
}
