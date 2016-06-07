/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.xml.dom;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.string.StringHelper;
import com.helger.xml.dom.EXMLDOMFeatureVersion;

/**
 * Test class for class {@link EXMLDOMFeatureVersion}.
 *
 * @author Philip Helger
 */
public final class EXMLDOMFeatureVersionTest
{
  @Test
  public void testAll ()
  {
    for (final EXMLDOMFeatureVersion e : EXMLDOMFeatureVersion.values ())
    {
      assertTrue (StringHelper.hasText (e.getID ()));
      assertSame (e, EXMLDOMFeatureVersion.valueOf (e.name ()));
    }
  }
}