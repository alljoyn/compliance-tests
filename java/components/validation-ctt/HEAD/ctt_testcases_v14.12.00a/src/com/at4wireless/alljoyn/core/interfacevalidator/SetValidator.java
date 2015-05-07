package com.at4wireless.alljoyn.core.interfacevalidator;

import java.util.HashSet;
import java.util.Set;



public class SetValidator<E>
{
    public ValidationResult validate(Set<E> expectedObjectSet, Set<E> foundObjectSet)
    {
        boolean valid = true;
        StringBuilder failureReasonBuilder = new StringBuilder();
        Set<Object> extraObjects = subtract(foundObjectSet, expectedObjectSet);
        Set<Object> missingObjects = subtract(expectedObjectSet, foundObjectSet);

        if (!missingObjects.isEmpty())
        {
            valid = false;

            for (Object object : missingObjects)
            {
                failureReasonBuilder.append(" - Missing ");
                failureReasonBuilder.append(object.toString());
            }
        }

        if (!extraObjects.isEmpty())
        {
            valid = false;

            for (Object object : extraObjects)
            {
                failureReasonBuilder.append(" - Undefined ");
                failureReasonBuilder.append(object.toString());
            }
        }

        return new ValidationResult(valid, failureReasonBuilder.toString());
    }

    private Set<Object> subtract(Set<E> expectedObjectSet, Set<E> foundObjectSet)
    {
        Set<Object> missingObjects = new HashSet<Object>();

        for (Object object : expectedObjectSet)
        {
            if (!foundObjectSet.contains(object))
            {
                missingObjects.add(object);
            }
        }

        return missingObjects;
    }
}