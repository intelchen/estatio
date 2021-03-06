/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.budgeting.budget;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.asset.Property;
import org.estatio.dom.valuetypes.LocalDateInterval;

@DomainService(repositoryFor = Budget.class, nature = NatureOfService.DOMAIN)
@DomainServiceLayout()
public class Budgets extends UdoDomainRepositoryAndFactory<Budget> {

    public Budgets() {
        super(Budgets.class, Budget.class);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Budget newBudget(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate) {
        Budget budget = newTransientInstance();
        budget.setProperty(property);
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        persistIfNotAlready(budget);

        return budget;
    }

    public String validateNewBudget(
            final Property property,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (!new LocalDateInterval(startDate, endDate).isValid()) {
            return "End date can not be before start date";
        }

        for (Budget budget : this.findByProperty(property)) {
            if (budget.getInterval().overlaps(new LocalDateInterval(startDate, endDate))) {
                return "A budget cannot overlap an existing budget.";
            }
        }

        return null;
    }

    @Programmatic
    public Budget findOrCreateBudget(
            final @ParameterLayout(named = "Property") Property property,
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") LocalDate endDate) {

        if (findByPropertyAndStartDate(property, startDate)!= null){
            return findByPropertyAndStartDate(property, startDate);
        } else {
            return newBudget(property,startDate,endDate);
        }
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Budget> allBudgets() {
        return allInstances();
    }

    @Programmatic
    public List<Budget> findByProperty(Property property){
        return allMatches("findByProperty", "property", property);
    }

    @Programmatic
    public Budget findByPropertyAndStartDate(Property property, LocalDate startDate){
        return uniqueMatch("findByPropertyAndStartDate", "property", property, "startDate", startDate);
    }
}
