package io.github.ghacupha.keeper.book.unit.time;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateRangeTest {

    private DateRange dateRange;

    @Before
    public void setUp() throws Exception {
        dateRange = new DateRange(Moment.newMoment(2017,9,30),Moment.newMoment(2017,12,30));
    }

    @Test
    public void upTo() {

        assertTrue(dateRange.includes(Moment.newMoment(2017,11,30)));
        assertTrue(dateRange.includes(Moment.newMoment(2017,12,30)));
        assertFalse(dateRange.includes(Moment.newMoment(2017,12,31)));

        DateRange infiniteStart = DateRange.upTo((Moment) Moment.newMoment(2017,11,30));
        DateRange infiniteEnd = DateRange.startingOn((Moment) Moment.newMoment(2017,11,30));

        assertTrue(infiniteStart.includes(Moment.newMoment(1900,01,01)));
        assertTrue(infiniteEnd.includes(Moment.newMoment(9999,01,01)));
    }


}