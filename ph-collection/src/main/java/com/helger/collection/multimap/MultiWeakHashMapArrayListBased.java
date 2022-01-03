/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Multi map based on {@link WeakHashMap} and {@link CommonsArrayList}
 * values.<br>
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        key type
 * @param <VALUETYPE>
 *        value type
 * @deprecated Deprecated in v10; will be removed for v11; use the ICommons
 *             container directly
 */
@NotThreadSafe
@Deprecated
public class MultiWeakHashMapArrayListBased <KEYTYPE, VALUETYPE> extends AbstractMultiWeakHashMapListBased <KEYTYPE, VALUETYPE>
{
  public MultiWeakHashMapArrayListBased ()
  {}

  public MultiWeakHashMapArrayListBased (@Nonnull final KEYTYPE aKey, @Nullable final VALUETYPE aValue)
  {
    super (aKey, aValue);
  }

  public MultiWeakHashMapArrayListBased (@Nonnull final KEYTYPE aKey, @Nullable final ICommonsList <VALUETYPE> aCollection)
  {
    super (aKey, aCollection);
  }

  public MultiWeakHashMapArrayListBased (@Nullable final Map <? extends KEYTYPE, ? extends ICommonsList <VALUETYPE>> aCont)
  {
    super (aCont);
  }

  @Override
  @Nonnull
  @ReturnsMutableCopy
  protected final CommonsArrayList <VALUETYPE> createNewCollection ()
  {
    return new CommonsArrayList <> ();
  }
}
