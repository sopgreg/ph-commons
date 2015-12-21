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
package com.helger.jaxb.validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.ValidationEventHandler;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.error.IHasResourceErrorGroup;
import com.helger.commons.error.IResourceError;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.error.ResourceErrorGroup;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * An implementation of the JAXB {@link javax.xml.bind.ValidationEventHandler}
 * interface. It collects all events that occurred!
 *
 * @author Philip Helger
 */
@ThreadSafe
public class CollectingValidationEventHandler extends AbstractValidationEventHandler implements IHasResourceErrorGroup
{
  protected final SimpleReadWriteLock m_aRWLock = new SimpleReadWriteLock ();
  private final ResourceErrorGroup m_aErrors = new ResourceErrorGroup ();

  public CollectingValidationEventHandler ()
  {}

  public CollectingValidationEventHandler (@Nullable final ValidationEventHandler aOrigHandler)
  {
    super (aOrigHandler);
  }

  @Override
  protected void onEvent (@Nonnull final IResourceError aEvent)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      m_aErrors.addResourceError (aEvent);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public IResourceErrorGroup getResourceErrors ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aErrors.getClone ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  /**
   * Clear all currently stored errors.
   *
   * @return {@link EChange#CHANGED} if at least one item was cleared.
   */
  @Nonnull
  public EChange clearResourceErrors ()
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      return m_aErrors.clear ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Override
  public String toString ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ToStringGenerator.getDerived (super.toString ()).append ("errors", m_aErrors).toString ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }
}
