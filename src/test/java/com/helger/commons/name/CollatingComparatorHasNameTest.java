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
package com.helger.commons.name;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.mock.AbstractCommonsTestCase;

/**
 * Test class for class {@link CollatingComparatorHasName}.
 *
 * @author Philip Helger
 */
public final class CollatingComparatorHasNameTest extends AbstractCommonsTestCase
{
  @Test
  public void testAll ()
  {
    final List <MockHasName> aList = CollectionHelper.newList (new MockHasName (10),
                                                               new MockHasName (2),
                                                               new MockHasName (5));
    List <MockHasName> l2 = CollectionHelper.getSorted (aList, new CollatingComparatorHasName <IHasName> (L_DE));
    assertEquals (3, l2.size ());
    assertEquals ("10", l2.get (0).getName ());
    assertEquals ("2", l2.get (1).getName ());
    assertEquals ("5", l2.get (2).getName ());

    l2 = CollectionHelper.getSorted (aList,
                                     new CollatingComparatorHasName <IHasName> (L_DE).setSortOrder (ESortOrder.DESCENDING));
    assertEquals (3, l2.size ());
    assertEquals ("5", l2.get (0).getName ());
    assertEquals ("2", l2.get (1).getName ());
    assertEquals ("10", l2.get (2).getName ());
  }
}
