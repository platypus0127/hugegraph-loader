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

package com.baidu.hugegraph.loader.executor;

import com.baidu.hugegraph.driver.HugeClient;

public class HugeClients {

    private static volatile HugeClient instance = null;

    public static HugeClient get(LoadOptions options) {
        if (instance == null) {
            synchronized (HugeClients.class) {
                if (instance == null) {
                    instance = newHugeClient(options);
                }
            }
        }
        return instance;
    }

    private HugeClients() {}

    private static HugeClient newHugeClient(LoadOptions options) {
        String address = options.host + ":" + options.port;
        return new HugeClient(address, options.graph, options.timeout);
    }
}
