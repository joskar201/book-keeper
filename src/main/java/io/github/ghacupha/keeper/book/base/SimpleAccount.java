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
import io.github.ghacupha.keeper.book.api.AccountAttributes;
import io.github.ghacupha.keeper.book.api.Entry;
import io.github.ghacupha.keeper.book.balance.AccountBalance;
import io.github.ghacupha.keeper.book.balance.AccountSide;
import io.github.ghacupha.keeper.book.unit.money.Cash;
import io.github.ghacupha.keeper.book.unit.money.HardCash;
import io.github.ghacupha.keeper.book.unit.time.DateRange;
import io.github.ghacupha.keeper.book.unit.time.Moment;
import io.github.ghacupha.keeper.book.unit.time.TimePoint;
import io.github.ghacupha.keeper.book.util.MismatchedCurrencyException;
import io.github.ghacupha.keeper.book.util.UntimelyBookingDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

/**
 * This is a container for {@link java.util.Collection} of entries with the ability to return
 * {@link AccountBalance} on request.
 *
 * @author edwin.njeru
 */
public class SimpleAccount implements Account {

    private static final Logger log = LoggerFactory.getLogger(SimpleAccount.class);
    private final Currency currency;
    private final AccountAttributes accountAttributes;
    private volatile AccountSide accountSide;
    private Collection<Entry> entries = new HashSet<>();


    public SimpleAccount(AccountSide accountSide, Currency currency, AccountAttributes accountAttributes) {
        this.accountSide = accountSide;
        this.currency = currency;
        this.accountAttributes = accountAttributes;

        log.debug("SimpleAccount created : {}", this);
    }

    private static Cash getCashAmount(Entry filteredEntry) {
        Cash amount = filteredEntry.getAmount();
        log.debug("Accounting entry : {} added into the balance with amount : {}", filteredEntry, amount);
        return amount;
    }

    @Override
    public void addEntry(Entry entry) throws UntimelyBookingDateException, MismatchedCurrencyException {

        log.debug("Adding entry to account : {}", entry);

        if (entry.getBookingDate().before(accountAttributes.getOpeningDate())) {

            String message = String.format("Opening date : %s . The entry date was %s", this.accountAttributes.getOpeningDate(), entry.getBookingDate());
            throw new UntimelyBookingDateException("The booking date cannot be earlier than the account opening date : " + message);

        } else if (!this.currency.equals(entry.getAmount().getCurrency())) {

            String message = String.format("Currencies mismatched :Expected currency : %s but found entry denominated in %s", this.currency.toString(), entry.getAmount().getCurrency());
            throw new MismatchedCurrencyException(message);

        } else {

            entries.add(entry); // done
        }
    }

    private AccountBalance balance(DateRange dateRange) {

        final Cash[] result = {new HardCash(0, currency)};

        entries.stream().filter(entry -> dateRange.includes(entry.getBookingDate())).map(SimpleAccount::getCashAmount).forEachOrdered(orderedAmount -> {
            log.debug("Adding amount : {}", orderedAmount);
            result[0] = result[0].plus(orderedAmount);
        });

        log.debug("Returning balance of amount : {} on the {} side", result[0], accountSide);

        return new AccountBalance(result[0], accountSide);
    }

    @Override
    public AccountBalance balance(TimePoint asAt) {

        AccountBalance balance = balance(new DateRange(accountAttributes.getOpeningDate(), asAt));

        log.debug("Returning accounting balance as at : {} as : {}", asAt, balance);

        return balance;
    }

    /**
     * Similar to the balance query for a given date except the date is provided through a
     * simple varags int argument
     *
     * @param asAt The date as at when the {@link AccountBalance} we want is effective given
     *             in the following order
     *             i) Year
     *             ii) Month
     *             iii) Date
     * @return {@link AccountBalance} effective the date specified by the varargs
     */
    @Override
    public AccountBalance balance(int... asAt) {
        AccountBalance balance = balance(new Moment(asAt[0],asAt[1],asAt[2]));

        log.debug("Returning accounting balance as at : {} as : {}", asAt, balance);

        return balance;
    }

    @Override
    public AccountBalance balance() {
        return balance(new Moment());
    }

    @Override
    public String toString() {
        return this.accountAttributes.toString();
    }

    @Override
    public Currency getCurrency() {

        return currency;
    }

    @Override
    public TimePoint getOpeningDate() {

        return accountAttributes.getOpeningDate();
    }

    @Override
    public AccountSide getAccountSide() {
        return accountSide;
    }

    AccountAttributes getAttributes() {

        return accountAttributes;
    }


    List<Entry> getEntries() {

        return entries.stream().collect(ImmutableListCollector.toImmutableList());
    }
}