package com.helger.commons.datetime;

import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;
import static java.time.temporal.ChronoUnit.NANOS;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneRules;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * A version of {@link OffsetTime} that has an optional {@link ZoneOffset}
 *
 * @since 10.1
 */
@Immutable
public class XMLOffsetTime implements Temporal, TemporalAdjuster, Comparable <XMLOffsetTime>
{
  /**
   * The minimum supported {@code XMLOffsetTime}, '00:00:00+18:00'. This is the
   * time of midnight at the start of the day in the maximum offset (larger
   * offsets are earlier on the time-line). This combines {@link LocalTime#MIN}
   * and {@link ZoneOffset#MAX}. This could be used by an application as a "far
   * past" date.
   */
  public static final XMLOffsetTime MIN = XMLOffsetTime.of (LocalTime.MIN, ZoneOffset.MAX);
  /**
   * The maximum supported {@code XMLOffsetTime}, '23:59:59.999999999-18:00'.
   * This is the time just before midnight at the end of the day in the minimum
   * offset (larger negative offsets are later on the time-line). This combines
   * {@link LocalTime#MAX} and {@link ZoneOffset#MIN}. This could be used by an
   * application as a "far future" date.
   */
  public static final XMLOffsetTime MAX = XMLOffsetTime.of (LocalTime.MAX, ZoneOffset.MIN);

  /**
   * The local date-time.
   */
  private final LocalTime m_aTime;
  /**
   * The offset from UTC/Greenwich.
   */
  private final ZoneOffset m_aOffset;

  /**
   * Obtains the current time from the system clock in the default time-zone.
   * <p>
   * This will query the {@link Clock#systemDefaultZone() system clock} in the
   * default time-zone to obtain the current time. The offset will be calculated
   * from the time-zone in the clock.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for
   * testing because the clock is hard-coded.
   *
   * @return the current time using the system clock and default time-zone, not
   *         null
   */
  @Nonnull
  public static XMLOffsetTime now ()
  {
    return now (Clock.systemDefaultZone ());
  }

  /**
   * Obtains the current time from the system clock in the specified time-zone.
   * <p>
   * This will query the {@link Clock#system(ZoneId) system clock} to obtain the
   * current time. Specifying the time-zone avoids dependence on the default
   * time-zone. The offset will be calculated from the specified time-zone.
   * <p>
   * Using this method will prevent the ability to use an alternate clock for
   * testing because the clock is hard-coded.
   *
   * @param zone
   *        the zone ID to use, not null
   * @return the current time using the system clock, not null
   */
  @Nonnull
  public static XMLOffsetTime now (final ZoneId zone)
  {
    return now (Clock.system (zone));
  }

  /**
   * Obtains the current time from the specified clock.
   * <p>
   * This will query the specified clock to obtain the current time. The offset
   * will be calculated from the time-zone in the clock.
   * <p>
   * Using this method allows the use of an alternate clock for testing. The
   * alternate clock may be introduced using {@link Clock dependency injection}.
   *
   * @param clock
   *        the clock to use, not null
   * @return the current time, not null
   */
  @Nonnull
  public static XMLOffsetTime now (@Nonnull final Clock clock)
  {
    Objects.requireNonNull (clock, "clock");
    final Instant now = clock.instant (); // called once
    return ofInstant (now, clock.getZone ().getRules ().getOffset (now));
  }

  /**
   * Obtains an instance of {@code XMLOffsetTime} from a local time and an
   * offset.
   *
   * @param time
   *        the local time, not null
   * @param offset
   *        the zone offset, not null
   * @return the offset time, not null
   */
  @Nonnull
  public static XMLOffsetTime of (@Nonnull final LocalTime time, @Nullable final ZoneOffset offset)
  {
    return new XMLOffsetTime (time, offset);
  }

  @Nonnull
  public static XMLOffsetTime of (@Nonnull final OffsetTime ofsTime)
  {
    return new XMLOffsetTime (ofsTime.toLocalTime (), ofsTime.getOffset ());
  }

  /**
   * Obtains an instance of {@code XMLOffsetTime} from an hour, minute, second
   * and nanosecond.
   * <p>
   * This creates an offset time with the four specified fields.
   * <p>
   * This method exists primarily for writing test cases. Non test-code will
   * typically use other methods to create an offset time. {@code LocalTime} has
   * two additional convenience variants of the equivalent factory method taking
   * fewer arguments. They are not provided here to reduce the footprint of the
   * API.
   *
   * @param hour
   *        the hour-of-day to represent, from 0 to 23
   * @param minute
   *        the minute-of-hour to represent, from 0 to 59
   * @param second
   *        the second-of-minute to represent, from 0 to 59
   * @param nanoOfSecond
   *        the nano-of-second to represent, from 0 to 999,999,999
   * @param offset
   *        the zone offset, not null
   * @return the offset time, not null
   * @throws DateTimeException
   *         if the value of any field is out of range
   */
  @Nonnull
  public static XMLOffsetTime of (final int hour,
                                  final int minute,
                                  final int second,
                                  final int nanoOfSecond,
                                  @Nullable final ZoneOffset offset)
  {
    return new XMLOffsetTime (LocalTime.of (hour, minute, second, nanoOfSecond), offset);
  }

  /**
   * Obtains an instance of {@code XMLOffsetTime} from an {@code Instant} and
   * zone ID.
   * <p>
   * This creates an offset time with the same instant as that specified.
   * Finding the offset from UTC/Greenwich is simple as there is only one valid
   * offset for each instant.
   * <p>
   * The date component of the instant is dropped during the conversion. This
   * means that the conversion can never fail due to the instant being out of
   * the valid range of dates.
   *
   * @param instant
   *        the instant to create the time from, not null
   * @param zone
   *        the time-zone, which may be an offset, not null
   * @return the offset time, not null
   */
  @Nonnull
  public static XMLOffsetTime ofInstant (@Nonnull final Instant instant, @Nonnull final ZoneId zone)
  {
    Objects.requireNonNull (instant, "instant");
    Objects.requireNonNull (zone, "zone");
    final ZoneRules rules = zone.getRules ();
    final ZoneOffset offset = rules.getOffset (instant);
    // overflow caught later
    final long localSecond = instant.getEpochSecond () + offset.getTotalSeconds ();
    final int secsOfDay = (int) Math.floorMod (localSecond, CGlobal.SECONDS_PER_DAY);
    final LocalTime time = LocalTime.ofNanoOfDay (secsOfDay * CGlobal.NANOSECONDS_PER_SECOND + instant.getNano ());
    return new XMLOffsetTime (time, offset);
  }

  /**
   * Obtains an instance of {@code XMLOffsetTime} from a temporal object.
   * <p>
   * This obtains an offset time based on the specified temporal. A
   * {@code TemporalAccessor} represents an arbitrary set of date and time
   * information, which this factory converts to an instance of
   * {@code XMLOffsetTime}.
   * <p>
   * The conversion extracts and combines the {@code ZoneOffset} and the
   * {@code LocalTime} from the temporal object. Implementations are permitted
   * to perform optimizations such as accessing those fields that are equivalent
   * to the relevant objects.
   * <p>
   * This method matches the signature of the functional interface
   * {@link TemporalQuery} allowing it to be used as a query via method
   * reference, {@code XMLOffsetTime::from}.
   *
   * @param temporal
   *        the temporal object to convert, not null
   * @return the offset time, not null
   * @throws DateTimeException
   *         if unable to convert to an {@code XMLOffsetTime}
   */
  @Nonnull
  public static XMLOffsetTime from (@Nonnull final TemporalAccessor temporal)
  {
    if (temporal instanceof XMLOffsetTime)
      return (XMLOffsetTime) temporal;

    try
    {
      final LocalTime time = LocalTime.from (temporal);
      final ZoneOffset offset = ZoneOffset.from (temporal);
      return new XMLOffsetTime (time, offset);
    }
    catch (final DateTimeException ex)
    {
      throw new DateTimeException ("Unable to obtain XMLOffsetTime from TemporalAccessor: " +
                                   temporal +
                                   " of type " +
                                   temporal.getClass ().getName (),
                                   ex);
    }
  }

  /**
   * Obtains an instance of {@code XMLOffsetTime} from a text string such as
   * {@code 10:15:30+01:00}.
   * <p>
   * The string must represent a valid time and is parsed using
   * {@link java.time.format.DateTimeFormatter#ISO_TIME}.
   *
   * @param text
   *        the text to parse such as "10:15:30+01:00", not null
   * @return the parsed local time, not null
   * @throws DateTimeParseException
   *         if the text cannot be parsed
   */
  @Nonnull
  public static XMLOffsetTime parse (@Nonnull final CharSequence text)
  {
    // Optional ZoneID
    return parse (text, DateTimeFormatter.ISO_TIME);
  }

  /**
   * Obtains an instance of {@code XMLOffsetTime} from a text string using a
   * specific formatter.
   * <p>
   * The text is parsed using the formatter, returning a time.
   *
   * @param text
   *        the text to parse, not null
   * @param formatter
   *        the formatter to use, not null
   * @return the parsed offset time, not null
   * @throws DateTimeParseException
   *         if the text cannot be parsed
   */
  @Nonnull
  public static XMLOffsetTime parse (@Nonnull final CharSequence text, @Nonnull final DateTimeFormatter formatter)
  {
    Objects.requireNonNull (formatter, "formatter");
    return formatter.parse (text, XMLOffsetTime::from);
  }

  /**
   * Constructor.
   *
   * @param time
   *        the local time, not null
   * @param offset
   *        the zone offset, maybe null
   */
  protected XMLOffsetTime (@Nonnull final LocalTime time, @Nullable final ZoneOffset offset)
  {
    m_aTime = Objects.requireNonNull (time, "time");
    m_aOffset = offset;
  }

  /**
   * Returns a new time based on this one, returning {@code this} where
   * possible.
   *
   * @param time
   *        the time to create with, not null
   * @param offset
   *        the zone offset to create with, maybe null
   */
  @Nonnull
  protected XMLOffsetTime with (@Nonnull final LocalTime time, @Nullable final ZoneOffset offset)
  {
    if (m_aTime == time && EqualsHelper.equals (m_aOffset, offset))
      return this;

    return new XMLOffsetTime (time, offset);
  }

  /**
   * Checks if the specified field is supported.
   * <p>
   * This checks if this time can be queried for the specified field. If false,
   * then calling the {@link #range(TemporalField) range},
   * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
   * methods will throw an exception.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The supported fields are:
   * <ul>
   * <li>{@code NANO_OF_SECOND}
   * <li>{@code NANO_OF_DAY}
   * <li>{@code MICRO_OF_SECOND}
   * <li>{@code MICRO_OF_DAY}
   * <li>{@code MILLI_OF_SECOND}
   * <li>{@code MILLI_OF_DAY}
   * <li>{@code SECOND_OF_MINUTE}
   * <li>{@code SECOND_OF_DAY}
   * <li>{@code MINUTE_OF_HOUR}
   * <li>{@code MINUTE_OF_DAY}
   * <li>{@code HOUR_OF_AMPM}
   * <li>{@code CLOCK_HOUR_OF_AMPM}
   * <li>{@code HOUR_OF_DAY}
   * <li>{@code CLOCK_HOUR_OF_DAY}
   * <li>{@code AMPM_OF_DAY}
   * <li>{@code OFFSET_SECONDS}
   * </ul>
   * All other {@code ChronoField} instances will return false.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking
   * {@code TemporalField.isSupportedBy(TemporalAccessor)} passing {@code this}
   * as the argument. Whether the field is supported is determined by the field.
   *
   * @param field
   *        the field to check, null returns false
   * @return true if the field is supported on this time, false if not
   */
  @Override
  public boolean isSupported (@Nullable final TemporalField field)
  {
    if (field instanceof ChronoField)
      return field.isTimeBased () || field == OFFSET_SECONDS;

    return field != null && field.isSupportedBy (this);
  }

  /**
   * Checks if the specified unit is supported.
   * <p>
   * This checks if the specified unit can be added to, or subtracted from, this
   * offset-time. If false, then calling the {@link #plus(long, TemporalUnit)}
   * and {@link #minus(long, TemporalUnit) minus} methods will throw an
   * exception.
   * <p>
   * If the unit is a {@link ChronoUnit} then the query is implemented here. The
   * supported units are:
   * <ul>
   * <li>{@code NANOS}
   * <li>{@code MICROS}
   * <li>{@code MILLIS}
   * <li>{@code SECONDS}
   * <li>{@code MINUTES}
   * <li>{@code HOURS}
   * <li>{@code HALF_DAYS}
   * </ul>
   * All other {@code ChronoUnit} instances will return false.
   * <p>
   * If the unit is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.isSupportedBy(Temporal)} passing
   * {@code this} as the argument. Whether the unit is supported is determined
   * by the unit.
   *
   * @param unit
   *        the unit to check, null returns false
   * @return true if the unit can be added/subtracted, false if not
   */
  @Override // override for Javadoc
  public boolean isSupported (@Nullable final TemporalUnit unit)
  {
    if (unit instanceof ChronoUnit)
      return unit.isTimeBased ();

    return unit != null && unit.isSupportedBy (this);
  }

  /**
   * Gets the range of valid values for the specified field.
   * <p>
   * The range object expresses the minimum and maximum valid values for a
   * field. This time is used to enhance the accuracy of the returned range. If
   * it is not possible to return the range, because the field is not supported
   * or for some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return
   * appropriate range instances. All other {@code ChronoField} instances will
   * throw an {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking
   * {@code TemporalField.rangeRefinedBy(TemporalAccessor)} passing {@code this}
   * as the argument. Whether the range can be obtained is determined by the
   * field.
   *
   * @param field
   *        the field to query the range for, not null
   * @return the range of valid values for the field, not null
   * @throws DateTimeException
   *         if the range for the field cannot be obtained
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   */
  @Override
  @Nonnull
  public ValueRange range (@Nonnull final TemporalField field)
  {
    if (field instanceof ChronoField)
    {
      if (field == OFFSET_SECONDS)
        return field.range ();
      return m_aTime.range (field);
    }
    return field.rangeRefinedBy (this);
  }

  /**
   * Gets the value of the specified field from this time as an {@code int}.
   * <p>
   * This queries this time for the value of the specified field. The returned
   * value will always be within the valid range of values for the field. If it
   * is not possible to return the value, because the field is not supported or
   * for some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return valid
   * values based on this time, except {@code NANO_OF_DAY} and
   * {@code MICRO_OF_DAY} which are too large to fit in an {@code int} and throw
   * a {@code DateTimeException}. All other {@code ChronoField} instances will
   * throw an {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
   * passing {@code this} as the argument. Whether the value can be obtained,
   * and what the value represents, is determined by the field.
   *
   * @param field
   *        the field to get, not null
   * @return the value for the field
   * @throws DateTimeException
   *         if a value for the field cannot be obtained or the value is outside
   *         the range of valid values for the field
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported or the range of values exceeds an
   *         {@code int}
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override // override for Javadoc
  public int get (@Nonnull final TemporalField field)
  {
    return Temporal.super.get (field);
  }

  /**
   * Gets the value of the specified field from this time as a {@code long}.
   * <p>
   * This queries this time for the value of the specified field. If it is not
   * possible to return the value, because the field is not supported or for
   * some other reason, an exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the query is implemented here.
   * The {@link #isSupported(TemporalField) supported fields} will return valid
   * values based on this time. All other {@code ChronoField} instances will
   * throw an {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking {@code TemporalField.getFrom(TemporalAccessor)}
   * passing {@code this} as the argument. Whether the value can be obtained,
   * and what the value represents, is determined by the field.
   *
   * @param field
   *        the field to get, not null
   * @return the value for the field
   * @throws DateTimeException
   *         if a value for the field cannot be obtained
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public long getLong (@Nonnull final TemporalField field)
  {
    if (field instanceof ChronoField)
    {
      if (field == OFFSET_SECONDS)
        return m_aOffset != null ? m_aOffset.getTotalSeconds () : 0;
      return m_aTime.getLong (field);
    }
    return field.getFrom (this);
  }

  /**
   * Gets the zone offset, such as '+01:00'.
   * <p>
   * This is the offset of the local time from UTC/Greenwich.
   *
   * @return the zone offset, maybe null
   */
  @Nullable
  public ZoneOffset getOffset ()
  {
    return m_aOffset;
  }

  /**
   * @return <code>true</code> if this date has a zone offset,
   *         <code>false</code> if not.
   */
  public boolean hasOffset ()
  {
    return m_aOffset != null;
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified offset
   * ensuring that the result has the same local time.
   * <p>
   * This method returns an object with the same {@code LocalTime} and the
   * specified {@code ZoneOffset}. No calculation is needed or performed. For
   * example, if this time represents {@code 10:30+02:00} and the offset
   * specified is {@code +03:00}, then this method will return
   * {@code 10:30+03:00}.
   * <p>
   * To take into account the difference between the offsets, and adjust the
   * time fields, use {@link #withOffsetSameInstant}.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param offset
   *        the zone offset to change to, not null
   * @return an {@code XMLOffsetTime} based on this time with the requested
   *         offset, not null
   */
  @Nonnull
  public XMLOffsetTime withOffsetSameLocal (@Nullable final ZoneOffset offset)
  {
    return EqualsHelper.equals (offset, m_aOffset) ? this : new XMLOffsetTime (m_aTime, offset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified offset
   * ensuring that the result is at the same instant on an implied day.
   * <p>
   * This method returns an object with the specified {@code ZoneOffset} and a
   * {@code LocalTime} adjusted by the difference between the two offsets. This
   * will result in the old and new objects representing the same instant on an
   * implied day. This is useful for finding the local time in a different
   * offset. For example, if this time represents {@code 10:30+02:00} and the
   * offset specified is {@code +03:00}, then this method will return
   * {@code 11:30+03:00}.
   * <p>
   * To change the offset without adjusting the local time use
   * {@link #withOffsetSameLocal}.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param offset
   *        the zone offset to change to, not null
   * @return an {@code XMLOffsetTime} based on this time with the requested
   *         offset, not null
   */
  @Nonnull
  public XMLOffsetTime withOffsetSameInstant (@Nullable final ZoneOffset offset)
  {
    if (EqualsHelper.equals (offset, m_aOffset))
      return this;

    final int difference = (offset != null ? offset.getTotalSeconds () : 0) -
                           (m_aOffset != null ? m_aOffset.getTotalSeconds () : 0);
    final LocalTime adjusted = m_aTime.plusSeconds (difference);
    return new XMLOffsetTime (adjusted, offset);
  }

  /**
   * Gets the {@code LocalTime} part of this date-time.
   * <p>
   * This returns a {@code LocalTime} with the same hour, minute, second and
   * nanosecond as this date-time.
   *
   * @return the time part of this date-time, not null
   */
  @Nonnull
  public LocalTime toLocalTime ()
  {
    return m_aTime;
  }

  /**
   * Gets the hour-of-day field.
   *
   * @return the hour-of-day, from 0 to 23
   */
  public int getHour ()
  {
    return m_aTime.getHour ();
  }

  /**
   * Gets the minute-of-hour field.
   *
   * @return the minute-of-hour, from 0 to 59
   */
  public int getMinute ()
  {
    return m_aTime.getMinute ();
  }

  /**
   * Gets the second-of-minute field.
   *
   * @return the second-of-minute, from 0 to 59
   */
  public int getSecond ()
  {
    return m_aTime.getSecond ();
  }

  /**
   * Gets the nano-of-second field.
   *
   * @return the nano-of-second, from 0 to 999,999,999
   */
  public int getNano ()
  {
    return m_aTime.getNano ();
  }

  /**
   * Returns an adjusted copy of this time.
   * <p>
   * This returns an {@code XMLOffsetTime}, based on this one, with the time
   * adjusted. The adjustment takes place using the specified adjuster strategy
   * object. Read the documentation of the adjuster to understand what
   * adjustment will be made.
   * <p>
   * A simple adjuster might simply set the one of the fields, such as the hour
   * field. A more complex adjuster might set the time to the last hour of the
   * day.
   * <p>
   * The classes {@link LocalTime} and {@link ZoneOffset} implement
   * {@code TemporalAdjuster}, thus this method can be used to change the time
   * or offset:
   *
   * <pre>
   * result = offsetTime.with (time);
   * result = offsetTime.with (offset);
   * </pre>
   * <p>
   * The result of this method is obtained by invoking the
   * {@link TemporalAdjuster#adjustInto(Temporal)} method on the specified
   * adjuster passing {@code this} as the argument.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param adjuster
   *        the adjuster to use, not null
   * @return an {@code XMLOffsetTime} based on {@code this} with the adjustment
   *         made, not null
   * @throws DateTimeException
   *         if the adjustment cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetTime with (@Nonnull final TemporalAdjuster adjuster)
  {
    // optimizations
    if (adjuster instanceof LocalTime)
      return with ((LocalTime) adjuster, m_aOffset);
    if (adjuster instanceof ZoneOffset)
      return with (m_aTime, (ZoneOffset) adjuster);
    if (adjuster instanceof XMLOffsetTime)
      return (XMLOffsetTime) adjuster;
    return (XMLOffsetTime) adjuster.adjustInto (this);
  }

  /**
   * Returns a copy of this time with the specified field set to a new value.
   * <p>
   * This returns an {@code XMLOffsetTime}, based on this one, with the value
   * for the specified field changed. This can be used to change any supported
   * field, such as the hour, minute or second. If it is not possible to set the
   * value, because the field is not supported or for some other reason, an
   * exception is thrown.
   * <p>
   * If the field is a {@link ChronoField} then the adjustment is implemented
   * here.
   * <p>
   * The {@code OFFSET_SECONDS} field will return a time with the specified
   * offset. The local time is unaltered. If the new offset value is outside the
   * valid range then a {@code DateTimeException} will be thrown.
   * <p>
   * The other {@link #isSupported(TemporalField) supported fields} will behave
   * as per the matching method on {@link LocalTime#with(TemporalField, long)}
   * LocalTime}. In this case, the offset is not part of the calculation and
   * will be unchanged.
   * <p>
   * All other {@code ChronoField} instances will throw an
   * {@code UnsupportedTemporalTypeException}.
   * <p>
   * If the field is not a {@code ChronoField}, then the result of this method
   * is obtained by invoking {@code TemporalField.adjustInto(Temporal, long)}
   * passing {@code this} as the argument. In this case, the field determines
   * whether and how to adjust the instant.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param field
   *        the field to set in the result, not null
   * @param newValue
   *        the new value of the field in the result
   * @return an {@code XMLOffsetTime} based on {@code this} with the specified
   *         field set, not null
   * @throws DateTimeException
   *         if the field cannot be set
   * @throws UnsupportedTemporalTypeException
   *         if the field is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetTime with (@Nonnull final TemporalField field, final long newValue)
  {
    if (field instanceof ChronoField)
    {
      if (field == OFFSET_SECONDS)
      {
        final ChronoField f = (ChronoField) field;
        return with (m_aTime, ZoneOffset.ofTotalSeconds (f.checkValidIntValue (newValue)));
      }
      return with (m_aTime.with (field, newValue), m_aOffset);
    }
    return field.adjustInto (this, newValue);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the hour-of-day altered.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param hour
   *        the hour-of-day to set in the result, from 0 to 23
   * @return an {@code XMLOffsetTime} based on this time with the requested
   *         hour, not null
   * @throws DateTimeException
   *         if the hour value is invalid
   */
  @Nonnull
  public XMLOffsetTime withHour (final int hour)
  {
    return with (m_aTime.withHour (hour), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the minute-of-hour
   * altered.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param minute
   *        the minute-of-hour to set in the result, from 0 to 59
   * @return an {@code XMLOffsetTime} based on this time with the requested
   *         minute, not null
   * @throws DateTimeException
   *         if the minute value is invalid
   */
  @Nonnull
  public XMLOffsetTime withMinute (final int minute)
  {
    return with (m_aTime.withMinute (minute), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the second-of-minute
   * altered.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param second
   *        the second-of-minute to set in the result, from 0 to 59
   * @return an {@code XMLOffsetTime} based on this time with the requested
   *         second, not null
   * @throws DateTimeException
   *         if the second value is invalid
   */
  @Nonnull
  public XMLOffsetTime withSecond (final int second)
  {
    return with (m_aTime.withSecond (second), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the nano-of-second
   * altered.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanoOfSecond
   *        the nano-of-second to set in the result, from 0 to 999,999,999
   * @return an {@code XMLOffsetTime} based on this time with the requested
   *         nanosecond, not null
   * @throws DateTimeException
   *         if the nanos value is invalid
   */
  @Nonnull
  public XMLOffsetTime withNano (final int nanoOfSecond)
  {
    return with (m_aTime.withNano (nanoOfSecond), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the time truncated.
   * <p>
   * Truncation returns a copy of the original time with fields smaller than the
   * specified unit set to zero. For example, truncating with the
   * {@link ChronoUnit#MINUTES minutes} unit will set the second-of-minute and
   * nano-of-second field to zero.
   * <p>
   * The unit must have a {@linkplain TemporalUnit#getDuration() duration} that
   * divides into the length of a standard day without remainder. This includes
   * all supplied time units on {@link ChronoUnit} and {@link ChronoUnit#DAYS
   * DAYS}. Other units throw an exception.
   * <p>
   * The offset does not affect the calculation and will be the same in the
   * result.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param unit
   *        the unit to truncate to, not null
   * @return an {@code XMLOffsetTime} based on this time with the time
   *         truncated, not null
   * @throws DateTimeException
   *         if unable to truncate
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   */
  @Nonnull
  public XMLOffsetTime truncatedTo (@Nonnull final TemporalUnit unit)
  {
    return with (m_aTime.truncatedTo (unit), m_aOffset);
  }

  /**
   * Returns a copy of this time with the specified amount added.
   * <p>
   * This returns an {@code XMLOffsetTime}, based on this one, with the
   * specified amount added. The amount is typically {@link Duration} but may be
   * any other type implementing the {@link TemporalAmount} interface.
   * <p>
   * The calculation is delegated to the amount object by calling
   * {@link TemporalAmount#addTo(Temporal)}. The amount implementation is free
   * to implement the addition in any way it wishes, however it typically calls
   * back to {@link #plus(long, TemporalUnit)}. Consult the documentation of the
   * amount implementation to determine if it can be successfully added.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToAdd
   *        the amount to add, not null
   * @return an {@code XMLOffsetTime} based on this time with the addition made,
   *         not null
   * @throws DateTimeException
   *         if the addition cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetTime plus (@Nonnull final TemporalAmount amountToAdd)
  {
    return (XMLOffsetTime) amountToAdd.addTo (this);
  }

  /**
   * Returns a copy of this time with the specified amount added.
   * <p>
   * This returns an {@code XMLOffsetTime}, based on this one, with the amount
   * in terms of the unit added. If it is not possible to add the amount,
   * because the unit is not supported or for some other reason, an exception is
   * thrown.
   * <p>
   * If the field is a {@link ChronoUnit} then the addition is implemented by
   * {@link LocalTime#plus(long, TemporalUnit)}. The offset is not part of the
   * calculation and will be unchanged in the result.
   * <p>
   * If the field is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.addTo(Temporal, long)} passing
   * {@code this} as the argument. In this case, the unit determines whether and
   * how to perform the addition.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToAdd
   *        the amount of the unit to add to the result, may be negative
   * @param unit
   *        the unit of the amount to add, not null
   * @return an {@code XMLOffsetTime} based on this time with the specified
   *         amount added, not null
   * @throws DateTimeException
   *         if the addition cannot be made
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetTime plus (final long amountToAdd, @Nonnull final TemporalUnit unit)
  {
    if (unit instanceof ChronoUnit)
      return with (m_aTime.plus (amountToAdd, unit), m_aOffset);

    return unit.addTo (this, amountToAdd);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * hours added.
   * <p>
   * This adds the specified number of hours to this time, returning a new time.
   * The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param hours
   *        the hours to add, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the hours added,
   *         not null
   */
  @Nonnull
  public XMLOffsetTime plusHours (final long hours)
  {
    return with (m_aTime.plusHours (hours), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * minutes added.
   * <p>
   * This adds the specified number of minutes to this time, returning a new
   * time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param minutes
   *        the minutes to add, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the minutes added,
   *         not null
   */
  @Nonnull
  public XMLOffsetTime plusMinutes (final long minutes)
  {
    return with (m_aTime.plusMinutes (minutes), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * seconds added.
   * <p>
   * This adds the specified number of seconds to this time, returning a new
   * time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param seconds
   *        the seconds to add, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the seconds added,
   *         not null
   */
  @Nonnull
  public XMLOffsetTime plusSeconds (final long seconds)
  {
    return with (m_aTime.plusSeconds (seconds), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * nanoseconds added.
   * <p>
   * This adds the specified number of nanoseconds to this time, returning a new
   * time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanos
   *        the nanos to add, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the nanoseconds
   *         added, not null
   */
  @Nonnull
  public XMLOffsetTime plusNanos (final long nanos)
  {
    return with (m_aTime.plusNanos (nanos), m_aOffset);
  }

  /**
   * Returns a copy of this time with the specified amount subtracted.
   * <p>
   * This returns an {@code XMLOffsetTime}, based on this one, with the
   * specified amount subtracted. The amount is typically {@link Duration} but
   * may be any other type implementing the {@link TemporalAmount} interface.
   * <p>
   * The calculation is delegated to the amount object by calling
   * {@link TemporalAmount#subtractFrom(Temporal)}. The amount implementation is
   * free to implement the subtraction in any way it wishes, however it
   * typically calls back to {@link #minus(long, TemporalUnit)}. Consult the
   * documentation of the amount implementation to determine if it can be
   * successfully subtracted.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToSubtract
   *        the amount to subtract, not null
   * @return an {@code XMLOffsetTime} based on this time with the subtraction
   *         made, not null
   * @throws DateTimeException
   *         if the subtraction cannot be made
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetTime minus (@Nonnull final TemporalAmount amountToSubtract)
  {
    return (XMLOffsetTime) amountToSubtract.subtractFrom (this);
  }

  /**
   * Returns a copy of this time with the specified amount subtracted.
   * <p>
   * This returns an {@code XMLOffsetTime}, based on this one, with the amount
   * in terms of the unit subtracted. If it is not possible to subtract the
   * amount, because the unit is not supported or for some other reason, an
   * exception is thrown.
   * <p>
   * This method is equivalent to {@link #plus(long, TemporalUnit)} with the
   * amount negated. See that method for a full description of how addition, and
   * thus subtraction, works.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param amountToSubtract
   *        the amount of the unit to subtract from the result, may be negative
   * @param unit
   *        the unit of the amount to subtract, not null
   * @return an {@code XMLOffsetTime} based on this time with the specified
   *         amount subtracted, not null
   * @throws DateTimeException
   *         if the subtraction cannot be made
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public XMLOffsetTime minus (final long amountToSubtract, @Nonnull final TemporalUnit unit)
  {
    return amountToSubtract == Long.MIN_VALUE ? plus (Long.MAX_VALUE, unit).plus (1, unit)
                                              : plus (-amountToSubtract, unit);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * hours subtracted.
   * <p>
   * This subtracts the specified number of hours from this time, returning a
   * new time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param hours
   *        the hours to subtract, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the hours
   *         subtracted, not null
   */
  @Nonnull
  public XMLOffsetTime minusHours (final long hours)
  {
    return with (m_aTime.minusHours (hours), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * minutes subtracted.
   * <p>
   * This subtracts the specified number of minutes from this time, returning a
   * new time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param minutes
   *        the minutes to subtract, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the minutes
   *         subtracted, not null
   */
  @Nonnull
  public XMLOffsetTime minusMinutes (final long minutes)
  {
    return with (m_aTime.minusMinutes (minutes), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * seconds subtracted.
   * <p>
   * This subtracts the specified number of seconds from this time, returning a
   * new time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param seconds
   *        the seconds to subtract, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the seconds
   *         subtracted, not null
   */
  @Nonnull
  public XMLOffsetTime minusSeconds (final long seconds)
  {
    return with (m_aTime.minusSeconds (seconds), m_aOffset);
  }

  /**
   * Returns a copy of this {@code XMLOffsetTime} with the specified number of
   * nanoseconds subtracted.
   * <p>
   * This subtracts the specified number of nanoseconds from this time,
   * returning a new time. The calculation wraps around midnight.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param nanos
   *        the nanos to subtract, may be negative
   * @return an {@code XMLOffsetTime} based on this time with the nanoseconds
   *         subtracted, not null
   */
  @Nonnull
  public XMLOffsetTime minusNanos (final long nanos)
  {
    return with (m_aTime.minusNanos (nanos), m_aOffset);
  }

  @Nonnull
  protected ZoneOffset getOffsetOrDefault ()
  {
    ZoneOffset ret = m_aOffset;
    if (ret == null)
    {
      // We don't have a date to reasonable determine a time zone
      ret = ZoneOffset.UTC;
    }
    return ret;
  }

  @Nonnull
  protected ZoneOffset getOffsetOrDefault (@Nonnull final LocalDateTime aAt)
  {
    ZoneOffset ret = m_aOffset;
    if (ret == null)
    {
      // Find the match
      ret = PDTConfig.getDefaultZoneId ().getRules ().getOffset (aAt);
    }
    return ret;
  }

  /**
   * Queries this time using the specified query.
   * <p>
   * This queries this time using the specified query strategy object. The
   * {@code TemporalQuery} object defines the logic to be used to obtain the
   * result. Read the documentation of the query to understand what the result
   * of this method will be.
   * <p>
   * The result of this method is obtained by invoking the
   * {@link TemporalQuery#queryFrom(TemporalAccessor)} method on the specified
   * query passing {@code this} as the argument.
   *
   * @param <R>
   *        the type of the result
   * @param query
   *        the query to invoke, not null
   * @return the query result, null may be returned (defined by the query)
   * @throws DateTimeException
   *         if unable to query (defined by the query)
   * @throws ArithmeticException
   *         if numeric overflow occurs (defined by the query)
   */
  @SuppressWarnings ("unchecked")
  @Override
  @Nonnull
  public <R> R query (@Nonnull final TemporalQuery <R> query)
  {
    if (query == TemporalQueries.offset () || query == TemporalQueries.zone ())
      return (R) getOffsetOrDefault ();
    if (query == TemporalQueries.zoneId () | query == TemporalQueries.chronology () ||
        query == TemporalQueries.localDate ())
      return null;
    if (query == TemporalQueries.localTime ())
      return (R) m_aTime;
    if (query == TemporalQueries.precision ())
      return (R) NANOS;
    // inline TemporalAccessor.super.query(query) as an optimization
    // non-JDK classes are not permitted to make this optimization
    return query.queryFrom (this);
  }

  /**
   * Adjusts the specified temporal object to have the same offset and time as
   * this object.
   * <p>
   * This returns a temporal object of the same observable type as the input
   * with the offset and time changed to be the same as this.
   * <p>
   * The adjustment is equivalent to using
   * {@link Temporal#with(TemporalField, long)} twice, passing
   * {@link ChronoField#NANO_OF_DAY} and {@link ChronoField#OFFSET_SECONDS} as
   * the fields.
   * <p>
   * In most cases, it is clearer to reverse the calling pattern by using
   * {@link Temporal#with(TemporalAdjuster)}:
   *
   * <pre>
   * // these two lines are equivalent, but the second approach is recommended
   * temporal = thisOffsetTime.adjustInto (temporal);
   * temporal = temporal.with (thisOffsetTime);
   * </pre>
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param temporal
   *        the target object to be adjusted, not null
   * @return the adjusted object, not null
   * @throws DateTimeException
   *         if unable to make the adjustment
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  @Nonnull
  public Temporal adjustInto (@Nonnull final Temporal temporal)
  {
    return temporal.with (NANO_OF_DAY, m_aTime.toNanoOfDay ())
                   .with (OFFSET_SECONDS, m_aOffset != null ? m_aOffset.getTotalSeconds () : 0);
  }

  /**
   * Calculates the amount of time until another time in terms of the specified
   * unit.
   * <p>
   * This calculates the amount of time between two {@code XMLOffsetTime}
   * objects in terms of a single {@code TemporalUnit}. The start and end points
   * are {@code this} and the specified time. The result will be negative if the
   * end is before the start. For example, the amount in hours between two times
   * can be calculated using {@code startTime.until(endTime, HOURS)}.
   * <p>
   * The {@code Temporal} passed to this method is converted to a
   * {@code XMLOffsetTime} using {@link #from(TemporalAccessor)}. If the offset
   * differs between the two times, then the specified end time is normalized to
   * have the same offset as this time.
   * <p>
   * The calculation returns a whole number, representing the number of complete
   * units between the two times. For example, the amount in hours between
   * 11:30Z and 13:29Z will only be one hour as it is one minute short of two
   * hours.
   * <p>
   * There are two equivalent ways of using this method. The first is to invoke
   * this method. The second is to use
   * {@link TemporalUnit#between(Temporal, Temporal)}:
   *
   * <pre>
   * // these two lines are equivalent
   * amount = start.until (end, MINUTES);
   * amount = MINUTES.between (start, end);
   * </pre>
   *
   * The choice should be made based on which makes the code more readable.
   * <p>
   * The calculation is implemented in this method for {@link ChronoUnit}. The
   * units {@code NANOS}, {@code MICROS}, {@code MILLIS}, {@code SECONDS},
   * {@code MINUTES}, {@code HOURS} and {@code HALF_DAYS} are supported. Other
   * {@code ChronoUnit} values will throw an exception.
   * <p>
   * If the unit is not a {@code ChronoUnit}, then the result of this method is
   * obtained by invoking {@code TemporalUnit.between(Temporal, Temporal)}
   * passing {@code this} as the first argument and the converted input temporal
   * as the second argument.
   * <p>
   * This instance is immutable and unaffected by this method call.
   *
   * @param endExclusive
   *        the end time, exclusive, which is converted to an
   *        {@code XMLOffsetTime}, not null
   * @param unit
   *        the unit to measure the amount in, not null
   * @return the amount of time between this time and the end time
   * @throws DateTimeException
   *         if the amount cannot be calculated, or the end temporal cannot be
   *         converted to an {@code XMLOffsetTime}
   * @throws UnsupportedTemporalTypeException
   *         if the unit is not supported
   * @throws ArithmeticException
   *         if numeric overflow occurs
   */
  @Override
  public long until (@Nonnull final Temporal endExclusive, @Nonnull final TemporalUnit unit)
  {
    final XMLOffsetTime end = XMLOffsetTime.from (endExclusive);
    if (unit instanceof ChronoUnit)
    {
      // no overflow
      final long nanosUntil = end.toEpochNano () - toEpochNano ();
      switch ((ChronoUnit) unit)
      {
        case NANOS:
          return nanosUntil;
        case MICROS:
          return nanosUntil / 1000;
        case MILLIS:
          return nanosUntil / 1000_000;
        case SECONDS:
          return nanosUntil / CGlobal.NANOSECONDS_PER_SECOND;
        case MINUTES:
          return nanosUntil / CGlobal.NANOSECONDS_PER_MINUTE;
        case HOURS:
          return nanosUntil / CGlobal.NANOSECONDS_PER_HOUR;
        case HALF_DAYS:
          return nanosUntil / (12 * CGlobal.NANOSECONDS_PER_HOUR);
      }
      throw new UnsupportedTemporalTypeException ("Unsupported unit: " + unit);
    }
    return unit.between (this, end);
  }

  /**
   * Formats this time using the specified formatter.
   * <p>
   * This time will be passed to the formatter to produce a string.
   *
   * @param formatter
   *        the formatter to use, not null
   * @return the formatted time string, not null
   * @throws DateTimeException
   *         if an error occurs during printing
   */
  @Nonnull
  public String format (@Nonnull final DateTimeFormatter formatter)
  {
    Objects.requireNonNull (formatter, "formatter");
    return formatter.format (this);
  }

  /**
   * Combines this time with a date to create an {@code OffsetDateTime}.
   * <p>
   * This returns an {@code OffsetDateTime} formed from this time and the
   * specified date. All possible combinations of date and time are valid.
   *
   * @param date
   *        the date to combine with, not null
   * @return the offset date-time formed from this time and the specified date,
   *         not null
   */
  @Nonnull
  public OffsetDateTime atDate (@Nonnull final LocalDate date)
  {
    final LocalDateTime aLDT = date.atTime (m_aTime);
    return OffsetDateTime.of (aLDT, getOffsetOrDefault (aLDT));
  }

  /**
   * Combines this time with a date to create an {@code XMLOffsetDateTime}.
   * <p>
   * This returns an {@code OffsetDateTime} formed from this time and the
   * specified date. All possible combinations of date and time are valid.
   *
   * @param date
   *        the date to combine with, not null
   * @return the offset date-time formed from this time and the specified date,
   *         not null
   */
  @Nonnull
  public XMLOffsetDateTime atXMLDate (@Nonnull final LocalDate date)
  {
    return XMLOffsetDateTime.of (date, m_aTime, m_aOffset);
  }

  /**
   * Converts this time to epoch nanos based on 1970-01-01Z.
   *
   * @return the epoch nanos value
   */
  protected long toEpochNano ()
  {
    final long nod = m_aTime.toNanoOfDay ();
    final long offsetNanos = m_aOffset != null ? m_aOffset.getTotalSeconds () * CGlobal.NANOSECONDS_PER_SECOND : 0;
    return nod - offsetNanos;
  }

  /**
   * Compares this {@code XMLOffsetTime} to another time.
   * <p>
   * The comparison is based first on the UTC equivalent instant, then on the
   * local time. It is "consistent with equals", as defined by
   * {@link Comparable}.
   * <p>
   * For example, the following is the comparator order:
   * <ol>
   * <li>{@code 10:30+01:00}</li>
   * <li>{@code 11:00+01:00}</li>
   * <li>{@code 12:00+02:00}</li>
   * <li>{@code 11:30+01:00}</li>
   * <li>{@code 12:00+01:00}</li>
   * <li>{@code 12:30+01:00}</li>
   * </ol>
   * Values #2 and #3 represent the same instant on the time-line. When two
   * values represent the same instant, the local time is compared to
   * distinguish them. This step is needed to make the ordering consistent with
   * {@code equals()}.
   * <p>
   * To compare the underlying local time of two {@code TemporalAccessor}
   * instances, use {@link ChronoField#NANO_OF_DAY} as a comparator.
   *
   * @param o
   *        the other time to compare to, not null
   * @return the comparator value, negative if less, positive if greater
   * @throws NullPointerException
   *         if {@code other} is null
   */
  @Override
  public int compareTo (@Nonnull final XMLOffsetTime o)
  {
    if (EqualsHelper.equals (m_aOffset, o.m_aOffset))
      return m_aTime.compareTo (o.m_aTime);

    int ret = Long.compare (toEpochNano (), o.toEpochNano ());
    if (ret == 0)
      ret = m_aTime.compareTo (o.m_aTime);
    return ret;
  }

  /**
   * Checks if the instant of this {@code XMLOffsetTime} is after that of the
   * specified time applying both times to a common date.
   * <p>
   * This method differs from the comparison in {@link #compareTo} in that it
   * only compares the instant of the time. This is equivalent to converting
   * both times to an instant using the same date and comparing the instants.
   *
   * @param other
   *        the other time to compare to, not null
   * @return true if this is after the instant of the specified time
   */
  public boolean isAfter (@Nonnull final XMLOffsetTime other)
  {
    return toEpochNano () > other.toEpochNano ();
  }

  /**
   * Checks if the instant of this {@code XMLOffsetTime} is before that of the
   * specified time applying both times to a common date.
   * <p>
   * This method differs from the comparison in {@link #compareTo} in that it
   * only compares the instant of the time. This is equivalent to converting
   * both times to an instant using the same date and comparing the instants.
   *
   * @param other
   *        the other time to compare to, not null
   * @return true if this is before the instant of the specified time
   */
  public boolean isBefore (@Nonnull final XMLOffsetTime other)
  {
    return toEpochNano () < other.toEpochNano ();
  }

  /**
   * Checks if the instant of this {@code XMLOffsetTime} is equal to that of the
   * specified time applying both times to a common date.
   * <p>
   * This method differs from the comparison in {@link #compareTo} and
   * {@link #equals} in that it only compares the instant of the time. This is
   * equivalent to converting both times to an instant using the same date and
   * comparing the instants.
   *
   * @param other
   *        the other time to compare to, not null
   * @return true if this is equal to the instant of the specified time
   */
  public boolean isEqual (@Nonnull final XMLOffsetTime other)
  {
    return toEpochNano () == other.toEpochNano ();
  }

  @Nullable
  public OffsetTime toOffsetTime ()
  {
    return OffsetTime.of (m_aTime, getOffsetOrDefault ());
  }

  /**
   * Checks if this time is equal to another time.
   * <p>
   * The comparison is based on the local-time and the offset. To compare for
   * the same instant on the time-line, use {@link #isEqual(XMLOffsetTime)}.
   * <p>
   * Only objects of type {@code XMLOffsetTime} are compared, other types return
   * false. To compare the underlying local time of two {@code TemporalAccessor}
   * instances, use {@link ChronoField#NANO_OF_DAY} as a comparator.
   *
   * @param o
   *        the object to check, null returns false
   * @return true if this is equal to the other time
   */
  @Override
  public boolean equals (final Object o)
  {
    if (this == o)
      return true;

    if (o == null || !getClass ().equals (o.getClass ()))
      return false;

    final XMLOffsetTime other = (XMLOffsetTime) o;
    return m_aTime.equals (other.m_aTime) && EqualsHelper.equals (m_aOffset, other.m_aOffset);
  }

  /**
   * A hash code for this time.
   *
   * @return a suitable hash code
   */
  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aTime).append (m_aOffset).getHashCode ();
  }

  /**
   * Outputs this time as a {@code String}, such as {@code 10:15:30+01:00}.
   * <p>
   * The output will be one of the following ISO-8601 formats:
   * <ul>
   * <li>{@code HH:mmXXXXX}</li>
   * <li>{@code HH:mm:ssXXXXX}</li>
   * <li>{@code HH:mm:ss.SSSXXXXX}</li>
   * <li>{@code HH:mm:ss.SSSSSSXXXXX}</li>
   * <li>{@code HH:mm:ss.SSSSSSSSSXXXXX}</li>
   * </ul>
   * The format used will be the shortest that outputs the full value of the
   * time where the omitted parts are implied to be zero.
   *
   * @return a string representation of this time, not null
   */
  @Nonnull
  @Nonempty
  public String getAsString ()
  {
    return m_aTime.toString () + (m_aOffset != null ? m_aOffset.toString () : "");
  }

  @Override
  @Deprecated
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Time", m_aTime).append ("Offset", m_aOffset).getToString ();
  }
}
