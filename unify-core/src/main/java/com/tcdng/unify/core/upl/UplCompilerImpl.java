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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tcdng.unify.core.AbstractUnifyComponent;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyComponent;
import com.tcdng.unify.core.UnifyCoreErrorConstants;
import com.tcdng.unify.core.UnifyCorePropertyConstants;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.Configurable;
import com.tcdng.unify.core.annotation.UplAttribute;
import com.tcdng.unify.core.annotation.UplAttributes;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.data.CycleDetector;
import com.tcdng.unify.core.data.FactoryMap;
import com.tcdng.unify.core.data.LocaleFactoryMaps;
import com.tcdng.unify.core.data.LocaleMaps;
import com.tcdng.unify.core.message.ResourceBundles;
import com.tcdng.unify.core.upl.UplUtils.UplComponentClone;
import com.tcdng.unify.core.upl.UplUtils.UplGeneratorTarget;
import com.tcdng.unify.core.util.AnnotationUtils;
import com.tcdng.unify.core.util.DataUtils;
import com.tcdng.unify.core.util.IOUtils;
import com.tcdng.unify.core.util.NameUtils;
import com.tcdng.unify.core.util.ReflectUtils;
import com.tcdng.unify.core.util.StringUtils;
import com.tcdng.unify.core.util.TokenUtils;
import com.tcdng.unify.core.util.UnifyConfigUtils;

/**
 * Default implementation of a UPL simple descriptor compiler.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component(ApplicationComponents.APPLICATION_UPLCOMPILER)
public class UplCompilerImpl extends AbstractUnifyComponent implements UplCompiler {

	@Configurable("512")
	private int multilineBufferSize;

	private LocaleMaps<String, UplElementAttributes> uplElementAttributesMap;

	private LocaleFactoryMaps<String, UplElement> uplElementByDescriptorMap;

	private LocaleFactoryMaps<String, UplDocument> uplDocumentByNameMap;

	private FactoryMap<String, UplAttributesInfo> uplAttributesInfoMap;

	private FactoryMap<String, String> qualifiedNameByNameMap;

	public UplCompilerImpl() {
		uplElementAttributesMap = new LocaleMaps<String, UplElementAttributes>();

		uplElementByDescriptorMap = new LocaleFactoryMaps<String, UplElement>() {
			@Override
			protected UplElement createObject(Locale locale, String descriptor, Object... params) throws Exception {
				ParserContext parserContext = new ParserContext();
				UplElement uplElement = parseElementType(parserContext, UplTypeConstants.DESCRIPTOR, "", 0, descriptor,
						0, true);
				parseDescriptor(parserContext, uplElement, descriptor, uplElement.getElementType().length() + 2);
				postParse(parserContext, uplElement);
				CompilerContext compilerContext = new CompilerContext("", getUnifyComponentContext().getMessages(),
						locale);
				compileAttributes(compilerContext, uplElement);
				postCompile(uplElement, locale);
				registerByAttributesKey(compilerContext, uplElement, descriptor);
				return uplElement;
			}
		};

		uplDocumentByNameMap = new LocaleFactoryMaps<String, UplDocument>(true) {

			@Override
			protected boolean valueStale(String name, UplDocument values) throws Exception {
				return isStaleDocument(name);
			}

			@Override
			protected UplDocument createObject(Locale locale, String name, Object... params) throws Exception {
				logDebug("Compiling UPL document [{0}] [{1}]...", name, locale);
				return innerCompileUplDocument(name, locale);
			}
		};

		uplAttributesInfoMap = new FactoryMap<String, UplAttributesInfo>() {
			@Override
			protected UplAttributesInfo create(String componentName, Object... params) throws Exception {
				Map<String, UplAttributeInfo> uplAttributeInfoMap = new HashMap<String, UplAttributeInfo>();
				for (Class<?> clazz : ReflectUtils
						.getClassHierachyList(getComponentType(UplComponent.class, componentName))) {
					UplAttributes uas = clazz.getAnnotation(UplAttributes.class);
					if (uas != null) {
						for (UplAttribute ua : uas.value()) {
							uplAttributeInfoMap.put(ua.name(), new UplAttributeInfo(ua.type(),
									AnnotationUtils.getAnnotationString(ua.defaultValue()), ua.mandatory()));
						}
					}
				}
				return new UplAttributesInfo(componentName, uplAttributeInfoMap);
			}
		};

		qualifiedNameByNameMap = new FactoryMap<String, String>() {

			@SuppressWarnings("unchecked")
			@Override
			protected String create(String name, Object... params) throws Exception {
				Class<? extends UnifyComponent> type = findComponentType(name);
				if (type != null) {
					StringBuilder qsb = new StringBuilder();
					boolean isAppendSym = false;
					for (Class<?> clazz : ReflectUtils.getClassHierachyList(type)) {
						if (UnifyComponent.class.isAssignableFrom(clazz)) {
							String componentName = UnifyConfigUtils
									.getComponentName((Class<? extends UnifyComponent>) clazz);
							if (componentName != null) {
								if (isAppendSym) {
									qsb.append(' ');
								} else {
									isAppendSym = true;
								}

								qsb.append(componentName);
							}
						}
					}
					return qsb.toString();
				}

				return name;
			}

		};
	}

	@Override
	public UplElementAttributes compileDescriptor(Locale locale, String descriptor) throws UnifyException {
		return uplElementByDescriptorMap.get(locale, descriptor);
	}

	@Override
	public UplDocumentAttributes compileComponentDocuments(Locale locale, String name) throws UnifyException {
		return uplDocumentByNameMap.get(locale, name);
	}

	@Override
	public boolean invalidateStaleDocument(String name) throws UnifyException {
		if (isStaleDocument(name)) {
			uplDocumentByNameMap.removeSubkeys(name);
			return true;
		}

		return false;
	}

	@Override
	public UplElementAttributes getUplElementAttributes(Locale locale, String attributesKey) throws UnifyException {
		UplElementAttributes uplElementAttributes = uplElementAttributesMap.get(locale, attributesKey);
		if (uplElementAttributes == null) {
			forceCompile(locale, attributesKey);
			uplElementAttributes = uplElementAttributesMap.get(locale, attributesKey);
		}

		if (uplElementAttributes == null) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_COMPILER_ATTRIBUTEKEY_UNKNOWN, attributesKey, locale);
		}

		return uplElementAttributes;
	}

	@Override
	public UplAttributesInfo getUplAttributesInfo(String componentName) throws UnifyException {
		return uplAttributesInfoMap.get(componentName);
	}

	@Override
	public void reset() throws UnifyException {
		if (!isProductionMode()) {
			uplElementAttributesMap.clear();
			uplElementByDescriptorMap.clear();
			uplDocumentByNameMap.clear();
			uplAttributesInfoMap.clear();
		}
	}

	@Override
	protected void onInitialize() throws UnifyException {

	}

	@Override
	protected void onTerminate() throws UnifyException {

	}

	private boolean isStaleDocument(String name) throws UnifyException {
		boolean stale = false;
		if (UplUtils.isUplGeneratorTargetName(name)) {
			UplGeneratorTarget ugt = UplUtils.getUplGeneratorTarget(name);
			stale = ((UplGenerator) getComponent(ugt.getGeneratorName())).isNewerVersion(ugt.getTarget());
		}

		return stale;
	}

	private UplDocument innerCompileUplDocument(String componentName, Locale locale) throws UnifyException {
		// Check if clone
		String actComponentName = componentName;
		if (UplUtils.isUplCloneName(componentName)) {
			UplComponentClone ucc = UplUtils.getUplComponentClone(componentName);
			actComponentName = ucc.getComponentName();
		}

		// Check if generator
		String generatedUpl = null;
		if (UplUtils.isUplGeneratorTargetName(actComponentName)) {
			UplGeneratorTarget ugt = UplUtils.getUplGeneratorTarget(actComponentName);
			UplGenerator uplGenerator = (UplGenerator) getComponent(ugt.getGeneratorName());
			actComponentName = uplGenerator.getUplComponentName();
			generatedUpl = uplGenerator.generateUplSource(ugt.getTarget());
		}

		Class<? extends UnifyComponent> typeClass = getComponentType(actComponentName);

		UplDocument uplDocument = new UplDocument();
		parseDocument(new ParserContext(actComponentName), uplDocument, typeClass, generatedUpl);
		uplDocument.generateLongNames(componentName);

		CycleDetector<String> cycleDetector = new CycleDetector<String>();
		updateUplElementReferences(uplDocument, cycleDetector);
		List<String> cycle = cycleDetector.detect();
		if (!cycle.isEmpty()) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_CYCLIC_REFERENCE_DETECTED, cycle.toString());
		}

		CompilerContext compilerContext = new CompilerContext(componentName, getUnifyComponentContext().getMessages(),
				locale);
		compile(compilerContext, uplDocument);

		// Register UPL attributes by attributes key
		registerByAttributesKey(compilerContext, uplDocument, null);
		for (String longName : uplDocument.getLongNames()) {
			registerByAttributesKey(compilerContext, uplDocument.getChildElementByLongName(longName), null);
		}

		// Finalize
		uplDocument.finalizeReferences();

		return uplDocument;
	}

	@SuppressWarnings("unchecked")
	private void updateUplElementReferences(UplElement parentUplElement, CycleDetector<String> cycleDetector)
			throws UnifyException {
		for (String attribute : parentUplElement.getAttributeNames()) {
			Object value = parentUplElement.getAttributeValue(attribute);
			if (value instanceof UplElementReferences) {
				((UplElementReferences) value).setLongNames(parentUplElement, cycleDetector);
			} else if (value instanceof UplElement) {
				updateInlineUplElementReferences(((UplElement) value), parentUplElement);
			} else if (value instanceof List) {
				for (Object listValue : (List<Object>) value) {
					if (listValue instanceof UplElementReferences) {
						((UplElementReferences) listValue).setLongNames(parentUplElement, cycleDetector);
					} else if (listValue instanceof UplElement) {
						updateInlineUplElementReferences(((UplElement) listValue), parentUplElement);
					}
				}
			} else if (value != null && value.getClass().isArray()
					&& UplElement.class.isAssignableFrom(value.getClass().getComponentType())) {
				for (Object arrValue : (Object[]) value) {
					updateInlineUplElementReferences(((UplElement) arrValue), parentUplElement);
				}
			}
		}

		// Update long names for element references for children too
		for (String id : parentUplElement.getChildIds()) {
			updateUplElementReferences(parentUplElement.getChildElement(id), cycleDetector);
		}
	}

	private void updateInlineUplElementReferences(UplElement inlineUplElement, UplElement parentUplElement)
			throws UnifyException {
		for (String inlineAtrribute : inlineUplElement.getAttributeNames()) {
			Object value = inlineUplElement.getAttributeValue(Object.class, inlineAtrribute);
			if (value instanceof UplElementReferences) {
				((UplElementReferences) value).setLongNames(parentUplElement, null);
			} else if (value instanceof UplElementReferences[]) {
				for (UplElementReferences ref : (UplElementReferences[]) value) {
					ref.setLongNames(parentUplElement, null);
				}
			}
		}
	}

	private void compile(CompilerContext compilerContext, UplElement compileUplElement) throws UnifyException {
		// Compile attributes
		compileAttributes(compilerContext, compileUplElement);

		// Compile child elements
		for (String id : compileUplElement.getChildIds()) {
			compile(compilerContext, compileUplElement.getChildElement(id));
		}

		postCompile(compileUplElement, compilerContext.getLocale());
	}

	private void compileAttributes(CompilerContext compilerContext, UplElement compileUplElement)
			throws UnifyException {
		for (String attribute : compileUplElement.getAttributeNames()) {
			Object value = compileValue(compilerContext, compileUplElement, attribute,
					compileUplElement.getAttributeValue(attribute));
			compileUplElement.setAttributeValue(attribute, value);
		}
	}

	@SuppressWarnings("unchecked")
	private Object compileValue(CompilerContext compilerContext, UplElement compileUplElement, String attribute,
			Object value) throws UnifyException {
		if (value instanceof String) {
			String string = (String) value;
			if (TokenUtils.isGuardedTag(string)) {
				string = TokenUtils.extractTokenValue(string);
				value = compileUplElement.getParentElement().getAttributeValue(string);
				if (value == null) {
					throw new UnifyException(UnifyCoreErrorConstants.UPL_MISSING_FOREIGN_REFERENCE,
							compileUplElement.getSource(), compileUplElement.getLineNumber(), compileUplElement.getId(),
							string);
				}
			} else if (TokenUtils.isForeignTag(string)) {
				string = TokenUtils.extractTokenValue(string);
				value = compileUplElement.getParentElement().getAttributeValue(string);
			} else if (TokenUtils.isMessageToken(string)) {
				value = compilerContext.getMessages().getMessage(compilerContext.getLocale(),
						TokenUtils.extractTokenValue(string));
			} else if (TokenUtils.isElementTypeTag(string)) {
				String ext = TokenUtils.extractTokenValue(string);
				if (StringUtils.isBlank(ext)) {
					value = compileUplElement.getQualifiedName();
				} else {
					value = compileUplElement.getQualifiedName() + ' ' + ext;
				}
			}
		} else if (value instanceof UplElement) {
			UplElement valUPLElement = (UplElement) value;
			compile(compilerContext, valUPLElement);
			registerByAttributesKey(compilerContext, valUPLElement, valUPLElement.getSource());
			value = getUplComponent(compilerContext.getLocale(), valUPLElement.getKey());
		} else if (value instanceof List) {
			List<Object> list = new ArrayList<Object>();
			for (Object listValue : (List<Object>) value) {
				Object compiledValue = compileValue(compilerContext, compileUplElement, attribute, listValue);
				if (compiledValue instanceof List) {
					list.addAll((List<Object>) compiledValue);
				} else {
					list.add(compiledValue);
				}
			}
			value = list;
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	private void extendParentAttributes(UplElement childUplElement) throws UnifyException {
		for (String attribute : childUplElement.getAttributeNames()) {
			Object value = childUplElement.getAttributeValue(attribute);
			if (value instanceof String) {
				String valueString = (String) value;
				if (TokenUtils.isGuardedTag(valueString) || TokenUtils.isForeignTag(valueString)) {
					extendParentAttributes(childUplElement, attribute, TokenUtils.extractTokenValue(valueString));
				}
			} else if (value instanceof List) { // Consider Lists
				for (Object arrValue : (List<Object>) value) {
					if (arrValue instanceof String) {
						String valueString = (String) arrValue;
						if (TokenUtils.isGuardedTag(valueString) || TokenUtils.isForeignTag(valueString)) {
							extendParentAttributes(childUplElement, attribute,
									TokenUtils.extractTokenValue(valueString));
						}
					}
				}
			} else if (value instanceof UplElement) { // Consider inline
														// elements
				extendParentAttributes((UplElement) value);
			}
		}
	}

	private void extendParentAttributes(UplElement uplElement, String attribute, String newAttribute)
			throws UnifyException {
		if (uplElement.getParentElement() == null) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_WITH_FOREIGN_MUST_HAVE_PARENT,
					uplElement.getSource(), uplElement.getLineNumber(), uplElement.getElementType(), attribute);
		}

		UplAttributesInfo uplAttributesInfo = uplAttributesInfoMap.get(uplElement.getElementType());

		// Get attribute info from child
		UplAttributeInfo uplAttributeInfo = uplAttributesInfo.getUplAttributeInfo(attribute);
		if (uplAttributeInfo == null) {
			uplAttributeInfo = uplElement.getAttributeExtension(attribute);
		}
		if (uplAttributeInfo == null) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_HAS_NO_SUCH_ATTRIBUTE, uplElement.getSource(),
					uplElement.getLineNumber(), uplElement.getElementType(), attribute);
		}

		// Extend parent attributes
		uplElement.getParentElement().extendAttributes(newAttribute, uplAttributeInfo);
	}

	private void postCompile(UplElement uplElement, Locale locale) throws UnifyException {
		// Convert attribute values to appropriate types
		UplAttributesInfo uplAttributesInfo = uplAttributesInfoMap.get(uplElement.getElementType());
		for (String attribute : uplAttributesInfo.getAttributes()) {
			UplAttributeInfo uplAttributeInfo = uplAttributesInfo.getUplAttributeInfo(attribute);

			// Detect missing mandatory attributes.
			Object value = uplElement.getAttributeValue(attribute);
			if (value == null && uplAttributeInfo.isMandatory()) {
				throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_NO_MANDATORY_ATTRIBUTE,
						uplElement.getSource(), uplElement.getLineNumber(), uplElement.getElementType(), attribute);
			}

			// Convert attribute values
			uplElement.setAttributeValue(attribute,
					DataUtils.convert(uplAttributeInfo.getAttributeClass(), value, null));
		}

		// Detect unknown attributes
		for (String attribute : uplElement.getAttributeNames()) {
			if (!uplAttributesInfo.isAttribute(attribute) && !uplElement.isAttributeExtension(attribute)) {
				throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_HAS_NO_SUCH_ATTRIBUTE,
						uplElement.getSource(), uplElement.getLineNumber(), uplElement.getElementType(), attribute);
			}
		}

		// Set element locale
		uplElement.setAttributeValue("locale", locale);
	}

	private void parseDocument(ParserContext parserContext, UplElement uplElement,
			Class<? extends UnifyComponent> componentClass, String generatedUpl) throws UnifyException {
		// Add generated UPL if necessary 18/08/18
		if (!StringUtils.isBlank(generatedUpl)) {
			logDebug("Parsing generated document for component type [{0}]...", componentClass);
			uplElement.merge(parseUplSource(parserContext, new StringReader(generatedUpl), componentClass.getName()));
		}

		// Loop through class and all super classes
		Class<?> clazz = componentClass;
		while (clazz != null) {
			com.tcdng.unify.core.annotation.UplBinding uba = clazz
					.getAnnotation(com.tcdng.unify.core.annotation.UplBinding.class);
			if (uba != null) {
				logDebug("Parsing binded document for component type [{0}] [{1}]...", componentClass, uba.value());
				uplElement.merge(parseUplSource(parserContext, uba));
			}
			clazz = clazz.getSuperclass();
		}
		postParse(parserContext, uplElement);
	}

	@SuppressWarnings("unchecked")
	private UplElement parseUplSource(ParserContext parserContext, UplBinding uba) throws UnifyException {
		String source = uba.value();
		String workingPath = getUnifyComponentContext().getWorkingPath();
		List<String> categoryList = DataUtils.convert(ArrayList.class, String.class,
				this.getContainerSetting(Object.class, UnifyCorePropertyConstants.APPLICATION_LAYOUT), null);
		if (categoryList != null && !categoryList.isEmpty()) {
			for (String category : categoryList) {
				String categorySource = NameUtils.getFilenameByCategory(source, category);
				if (IOUtils.isResourceFileInstance(categorySource, workingPath)) {
					source = categorySource;
					break;
				}
			}
		}

		Reader reader = new InputStreamReader(IOUtils.openFileResourceInputStream(source, workingPath));
		return parseUplSource(parserContext, reader, source);
	}

	private UplElement parseUplSource(ParserContext parserContext, Reader reader, String srcFileName)
			throws UnifyException {
		UplElement parentUplElement = null;
		try {
			UplElement currentUplElement = null;
			LineNumberReader lineReader = new LineNumberReader(reader);
			StringBuilder lineSb = new StringBuilder(multilineBufferSize);
			String multilineOrigLine = null;
			String line = null;
			int braceDeficit = 0;
			int multilineNumber = 0;
			int multilineOffset = 0;
			boolean isMultiline = false;
			while ((line = lineReader.readLine()) != null) {
				int offset = skipSpace(line, 0);
				if (isComment(line, offset)) {
					continue;
				}

				if (offset >= line.length()) {
					continue;
				}

				UplElement newUplElement = parseElementType(parserContext, UplTypeConstants.DOCUMENT, srcFileName,
						lineReader.getLineNumber(), line, offset, false);
				if (newUplElement != null) {
					if (braceDeficit > 0) {
						throw new UnifyException(UnifyCoreErrorConstants.UPL_DESCRIPTOR_CLOSING_BRACE_EXPECTED,
								srcFileName, multilineNumber, multilineOrigLine);
					}

					offset += newUplElement.getElementType().length() + 2;
					if (!parserContext.isIdGeneratorUsed()) {
						offset += newUplElement.getId().length() + 1;
					}

					if (parentUplElement == null) {
						parentUplElement = newUplElement;
					} else {
						parentUplElement.addChildElement(newUplElement, false);
					}
					currentUplElement = newUplElement;

					multilineOffset = offset;
					multilineOrigLine = line;
					multilineNumber = lineReader.getLineNumber();
				}

				braceDeficit = scanBraces(line, braceDeficit);
				if (braceDeficit < 0) {
					throw new UnifyException(UnifyCoreErrorConstants.UPL_DESCRIPTOR_NO_OPENING_BRACE, srcFileName,
							lineReader.getLineNumber(), line);
				}

				if (braceDeficit == 0) {
					String actLine = line;
					if (isMultiline) {
						lineSb.append(line);
						actLine = lineSb.toString();
						offset = multilineOffset;
						isMultiline = false;
						lineSb.setLength(0);
					}

					offset = skipSpace(actLine, offset);
					if (offset < actLine.length()) {
						if (currentUplElement == null) {
							throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_TYPE_REQUIRED, srcFileName,
									lineReader.getLineNumber(), actLine);
						}
						parseDescriptor(parserContext, currentUplElement, actLine, offset);
					}
				} else {
					multilineOffset = 0;
					isMultiline = true;
					lineSb.append(line);
				}
			}
		} catch (IOException e) {
			throw new UnifyException(e, UnifyCoreErrorConstants.UPL_PARSE_ERROR, srcFileName);
		} finally {
			IOUtils.close(reader);
		}

		for (String elementId : parentUplElement.getChildIds()) {
			// Parse source for child elements that have UPL bindings
			UplElement childUplElement = (UplElement) parentUplElement.getChildElement(elementId);
			Class<? extends UnifyComponent> componentClass = getComponentType(childUplElement.getElementType());
			parseDocument(parserContext, childUplElement, componentClass, null);

			// Extend attributes for parent for child attributes that are
			// foreign
			extendParentAttributes(childUplElement);
		}

		return parentUplElement;
	}

	private void postParse(ParserContext parserContext, UplElement uplElement) throws UnifyException {
		// Set default values where properties have not been set during parse
		UplAttributesInfo uplAttributesInfo = uplAttributesInfoMap.get(uplElement.getComponentName());
		for (String attribute : uplAttributesInfo.getAttributes()) {
			if (!uplElement.isAttribute(attribute)) {
				UplAttributeInfo uplAttributeInfo = uplAttributesInfo.getUplAttributeInfo(attribute);
				Object defaultValue = uplAttributeInfo.getDefaultValue();
				if (defaultValue != null) {
					defaultValue = parseValue(parserContext, uplElement, attribute, (String) defaultValue, null);
				}
				uplElement.setAttributeValue(attribute, defaultValue);
			}
		}
	}

	private void parseDescriptor(ParserContext parserContext, UplElement uplElement, String descriptor, int offset)
			throws UnifyException {
		offset = skipSpace(descriptor, offset);
		while (offset < descriptor.length()) {
			if (isComment(descriptor, offset)) {
				break;
			}

			int colOffset = descriptor.indexOf(':', offset);
			if (colOffset < 0) {
				throw new UnifyException(UnifyCoreErrorConstants.UPL_DESCRIPTOR_COLUMN_EXPECTED, uplElement.getSource(),
						uplElement.getLineNumber(), uplElement.getElementType(), descriptor, offset);
			}

			String attribute = descriptor.substring(offset, colOffset).trim();
			if (attribute.isEmpty()) {
				throw new UnifyException(UnifyCoreErrorConstants.UPL_DESCRIPTOR_PROPERTY_NAME_REQUIRED,
						uplElement.getSource(), uplElement.getLineNumber(), descriptor);
			}

			colOffset = skipSpace(descriptor, ++colOffset);
			offset = skipValue(uplElement, descriptor, colOffset);
			String valueString = descriptor.substring(colOffset, offset);
			Object multiValue = uplElement.getAttributeValue(attribute);
			uplElement.setAttributeValue(attribute,
					parseValue(parserContext, uplElement, attribute, valueString, multiValue));
			offset = skipSpace(descriptor, offset);
		}
	}

	@SuppressWarnings("unchecked")
	private Object parseValue(ParserContext parserContext, UplElement uplElement, String attribute, String valueString,
			Object multiValue) throws UnifyException {
		// Handle special values
		boolean isComponentList = false;
		Object value = valueString;
		if (TokenUtils.isDescriptorToken(valueString)) {
			// Inline descriptors
			String inlineDescriptor = TokenUtils.extractTokenValue(valueString);
			int inlineUplType = UplTypeConstants.DOCUMENT_INLINE;
			if (UplTypeConstants.DESCRIPTOR == uplElement.getUplType()) {
				inlineUplType = UplTypeConstants.DESCRIPTOR_INLINE;
			}

			UplElement inlineUplElement = parseElementType(parserContext, inlineUplType, uplElement.getSource(),
					uplElement.getLineNumber(), inlineDescriptor, 0, true);
			inlineUplElement.setParentElement(uplElement.getParentElement());
			int offset = inlineUplElement.getElementType().length() + 2;
			if (!parserContext.isIdGeneratorUsed()) {
				offset += inlineUplElement.getId().length();
			}

			parseDescriptor(parserContext, inlineUplElement, inlineDescriptor, offset);
			postParse(parserContext, inlineUplElement);
			value = inlineUplElement;
		} else if (TokenUtils.isStringToken(valueString)) {
			value = TokenUtils.extractTokenValue(valueString);
		} else if (TokenUtils.isNameTag(valueString)) {
			value = parserContext.getComponentName() + TokenUtils.extractTokenValue(valueString);
		} else if (TokenUtils.isSettingToken(valueString)) {
			value = getContainerSetting(String.class, TokenUtils.extractTokenValue(valueString).trim());
		} else if (TokenUtils.isJavaConstantToken(valueString)) {
			value = ReflectUtils.getPublicStaticStringConstant(TokenUtils.extractTokenValue(valueString));
		} else if (TokenUtils.isListToken(valueString)
				|| (isComponentList = TokenUtils.isComponentListToken(valueString))) { // Handle
			// lists
			value = StringUtils.whiteSpaceSplit(TokenUtils.extractTokenValue(valueString));
			if (isComponentList) {
				value = new UplElementReferences((String[]) value);
			}
		}

		// Handle multi values
		if (multiValue != null) {
			if (value instanceof List) {
				if (multiValue instanceof List) {
					((List<Object>) value).addAll((List<Object>) multiValue);
				} else {
					((List<Object>) value).add(multiValue);
				}
			} else {
				if (!(multiValue instanceof List)) {
					List<Object> list = new ArrayList<Object>();
					list.add(multiValue);
					multiValue = list;
				}

				((List<Object>) multiValue).add(value);
				value = multiValue;
			}
		}

		return value;
	}

	/**
	 * Constructs and sets a UPL element attributes key
	 * 
	 * @param compilerContext
	 *            the compiler context
	 * @param uplElement
	 *            the UPL element
	 * @throws UnifyException
	 *             if an error occurs
	 */
	private void registerByAttributesKey(CompilerContext compilerContext, UplElement uplElement, String descriptor)
			throws UnifyException {
		String attributesKey = null;
		int uplType = uplElement.getUplType();
		switch (uplType) {
		case UplTypeConstants.DOCUMENT:
			attributesKey = UplUtils.generateUplAttributesKey(uplType, compilerContext.getComponentName(),
					uplElement.getLongName(), null);
			break;
		case UplTypeConstants.DOCUMENT_INLINE:
			attributesKey = UplUtils.generateUplAttributesKey(uplType, compilerContext.getComponentName(),
					uplElement.getId(), null);
			break;
		case UplTypeConstants.DESCRIPTOR:
		case UplTypeConstants.DESCRIPTOR_INLINE:
			attributesKey = UplUtils.generateUplAttributesKey(uplType, null, null, descriptor);
			break;
		}

		// if (!uplElementAttributesMap.containsKey(compilerContext.getLocale(),
		// attributesKey)) {
		// uplElementAttributesMap.put(compilerContext.getLocale(), attributesKey,
		// uplElement);
		// }
		uplElementAttributesMap.put(compilerContext.getLocale(), attributesKey, uplElement);

		uplElement.setKey(attributesKey);
	}

	/**
	 * Tries to compile UPL documents or descriptors based on information encoded in
	 * attributes key. This is triggered when a UPL element has migrated to another
	 * node in a cluster and is trying to fetch its UPL attributes which may not
	 * have been compiled in the new node.
	 * 
	 * @param locale
	 *            the locale to compile for
	 * @param attributesKey
	 *            the attributes key
	 * @throws UnifyException
	 *             if an error occurs
	 */
	private void forceCompile(Locale locale, String attributesKey) throws UnifyException {
		UplAttributesKeyFields uakf = UplUtils.extractUplAtributesKeyFields(attributesKey);
		switch (uakf.getUplType()) {
		case UplTypeConstants.DOCUMENT:
		case UplTypeConstants.DOCUMENT_INLINE:
			compileComponentDocuments(locale, uakf.getComponentName());
			break;
		default: // Free form
			compileDescriptor(locale, uakf.getDescriptor());
		}
	}

	private int skipSpace(String string, int offset) {
		int len = string.length();
		while (offset < len && Character.isWhitespace(string.charAt(offset))) {
			offset++;
		}
		return offset;
	}

	private int scanBraces(String string, int braceDeficit) {
		int len = string.length();
		for (int i = 0; i < len; i++) {
			char ch = string.charAt(i);
			if (ch == '{') {
				braceDeficit++;
			}

			if (ch == '}') {
				braceDeficit--;
			}
		}
		return braceDeficit;
	}

	private int skipValue(UplElement uplElement, String string, int offset) throws UnifyException {
		if (TokenUtils.isTokenPrefix(string, offset)) {
			int braces = 1;
			offset += 3;
			while (offset < string.length()) {
				char ch = string.charAt(offset++);
				if (ch == '}') {
					if ((--braces) == 0) {
						return offset;
					}
				} else if (ch == '{') {
					braces++;
				}
			}
			throw new UnifyException(UnifyCoreErrorConstants.UPL_DESCRIPTOR_CLOSING_BRACES_EXPECTED, braces,
					uplElement.getSource(), uplElement.getLineNumber(), string);
		} else {
			while (offset < string.length()) {
				if (Character.isWhitespace(string.charAt(offset))) {
					break;
				} else {
					offset++;
				}
			}
		}
		return offset;
	}

	private UplElement parseElementType(ParserContext parserContext, int uplType, String source, int lineNumber,
			String descriptor, final int offset, boolean required) throws UnifyException {
		if (descriptor.charAt(offset) == '!') {
			int len = descriptor.length();
			int idEndOffset = 0;
			int typeEndOffset = offset;
			while ((++typeEndOffset) < len) {
				char ch = descriptor.charAt(typeEndOffset);
				if (Character.isWhitespace(ch)) {
					break;
				}

				if (ch == ':') {
					idEndOffset = typeEndOffset;
					while ((++idEndOffset) < len) {
						if (Character.isWhitespace(descriptor.charAt(idEndOffset))) {
							break;
						}
					}
					break;
				}
			}

			String type = descriptor.substring(offset + 1, typeEndOffset);
			String id = null;
			if (idEndOffset > 0) {
				id = descriptor.substring(typeEndOffset + 1, idEndOffset);
				if (id.length() == 0) {
					throw new UnifyException(UnifyCoreErrorConstants.UPL_COMPILER_DESCRIPTOR_ID_EXPECTED, descriptor,
							source);
				}
			}

			parserContext.setIdGeneratorUsed(false);
			if (id == null) {
				id = String.valueOf(parserContext.nextId());
				parserContext.setIdGeneratorUsed(true);
			}

			String qualifiedName = qualifiedNameByNameMap.get(type);
			if (uplType == UplTypeConstants.DESCRIPTOR_INLINE || uplType == UplTypeConstants.DOCUMENT_INLINE) {
				return new UplElement(uplType, descriptor, lineNumber, qualifiedName, type, id);
			}

			return new UplElement(uplType, source, lineNumber, qualifiedName, type, id);
		}

		if (required) {
			throw new UnifyException(UnifyCoreErrorConstants.UPL_ELEMENT_TYPE_TOKEN_REQUIRED, source, lineNumber,
					descriptor);
		}
		return null;
	}

	private boolean isComment(String string, int offset) {
		return string.startsWith("//", offset);
	}

	private class ParserContext {

		private String componentName;

		private int idCounter;

		private boolean idGeneratorUsed;

		public ParserContext(String componentName) {
			this.componentName = componentName;
		}

		public ParserContext() {

		}

		public String getComponentName() {
			return componentName;
		}

		public int nextId() {
			return idCounter++;
		}

		public boolean isIdGeneratorUsed() {
			return idGeneratorUsed;
		}

		public void setIdGeneratorUsed(boolean idGeneratorUsed) {
			this.idGeneratorUsed = idGeneratorUsed;
		}
	}

	private class CompilerContext {

		private String componentName;

		private ResourceBundles messages;

		private Locale locale;

		public CompilerContext(String componentName, ResourceBundles messages, Locale locale) {
			this.componentName = componentName;
			this.messages = messages;
			this.locale = locale;
		}

		public String getComponentName() {
			return componentName;
		}

		public ResourceBundles getMessages() {
			return messages;
		}

		public Locale getLocale() {
			return locale;
		}
	}
}
