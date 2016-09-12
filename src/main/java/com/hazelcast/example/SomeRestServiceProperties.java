/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.example;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.query.impl.TypeConverters;

public final class SomeRestServiceProperties {

    private SomeRestServiceProperties() {
    }

    /**
     * Defines a name for the application scope. All instances registered using the same application scope
     * will automatically be discovered.
     */
    public static final PropertyDefinition APPLICATION_SCOPE = //
            new SimplePropertyDefinition("application.scope", true, TypeConverters.STRING_CONVERTER);

    /**
     * Defines the url of the remote REST API URL for service discovery
     */
    public static final PropertyDefinition DISCOVERY_URL = //
            new SimplePropertyDefinition("discovery.url", true, TypeConverters.STRING_CONVERTER);

}
