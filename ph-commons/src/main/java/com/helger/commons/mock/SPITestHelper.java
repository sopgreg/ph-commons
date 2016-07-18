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
package com.helger.commons.mock;

import java.io.File;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.annotation.IsSPIInterface;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.cache.AnnotationUsageCache;
import com.helger.commons.charset.CCharset;
import com.helger.commons.collection.multimap.MultiTreeMapTreeSetBased;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.iterate.FileSystemIterator;
import com.helger.commons.io.stream.NonBlockingBufferedReader;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.string.StringHelper;

public final class SPITestHelper
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SPITestHelper.class);
  private static final AnnotationUsageCache s_aCacheInterface = new AnnotationUsageCache (IsSPIInterface.class);
  private static final AnnotationUsageCache s_aCacheImplementation = new AnnotationUsageCache (IsSPIImplementation.class);

  private SPITestHelper ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static MultiTreeMapTreeSetBased <String, String> testIfAllSPIImplementationsAreValid (@Nonnull final String sBaseDir,
                                                                                               final boolean bContinueOnError) throws Exception
  {
    final MultiTreeMapTreeSetBased <String, String> aAllImplementations = new MultiTreeMapTreeSetBased<> ();
    final File aBaseDir = new File (sBaseDir);
    if (aBaseDir.exists () && aBaseDir.isDirectory ())
      for (final File aFile : new FileSystemIterator (sBaseDir))
        if (aFile.isFile ())
        {
          s_aLogger.info ("Checking SPI file " + aFile.getAbsolutePath ());
          final String sInterfaceClassName = aFile.getName ();

          // Check if interface exists
          try
          {
            final Class <?> aInterfaceClass = Class.forName (sInterfaceClassName);
            if (!s_aCacheInterface.hasAnnotation (aInterfaceClass))
              s_aLogger.warn (aInterfaceClass + " should have the @IsSPIInterface annotation");
          }
          catch (final Throwable t)
          {
            final String sMsg = "No interface representing " +
                                sInterfaceClassName +
                                " exists: " +
                                ClassHelper.getClassLocalName (t) +
                                " - " +
                                t.getMessage ();
            s_aLogger.warn (sMsg);
            if (!bContinueOnError)
              throw new Exception (sMsg);
          }

          // Check content
          try (
              final NonBlockingBufferedReader aReader = new NonBlockingBufferedReader (StreamHelper.createReader (FileHelper.getInputStream (aFile),
                                                                                                                  CCharset.CHARSET_SERVICE_LOADER_OBJ)))
          {
            int nCount = 0;
            String sLine;
            while ((sLine = aReader.readLine ()) != null)
            {
              final String sImplClassName = StringHelper.trim (sLine);
              if (StringHelper.hasText (sImplClassName))
              {
                // Resolve class
                final Class <?> aImplClass = Class.forName (sLine);
                if (!s_aCacheImplementation.hasAnnotation (aImplClass))
                  s_aLogger.warn (aImplClass + " should have the @IsSPIImplementation annotation");
                ++nCount;
                aAllImplementations.putSingle (sInterfaceClassName, sImplClassName);
              }
            }
            if (nCount == 0)
              s_aLogger.warn ("  Contains no single valid implementation!");
            else
              s_aLogger.info ("  All implementations (" + nCount + ") are valid!");
          }
          catch (final Throwable t)
          {
            // Ensure the path name of the currently checked file is contained
            // in the exception text!
            s_aLogger.warn ("  Error checking content: " + t.getMessage ());
            if (!bContinueOnError)
              throw new Exception ("Error checking SPI file " + aFile.getAbsolutePath (), t);
          }
        }
    return aAllImplementations;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultiTreeMapTreeSetBased <String, String> testIfAllMainSPIImplementationsAreValid (final boolean bContinueOnError) throws Exception
  {
    return testIfAllSPIImplementationsAreValid ("src/main/resources/META-INF/services", bContinueOnError);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultiTreeMapTreeSetBased <String, String> testIfAllTestSPIImplementationsAreValid (final boolean bContinueOnError) throws Exception
  {
    return testIfAllSPIImplementationsAreValid ("src/test/resources/META-INF/services", bContinueOnError);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultiTreeMapTreeSetBased <String, String> testIfAllSPIImplementationsAreValid (final boolean bContinueOnError) throws Exception
  {
    final MultiTreeMapTreeSetBased <String, String> aAllImplementations = testIfAllMainSPIImplementationsAreValid (bContinueOnError);
    aAllImplementations.putAll (testIfAllTestSPIImplementationsAreValid (bContinueOnError));
    return aAllImplementations;
  }

  @Nonnull
  @ReturnsMutableCopy
  public static MultiTreeMapTreeSetBased <String, String> testIfAllSPIImplementationsAreValid () throws Exception
  {
    return testIfAllSPIImplementationsAreValid (false);
  }
}
