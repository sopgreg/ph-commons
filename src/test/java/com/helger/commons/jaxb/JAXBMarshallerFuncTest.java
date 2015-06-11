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
package com.helger.commons.jaxb;

import static org.junit.Assert.assertTrue;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.stream.NonBlockingByteArrayOutputStream;
import com.helger.commons.mutable.MutableBoolean;
import com.helger.commons.xml.namespace.MapBasedNamespaceContext;

public final class JAXBMarshallerFuncTest
{
  private static final class MyMarshaller extends AbstractJAXBMarshaller <MockJAXBArchive>
  {
    public MyMarshaller ()
    {
      super (MockJAXBArchive.class, (IReadableResource) null);
    }

    @Override
    protected void customizeMarshaller (@Nonnull final Marshaller aMarshaller)
    {
      JAXBMarshallerHelper.setFormattedOutput (aMarshaller, true);
      JAXBMarshallerHelper.setSunNamespacePrefixMapper (aMarshaller,
                                                       new MapBasedNamespaceContext ().addMapping ("def", "urn:test"));
    }

    @Override
    protected JAXBElement <MockJAXBArchive> wrapObject (final MockJAXBArchive aObject)
    {
      return new JAXBElement <MockJAXBArchive> (new QName ("urn:test", "any"), MockJAXBArchive.class, aObject);
    }
  }

  private static final Logger s_aLogger = LoggerFactory.getLogger (JAXBMarshallerFuncTest.class);

  @Test
  public void testCloseOnWriteToOutputStream ()
  {
    final MyMarshaller m = new MyMarshaller ();
    final MutableBoolean aClosed = new MutableBoolean (false);
    final NonBlockingByteArrayOutputStream aOS = new NonBlockingByteArrayOutputStream ()
    {
      @Override
      public void close ()
      {
        super.close ();
        aClosed.set (true);
      }
    };
    {
      final MockJAXBArchive aArc = new MockJAXBArchive ();
      aArc.setVersion ("1.23");
      m.write (aArc, aOS);
    }
    s_aLogger.info (aOS.getAsString (CCharset.CHARSET_UTF_8_OBJ));
    // Must be closed!
    assertTrue ("Not closed!", aClosed.booleanValue ());
  }
}
