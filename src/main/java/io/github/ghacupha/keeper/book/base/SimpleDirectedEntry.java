/*
 *  Copyright 2018 Edwin Njeru
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

package io.github.ghacupha.keeper.book.base;

import io.github.ghacupha.keeper.book.api.Account;
import io.github.ghacupha.keeper.book.api.EntryAttributes;
import io.github.ghacupha.keeper.book.api.Transaction;
import io.github.ghacupha.keeper.book.balance.AccountBalance;
import io.github.ghacupha.keeper.book.balance.AccountSide;
import io.github.ghacupha.keeper.book.unit.money.Cash;
import io.github.ghacupha.keeper.book.unit.time.TimePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object records and has a pointer towards the {@link AccountSide} to which
 * it belongs, making it possible for it to create proper {@link AccountBalance} that
 * denote the situation of a {@link SimpleAccount} as per the business domain
 *
 * @author edwin.njeru
 */
public class SimpleDirectedEntry extends TransactionalEntryDecorator {

    private static final Logger log = LoggerFactory.getLogger(SimpleDirectedEntry.class);

    private final AccountSide accountSide;

    SimpleDirectedEntry(Account forJournal, EntryAttributes entryAttributes, Cash amount, TimePoint bookingDate, Transaction transaction, AccountSide accountSide) {
        super(forJournal, entryAttributes, amount, bookingDate, transaction);
        this.accountSide = accountSide;

        log.debug("SimpleDirectedEntry created : {}", this);
    }


    public AccountSide getAccountSide() {
        return accountSide;
    }

    @Override
    public String toString() {
        return "SimpleDirectedEntry { " + super.toString() + this.accountSide;
    }
}