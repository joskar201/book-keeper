/*
 *  Copyright 2018 Edwin Njeru
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.ghacupha.keeper.book.base;

import io.github.ghacupha.keeper.book.api.Account;
import io.github.ghacupha.keeper.book.api.Entry;
import io.github.ghacupha.keeper.book.balance.AccountSide;
import io.github.ghacupha.keeper.book.unit.money.Cash;
import io.github.ghacupha.keeper.book.unit.time.TimePoint;
import io.github.ghacupha.keeper.book.util.MismatchedCurrencyException;
import io.github.ghacupha.keeper.book.util.UntimelyBookingDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;

public final class SimpleEntry implements Entry {

    private static final Logger log = LoggerFactory.getLogger(SimpleEntry.class);

    // pointer to the Account
    private final Account forAccount;

    private final Cash amount;

    // AccountSide
    private final AccountSide accountSide;

    private final TimePoint bookingDate;

    private final EntryDetails entryDetails;


    public SimpleEntry(AccountSide accountSide, Account forAccount, Cash amount, TimePoint bookingDate, EntryDetails entryDetails) {
        this.forAccount = forAccount;
        this.accountSide = accountSide;
        this.amount = amount;
        this.bookingDate = bookingDate;
        this.entryDetails = entryDetails;
    }

    @Override
    public EntryDetails getEntryDetails() {
        return entryDetails;
    }

    @Override
    public Currency getCurrency() {
        return amount.getCurrency();
    }

    @Override
    public AccountSide getAccountSide() {
        return accountSide;
    }

    @Override
    public TimePoint getBookingDate() {
        return bookingDate;
    }

    @Override
    public Cash getAmount() {
        return amount;
    }

    @Override
    public void post() {
        try {
            log.debug("Adding entry : {} into account : {}",this,forAccount);
            forAccount.addEntry(this);
        } catch (UntimelyBookingDateException e) {
            log.error("Could not post the entry : {} into forAccount : {}", this, forAccount, e.getStackTrace());

            log.error("Cause : the Entry booking date :{} is sooner than the forAccount's opening date {} ", bookingDate, forAccount.getOpeningDate(), e.getStackTrace());
        } catch (MismatchedCurrencyException e) {
            log.error("Could not post the entry : {} into the forAccount : {} ", this, forAccount, e.getStackTrace());
            log.error("Cause the entry's currency : {} does not match the forAccount's currency : {}", amount.getCurrency(), forAccount.getCurrency(), e.getStackTrace());
        }
    }

    @Override
    public String toString() {
        return entryDetails.toString();
    }
}