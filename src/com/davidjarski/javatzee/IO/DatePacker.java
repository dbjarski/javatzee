/* 
 * The MIT License
 *
 * Copyright 2014 David Jarski.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.davidjarski.javatzee.IO;

import java.util.GregorianCalendar;

public class DatePacker
{
    public static final int MIN_YEAR = 0;
    public static final int MAX_YEAR = MIN_YEAR + 4095;
    
    private static final int BASE_YEAR = MIN_YEAR;

    private static final int MONTH_LSHIFT  = 4;
    private static final int DAY_LSHIFT    = 5;
    private static final int HOUR_LSHIFT   = 5;
    private static final int MINUTE_LSHIFT = 6;

    private static final int YEAR_RSHIFT  = 20;
    private static final int MONTH_RSHIFT = 16;
    private static final int DAY_RSHIFT   = 11;
    private static final int HOUR_RSHIFT  =  6;

    private static final int YEAR_MASK   = 0xFFF00000;
    private static final int MONTH_MASK  = 0x000F0000;
    private static final int DAY_MASK    = 0x0000F800;
    private static final int HOUR_MASK   = 0x000007C0;
    private static final int MINUTE_MASK = 0x0000003F;

    public static final int pack(GregorianCalendar calendar) {
        final int year = calendar.get(GregorianCalendar.YEAR) - BASE_YEAR;
        final int month = calendar.get(GregorianCalendar.MONTH);
        final int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
        final int hour = calendar.get(GregorianCalendar.HOUR_OF_DAY);
        final int minute = calendar.get(GregorianCalendar.MINUTE);

        return (((year << MONTH_LSHIFT | month) << DAY_LSHIFT | day) << HOUR_LSHIFT | hour) << MINUTE_LSHIFT | minute;
    }

    public static final GregorianCalendar unpack(int pack) {
            return new GregorianCalendar(
                    ((pack & YEAR_MASK) >>> YEAR_RSHIFT) + BASE_YEAR,
                    (pack & MONTH_MASK) >> MONTH_RSHIFT,
                    (pack & DAY_MASK) >> DAY_RSHIFT,
                    (pack & HOUR_MASK) >> HOUR_RSHIFT,
                    pack & MINUTE_MASK);
    }
}
