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

import com.hazelcast.config.Config;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;
import org.junit.Test;
import rest.AbstractDiscoveryTest;

import static junit.framework.TestCase.assertEquals;

public class DiscoveryTest
        extends AbstractDiscoveryTest {

    @Test
    public void test() {
        Config config = new XmlConfigBuilder().build();
        config.setProperty("hazelcast.discovery.enabled", "true");

        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(false);

        DiscoveryConfig discoveryConfig = joinConfig.getDiscoveryConfig();

        DiscoveryStrategyFactory discoveryStrategyFactory = new SomeRestServiceDiscoveryStrategyFactory();
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(discoveryStrategyFactory);

        discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig);

        HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(config);
        HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(config);

        assertEquals(2, hazelcastInstance1.getCluster().getMembers().size());
        assertEquals(2, hazelcastInstance2.getCluster().getMembers().size());

        Hazelcast.shutdownAll();
    }

}
