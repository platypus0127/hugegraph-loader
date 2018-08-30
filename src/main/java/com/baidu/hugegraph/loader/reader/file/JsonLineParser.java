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

package com.baidu.hugegraph.loader.reader.file;

import java.util.Map;

import com.baidu.hugegraph.loader.exception.ParseException;
import com.baidu.hugegraph.loader.reader.Line;
import com.baidu.hugegraph.rest.SerializeException;
import com.baidu.hugegraph.util.JsonUtil;

public class JsonLineParser implements LineParser {

    @Override
    public void init(AbstractFileReader reader) {
        // pass
    }

    @Override
    @SuppressWarnings("unchecked")
    public Line parse(String line) {
        try {
            Map<String, Object> keyValues = JsonUtil.fromJson(line, Map.class);
            Line result = new Line(line, keyValues.size());
            for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
                result.add(entry.getKey(), entry.getValue());
            }
            return result;
        } catch (SerializeException e) {
            throw new ParseException(line, "Deserialize line '%s' error",
                                     e, line);
        }
    }

    @Override
    public void close() {
        // pass
    }
}
