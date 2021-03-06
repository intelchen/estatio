/*
 * Copyright 2015 Yodo Int. Projects and Consultancy
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.estatio.fixture.budget;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.estatio.dom.asset.PropertyMenu;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;
import org.estatio.dom.budgeting.keytable.FoundationValueType;
import org.estatio.dom.budgeting.keytable.KeyTable;
import org.estatio.dom.budgeting.keytable.KeyTables;
import org.estatio.dom.budgeting.keytable.KeyValueMethod;
import org.estatio.fixture.EstatioFixtureScript;

/**
 * Created by jodo on 22/04/15.
 */
public abstract class KeyTableAbstact extends EstatioFixtureScript {

    protected KeyTable createKeyTable(
            final Property property,
            final String name,
            final LocalDate startDate,
            final LocalDate endDate,
            final FoundationValueType foundationValueType,
            final KeyValueMethod keyValueMethod,
            final Integer numberOfDigits,
            final ExecutionContext fixtureResults){
        KeyTable keyTable = keyTables.newKeyTable(property, name, startDate, endDate, foundationValueType, keyValueMethod, numberOfDigits);
        keyTable.generateItems(true);
        return fixtureResults.addResult(this, keyTable);
    }

    @Inject
    protected KeyTables keyTables;

    @Inject
    PropertyRepository propertyRepository;
    @Inject
    protected PropertyMenu propertyMenu;
}
