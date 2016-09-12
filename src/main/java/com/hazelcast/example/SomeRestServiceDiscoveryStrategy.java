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

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.hazelcast.example.SomeRestServiceProperties.APPLICATION_SCOPE;
import static com.hazelcast.example.SomeRestServiceProperties.DISCOVERY_URL;

class SomeRestServiceDiscoveryStrategy
        extends AbstractDiscoveryStrategy {

    private final String applicationScope;
    private final String discoveryUrl;

    private final DiscoveryNode localNode;
    private final SomeRestService someRestService;

    SomeRestServiceDiscoveryStrategy(DiscoveryNode localNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.localNode = localNode;

        this.applicationScope = getOrDefault("hazelcast.rest", APPLICATION_SCOPE, "default");
        this.discoveryUrl = getOrDefault("hazelcast.rest", DISCOVERY_URL, "http://localhost:12345/");

        GsonConverterFactory converter = GsonConverterFactory.create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(discoveryUrl).addConverterFactory(converter).build();
        this.someRestService = retrofit.create(SomeRestService.class);
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        Service service = execute(() -> someRestService.services(applicationScope));
        List<Endpoint> endpoints = service.getEndpoints();
        return mapEndpoints(endpoints);
    }

    @Override
    public void start() {
        Address address = localNode.getPrivateAddress();
        String host = address.getHost();
        int port = address.getPort();

        execute(() -> someRestService.register(applicationScope, host, port));
    }

    @Override
    public void destroy() {
        Address address = localNode.getPrivateAddress();
        String host = address.getHost();
        int port = address.getPort();

        execute(() -> someRestService.unregister(applicationScope, host, port));
    }

    private Iterable<DiscoveryNode> mapEndpoints(List<Endpoint> endpoints) {
        List<DiscoveryNode> discoveryNodes = new ArrayList<>();
        for (Endpoint endpoint : endpoints) {
            discoveryNodes.add(new SimpleDiscoveryNode(mapEndpoint(endpoint)));
        }
        return discoveryNodes;
    }

    private Address mapEndpoint(Endpoint endpoint) {
        try {
            String host = endpoint.getHost();
            int port = endpoint.getPort();
            return new Address(host, port);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T execute(Callable<Call<T>> callable) {
        try {
            Call<T> call = callable.call();
            return call.execute().body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
