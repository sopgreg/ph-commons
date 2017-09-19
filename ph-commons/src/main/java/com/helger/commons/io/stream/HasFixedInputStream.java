/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.commons.io.stream;

import java.io.InputStream;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.io.IHasInputStream;
import com.helger.commons.string.ToStringGenerator;

/**
 * Special implementation of {@link IHasInputStream} with a single fixed
 * {@link InputStream} than can not be read more than once.
 *
 * @author Philip Helger
 */
public class HasFixedInputStream implements IHasInputStream
{
  private final InputStream m_aIS;

  public HasFixedInputStream (@Nonnull final InputStream aIS)
  {
    m_aIS = ValueEnforcer.notNull (aIS, "IS");
  }

  public boolean isReadMultiple ()
  {
    return false;
  }

  public InputStream getInputStream ()
  {
    return m_aIS;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("IS", m_aIS).getToString ();
  }
}
