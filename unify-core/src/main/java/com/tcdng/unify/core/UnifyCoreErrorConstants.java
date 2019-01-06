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

/**
 * Unify core error constants.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
public interface UnifyCoreErrorConstants {

    /** Field injection error */
    String FIELD_INJECTION_ERROR = "UC_0001";

    /** Field injection incompatible types. Field type {0}. Value type {1} */
    String FIELD_INJECTION_INCOMPATIBLE = "UC_0002";

    /** Missing resource {0} */
    String COMPONENT_MISSING_RESOURCE = "UC_0003";

    /** Component is already initialized. Component - {0} */
    String COMPONENT_ALREADY_INITIALIZED = "UC_0004";

    /** Component with name {0} already exists. Found Type = {1}, Type = {2} */
    String COMPONENT_WITH_NAME_EXISTS = "UC_0005";

    /** Component has no name. Type = {0} */
    String COMPONENT_HAS_NO_NAME = "UC_0006";

    /** Component initialization error. Component = {0} */
    String COMPONENT_INITIALIZATION_ERROR = "UC_0007";

    /** Component operation error. Component = {0}, message = {1} */
    String COMPONENT_OPERATION_ERROR = "UC_0008";

    /** Unknown component. Component = {0} */
    String COMPONENT_UNKNOWN_COMP = "UC_0009";

    /** Object instantiation error. Component = {0} */
    String COMPONENT_INSTANTIATION_ERROR = "UC_000A";

    /** Annotation {0} required for type {1} */
    String REFLECT_ANNOTATION_REQUIRED = "UC_000B";

    /** Interfaces extending {0} required for type {1} */
    String REFLECT_INTERFACES_REQUIRED = "UC_000C";

    /** Class {0} is not public-concrete-non-final */
    String REFLECT_CLASS_IS_NOT_PUBLIC_CONCRETE_NONFINAL = "UC_000D";

    /** Method {0} has unsupported override modifiers */
    String REFLECT_METHOD_WITH_UNSUPORTED_MODIFIERS = "UC_000E";

    /**
     * Multiple locks not allowed for parameter synchronized method. Method {0}
     */
    String REFLECT_PARAMETER_SYNCH_METHOD_MULTIPLE_LOCKS = "UC_000F";

    /** Field {0} has unsupported modifiers */
    String REFLECT_FIELD_WITH_UNSUPORTED_MODIFIERS = "UC_0010";

    /** Unknown or unaccessible class method. Class - {0}, Method - {1} */
    String REFLECT_CLASS_UNKNOWN_UNACCESSIBLE_METHOD = "UC_0011";

    /** Reflection error. Class - {0} and nested property {1} */
    String REFLECT_REFLECTION_ERROR = "UC_0012";

    /** Field is unknown. Class - {0}, name - {1} */
    String REFLECT_FIELD_UNKNOWN = "UC_0013";

    /** Field {0} is not public-concrete-non-final */
    String REFLECT_FIELD_IS_NOT_PUBLIC_NONFINAL = "UC_0014";

    /**
     * Business service transactional method {0} for class {1} must throw
     * UnifyException
     */
    String MODULE_TRANSACTIONAL_MUST_THROW_EXCEPTION = "UC_0015";

    /** Compilation or class load error */
    String COMPILER_CLASSLOAD_ERROR = "UC_0016";

    /** Transaction is already completed */
    String TRANSACTION_IS_ALREADY_COMPLETED = "UC_0017";

    /** Transaction is never required */
    String TRANSACTION_IS_NEVER_REQUIRED = "UC_0018";

    /** Transaction is required */
    String TRANSACTION_IS_REQUIRED = "UC_0019";

    /** Data source session error. Data source - {0} */
    String DATASOURCE_SESSION_ERROR = "UC_001A";

    /** Data source missing driver. Data source - {0}, Driver - {1} */
    String DATASOURCE_MISSING_DRIVER = "UC_001B";

    /** Data source session is closed. Data source - {0} */
    String DATASOURCE_SESSION_IS_CLOSED = "UC_001C";

    /** Entity property information not found. Type = {0}, Column = {1} */
    String RECORD_FIELDINFO_NOT_FOUND = "UC_001D";

    /**
     * Entity list property information not found. Type = {0}, Column = {1}
     */
    String RECORD_LISTFIELDINFO_NOT_FOUND = "UC_001E";

    /** Entity has multiple Id annotated fields. Type = {0}, field = {1} */
    String RECORD_MULTIPLE_ID_ANNOTATION = "UC_001F";

    /**
     * Entity has multiple Version annotated fields. Type = {0}, field = {1}
     */
    String RECORD_MULTIPLE_VERSION_ANNOTATION = "UC_0020";

    /** Entity requires an Id field. Type = {0} */
    String RECORD_REQUIRES_ID = "UC_0021";

    /** Version number field must integer type. Type = {0} */
    String RECORD_VERSION_NOT_INTEGER = "UC_0022";

    /** Unsupported property type. Type = {0} */
    String RECORD_UNSUPPORTED_PROPERTY_TYPE = "UC_0023";

    /** Criteria required for prepared statement */
    String RECORD_CRITERIA_REQ_FOR_STATEMENT = "UC_0024";

    /** Key is required for criteria */
    String RECORD_CRITERIA_KEY_REQUIRED = "UC_0025";

    /** Update field required for prepared statement */
    String RECORD_UPDATE_FIELD_REQ_FOR_STATEMENT = "UC_0026";

    /** Can not update primary key field. Entity Type {0}, field {1} */
    String RECORD_CANT_UPDATE_PRIMARYKEY = "UC_0027";

    /** Can not update view-only field. Entity Type {0}, field {1} */
    String RECORD_CANT_UPDATE_LISTONLY_FIELD = "UC_0028";

    /** Entity of type {0} with primary key {1} not found */
    String RECORD_WITH_PK_NOT_FOUND = "UC_0029";

    /**
     * Entity of type {0} with primary key {1} and version number {2} not found
     */
    String RECORD_WITH_PK_VERSION_NOT_FOUND = "UC_002A";

    /** Configuration read error */
    String CONFIGURATION_READ_ERROR = "UC_002B";

    /** Taskable with ID already running. ID = {0} */
    String TASK_WITH_ID_ALREADY_RUNNING = "UC_002C";

    /** Email recipients required */
    String EMAIL_RECIPIENTS_REQUIRED = "UC_002D";

    /** Data conversion error. Type - {0} Value - {1} */
    String DATA_CONVERSION_ERROR = "UC_002E";

    /** Util Error */
    String UTIL_ERROR = "UC_002F";

    /** Annotated field not found. Component={0}, Annotation = {1} */
    String REFLECT_ANNOTATED_FIELD_NOT_FOUND = "UC_0030";

    /** Generic object pool exception */
    String GENERIC_OBJECT_POOL_ERROR = "UC_0031";

    /** Generic object pool get object timeout */
    String GENERIC_OBJECT_POOL_TIMEOUT = "UC_0032";

    /** IO utility stream read/write error */
    String IOUTIL_STREAM_RW_ERROR = "UC_0033";

    /**
     * Entity type field with invalid combination of annotations. Type = {0}, field
     * = {1}
     */
    String RECORD_INVALID_ANNOTATION_COMBO = "UC_0034";

    /**
     * Entity type foreign key type must match foreign record ID field type. Type =
     * {0}, field = {1}, Foreign Type = {2}
     */
    String RECORD_FOREIGNKEY_TYPE_MUST_MATCH_ID = "UC_0035";

    /** Entity cycle detected. Type = {0}, cycle = {1} */
    String RECORD_CYCLE_DETECTED = "UC_0036";

    /**
     * Entity unknown foreign key. Type = {0}, property = {1}, foreign key {2}
     */
    String RECORD_UNKNOWN_FOREIGN_KEY = "UC_0037";

    /** IO utility error. Unable to open resource stream Resource = {0} */
    String IOUTIL_UNABLE_TO_OPEN_RESOURCE_STREAM = "UC_0038";

    /** Annotation utility error */
    String ANNOTATIONUTIL_ERROR = "UC_0039";

    /** Data source manager manage schema error. Data source {0} */
    String DATASOURCEMANAGER_MANAGE_SCHEMA_ERROR = "UC_003A";

    /**
     * Data source manager unable to update table/view. Data source {0}, Component
     * {1}, Table Type {2}
     */
    String DATASOURCEMANAGER_UNABLE_TO_UPDATE_TABLE = "UC_003B";

    /**
     * Multiple record found. Single record expected for criteria
     */
    String RECORD_MULTIPLE_RESULT_FOUND = "UC_003C";

    /** Converter exception */
    String CONVERTER_EXCEPTION = "UC_003D";

    /** At least one value is expected for multi-value operation. Column - {0} */
    String RECORD_AT_LEAST_ONE_VALUE_EXPECTED = "UC_003E";

    /** Unsupported time pattern. {0} */
    String UNSUPPORTED_TIME_PATTERN = "UC_003F";

    /**
     * Component configuration is for a different type. Component = {0}. Type = {1}.
     * Config Type = {2}
     */
    String COMPONENT_CONFIG_DIFF_TYPE = "UC_0040";

    /** Invalid frequency unit. Unit = {0} */
    String INVALID_FREQUENCY_UNIT = "UC_0041";

    /** Container not initialized. */
    String CONTAINER_NOT_INITIALIZED = "UC_0042";

    /** Container already initialized. */
    String CONTAINER_ALREADY_INITIALIZED = "UC_0043";

    /**
     * Public setter method not found for property. Class - {0}, property - {1}
     */
    String REFLECT_NO_SETTER = "UC_0044";

    /**
     * Public getter method not found for property. Class - {0}, property - {1}
     */
    String REFLECT_NO_GETTER = "UC_0045";

    /** Field getter setter type mismatch. Class - {0}, property - {1} */
    String REFLECT_FIELD_GETTER_SETTER_MISMATCH = "UC_0046";

    /**
     * Multiple or no field selected in criteria for list value operation .Type {0}
     */
    String RECORD_MULTIPLE_OR_NOFIELD_SELECTED = "UC_0047";

    /**
     * Selected field {0} is not numeric and is not suitable for aggregate function.
     * Type - {1}
     */
    String RECORD_SELECT_NOT_SUITABLE_FOR_AGGREGATE = "UC_0048";

    /** No selected field in criteria for aggregate function. Type - {0} */
    String RECORD_NO_SELECT_FOR_AGGREGATE = "UC_0049";

    /** ParameterHolder {0} can not be null. */
    String PARAMETER_CAN_NOT_BE_NULL = "UC_004A";

    /** UPL utility error. Missing element. Long name = {0} */
    String PAGEUTIL_MISSING_ELEMENT = "UC_004B";

    /**
     * UPL child with element ID is not found. Referrer = {0}, child ID = {1}
     */
    String UPL_CHILD_WITH_ELEMENT_ID_NOTFOUND = "UC_004C";

    /**
     * UPL element ID is duplicate. Source = {0}, Line number = {1}, id = {2}
     */
    String UPL_ELEMENT_ID_DUPLICATE = "UC_004D";

    /**
     * UPL descriptor parse error. Column expected. Source = {0}, Line number = {1},
     * type = {2}, descriptor = {3}, offset = {4}
     */
    String UPL_DESCRIPTOR_COLUMN_EXPECTED = "UC_004F";

    /**
     * Page descriptor parse error. Property name required. Source = {0}, Line
     * number = {1}, descriptor = {2}
     */
    String UPL_DESCRIPTOR_PROPERTY_NAME_REQUIRED = "UC_0050";

    /**
     * Page descriptor {0} closing braces expected. Source = {1}, Line number = {2},
     * Part = {3}.
     */
    String UPL_DESCRIPTOR_CLOSING_BRACES_EXPECTED = "UC_0051";

    /** Unknown page name. Name = {0} */
    String UNKNOWN_PAGE_NAME = "UC_0052";

    /** Page utility error. Unknown Long name = {0} */
    String PAGEUTIL_UNKNOWN_LONGNAME = "UC_0053";

    /** Missing build property. Type={0}, Property = {1} */
    String BUILDER_MISSING_BUILD_PROPERTY = "UC_0054";

    /** Bad data source connection object. Data source = [{0}] */
    String DATASOURCE_BAD_CONNECTION = "UC_0055";

    /**
     * Cluster member is not synchronization object owner. Sync = [{0}]. Member =
     * [{1}].
     */
    String CLUSTERMANAGEMENT_MEMBER_NOT_SYNC_OWNER = "UC_0056";

    /** Cluster member ID can not be null. Sync = [{0}] */
    String CLUSTERMANAGEMENT_MEMBERID_ISNULL = "UC_0057";

    /**
     * Cycle detected in component initialization. Component = {0}, cycle = {1}
     */
    String COMPONENT_CYCLIC_INITIALIZATION = "UC_0058";

    /** Invalid use of @Periodic annotation. Component = {0}, method = {1} */
    String COMPONENT_INVALID_PERIOD_METHOD = "UC_0059";

    /** UPL component attribute is unknown. type = {0}, attribute = {1} */
    String UPL_COMPONENT_ATTRIBUTE_UNKNOWN = "UC_005A";

    /**
     * UPL element type is required. Source= {0}, Line number = {1}, descriptor =
     * {2}
     */
    String UPL_ELEMENT_TYPE_REQUIRED = "UC_005B";

    /** UPL parse error. Source {0} */
    String UPL_PARSE_ERROR = "UC_005C";

    /**
     * UPL with missing foreign reference. Source= {0}, Line number = {1}, Component
     * ID = {2}, reference = {3}
     */
    String UPL_MISSING_FOREIGN_REFERENCE = "UC_005D";

    /**
     * Element type token is required. Source = {0}, Line number = {1}, line = {2}
     */
    String UPL_ELEMENT_TYPE_TOKEN_REQUIRED = "UC_005E";

    /**
     * Element has no such attribute. Source={0}, lineNumber = {1}, type = {2},
     * attribute = {3}
     */
    String UPL_ELEMENT_HAS_NO_SUCH_ATTRIBUTE = "UC_005F";

    /**
     * Elements with foreign references must have parent element. Source={0},
     * lineNumber = {1}, type = {2}, attribute = {3}
     */
    String UPL_ELEMENT_WITH_FOREIGN_MUST_HAVE_PARENT = "UC_0060";

    /** UPL cyclic reference detected. Cycle = {0} */
    String UPL_CYCLIC_REFERENCE_DETECTED = "UC_0061";

    /**
     * UPL element mandatory attribute not supplied. Source={0}, lineNumber = {1},
     * type = {2}, attribute = {3}
     */
    String UPL_ELEMENT_NO_MANDATORY_ATTRIBUTE = "UC_0062";

    /** Can not find unique constraint property. Type = {0}, property = {1} */
    String RECORD_NO_PROPERTY_FOR_UNIQUECONSTRAINT = "UC_0063";

    /**
     * Can not apply unique constraint to view-only property. Type = {0}, property =
     * {1}
     */
    String RECORD_LISTONLY_PROPERTY_FOR_UNIQUECONSTRAINT = "UC_0064";

    /** Can not find index property. Type = {0}, property = {1} */
    String RECORD_NO_PROPERTY_FOR_INDEX = "UC_0065";

    /** Can not add view-only property to index. Type = {0}, property = {1} */
    String RECORD_LISTONLY_PROPERTY_FOR_INDEX = "UC_0066";

    /** Must specify attribute {0} or {1} of annotation {2}. Type = {3} */
    String ANNOTATION_MUST_SPECIFY_ATTRIBUTE_OF_TWO = "UC_0067";

    /**
     * Can not use both attributes {0} and {1} of annotation {2} together. Type =
     * {3}
     */
    String ANNOTATION_BAD_ATTRIBUTE_COMBINATION = "UC_0068";

    /**
     * At least one property is required for unique constraint. Type = {0}, name =
     * {1}
     */
    String RECORD_PROPERTY_REQUIRED_UNIQUECONSTRAINT = "UC_0069";

    /**
     * Multiple unique constraints with the same name. Type = {0}, name = {1}
     */
    String RECORD_SAME_NAME_UNIQUECONSTRAINT = "UC_006A";

    /** At least one property is required for index. Type = {0}, name = {1} */
    String RECORD_PROPERTY_REQUIRED_INDEX = "UC_006B";

    /** Multiple indexes with the same name. Type = {0}, name = {1} */
    String RECORD_SAME_NAME_INDEX = "UC_006C";

    /** Container node ID is required to be set in configuration. */
    String CONTAINER_NODEID_REQUIRED = "UC_006D";

    /** No record type assembled for datasource - {0} */
    String DATASOURCE_NO_RECORD_TYPE_ASSEMBLED = "UC_006E";

    /** Query result offset is unsupported by dialect - {0} */
    String QUERY_RESULT_OFFSET_NOT_SUPPORTED = "UC_006F";

    /** Container logger initialization error. */
    String CONTAINER_LOGGER_INITIALIZATION_ERROR = "UC_0070";

    /** PackableDoc has no such field {0}. */
    String DOCUMENT_NO_SUCH_FIELD = "UC_0071";

    /** Packable document can not read from null document reference. */
    String DOCUMENT_CANT_READ_FROM_NULL = "UC_0072";

    /** Packable document can not populate a null document reference. */
    String DOCUMENT_CANT_WRITE_TO_NULL = "UC_0073";

    /** Field {0} for class {1} is not bean compliant */
    String REFLECT_CLASS_FIELD_NOT_BEAN_COMPLIANT = "UC_0074";

    /** Component type {0} does not match expected type {1} */
    String CONTEXT_COMPONENT_TYPE_MISMATCH = "UC_0075";

    /**
     * Closing characters required for parameter in parameterized message. Message
     * key = {0}.
     */
    String MESSAGE_CLOSING_CHARACTERS_REQUIRED = "UC_0076";

    /** ParameterHolder tag is empty. Message key = {0}. */
    String MESSAGE_PARAMETER_TAG_EMPTY = "UC_0077";

    /** ParameterHolder entry missing in dictionary. ParameterHolder = {0}. */
    String MESSAGE_PARAMETER_MISSING_IN_DICTIONARY = "UC_0078";

    /** Component termination error. Component = {0} */
    String COMPONENT_TERMINATION_ERROR = "UC_0079";

    /** Container error. */
    String CONTAINER_ERROR = "UC_007A";

    /** Batch file reader error on line {0} */
    String BATCH_FILE_READER_ERROR = "UC_007B";

    /**
     * Entity with unique fields already exist. Existing record = {0}, new record =
     * {1}
     */
    String BATCH_FILE_READER_RECORD_EXISTS = "UC_007C";

    /** ParameterHolder type is unsupported. Type {0} */
    String PARAMETER_TYPE_UNSUPPORTED = "UC_007D";

    /** Network operation error. */
    String NETWORK_OPERATION_ERROR = "UC_007E";

    /** Property {0} is not configurable. Type = {1}. */
    String PROPERTY_IS_NOT_CONFIGURABLE = "UC_007F";

    /**
     * No two-factor autentication service component configured for application.
     */
    String NO_TWOFACTOR_AUTH_SERVICE_COMP = "UC_0080";

    /** Element type with attribute key {0} already exists for locale {1}. */
    String UPL_ELEMENT_ATTRIBUTEKEY_EXISTS = "UC_0081";

    /**
     * No UPL element attributes with attribute key {0} exists for locale {1}.
     */
    String UPL_COMPILER_ATTRIBUTEKEY_UNKNOWN = "UC_0082";

    /** No Unify container in runtime. */
    String NO_CONTAINER_IN_RUNTIME = "UC_0083";

    /** An ID is expected in descriptor {0} */
    String UPL_COMPILER_DESCRIPTOR_ID_EXPECTED = "UC_0084";

    /** Unify container instance already in runtime. */
    String CONTAINER_IN_RUNTIME = "UC_0085";

    /** Invalid container runtime access key. */
    String INVALID_CONTAINER_RUNTIME_ACCESSKEY = "UC_0086";

    /** Container startup error. */
    String CONTAINER_STARTUP_ERROR = "UC_0087";

    /**
     * You have been logged out from this session because you are logged on in
     * another session.
     */
    String FORCELOGOUT_MULTIPLE_LOGIN = "UC_0088";

    /**
     * You have been logged out from this session by system administrator.
     */
    String FORCELOGOUT_ADMINISTRATOR = "UC_0089";

    /**
     * You have been logged out from this session by system.
     */
    String FORCELOGOUT_SYSTEM = "UC_008A";

    /** Version {0} does not exist for root record ID {1} and category {2} */
    String UNKNOWN_RECORD_ROOT_VERSION = "UC_008B";

    /** No version root exists for root record ID {0} and category {1} */
    String NO_RECORD_VERSION_ROOT = "UC_008C";

    /** No record versions for root record ID {0} and category {1} */
    String NO_RECORD_VERSIONS = "UC_008D";

    /**
     * Cannot delete parent versioned record {0} for root record ID {1} and category
     * {2}
     */
    String CANNOT_DELETE_PARENT_VERSIONEDRECORD = "UC_008E";

    /** Unsupported sequenced record type {0} for category {1} */
    String UNSUPPORTED_SEQUENCEDRECORDTYPE = "UC_008F";

    /** Parent record ID is required for child type {0} for category {1} */
    String PARENT_RECORDID_REQUIRED_FOR_VERSIONEDRECORD = "UC_0090";

    /** Parent record ID {0} is unknown for root ID {0} for category {1} */
    String PARENT_RECORDID_UNKOWN = "UC_0091";

    /**
     * Entity does not require a parent record ID for type {0} for category {1}
     */
    String RECORD_DOESNT_REQUIRE_PARENT = "UC_0092";

    /**
     * Versioned record with ID {0} is unknown for type {1}, root ID {2} and
     * category {2}
     */
    String VERSIONED_RECORDID_UNKOWN = "UC_0093";

    /**
     * Conflicting UPL component writers for type {0} on user platform {1}. Writers
     * {2}, {3}
     */
    String CONFLICTING_UPLCOMPONENT_WRITERS = "UC_0094";

    /** Unsupported collection type. Type = {0} */
    String UNSUPPORTED_COLLECTIONTYPE = "UC_0095";

    /** Network communicator already open. Name = {0} */
    String NETWORKCOMMUNICATOR_OPEN = "UC_0096";

    /** Network communicator receive error. Name = {0} */
    String NETWORKCOMMUNICATOR_RECEIVE_ERROR = "UC_0097";

    /** Network communicator transmit error. Name = {0} */
    String NETWORKCOMMUNICATOR_TRANSMIT_ERROR = "UC_0098";

    /** Network interface configuration exists. Name = {0} */
    String NETWORKINTERFACE_CONFIG_EXISTS = "UC_0099";

    /** Network interface configuration unknown. Name = {0} */
    String NETWORKINTERFACE_CONFIG_UNKNOWN = "UC_009A";

    /** Network interface configuration incompatible type. Name = {0} */
    String NETWORKINTERFACE_CONFIG_INCOMPATIBLE = "UC_009B";

    /**
     * Network interface communcator incompatible with type. Communicator = {0},
     * type = {1}
     */
    String NETWORKINTERFACE_TYPE_COMM_INCOMPATIBLE = "UC_009C";

    /**
     * Network interface unable to bind local unicast server to port. Name = {0},
     * Port = {1}
     */
    String NETWORKINTERFACE_UNABLE_BIND_LOCALSERVER = "UC_009D";

    /** Network interface local unicast server already started. Name = {0} */
    String NETWORKINTERFACE_UNICASTSERVER_STARTED = "UC_009E";

    /** Network interface no local unicast server started. Name = {0} */
    String NETWORKINTERFACE_UNICASTSERVER_NOT_STARTED = "UC_009F";

    /**
     * Network interface no local unicast client started. ID = {0}
     */
    String NETWORKINTERFACE_UNICASTCLIENT_NOT_STARTED = "UC_00A0";

    /**
     * Network interface no local multicast server started. ID = {0}
     */
    String NETWORKINTERFACE_MULTICASTSERVER_NOT_STARTED = "UC_00A1";

    /** Network interface local multicast client already started. Name = {0} */
    String NETWORKINTERFACE_MULTICLIENT_STARTED = "UC_00A2";

    /** Network interface no local multicast client started. Name = {0} */
    String NETWORKINTERFACE_MULTICASTCLIENT_NOT_STARTED = "UC_00A3";

    /**
     * Network interface unable to bind local multicast client to port. Name = {0},
     * Port = {1}
     */
    String NETWORKINTERFACE_UNABLE_BIND_LOCALCLIENT = "UC_00A4";

    /**
     * Network interface message larger than maximum packet size. Max = {0}, Message
     * size = {1}
     */
    String NETWORKINTERFACE_MESSAGE_LARGER_THAN_MAXPACKETSIZE = "UC_00A5";

    /** Document document must have at least one field. */
    String DOCUMENT_HAS_NO_FIELD = "UC_00A6";

    /**
     * Foreign type not allowed for enumeration constant type {0}. Type = {1}, field
     * = {2}
     */
    String ANNOTATION_FOREIGN_TYPE_NOT_PERMITTED = "UC_00A7";

    /**
     * List-only key is not referencing a foriegn key. Type = {0}, property = {1},
     * key {2}
     */
    String RECORD_LISTONLY_KEY_NOT_REF_FOREIGN_KEY = "UC_00A8";

    /** No matching record found for single record query. Type = {0} */
    String RECORD_NO_MATCHING_RECORD = "UC_00A9";

    /** No matching record found for single object query. Type = {0} */
    String RECORD_SINGLEOBJECT_NO_MATCHING_RECORD = "UC_00AA";

    /** Map value store has no such entry {0}. */
    String MAPVALUESTORE_NO_SUCH_ENTRY = "UC_00AB";

    /** Document definitiion with code is unknown {0}. */
    String DOCUMENT_DEFINITION_UNKNOWN = "UC_00AC";

    /** Business logic plug-in {0} is targeting an unknown target {1}. */
    String BUSINESSLOGIC_PLUGIN_TARGET_UNKNOWN = "UC_00AD";

    /**
     * Business logic plug-in {0} can not plug into a non-business service component
     * {1}.
     */
    String BUSINESSLOGIC_PLUGIN_TARGET_NON_BUSINESSMODULE = "UC_00AE";

    /**
     * Business logic plug-in {0} is targeting an unknown socket {1} on business
     * service component {2}.
     */
    String BUSINESSLOGIC_PLUGIN_TARGET_NON_SOCKET = "UC_00AF";

    /** Data utility error */
    String DATAUTIL_ERROR = "UC_00B0";

    /** Reflect utility incompatible getter and setter for field {0}. Type {1}. */
    String REFLECTUTIL_INCOMPATIBLE_GETTER_SETTER = "UC_00B1";

    /** Invalid use of @Broadcast annotation. Component = {0}, method = {1} */
    String COMPONENT_INVALID_BROADCAST_METHOD = "UC_00B2";

    /** Dynamic SQL data source {0} is already configured. */
    String DYNAMIC_DATASOURCE_ALREADY_CONFIGURED = "UC_00B3";

    /** Dynamic SQL data source {0} is unknown. */
    String DYNAMIC_DATASOURCE_IS_UNKNOWN = "UC_00B4";

    /**
     * Entity type of attribute-only must have one foreign key. Type = {0}
     */
    String RECORD_ATTRIBUTE_ONE_FK_ONLY = "UC_00B5";

    /**
     * Entity type of attribute-only can not have list-only fields. Type = {0}
     */
    String RECORD_ATTRIBUTE_NO_LISTONLY = "UC_00B6";

    /**
     * Entity type of attribute-only can not have attribute list fields. Type = {0}
     */
    String RECORD_ATTRIBUTE_NO_ATTRIBUTELIST = "UC_00B7";

    /**
     * Invalid child list field type. Type = {0}, field = {1}
     */
    String RECORD_INVALID_CHILDLIST_FIELD_TYPE = "UC_00B8";

    /**
     * Child list type has no matching foreign key reference. Field = {0}, Child
     * type = {1}
     */
    String RECORD_CHILDLIST_NO_MATCHING_FK = "UC_00B9";

    /**
     * Multiple child-only foreign keys detected. Type = {0}
     */
    String RECORD_MULTIPLE_CHILDONLY_FOREIGNKEYS = "UC_00BA";

    /**
     * Document unpacking definition mismatch. Configuration = {0}, size = {1},
     * expected size = {2}
     */
    String DOCUMENT_UNPACK_DEFINTION_MISMATCH = "UC_00BB";

    /**
     * PackableDoc unmatched types. Packable type = {0}, Document type = {1}
     */
    String DOCUMENT_TYPE_MISMATCH = "UC_00BC";

    /** Packable document is not auditable */
    String DOCUMENT_PACKABLE_NOT_AUDITABLE = "UC_00BD";

    /** Taskable method {0} is unknown */
    String TASKABLE_METHOD_UNKNOWN = "UC_00BE";

    /**
     * Taskable method {0} for component {1} and method {2} has invalid signature.
     * TaskMonitor required as first parameter
     */
    String TASKABLE_METHOD_MUST_HAVE_FIRST_TASKMONITOR = "UC_00BF";

    /**
     * Taskable method {0} for component {1} already exists for component {2}
     */
    String TASKABLE_METHOD_ALREADY_EXISTS = "UC_00C0";

    /**
     * Taskable method {0} for component {1} must return a non-void type.
     */
    String TASKABLE_METHOD_RETURN_NON_VOID = "UC_00C1";

    /**
     * Taskable method {0} for component {1} mismatched parameter count
     */
    String TASKABLE_METHOD_MISMATCHED_PARAMS = "UC_00C2";

    /**
     * Taskable method {1} component {0} must be a singleton.
     */
    String TASKABLE_METHOD_SINGLETON_ONLY = "UC_00C3";

    /**
     * Expirable method {1} component {0} must be a singleton.
     */
    String EXPIRABLE_METHOD_SINGLETON_ONLY = "UC_00C4";

    /**
     * Expirable method {0} for component {1} must have no parameters
     */
    String EXPIRABLE_METHOD_NO_PARAMS = "UC_00C5";

    /**
     * Unknown document configuration setting. Configuration = {0}, setting = {1}.
     */
    String DOCUMENT_CONFIG_UNKNOWN_SETTING = "UC_00C6";

    /** Document configuration with name {0} already exists. */
    String DOCUMENT_CONFIG_WITH_NAME_EXISTS = "UC_00C7";

    /** Document configuration with name {0} is unknown. */
    String DOCUMENT_CONFIG_WITH_NAME_UNKNOWN = "UC_00C8";

    /** Parameter definition with name {0} is unknown. */
    String PARAMETER_DEFINITION_UNKNOWN = "UC_00C9";

    /**
     * Parameter value {0} is required. Parameter definition = {1}, instance type =
     * {2}.
     */
    String PARAMETER_VALUE_REQUIRED = "UC_00CA";

    /**
     * Parameter {0} of type {1} of taskable method {2} is incompatible with method
     * parameter type {3}.
     */
    String TASKABLE_PARAMETER_TYPE_INCOMPATIBLE = "UC_00CB";

    /**
     * Multiple records with same key {0} found. Type = {1}.
     */
    String RECORD_MULTIPLE_SAME_KEY_FOUND = "UC_00CC";

    /**
     * Multiple values with same key {0} found. Type = {1}.
     */
    String VALUE_MULTIPLE_SAME_KEY_FOUND = "UC_00CD";

    /**
     * XML batch file start tag {0} missing.
     */
    String XMLBATCHFILEREADER_STARTTAG_MISSING = "UC_00CE";

    /**
     * XML batch file enclosing batch item tag {0} expected.
     */
    String XMLBATCHFILEREADER_BATCHITEMTAG_MULTIPLE = "UC_00CF";

    /**
     * XML batch file has multiple batch item with tag {0}.
     */
    String XMLBATCHFILEREADER_BATCHITEM_MULTIPLE = "UC_00D0";

    /**
     * XML batch file batch item tag {0} unknown.
     */
    String XMLBATCHFILEREADER_BATCHITEMTAG_UNKNOWN = "UC_00D1";

    /**
     * XML batch file batch item tag {0} exception.
     */
    String XMLBATCHFILEREADER_BATCHITEM_EXCEPTION = "UC_00D2";

    /**
     * Incompatible money currencies. Currency 1 = {0}, currency 2 = {1}.
     */
    String INCOMPATIBLE_MONEY_CURRENCY = "UC_00D3";

    /**
     * Attempt to apply alternate settings to a singleton component. Component = {0}
     */
    String COMPONENT_ALTSETTINGS_SINGLETON = "UC_00D4";

    /** Unknown property {1} in alternate settings for component. Component = {0} */
    String COMPONENT_ALTSETTINGS_UNKNOWN_PROPERTY = "UC_00D5";

    /** Property {2} for component {0} is not configurable. Type = {1} */
    String COMPONENT_PROPERTY_NOT_CONFIGURABLE = "UC_00D6";

    /**
     * UPL descriptor closing brace is expected. Source= {0}, Line number = {1},
     * descriptor = {2}
     */
    String UPL_DESCRIPTOR_CLOSING_BRACE_EXPECTED = "UC_00D7";

    /**
     * UPL descriptor element has no opening brace. Source= {0}, Line number = {1},
     * line = {2}
     */
    String UPL_DESCRIPTOR_NO_OPENING_BRACE = "UC_00D8";

    /**
     * Email server configuration with code {0} is unknown.
     */
    String EMAILSERVER_CONFIGURATION_UNKNOWN = "UC_00D9";

    /** PackableDoc field {0} is not a complex type. */
    String DOCUMENT_FIELD_NOT_COMPLEX = "UC_00DA";

    /** PackableDoc field mapping {0} is not a complex type. Bean field = {1} */
    String DOCUMENT_FIELDMAPPING_NOT_COMPLEX = "UC_00DB";

    /** PackableDoc field mapping {0} not found. */
    String DOCUMENT_FIELDMAPPING_NOT_FOUND = "UC_00DC";

    /** PackableDoc can not write directly to a complex field. Field = {0} */
    String DOCUMENT_FIELD_COMPLEX_DIRECT_WRITE = "UC_00DD";

    /** Build error. Message = {0} */
    String BUILD_ERROR = "UC_00DE";

    /** Task setup has no task */
    String TASKSETUP_NO_TASK = "UC_00DF";

    /** Task setup has multiple tasks */
    String TASKSETUP_MULTIPLE_TASKS = "UC_00E0";

    /**
     * Child type has no matching foreign key reference. Field = {0}, Child type =
     * {1}
     */
    String RECORD_CHILD_NO_MATCHING_FK = "UC_00E1";

    /**
     * Multiple child records found for record. Type {0} with primary key {1} at
     * field = {2}
     */
    String RECORD_MULTIPLE_CHILD_FOUND = "UC_00E2";
}
