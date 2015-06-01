/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.commons.compare;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.mock.AbstractCommonsTestCase;
import com.helger.commons.mock.CommonsTestUtils;

/**
 * Test class for class {@link ComparatorStringShortestFirst}.
 *
 * @author Philip Helger
 */
public final class ComparatorStringShortestFirstTest extends AbstractCommonsTestCase
{
  @Test
  public void testBasic ()
  {
    final List <String> l = CollectionHelper.newList ("A", "dd", "a");
    final List <String> s = CollectionHelper.getSorted (l, new ComparatorStringShortestFirst ());
    assertEquals (3, s.size ());
    assertEquals ("A", s.get (0));
    assertEquals ("a", s.get (1));
    assertEquals ("dd", s.get (2));

    CommonsTestUtils.testToStringImplementation (new ComparatorStringShortestFirst ());
  }

  @Test
  public void testWithContentComparator ()
  {
    final List <String> l = CollectionHelper.newList ("A", "dd", "a");
    final List <String> s = CollectionHelper.getSorted (l,
                                                        new ComparatorStringShortestFirst (new CollatingComparator (L_DE)));
    assertEquals (3, s.size ());
    assertEquals ("a", s.get (0));
    assertEquals ("A", s.get (1));
    assertEquals ("dd", s.get (2));
  }
}
