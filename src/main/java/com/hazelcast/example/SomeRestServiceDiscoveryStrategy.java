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
import java.util.function.Supplier;

import static com.hazelcast.example.SomeRestServiceProperties.APPLICATION_SCOPE;
import static com.hazelcast.example.SomeRestServiceProperties.DISCOVERY_URL;

public class SomeRestServiceDiscoveryStrategy
        extends AbstractDiscoveryStrategy {

    private final String baseUrl;
    private final String applicationScope;

    private final SomeRestService someRestService;

    private final DiscoveryNode discoveryNode;

    public SomeRestServiceDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, //
                                            Map<String, Comparable> properties) {
        super(logger, properties);
        this.discoveryNode = discoveryNode;

        this.baseUrl = getOrDefault("discovery.rest", DISCOVERY_URL, "http://localhost:12345/");
        this.applicationScope = getOrDefault("discovery.rest", APPLICATION_SCOPE, "hazelcast-cluster");

        logger.info("SomeRestService discovery strategy started {url=" //
                + baseUrl + ", scope=" + applicationScope + "}");

        GsonConverterFactory converterFactory = GsonConverterFactory.create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(converterFactory).build();
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
        Address address = discoveryNode.getPrivateAddress();
        String host = address.getHost();
        int port = address.getPort();
        execute(() -> someRestService.register(applicationScope, host, port));
    }

    @Override
    public void destroy() {
        Address address = discoveryNode.getPrivateAddress();
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

    private <T> T execute(Supplier<Call<T>> supplier) {
        try {
            Call<T> call = supplier.get();
            return call.execute().body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
