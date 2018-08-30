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

package com.baidu.hugegraph.loader.source;

import java.util.Map;
import java.util.Set;

public class VertexSource extends ElementSource {

    // Be null when id strategy is primary key
    private final String idField;

    public VertexSource(String label, InputSource input, String idField,
                        Map<String, String> mappingFields,
                        Set<String> ignoredFields) {
        super(label, input, mappingFields, ignoredFields);
        this.idField = idField;
    }

    public String idField() {
        return this.idField;
    }

    @Override
    public String toString() {
        return String.format("vertex-source(%s)", this.label());
    }
}
