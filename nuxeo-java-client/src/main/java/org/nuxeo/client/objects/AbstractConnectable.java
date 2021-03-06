/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Vladimir Pasquier <vpasquier@nuxeo.com>
 *     Kevin Leturc <kleturc@nuxeo.com>
 */
package org.nuxeo.client.objects;

import java.util.Objects;

import org.nuxeo.client.NuxeoClient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * @param <A> The api interface type.
 * @param <B> The type of object extending this one.
 * @since 3.0
 */
public class AbstractConnectable<A, B extends AbstractConnectable<A, B>> extends AbstractBase<B>
        implements Connectable {

    @JsonIgnore
    protected final Class<A> apiClass;

    @JsonIgnore
    protected NuxeoClient nuxeoClient;

    @JsonIgnore
    protected A api;

    /**
     * Minimal constructor to use benefit of injection mechanism.
     */
    protected AbstractConnectable(Class<A> apiClass) {
        // don't call super, this AbstractConnectable constructor is used for not yet connected object,
        // at this moment we don't need okhttp or retrofit objects
        this.apiClass = Objects.requireNonNull(apiClass, "API interface must be provided");
    }

    protected AbstractConnectable(Class<A> apiClass, NuxeoClient nuxeoClient) {
        super(nuxeoClient);
        this.apiClass = Objects.requireNonNull(apiClass, "API interface must be provided");
        this.nuxeoClient = nuxeoClient;
        this.api = retrofit.create(apiClass);
    }

    protected <T> T fetchResponse(Call<T> call) {
        return nuxeoClient.fetchResponse(call);
    }

    protected <T> void fetchResponse(Call<T> call, Callback<T> callback) {
        nuxeoClient.fetchResponse(call, callback);
    }

    @Override
    public void reconnectWith(NuxeoClient nuxeoClient) {
        replaceWith(nuxeoClient);
        this.nuxeoClient = nuxeoClient;
        this.api = retrofit.create(apiClass);
    }

    @Override
    protected void buildRetrofit() {
        super.buildRetrofit();
        // now re-create an API
        this.api = retrofit.create(this.apiClass);
    }

}
