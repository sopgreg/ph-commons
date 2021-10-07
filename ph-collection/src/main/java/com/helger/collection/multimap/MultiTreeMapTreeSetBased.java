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

import java.util.Comparator;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsTreeSet;
import com.helger.commons.collection.impl.ICommonsNavigableSet;

/**
 * Multi map based on {@link com.helger.commons.collection.impl.CommonsTreeMap}
 * and {@link CommonsTreeSet} values. <br>
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        key type
 * @param <VALUETYPE>
 *        value type
 */
@NotThreadSafe
@Deprecated
public class MultiTreeMapTreeSetBased <KEYTYPE, VALUETYPE extends Comparable <? super VALUETYPE>> extends
                                      AbstractMultiTreeMapSetBased <KEYTYPE, VALUETYPE, ICommonsNavigableSet <VALUETYPE>>
{
  public MultiTreeMapTreeSetBased ()
  {}

  public MultiTreeMapTreeSetBased (@Nullable final Comparator <? super KEYTYPE> aComparator)
  {
    super (aComparator);
  }

  public MultiTreeMapTreeSetBased (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    super (aKey, aValue);
  }

  public MultiTreeMapTreeSetBased (@Nullable final KEYTYPE aKey, @Nullable final ICommonsNavigableSet <VALUETYPE> aCollection)
  {
    super (aKey, aCollection);
  }

  public MultiTreeMapTreeSetBased (@Nullable final Map <? extends KEYTYPE, ? extends ICommonsNavigableSet <VALUETYPE>> aCont)
  {
    super (aCont);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  protected final CommonsTreeSet <VALUETYPE> createNewCollection ()
  {
    return new CommonsTreeSet <> ();
  }
}
