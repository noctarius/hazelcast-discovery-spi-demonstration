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

package rest;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
@Path("api/services")
public class SimpleRestApi {

    private final ConcurrentMap<String, Set<Address>> addressScopes = new ConcurrentHashMap<>();

    @POST
    @Path("{scope}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(@PathParam("scope") String scope, @QueryParam("host") String host, @QueryParam("port") int port) {
        Set<Address> addresses = getAddresses(scope);
        if (addresses.add(new Address(host, port))) {
            return Response.ok().entity("{ \"host\": \"" + host + "\", \"port\": " + port + " }").build();
        }
        return Response.notModified().build();
    }

    @DELETE
    @Path("{scope}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("scope") String scope, @QueryParam("host") String host, @QueryParam("port") int port) {
        Set<Address> addresses = getAddresses(scope);
        if (addresses.remove(new Address(host, port))) {
            return Response.ok().build();
        }
        return Response.notModified().build();
    }

    @GET
    @Path("{scope}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response services(@PathParam("scope") String scope) {
        Set<Address> addresses = getAddresses(scope);
        StringBuilder sb = new StringBuilder("{ \"service\": \"" + scope + "\", \"endpoints\": [");
        for (Address address : addresses) {
            sb.append("{ \"host\": \"" + address.host + "\", \"port\": " + address.port + " },");
        }
        sb.deleteCharAt(sb.length() - 1).append("] }");
        return Response.ok().entity(sb.toString()).build();
    }

    private Set<Address> getAddresses(String scope) {
        Set<Address> addresses = addressScopes.get(scope);
        if (addresses == null) {
            addresses = newThreadSafeSet();
            Set<Address> temp = addressScopes.putIfAbsent(scope, addresses);
            if (temp != null) {
                addresses = temp;
            }
        }
        return addresses;
    }

    private Set<Address> newThreadSafeSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<Address, Boolean>());
    }

    private static class Address {
        private final String host;
        private final int port;

        private Address(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Address address = (Address) o;

            if (port != address.port) {
                return false;
            }
            return host != null ? host.equals(address.host) : address.host == null;
        }

        @Override
        public int hashCode() {
            int result = host != null ? host.hashCode() : 0;
            result = 31 * result + port;
            return result;
        }
    }

}
