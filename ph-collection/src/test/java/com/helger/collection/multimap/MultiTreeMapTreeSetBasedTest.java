/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.collection.multimap;

import org.junit.Test;

import com.helger.commons.collection.impl.ICommonsSet;

/**
 * Test class for class {@link MultiTreeMapTreeSetBased}.
 *
 * @author Philip Helger
 */
@Deprecated
public final class MultiTreeMapTreeSetBasedTest extends AbstractMultiMapTestCase
{
  @Test
  public void testAll ()
  {
    IMultiMapSetBased <String, String, ? extends ICommonsSet <String>> aMultiMap = new MultiTreeMapTreeSetBased <> ();
    testEmpty (aMultiMap);
    aMultiMap = new MultiTreeMapTreeSetBased <> (getKey1 (), getValue1 ());
    testOne (aMultiMap);
    aMultiMap = new MultiTreeMapTreeSetBased <> (getKey1 (), getValueSetNavigable1 ());
    testOne (aMultiMap);
    aMultiMap = new MultiTreeMapTreeSetBased <> (getMapSetNavigable1 ());
    testOne (aMultiMap);
  }
}
