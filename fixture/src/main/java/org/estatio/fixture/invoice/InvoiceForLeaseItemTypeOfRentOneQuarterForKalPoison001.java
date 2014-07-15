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
package org.estatio.fixture.invoice;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.fixture.currency.refdata.CurrenciesRefData;
import org.estatio.fixture.lease.LeaseForKalPoison001;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForKalPoison001;
import org.estatio.fixture.party.OrganisationForAcme;
import org.estatio.fixture.party.OrganisationForPoison;
import org.joda.time.LocalDate;

import org.apache.isis.core.commons.ensure.Ensure;

import static org.estatio.integtests.VT.ldix;
import static org.hamcrest.CoreMatchers.is;

public class InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001 extends InvoiceAbstract {

    public static final String SELLER_PARTY = OrganisationForAcme.PARTY_REFERENCE;
    public static final String BUYER_PARTY = OrganisationForPoison.PARTY_REFERENCE;
    public static final String LEASE = LeaseForKalPoison001.LEASE_REFERENCE;

    // simply within the lease's start/end date
    public static LocalDate startDateFor(Lease lease) {
        Ensure.ensureThatArg(lease.getReference(), is(LEASE));
        return lease.getStartDate().plusYears(1);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001() {
        this(null, null);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        execute(new OrganisationForAcme(), executionContext);
        execute(new LeaseItemAndLeaseTermForRentForKalPoison001(), executionContext);

        // exec

        final Lease lease = leases.findLeaseByReference(LEASE);

        // simply within the lease's start/end date
        final LocalDate startDate = startDateFor(lease);

        final Invoice invoice = createInvoice(
                lease, SELLER_PARTY, BUYER_PARTY,
                PaymentMethod.DIRECT_DEBIT, CurrenciesRefData.EUR,
                startDate,
                executionContext);

        createInvoiceItemsForTermsOfFirstLeaseItemOfType(
                invoice, LeaseItemType.RENT,
                startDate, ldix(startDate, startDate.plusMonths(3)),
                executionContext);
    }

}