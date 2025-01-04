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

package com.tcdng.unify.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tcdng.unify.common.annotation.ColumnOverride;
import com.tcdng.unify.common.annotation.ColumnType;
import com.tcdng.unify.common.annotation.Index;
import com.tcdng.unify.common.annotation.Table;
import com.tcdng.unify.common.annotation.UniqueConstraint;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UnifyOperationException;
import com.tcdng.unify.core.annotation.Child;
import com.tcdng.unify.core.annotation.ChildList;
import com.tcdng.unify.core.annotation.Column;
import com.tcdng.unify.core.annotation.DynamicEntityType;
import com.tcdng.unify.core.annotation.ForeignKey;
import com.tcdng.unify.core.annotation.ListOnly;
import com.tcdng.unify.core.annotation.Mapped;
import com.tcdng.unify.core.annotation.TableExt;
import com.tcdng.unify.core.annotation.TableName;
import com.tcdng.unify.core.annotation.TenantId;
import com.tcdng.unify.core.constant.DynamicEntityFieldType;
import com.tcdng.unify.core.database.dynamic.DynamicChildFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicChildListFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicColumnFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicEntityInfo;
import com.tcdng.unify.core.database.dynamic.DynamicFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicForeignKeyFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicIndexInfo;
import com.tcdng.unify.core.database.dynamic.DynamicListOnlyFieldInfo;
import com.tcdng.unify.core.database.dynamic.DynamicUniqueConstraintInfo;

/**
 * Dynamic entity utilities.
 * 
 * @author The Code Department
 * @since 1.0
 */
public final class DynamicEntityUtils {

	private DynamicEntityUtils() {

	}

	public static String generateEntityJavaClassSource(DynamicEntityInfo dynamicEntityInfo) throws UnifyException {
		switch (dynamicEntityInfo.getType()) {
		case TABLE:
		case TABLE_EXT:
			return DynamicEntityUtils.generateTableEntityJavaClassSource(dynamicEntityInfo);
		case VIEW:
		case INFO_ONLY:
			throw new UnifyOperationException(DynamicEntityUtils.class,
					"View or information-only entity type is unsupported for class source generation.");
		default:
			throw new UnifyOperationException(DynamicEntityUtils.class,
					"Entity type not specified for class source generation.");
		}
	}

	private static String generateTableEntityJavaClassSource(DynamicEntityInfo dynamicEntityInfo)
			throws UnifyException {
		StringBuilder esb = new StringBuilder();
		StringBuilder fsb = new StringBuilder();
		StringBuilder msb = new StringBuilder();
		Set<String> importSet = new HashSet<String>();
		final boolean managed = dynamicEntityInfo.isManaged();

		// Evaluate fields
		Set<String> fieldNames = new HashSet<String>();
		List<DynamicFieldInfo> overrides = new ArrayList<DynamicFieldInfo>();
		List<String> descList = null;
		for (DynamicFieldInfo dynamicFieldInfo : dynamicEntityInfo.getFieldInfos()) {
			final String fieldName = dynamicFieldInfo.getFieldName();
			final String capField = StringUtils.capitalizeFirstLetter(fieldName);
			fieldNames.add(fieldName);
			if (dynamicFieldInfo.isDescriptive()) {
				if (descList == null) {
					descList = new ArrayList<String>();
				}

				descList.add(capField);
			}

			if (dynamicFieldInfo.isGeneration()) {
				TypeInfo enumEntityInfo = null;
				if (dynamicFieldInfo.isEnum()) {
					enumEntityInfo = new TypeInfo(dynamicFieldInfo.getEnumClassName());
					importSet.add(dynamicFieldInfo.getEnumClassName());
				}

				final DynamicEntityFieldType type = dynamicFieldInfo.getFieldType();
				String childClass = null;
				if (managed) {
					if (type.isForeignKey()) {
						DynamicForeignKeyFieldInfo fkInfo = (DynamicForeignKeyFieldInfo) dynamicFieldInfo;
						DynamicEntityUtils.generateForeignKeyAnnotation(fsb, fkInfo);
						importSet.add(ForeignKey.class.getCanonicalName());
						if (!fkInfo.isEnum()) {
							if (!dynamicEntityInfo.getClassName()
									.equals(fkInfo.getParentDynamicEntityInfo().getClassName())) {
							}
						}
					} else if (type.isTableColumn()) {
						DynamicEntityUtils.generateColumnAnnotation(fsb, (DynamicColumnFieldInfo) dynamicFieldInfo);
						if (!DataUtils.isMappedColumnType(dynamicFieldInfo.getDataType().columnType())) {
							importSet.add(ColumnType.class.getCanonicalName());
						}

						importSet.add(Column.class.getCanonicalName());
						if (dynamicFieldInfo.isTenantId()) {
							importSet.add(TenantId.class.getCanonicalName());
						}

						if (dynamicFieldInfo.isWithMapping()) {
							importSet.add(Mapped.class.getCanonicalName());
						}
					} else if (type.isChild()) {
						DynamicChildFieldInfo childInfo = (DynamicChildFieldInfo) dynamicFieldInfo;
						DynamicEntityUtils.generateChildAnnotation(fsb, childInfo);
						importSet.add(Child.class.getCanonicalName());
						childClass = childInfo.getChildDynamicEntityInfo().getClassName();
					} else if (type.isChildList()) {
						DynamicChildListFieldInfo childListInfo = (DynamicChildListFieldInfo) dynamicFieldInfo;
						DynamicEntityUtils.generateChildListAnnotation(fsb, childListInfo);
						importSet.add(List.class.getCanonicalName());
						importSet.add(ChildList.class.getCanonicalName());
						childClass = childListInfo.getChildDynamicEntityInfo().getClassName();
					} else {
						DynamicEntityUtils.generateLisOnlyAnnotation(fsb, (DynamicListOnlyFieldInfo) dynamicFieldInfo);
						importSet.add(ListOnly.class.getCanonicalName());
					}
				} else {
					if (type.isForeignKey()) {
						DynamicForeignKeyFieldInfo fkInfo = (DynamicForeignKeyFieldInfo) dynamicFieldInfo;
						DynamicEntityUtils.generateForeignKeyAnnotation(fsb, fkInfo);
						importSet.add(ForeignKey.class.getCanonicalName());
						if (!fkInfo.isEnum()) {
							if (!dynamicEntityInfo.getClassName()
									.equals(fkInfo.getParentDynamicEntityInfo().getClassName())) {
							}
						}

					} else if (type.isTableColumn()) {
						DynamicEntityUtils.generateColumnAnnotation(fsb, (DynamicColumnFieldInfo) dynamicFieldInfo);
						if (!DataUtils.isMappedColumnType(dynamicFieldInfo.getDataType().columnType())) {
							importSet.add(ColumnType.class.getCanonicalName());
						}

						importSet.add(Column.class.getCanonicalName());
						if (dynamicFieldInfo.isTenantId()) {
							importSet.add(TenantId.class.getCanonicalName());
						}

						if (dynamicFieldInfo.isWithMapping()) {
							importSet.add(Mapped.class.getCanonicalName());
						}
					} else if (type.isChild()) {
						DynamicChildFieldInfo childInfo = (DynamicChildFieldInfo) dynamicFieldInfo;
						childClass = childInfo.getChildDynamicEntityInfo().getClassName();
					} else if (type.isChildList()) {
						DynamicChildListFieldInfo childListInfo = (DynamicChildListFieldInfo) dynamicFieldInfo;
						importSet.add(List.class.getCanonicalName());
						childClass = childListInfo.getChildDynamicEntityInfo().getClassName();
					} else if (type.isListOnly()) {
						DynamicEntityUtils.generateLisOnlyAnnotation(fsb, (DynamicListOnlyFieldInfo) dynamicFieldInfo);
						importSet.add(ListOnly.class.getCanonicalName());
					}
				}

				String simpleName = null;
				if (type.isChild()) {
					simpleName = childClass;
				} else if (type.isChildList()) {
					simpleName = "List<" + childClass + ">";
				} else {
					Class<?> javaClass = dynamicFieldInfo.getDataType().javaClass();
					if (Date.class.equals(javaClass)) {
						importSet.add(Date.class.getCanonicalName());
					} else if (BigDecimal.class.equals(javaClass)) {
						importSet.add(BigDecimal.class.getCanonicalName());
					}

					simpleName = enumEntityInfo != null ? enumEntityInfo.getSimpleName() : javaClass.getSimpleName();
				}

				fsb.append(" private ").append(simpleName).append(" ").append(fieldName).append(";\n");

				msb.append(" public ").append(simpleName).append(" get").append(capField).append("() {return ")
						.append(fieldName).append(";}\n");
				msb.append(" public void set").append(capField).append("(").append(simpleName).append(" ")
						.append(fieldName).append(") {this.").append(fieldName).append(" = ").append(fieldName)
						.append(";}\n");
			} else {
				if (!StringUtils.isBlank(dynamicFieldInfo.getColumnName())) {
					overrides.add(dynamicFieldInfo);
				}
			}
		}

		// Construct class
		TypeInfo baseEntityInfo = new TypeInfo(dynamicEntityInfo.getBaseClassName());
		TypeInfo typeInfo = new TypeInfo(dynamicEntityInfo.getClassName());
		esb.append("package ").append(typeInfo.getPackageName()).append(";\n");
		List<String> importList = new ArrayList<String>(importSet);
		Collections.sort(importList);
		for (String imprt : importList) {
			esb.append("import ").append(imprt).append(";\n");
		}

		esb.append("import ").append(baseEntityInfo.getCanonicalName()).append(";\n");
		final boolean additional = !overrides.isEmpty() || dynamicEntityInfo.isWithUniqueConstraints()
				|| dynamicEntityInfo.isWithIndexes();
		if (!overrides.isEmpty()) {
			esb.append("import ").append(ColumnOverride.class.getCanonicalName()).append(";\n");
		}

		if (dynamicEntityInfo.isWithIndexes()) {
			esb.append("import ").append(Index.class.getCanonicalName()).append(";\n");
		}

		if (dynamicEntityInfo.isWithUniqueConstraints()) {
			esb.append("import ").append(UniqueConstraint.class.getCanonicalName()).append(";\n");
		}

		if (managed) {
			if (DynamicEntityType.TABLE.equals(dynamicEntityInfo.getType())) {
				esb.append("import ").append(Table.class.getCanonicalName()).append(";\n");
				if (additional) {
					esb.append("@Table(name = \"").append(dynamicEntityInfo.getTableName()).append("\",\n");
					boolean appendSym = DynamicEntityUtils.generateOverrides(esb, overrides);
					DynamicEntityUtils.generateIndexes(esb, dynamicEntityInfo.getIndexes(), appendSym);
					DynamicEntityUtils.generateUniqueConstraints(esb, dynamicEntityInfo.getUniqueConstraints(),
							appendSym);
					esb.append(")\n");
				} else {
					esb.append("@Table(\"").append(dynamicEntityInfo.getTableName()).append("\")\n");
				}
			} else {
				esb.append("import ").append(TableExt.class.getCanonicalName()).append(";\n");
				esb.append("@TableExt\n");
			}
		} else {
			if (DynamicEntityType.TABLE.equals(dynamicEntityInfo.getType())) {
				esb.append("import ").append(TableName.class.getCanonicalName()).append(";\n");
				if (!overrides.isEmpty()) {
					esb.append("import ").append(ColumnOverride.class.getCanonicalName()).append(";\n");
					esb.append("@TableName(name = \"").append(dynamicEntityInfo.getTableName()).append("\",\n");
					DynamicEntityUtils.generateOverrides(esb, overrides);
					esb.append(")\n");
				} else {
					esb.append("@TableName(\"").append(dynamicEntityInfo.getTableName()).append("\")\n");
				}
			}
		}

		esb.append("public class ").append(typeInfo.getSimpleName()).append(" extends ")
				.append(baseEntityInfo.getSimpleName()).append(" {\n");
		esb.append(fsb);
		if (!fieldNames.contains("description")) {
			esb.append(" public String getDescription(){\n");
			if (descList != null) {
				esb.append("  StringBuilder sb = new StringBuilder();\n");
				boolean appendSym = false;
				for (String cFieldName : descList) {
					if (appendSym) {
						esb.append("  sb.append(\" \");\n");
					} else {
						appendSym = true;
					}

					esb.append("  sb.append(get").append(cFieldName).append("());\n");
				}

				esb.append("  return sb.toString();\n");
			} else {
				esb.append("  return null;\n");
			}

			esb.append(" }\n");
		}

		esb.append(msb);
		esb.append("}\n");
		return esb.toString();
	}

	private static boolean generateOverrides(StringBuilder esb, List<DynamicFieldInfo> overrides) {
		if (!overrides.isEmpty()) {
			esb.append("\tcolumnOverrides = {\n");
			boolean appendSym = false;
			for (DynamicFieldInfo dynamicFieldInfo : overrides) {
				if (appendSym) {
					esb.append("),\n");
				} else {
					appendSym = true;
				}

				esb.append("\t\t@ColumnOverride(");
				esb.append("field = \"").append(dynamicFieldInfo.getFieldName()).append("\",");
				esb.append("name = \"").append(dynamicFieldInfo.getColumnName()).append("\"");
			}
			esb.append(")\n");
			esb.append("\t}\n");

			return true;
		}

		return false;
	}

	private static boolean generateIndexes(StringBuilder esb, List<DynamicIndexInfo> indexes, boolean appendSym) {
		if (!indexes.isEmpty()) {
			if (appendSym) {
				esb.append(",");
			}

			esb.append("\tindexes = {\n");
			boolean appendSym0 = false;
			for (DynamicIndexInfo dynamicIndexInfo : indexes) {
				if (appendSym0) {
					esb.append("),\n");
				} else {
					appendSym0 = true;
				}

				esb.append("\t\t@Index(value = {");
				boolean appendSym1 = false;
				for (String field : dynamicIndexInfo.getFields()) {
					if (appendSym1) {
						esb.append(",");
					} else {
						appendSym1 = true;
					}
					esb.append("\"").append(field).append("\"");
				}
				esb.append("}, unique = ").append(dynamicIndexInfo.isUnique());
			}
			esb.append(")\n");
			esb.append("\t}\n");

			return true;
		}

		return false;
	}

	private static boolean generateUniqueConstraints(StringBuilder esb,
			List<DynamicUniqueConstraintInfo> uniqueConstraints, boolean appendSym) {
		if (!uniqueConstraints.isEmpty()) {
			if (appendSym) {
				esb.append(",");
			}

			esb.append("\tuniqueConstraints = {\n");
			boolean appendSym0 = false;
			for (DynamicUniqueConstraintInfo dynamicUniqueConstraintInfo : uniqueConstraints) {
				if (appendSym0) {
					esb.append("),\n");
				} else {
					appendSym0 = true;
				}

				esb.append("\t\t@UniqueConstraint({");
				boolean appendSym1 = false;
				for (String field : dynamicUniqueConstraintInfo.getFields()) {
					if (appendSym1) {
						esb.append(",");
					} else {
						appendSym1 = true;
					}
					esb.append("\"").append(field).append("\"");
				}
				esb.append("}");
			}
			esb.append(")\n");
			esb.append("\t}\n");

			return true;
		}

		return false;
	}

	private static void generateChildAnnotation(StringBuilder fsb, DynamicChildFieldInfo childInfo) {
		if (childInfo.isEditable()) {
			fsb.append(" @Child\n");
		} else {
			fsb.append(" @Child(editable = false)\n");
		}
	}

	private static void generateChildListAnnotation(StringBuilder fsb, DynamicChildListFieldInfo childListInfo) {
		fsb.append(" @ChildList(listType = ");
		fsb.append(childListInfo.getChildDynamicEntityInfo().getClassName());
		fsb.append(".class");
		if (!childListInfo.isEditable()) {
			fsb.append(", editable = false");
		}
		fsb.append(")\n");
	}

	private static void generateForeignKeyAnnotation(StringBuilder fsb,
			DynamicForeignKeyFieldInfo dynamicForeignKeyFieldInfo) {
		fsb.append(" @ForeignKey");
		boolean appendSym = false;
		if (!dynamicForeignKeyFieldInfo.isEnum()) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("type = ").append(dynamicForeignKeyFieldInfo.getParentDynamicEntityInfo().getClassName())
					.append(".class");
		}

		if (!StringUtils.isBlank(dynamicForeignKeyFieldInfo.getColumnName())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("name = \"").append(dynamicForeignKeyFieldInfo.getColumnName()).append("\"");
		}

		if (!StringUtils.isBlank(dynamicForeignKeyFieldInfo.getDefaultVal())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("defaultVal = \"").append(dynamicForeignKeyFieldInfo.getDefaultVal()).append("\"");
		}

		if (dynamicForeignKeyFieldInfo.isUnlinked()) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("childKey = false");
		}

		if (dynamicForeignKeyFieldInfo.isNullable()) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("nullable = true");
		}

		if (appendSym) {
			fsb.append(")");
		}

		fsb.append("\n");
	}

	private static void generateColumnAnnotation(StringBuilder fsb, DynamicColumnFieldInfo dynamicColumnFieldInfo) {
		if (dynamicColumnFieldInfo.isEnum()) {
			fsb.append(" @Column");
			boolean appendSym = false;
			if (!StringUtils.isBlank(dynamicColumnFieldInfo.getColumnName())) {
				appendSym = appendSymbol(fsb, appendSym);
				fsb.append("name = \"").append(dynamicColumnFieldInfo.getColumnName()).append("\"");
			}

			if (dynamicColumnFieldInfo.isNullable()) {
				appendSym = appendSymbol(fsb, appendSym);
				fsb.append("nullable = true");
			}

			if (appendSym) {
				fsb.append(")");
			}

			fsb.append("\n");
			return;
		}

		if (dynamicColumnFieldInfo.isTenantId()) {
			fsb.append(" @TenantId\n");
		}

		if (dynamicColumnFieldInfo.isWithMapping()) {
			fsb.append(" @Mapped(\"" + dynamicColumnFieldInfo.getMapping() + "\")\n");
		}

		fsb.append(" @Column");
		boolean appendSym = false;
		if (!DataUtils.isMappedColumnType(dynamicColumnFieldInfo.getDataType().columnType())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("type = ColumnType.").append(dynamicColumnFieldInfo.getDataType().columnType());
		}

		if (!StringUtils.isBlank(dynamicColumnFieldInfo.getColumnName())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("name = \"").append(dynamicColumnFieldInfo.getColumnName()).append("\"");
		}

		if (!StringUtils.isBlank(dynamicColumnFieldInfo.getTransformer())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("transformer = \"").append(dynamicColumnFieldInfo.getTransformer()).append("\"");
		}

		if (!StringUtils.isBlank(dynamicColumnFieldInfo.getDefaultVal())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("defaultVal = \"").append(dynamicColumnFieldInfo.getDefaultVal()).append("\"");
		}

		if (dynamicColumnFieldInfo.getLength() > 0) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("length = ").append(dynamicColumnFieldInfo.getLength());
		}

		if (dynamicColumnFieldInfo.getPrecision() > 0) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("precision = ").append(dynamicColumnFieldInfo.getPrecision());
		}

		if (dynamicColumnFieldInfo.getScale() > 0) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("scale = ").append(dynamicColumnFieldInfo.getScale());
		}

		if (dynamicColumnFieldInfo.isNullable()) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("nullable = true");
		}

		if (appendSym) {
			fsb.append(")");
		}

		fsb.append("\n");
	}

	private static void generateLisOnlyAnnotation(StringBuilder fsb,
			DynamicListOnlyFieldInfo dynamicListOnlyFieldInfo) {
		fsb.append(" @ListOnly");
		boolean appendSym = false;
		if (!StringUtils.isBlank(dynamicListOnlyFieldInfo.getColumnName())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("name = \"").append(dynamicListOnlyFieldInfo.getColumnName()).append("\"");
		}

		if (!StringUtils.isBlank(dynamicListOnlyFieldInfo.getKey())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append("key = \"").append(dynamicListOnlyFieldInfo.getKey()).append("\"");
		}

		if (!StringUtils.isBlank(dynamicListOnlyFieldInfo.getProperty())) {
			appendSym = appendSymbol(fsb, appendSym);
			fsb.append(" property = \"").append(dynamicListOnlyFieldInfo.getProperty()).append("\"");
		}

		if (appendSym) {
			fsb.append(")");
		}

		fsb.append("\n");
	}

	private static boolean appendSymbol(StringBuilder fsb, boolean appendSym) {
		if (appendSym) {
			fsb.append(", ");
		} else {
			fsb.append("(");
		}

		return true;
	}
}
