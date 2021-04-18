/**
 * P6Spy
 *
 * Copyright (C) 2002 P6Spy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.p6spy.engine.spy.option;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import javax.management.JMException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.p6spy.engine.logging.P6LogOptions;
import com.p6spy.engine.test.BaseTestCase;
import com.p6spy.engine.test.P6TestFramework;

public class P6TestOptionsSources extends BaseTestCase {

	@BeforeEach
	public void setUp() throws JMException, SQLException, IOException,
			InterruptedException {
		// make sure to reinit properly
		new P6TestFramework("blank") {
		};
	}

	@AfterEach
	public void tearDown() {
		// cleanup to make sure other tests work as expected
		System.getProperties().remove(
				SystemProperties.P6SPY_PREFIX + P6LogOptions.EXCLUDECATEGORIES);
	}

	@Test
	public void testSpyDotPropetiesNoKeyValueDoesntModifySetOption()
			throws SQLException, IOException {
		new P6TestFramework("blank") {
		};

		assertTrue(P6LogOptions
				.getActiveInstance()
				.getExcludeCategoriesSet()
				.containsAll(
						Arrays.asList(P6TestOptionDefaults.DEFAULT_CATEGORIES)));
	}

	@Test
	public void testSpyDotPropetiesEmptyStringValueClearsSetOption()
			throws SQLException, IOException {
		new P6TestFramework("override_clear") {
		};

		assertNull(P6LogOptions.getActiveInstance()
				.getExcludeCategoriesSet());
	}

	@Test
	public void testSysPropertyNoKeyValueDoesntModifySetOption()
			throws SQLException, IOException {
		new P6TestFramework("blank") {
		};

		assertTrue(P6LogOptions
				.getActiveInstance()
				.getExcludeCategoriesSet()
				.containsAll(
						Arrays.asList(P6TestOptionDefaults.DEFAULT_CATEGORIES)));
	}

	@Test
	public void testSysPropertyEmptyStringValueClearsSetOption() throws SQLException,
			IOException {
		System.getProperties().setProperty(
				SystemProperties.P6SPY_PREFIX + P6LogOptions.EXCLUDECATEGORIES,
				"");

		new P6TestFramework("blank") {
		};

		assertNull(P6LogOptions.getActiveInstance()
				.getExcludeCategoriesSet());
	}
	
	@Test
	public void testPojoSetterNullValueDoesntModifySetOption()
			throws SQLException, IOException {

		P6LogOptions.getActiveInstance().setExcludecategories(null);
		
		assertTrue(P6LogOptions
				.getActiveInstance()
				.getExcludeCategoriesSet()
				.containsAll(
						Arrays.asList(P6TestOptionDefaults.DEFAULT_CATEGORIES)));
	}

	@Test
	public void testPojoSetterEmptyStringValueClearsSetOption() throws SQLException,
			IOException {
		
		P6LogOptions.getActiveInstance().setExcludecategories("");

		assertNull(P6LogOptions.getActiveInstance()
				.getExcludeCategoriesSet());
	}
}
