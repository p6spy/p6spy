/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2017 P6Spy
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

package com.p6spy.engine.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.p6spy.engine.logging.P6LogFactory;
import com.p6spy.engine.spy.P6Factory;
import com.p6spy.engine.spy.P6SpyFactory;

public class CustomHashedHashSetTest {

	private ClassHasher hasher;
	private Set<P6Factory> set;
	
	private P6LogFactory fl1 = new P6LogFactory();
	private P6LogFactory fl2 = new P6LogFactory();
	private P6SpyFactory fs1 = new P6SpyFactory();
	private P6SpyFactory fs2 = new P6SpyFactory();
	
	@Before
	public void before() {
		hasher = new ClassHasher();
		set = new CustomHashedHashSet<P6Factory>(hasher);
		
		fl1 = new P6LogFactory();
		fl2 = new P6LogFactory();
		
		Assert.assertNotEquals(fl1.hashCode(), fl2.hashCode());
		Assert.assertEquals(hasher.getHashCode(fl1), hasher.getHashCode(fl2));
		
		fs1 = new P6SpyFactory();
		fs2 = new P6SpyFactory();
		
		Assert.assertNotEquals(fs1.hashCode(), fs2.hashCode());
		Assert.assertEquals(hasher.getHashCode(fs1), hasher.getHashCode(fs2));

		Assert.assertEquals(0, set.size());
	}
	
	@Test
	public void testAdd() {
		set.add(fl1);
		Assert.assertEquals(1, set.size());
		set.add(fl2);
		Assert.assertEquals(1, set.size());
		
		set.addAll(Arrays.asList(fs1, fs2));
		Assert.assertEquals(2, set.size());
	}
	
	@Test
	public void testRemove() {
		set.addAll(Arrays.asList(fl1, fl2, fs1, fs2));
		Assert.assertEquals(2, set.size());
		
		set.remove(fl1);
		Assert.assertEquals(1, set.size());
		set.removeAll(Arrays.asList(fs1, fs2));
		Assert.assertEquals(0, set.size());
	}
	
	@Test
	public void testContains() {
		Assert.assertFalse(set.contains(fl1));
		
		set.addAll(Arrays.asList(fl1, fs1));
		Assert.assertTrue(set.contains(fl1));
		Assert.assertTrue(set.contains(fl2));
		Assert.assertTrue(set.contains(fs1));
		Assert.assertTrue(set.contains(fs2));
	}
	
	@Test
	public void testIterator() {
		final List<P6Factory> list = Arrays.asList(fl1, fs1);
		final List<P6Factory> classHashEqualList = Arrays.asList(fl2, fs2);
		set.addAll(list);
		
		for (Iterator<P6Factory> it = set.iterator(); it.hasNext();) {
			Object elem = it.next();
			Assert.assertTrue(list.contains(elem));
			Assert.assertFalse(classHashEqualList.contains(elem));
		}
	}
}
