/*******************************************************************************
 *  Copyright AllSeen Alliance. All rights reserved.
 *
 *     Permission to use, copy, modify, and/or distribute this software for any
 *     purpose with or without fee is hereby granted, provided that the above
 *     copyright notice and this permission notice appear in all copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *     WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *     MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *     ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *     WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *     ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *     OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
package org.alljoyn.validation.testing.utils;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.alljoyn.validation.framework.annotation.ValidationTest;

public class ValidationTestComparator implements Comparator<Method>
{
    private static final ValidationTestComparator INSTANCE = new ValidationTestComparator();

    private ValidationTestComparator()
    {
    }

    public static ValidationTestComparator getInstance()
    {
        return INSTANCE;
    }

    @Override
    public int compare(Method o1, Method o2)
    {
        ValidationTest validationTest1 = o1.getAnnotation(ValidationTest.class);
        ValidationTest validationTest2 = o2.getAnnotation(ValidationTest.class);
        if (validationTest1.order() == validationTest2.order())
        {
            return validationTest1.name().compareTo(validationTest2.name());
        }
        else
        {
            return Integer.valueOf(validationTest1.order()).compareTo(validationTest2.order());
        }
    }
}