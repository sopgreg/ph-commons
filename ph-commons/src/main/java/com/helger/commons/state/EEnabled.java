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
package com.helger.commons.state;

import javax.annotation.Nonnull;

/**
 * Small enum for setter method to identify whether an object is enabled or not.
 *
 * @author Philip Helger
 */
public enum EEnabled implements IEnabledIndicator
{
  ENABLED,
  DISABLED;

  public boolean isEnabled ()
  {
    return this == ENABLED;
  }

  @Nonnull
  public static EEnabled valueOf (final boolean bEnabled)
  {
    return bEnabled ? ENABLED : DISABLED;
  }

  @Nonnull
  public static EEnabled valueOf (@Nonnull final IEnabledIndicator aEnabledIndicator)
  {
    return valueOf (aEnabledIndicator.isEnabled ());
  }
}
