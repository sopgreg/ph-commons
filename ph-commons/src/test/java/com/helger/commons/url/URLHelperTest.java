/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.url;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Test class for class {@link URLHelper}.
 *
 * @author Philip Helger
 */
public final class URLHelperTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (URLHelperTest.class);

  @Test
  public void testGetCleanURL ()
  {
    assertEquals ("aeoeue", URLHelper.getCleanURLPartWithoutUmlauts ("äöü"));
    assertEquals ("AeoeUe", URLHelper.getCleanURLPartWithoutUmlauts ("ÄöÜ"));
    assertEquals ("Ae-Uesz", URLHelper.getCleanURLPartWithoutUmlauts ("Ä Üß"));
    assertEquals ("Weisze-Waeste", URLHelper.getCleanURLPartWithoutUmlauts ("Weiße Wäste"));
    assertEquals ("hallo", URLHelper.getCleanURLPartWithoutUmlauts ("hállò"));
    assertEquals ("ffi", URLHelper.getCleanURLPartWithoutUmlauts ("\uFB03"));
    assertEquals ("ffl", URLHelper.getCleanURLPartWithoutUmlauts ("\uFB04"));
    assertEquals ("hallo;jsessionid=1234", URLHelper.getCleanURLPartWithoutUmlauts ("hállò;jsessionid=1234"));
  }

  @Test
  public void testUrlEncodeDecode ()
  {
    String sDec = "hallo welt";
    String sEnc = URLHelper.urlEncode (sDec);
    assertEquals ("hallo+welt", sEnc);
    assertEquals (sDec, URLHelper.urlDecode (sEnc));

    // default: UTF-8
    sDec = "äöü";
    sEnc = URLHelper.urlEncode (sDec);
    assertEquals ("%C3%A4%C3%B6%C3%BC", sEnc);
    assertEquals (sDec, URLHelper.urlDecode (sEnc));

    sDec = "äöü";
    sEnc = URLHelper.urlEncode (sDec, StandardCharsets.ISO_8859_1);
    assertEquals ("%E4%F6%FC", sEnc);
    assertEquals (sDec, URLHelper.urlDecode (sEnc, StandardCharsets.ISO_8859_1));
  }

  @Test
  public void testGetURLData ()
  {
    ISimpleURL aData = URLHelper.getAsURLData ("http://www.helger.com/folder?x=y&a=b#c");
    assertNotNull (aData);
    assertEquals (EURLProtocol.HTTP, aData.getProtocol ());
    assertEquals ("http://www.helger.com/folder", aData.getPath ());
    assertEquals (2, aData.params ().size ());
    assertEquals ("y", aData.params ().getFirstParamValue ("x"));
    assertEquals ("b", aData.params ().getFirstParamValue ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = URLHelper.getAsURLData ("?x=y&a=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (2, aData.params ().size ());
    assertEquals ("y", aData.params ().getFirstParamValue ("x"));
    assertEquals ("b", aData.params ().getFirstParamValue ("a"));
    assertEquals ("c", aData.getAnchor ());

    aData = URLHelper.getAsURLData ("?x=y&=b#c");
    assertNotNull (aData);
    assertNull (aData.getProtocol ());
    assertEquals ("", aData.getPath ());
    assertEquals (1, aData.params ().size ());
    assertEquals ("y", aData.params ().getFirstParamValue ("x"));
    assertEquals ("c", aData.getAnchor ());
  }

  @Test
  @Ignore ("Works only when being online")
  public void testGetInputStream ()
  {
    try
    {
      final InputStream aIS = URLHelper.getInputStream (new URL ("http://www.orf.at"), 3000, -1, null, null);
      final byte [] aContent = StreamHelper.getAllBytes (aIS);
      s_aLogger.info ("Read " + aContent.length + " bytes");
    }
    catch (final Throwable t)
    {
      // ignore
      s_aLogger.info ("Failed to GET: " + t.getMessage ());
    }
  }

  @Test
  public void testGetApplicationFormEncoded ()
  {
    final URLParameterEncoder enc = new URLParameterEncoder (StandardCharsets.UTF_8);
    assertNull (URLHelper.getQueryParametersAsString ((URLParameterList) null, enc));
    assertNull (URLHelper.getQueryParametersAsString (new URLParameterList (), enc));
    assertEquals ("a=b", URLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b"), enc));
    assertEquals ("a=b&c=d",
                  URLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b").add ("c", "d"), enc));
    assertEquals ("a=b&c=d&e=f+g",
                  URLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b")
                                                                               .add ("c", "d")
                                                                               .add ("e", "f g"),
                                                        enc));
    assertEquals ("a=b&c=d%26e",
                  URLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b").add ("c", "d&e"), enc));

    // Using identity encoder
    assertEquals ("a=b&c=d&e",
                  URLHelper.getQueryParametersAsString (new URLParameterList ().add ("a", "b").add ("c", "d&e"), null));
  }

  @Test
  public void testIsClassPathURLExisting ()
  {
    assertTrue (URLHelper.isClassPathURLExisting ("/test1.txt"));
    assertFalse (URLHelper.isClassPathURLExisting ("/test1 not existing.txt"));
  }

  @Test
  public void testGetAsURL ()
  {
    assertNull (URLHelper.getAsURL ("../common/file.xsd"));
    assertNotNull (URLHelper.getAsURL ("http://www.helger.com"));
  }

  @Test
  public void testGetAsURI ()
  {
    assertNotNull (URLHelper.getAsURI ("../common/file.xsd"));
    assertNotNull (URLHelper.getAsURI ("http://www.helger.com"));
    assertNotNull (URLHelper.getAsURI ("iso6523-actorid-upis"));
    assertNull (URLHelper.getAsURI ("test/##"));
    assertNull (URLHelper.getAsURI ("test##"));
  }

  @Test
  public void testGetAsFile ()
  {
    final URL u = URLHelper.getAsURL ("file:/../dir/include.xml");
    final File f = URLHelper.getAsFile (u);
    assertNotNull (f);
    assertEquals (new File ("/../dir/include.xml").getAbsolutePath (), f.getAbsolutePath ());
    final FileSystemResource fs = new FileSystemResource (f);
    assertEquals (new File ("/dir/include.xml").getAbsolutePath (), fs.getPath ());
  }
}
