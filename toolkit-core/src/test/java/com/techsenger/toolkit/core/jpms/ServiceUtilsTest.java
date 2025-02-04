/*
 * Copyright 2016-2025 Pavel Castornii.
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
 */

package com.techsenger.toolkit.core.jpms;

import java.util.HashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Pavel Castornii
 */
public class ServiceUtilsTest {

    @Test
    public void services_oneServiceWithMultipleProviders_success() {
        var services = ServiceUtils.services(this.getClass().getModule().getLayer());
        assertThat(services.contains(TestServiceB.class.getName())).isTrue();
    }

    @Test
    public void providers_oneServiceWithMultipleProvidersByType_success() {
        var providers = ServiceUtils.providers(this.getClass().getModule().getLayer(),
                new ServiceType<TestServiceB>() { });
        assertThat(providers).hasSize(2);
        assertThat(providers.contains(TestServiceBProvider1.class.getName())).isTrue();
        assertThat(providers.contains(TestServiceBProvider2.class.getName())).isTrue();
    }

    @Test
    public void providers_oneServiceWithMultipleProvidersByClass_success() {
        var providers = ServiceUtils.providers(this.getClass().getModule().getLayer(), TestServiceB.class);
        assertThat(providers).hasSize(2);
        assertThat(providers.contains(TestServiceBProvider1.class.getName())).isTrue();
        assertThat(providers.contains(TestServiceBProvider2.class.getName())).isTrue();
    }

    /* ---------------- */

    @Test
    public void loadProvider_oneServiceWithSingleProvidersWithoutParentByType_success() {
        var provider = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(),
                false, new ServiceType<TestServiceA>() { });
        assertThat(provider.get()).isNotNull();
        assertThat(provider.get()).isInstanceOf(TestServiceA.class);
    }

    @Test
    public void loadProvider_oneServiceWithSingleProvidersWithParentByType_success() {
        var provider = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(),
                true, new ServiceType<TestServiceA>() { });
        assertThat(provider.get()).isNotNull();
        assertThat(provider.get()).isInstanceOf(TestServiceA.class);
    }

    @Test
    public void loadProvider_oneServiceWithSingleProvidersWithoutParentByClass_success() {
        var provider = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(), false, TestServiceA.class);
        assertThat(provider.get()).isNotNull();
        assertThat(provider.get()).isInstanceOf(TestServiceA.class);
    }

    @Test
    public void loadProvider_oneServiceWithSingleProvidersWithParentByClass_success() {
        var provider = ServiceUtils.loadProvider(this.getClass().getModule().getLayer(), true, TestServiceA.class);
        assertThat(provider.get()).isNotNull();
        assertThat(provider.get()).isInstanceOf(TestServiceA.class);
    }

    /* ---------------- */

    @Test
    public void loadProvider_oneServiceWithMultipleProvidersWithoutParentByType_illegalStateException() {
        assertThatThrownBy(() -> ServiceUtils.loadProvider(this.getClass().getModule().getLayer(),
                false, new ServiceType<TestServiceB>() { })).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void loadProvider_oneServiceWithMultipleProvidersWithParentByType_illegalStateException() {
        assertThatThrownBy(() -> ServiceUtils.loadProvider(this.getClass().getModule().getLayer(),
                true, new ServiceType<TestServiceB>() { })).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void loadProvider_oneServiceWithMultipleProvidersWithoutParentByClass_illegalStateException() {
        assertThatThrownBy(() -> ServiceUtils.loadProvider(this.getClass().getModule().getLayer(),
                false, TestServiceB.class)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void loadProvider_oneServiceWithMultipleProvidersWithParentByClass_illegalStateException() {
        assertThatThrownBy(() -> ServiceUtils.loadProvider(this.getClass().getModule().getLayer(),
                true, TestServiceB.class)).isInstanceOf(IllegalStateException.class);
    }

    /* ---------------- */

    @Test
    public void loadProviders_oneServiceWithSingleProvidersWithoutParentByType_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(),
                false, new ServiceType<TestServiceA>() { });
        assertThat(providers).hasSize(1);
        assertThat(providers.get(0)).isNotNull();
        assertThat(providers.get(0)).isInstanceOf(TestServiceA.class);
    }

    @Test
    public void loadProviders_oneServiceWithSingleProvidersWithParentByType_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(),
                true, new ServiceType<TestServiceA>() { });
        assertThat(providers).hasSize(1);
        assertThat(providers.get(0)).isNotNull();
        assertThat(providers.get(0)).isInstanceOf(TestServiceA.class);
    }

    @Test
    public void loadProviders_oneServiceWithSingleProvidersWithoutParentByClass_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(), false, TestServiceA.class);
        assertThat(providers).hasSize(1);
        assertThat(providers.get(0)).isNotNull();
        assertThat(providers.get(0)).isInstanceOf(TestServiceA.class);
    }

    @Test
    public void loadProviders_oneServiceWithMSingleProvidersWithParentByClass_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(), true, TestServiceA.class);
        assertThat(providers).hasSize(1);
        assertThat(providers.get(0)).isNotNull();
        assertThat(providers.get(0)).isInstanceOf(TestServiceA.class);
    }

    /* ---------------- */

    @Test
    public void loadProviders_oneServiceWithMultipleProvidersWithoutParentByType_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(),
                false, new ServiceType<TestServiceB>() { });
        var set = new HashSet<>(providers);
        assertThat(set).hasSize(2);
    }

    @Test
    public void loadProviders_oneServiceWithMultipleProvidersWithParentByType_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(),
                true, new ServiceType<TestServiceB>() { });
        var set = new HashSet<>(providers);
        assertThat(set).hasSize(2);
    }

    @Test
    public void loadProviders_oneServiceWithMultipleProvidersWithoutParentByClass_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(), false, TestServiceB.class);
        var set = new HashSet<>(providers);
        assertThat(set).hasSize(2);
    }

    @Test
    public void loadProviders_oneServiceWithMultipleProvidersWithParentByClass_illegalStateException() {
        var providers = ServiceUtils.loadProviders(this.getClass().getModule().getLayer(), true, TestServiceB.class);
        var set = new HashSet<>(providers);
        assertThat(set).hasSize(2);
    }

    /* ---------------- */
}
