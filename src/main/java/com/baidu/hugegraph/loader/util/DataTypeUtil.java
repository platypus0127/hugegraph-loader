/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.loader.util;

import static com.baidu.hugegraph.structure.constant.DataType.BYTE;
import static com.baidu.hugegraph.structure.constant.DataType.DOUBLE;
import static com.baidu.hugegraph.structure.constant.DataType.FLOAT;
import static com.baidu.hugegraph.structure.constant.DataType.INT;
import static com.baidu.hugegraph.structure.constant.DataType.LONG;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.baidu.hugegraph.structure.constant.Cardinality;
import com.baidu.hugegraph.structure.constant.DataType;
import com.baidu.hugegraph.structure.schema.PropertyKey;
import com.baidu.hugegraph.util.E;

public final class DataTypeUtil {

    public static Object convert(Object value, PropertyKey propertyKey) {
        if (value == null) {
            return null;
        }

        DataType dataType = propertyKey.dataType();
        if (propertyKey.cardinality() == Cardinality.SINGLE) {
            if (dataType.isNumber()) {
                return valueToNumber(value, dataType);
            } else if (dataType.isDate()) {
                return valueToDate(value, dataType);
            } else if (dataType.isUUID()) {
                return valueToUUID(value, dataType);
            }
        } else if (propertyKey.cardinality() == Cardinality.SET) {
            if (value instanceof String) {
                return JsonUtil.fromJson((String) value, Set.class);
            }
        } else {
            assert propertyKey.cardinality() == Cardinality.LIST;
            if (value instanceof String) {
                return JsonUtil.fromJson((String) value, List.class);
            }
        }

        if (checkValue(value, propertyKey)) {
            return value;
        }

        return null;
    }

    public static boolean isNumber(DataType dataType) {
        return dataType == BYTE || dataType == INT || dataType == LONG ||
               dataType == FLOAT || dataType == DOUBLE;
    }

    public static boolean isDate(DataType dataType) {
        return dataType == DataType.DATE;
    }

    public static boolean isUUID(DataType dataType) {
        return dataType == DataType.UUID;
    }

    public static Number valueToNumber(Object value, DataType dataType) {
        E.checkState(isNumber(dataType), "The target data type must be number");

        if (dataType.clazz().isInstance(value)) {
            return (Number) value;
        }

        Number number = null;
        try {
            switch (dataType) {
                case BYTE:
                    number = Byte.valueOf(value.toString());
                    break;
                case INT:
                    number = Integer.valueOf(value.toString());
                    break;
                case LONG:
                    number = Long.valueOf(value.toString());
                    break;
                case FLOAT:
                    number = Float.valueOf(value.toString());
                    break;
                case DOUBLE:
                    number = Double.valueOf(value.toString());
                    break;
                default:
                    throw new AssertionError(String.format(
                              "Number type only contains Byte, Integer, " +
                              "Long, Float, Double, but got %s",
                              dataType.clazz()));
            }
        } catch (NumberFormatException ignored) {
            // Unmatched type found
        }
        return number;
    }

    public static Date valueToDate(Object value, DataType dataType) {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (isDate(dataType)) {
            if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            } else if (value instanceof String) {
                try {
                    return DateUtil.parse((String) value);
                } catch (ParseException e) {
                    E.checkArgument(false, "%s, expect format: %s",
                                    e.getMessage(), DateUtil.toPattern());
                }
            }
        }
        return null;
    }

    public static UUID valueToUUID(Object value, DataType dataType) {
        if (value instanceof UUID) {
            return (UUID) value;
        }
        if (isUUID(dataType) && value instanceof String) {
            return UUID.fromString((String) value);
        }
        return null;
    }

    public static boolean checkValue(Object value, PropertyKey propertyKey) {
        boolean valid;

        Cardinality cardinality = propertyKey.cardinality();
        DataType dataType = propertyKey.dataType();
        switch (cardinality) {
            case SINGLE:
                valid = checkDataType(value, dataType);
                break;
            case SET:
                valid = value instanceof Set;
                valid = valid && checkCollectionDataType((Set<?>) value,
                                                         dataType);
                break;
            case LIST:
                valid = value instanceof List;
                valid = valid && checkCollectionDataType((List<?>) value,
                                                         dataType);
                break;
            default:
                throw new AssertionError(String.format(
                          "Unsupported cardinality: '%s'", cardinality));
        }
        return valid;
    }

    /**
     * Check type of the value valid
     */
    public static boolean checkDataType(Object value, DataType dataType) {
        if (value instanceof Number) {
            return valueToNumber(value, dataType) != null;
        }
        return dataType.clazz().isInstance(value);
    }

    /**
     * Check type of all the values(may be some of list properties) valid
     */
    public static boolean checkCollectionDataType(Collection<?> values,
                                                  DataType dataType) {
        boolean valid = true;
        for (Object value : values) {
            if (!checkDataType(value, dataType)) {
                valid = false;
                break;
            }
        }
        return valid;
    }
}
