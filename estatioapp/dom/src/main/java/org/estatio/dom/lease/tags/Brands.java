/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease.tags;

import com.google.common.collect.Lists;
import org.apache.isis.applib.annotation.*;
import org.estatio.dom.Dflt;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.apptenancy.ApplicationTenancyRepository;
import org.estatio.dom.geography.Country;
import org.estatio.dom.utils.StringUtils;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import javax.inject.Inject;
import javax.jdo.Query;
import java.util.List;

@DomainService(repositoryFor = Brand.class, nature = NatureOfService.VIEW_MENU_ONLY)
@DomainServiceLayout(
        named = "Other",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "80.9"
)
public class Brands extends UdoDomainRepositoryAndFactory<Brand> {

    public Brands() {
        super(Brands.class, Brand.class);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    public Brand newBrand(
            final @ParameterLayout(named = "Brand name") String name,
            final @ParameterLayout(named = "Brand coverage") @Parameter(optionality = Optionality.OPTIONAL) BrandCoverage coverage,
            final @ParameterLayout(named = "Country of origin") @Parameter(optionality = Optionality.OPTIONAL) Country countryOfOrigin,
            final @ParameterLayout(named = "Parent brand") @Parameter(optionality = Optionality.OPTIONAL) Brand parentBrand,
            final @ParameterLayout(named = "Global or Country") ApplicationTenancy applicationTenancy) {
        Brand brand;
        brand = newTransientInstance(Brand.class);
        brand.setName(name);
        brand.setCoverage(coverage);
        brand.setCountryOfOrigin(countryOfOrigin);
        brand.setParentBrand(parentBrand);
        brand.setApplicationTenancyPath(applicationTenancy.getPath());
        persist(brand);
        return brand;
    }

    public List<ApplicationTenancy> choices4NewBrand() {
        return applicationTenancyRepository.countryTenanciesForCurrentUser();
    }

    public ApplicationTenancy default4NewBrand() {
        return Dflt.of(choices4NewBrand());
    }

    @Inject
    private ApplicationTenancyRepository applicationTenancyRepository;

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public List<Brand> allBrands() {
        return allInstances();
    }

    @SuppressWarnings({"unchecked"})
    @Action(semantics = SemanticsOf.SAFE, hidden = Where.EVERYWHERE)
    public List<String> findUniqueNames() {
        final Query query = newQuery("SELECT name FROM org.estatio.dom.lease.tags.Brand");
        return (List<String>) query.execute();
    }

    @Action(hidden = Where.EVERYWHERE)
    public Brand findByName(final String name) {
        return firstMatch("findByName", "name", name);
    }

    @Action(hidden = Where.EVERYWHERE)
    public List<Brand> matchByName(final String name) {
        return allMatches("matchByName", "name", StringUtils.wildcardToCaseInsensitiveRegex(name));
    }

    @Programmatic
    public Brand findOrCreate(
            final ApplicationTenancy applicationTenancy,
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL) final BrandCoverage brandCoverage,
            @Parameter(optionality = Optionality.OPTIONAL) final Country countryOfOrigin) {
        if (name == null) {
            return null;
        }
        Brand brand = findByName(name);
        if (brand == null) {
            brand = newBrand(name, brandCoverage, countryOfOrigin, null, applicationTenancy);
        }
        return brand;
    }

    @Action(hidden = Where.EVERYWHERE)
    public List<Brand> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 0
                ? matchByName("*" + searchPhrase + "*")
                : Lists.<Brand>newArrayList();
    }

}
