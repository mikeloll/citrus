/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.validation.matcher.hamcrest;

import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Christoph Deppisch
 * @since 2.5
 */
public class HamcrestValidationMatcherTest extends AbstractTestNGUnitTest {

    @Test(dataProvider = "testData")
    public void testValidate(String path, String value, List<String> params) throws Exception {
        HamcrestValidationMatcher validationMatcher = new HamcrestValidationMatcher();
        validationMatcher.validate( path, value, params, context);
    }

    @DataProvider
    public Object[][] testData() {
        return new Object[][] {
            new Object[]{ "foo", "value", Collections.singletonList("equalTo(value)") },
            new Object[]{"foo", "value", Collections.singletonList("not(equalTo(other))")},
            new Object[]{"foo", "value", Collections.singletonList("is(not(other))")},
            new Object[]{"foo", "value", Collections.singletonList("not(is(other))")},
            new Object[]{"foo", "value", Collections.singletonList("equalToIgnoringCase(VALUE)")},
            new Object[]{"foo", "value", Collections.singletonList("containsString(lue)")},
            new Object[]{"foo", "value", Collections.singletonList("not(containsString(other))")},
            new Object[]{"foo", "value", Collections.singletonList("startsWith(val)")},
            new Object[]{"foo", "value", Collections.singletonList("endsWith(lue)")},
            new Object[]{"foo", "value", Collections.singletonList("anyOf(startsWith(val), endsWith(lue))")},
            new Object[]{"foo", "value", Collections.singletonList("allOf(startsWith(val), endsWith(lue))")},
            new Object[]{"foo", "", Collections.singletonList("isEmptyString()")},
            new Object[]{"foo", "bar", Collections.singletonList("not(isEmptyString())")},
            new Object[]{"foo", null, Collections.singletonList("isEmptyOrNullString()")},
            new Object[]{"foo", null, Collections.singletonList("nullValue()")},
            new Object[]{"foo", "bar", Collections.singletonList("notNullValue()")},
            new Object[]{"foo", "[]", Collections.singletonList("empty()")},
            new Object[]{"foo", "", Collections.singletonList("empty()")},
            new Object[]{"foo", "bar", Collections.singletonList("not(empty())")},
            new Object[]{"foo", "10", Collections.singletonList("greaterThan(9)")},
            new Object[]{"foo", "10.0", Collections.singletonList("greaterThan(9.0)")},
            new Object[]{"foo", "10.4", Collections.singletonList("greaterThanOrEqualTo(10.4)")},
            new Object[]{"foo", "10.5", Collections.singletonList("greaterThanOrEqualTo(10.4)")},
            new Object[]{"foo", "10", Collections.singletonList("allOf(greaterThan(9), lessThan(11), not(lessThan(10)))")},
            new Object[]{"foo", "10", Collections.singletonList("is(not(greaterThan(10)))")},
            new Object[]{"foo", "10", Collections.singletonList("greaterThanOrEqualTo(10)")},
            new Object[]{"foo", "9", Collections.singletonList("lessThan(10)")},
            new Object[]{"foo", "9", Collections.singletonList("not(lessThan(1))")},
            new Object[]{"foo", "9", Collections.singletonList("lessThanOrEqualTo(9)")},
            new Object[]{"foo", "", Arrays.asList("1", "lessThanOrEqualTo(9)")},
            new Object[]{"foo", "", Arrays.asList("9", "lessThanOrEqualTo(9)")},
            new Object[]{"foo", "[value1,value2,value3,value4,value5]", Collections.singletonList("hasSize(5)") }
        };
    }

    @Test(dataProvider = "testDataFailed", expectedExceptions = AssertionError.class)
    public void testValidateFailed(String path, String value, List<String> params) throws Exception {
        HamcrestValidationMatcher validationMatcher = new HamcrestValidationMatcher();
        validationMatcher.validate( path, value, params, context);
    }

    @DataProvider
    public Object[][] testDataFailed() {
        return new Object[][] {
            new Object[]{ "foo", "value", Collections.singletonList("equalTo(wrong)") },
            new Object[]{"foo", "value", Collections.singletonList("not(equalTo(value))")},
            new Object[]{"foo", "value", Collections.singletonList("is(not(value))")},
            new Object[]{"foo", "value", Collections.singletonList("not(is(value))")},
            new Object[]{"foo", "value", Collections.singletonList("equalToIgnoringCase(WRONG)")},
            new Object[]{"foo", "value", Collections.singletonList("containsString(wrong)")},
            new Object[]{"foo", "value", Collections.singletonList("not(containsString(value))")},
            new Object[]{"foo", "value", Collections.singletonList("startsWith(wrong)")},
            new Object[]{"foo", "value", Collections.singletonList("endsWith(wrong)")},
            new Object[]{"foo", "value", Collections.singletonList("anyOf(startsWith(wrong), endsWith(wrong))")},
            new Object[]{"foo", "value", Collections.singletonList("allOf(startsWith(wrong), endsWith(wrong))")},
            new Object[]{"foo", "bar", Collections.singletonList("isEmptyString()")},
            new Object[]{"foo", "", Collections.singletonList("not(isEmptyString())")},
            new Object[]{"foo", "bar", Collections.singletonList("isEmptyOrNullString()")},
            new Object[]{"foo", "bar", Collections.singletonList("nullValue()")},
            new Object[]{"foo", null, Collections.singletonList("notNullValue()")},
            new Object[]{"foo", "[bar]", Collections.singletonList("empty()")},
            new Object[]{"foo", "bar", Collections.singletonList("empty()")},
            new Object[]{"foo", "9", Collections.singletonList("greaterThan(9)")},
            new Object[]{"foo", "9.0", Collections.singletonList("greaterThan(9.0)")},
            new Object[]{"foo", "9.3", Collections.singletonList("greaterThanOrEqualTo(9.4)")},
            new Object[]{"foo", "9", Collections.singletonList("allOf(greaterThan(9), lessThan(11), not(lessThan(10)))")},
            new Object[]{"foo", "11", Collections.singletonList("is(not(greaterThan(10)))")},
            new Object[]{"foo", "9", Collections.singletonList("greaterThanOrEqualTo(10)")},
            new Object[]{"foo", "10", Collections.singletonList("lessThan(10)")},
            new Object[]{"foo", "ten", Collections.singletonList("lessThan(10)")},
            new Object[]{"foo", "0", Collections.singletonList("not(lessThan(1))")},
            new Object[]{"foo", "10", Collections.singletonList("lessThanOrEqualTo(9)")},
            new Object[]{"foo", "", Arrays.asList("10", "lessThanOrEqualTo(9)")},
            new Object[]{"foo", "[value1,value2]", Collections.singletonList("hasSize(5)") }
        };
    }
}