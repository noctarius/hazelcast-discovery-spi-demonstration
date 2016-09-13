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
import com.hazelcast.config.properties.ValidationException;
import com.hazelcast.config.properties.ValueValidator;

import java.net.URL;

import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

public final class SomeRestServiceProperties {

    private static final ValueValidator URL_VALIDATOR = value -> {
        String url = value.toString();
        try {
            new URL(url);
        } catch (Exception e) {
            throw new ValidationException(e);
        }
    };

    private SomeRestServiceProperties() {
    }

    /**
     * Defines a name for the application scope. All instances registered using the same
     * application scope will automatically be discovered.
     * <p/>
     * <pre>default: hazelcast-cluster</pre>
     */
    public static final PropertyDefinition APPLICATION_SCOPE = //
            new SimplePropertyDefinition("application.scope", true, STRING);

    /**
     * Defines the url of the remote REST API URL for service discovery.
     * <p/>
     * <pre>default: http://localhost:12345/</pre>
     */
    public static final PropertyDefinition DISCOVERY_URL = //
            new SimplePropertyDefinition("discovery.url", true, STRING, URL_VALIDATOR);

}
