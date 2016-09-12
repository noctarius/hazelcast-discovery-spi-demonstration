/*
 * Copyright (c) 2008-2015, Hazelcast, Inc. All Rights Reserved.
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

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface SomeRestService {
    @GET("/api/services/{scope}")
    Call<Service> services(@Path("scope") String scope);

    @POST("/api/services/{scope}")
    Call<Endpoint> register(@Path("scope") String scope, @Query("host") String host, @Query("port") int port);

    @DELETE("/api/services/{scope}")
    Call<Void> unregister(@Path("scope") String scope, @Query("host") String host, @Query("port") int port);
}
