package seedu.address.model.appointment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a Person's appointment in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidAppointment(String)}
 */
public class Appointment implements Comparable<Appointment> {
    public static final String MESSAGE_CONSTRAINTS = "Appointment should be of the format 'HH:MM-HH:MM DAY' "
            + "and adhere to the following constraints:\n"
            + "1. HH:MM follows 24 hour time; "
            + "HH is from 00 to 23, "
            + "MM is from 00 to 59.\n"
            + "2. This is followed by a DAY. "
            + "DAY must be one of: 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT','SUN'\n";
    public static final HashMap<String, DayOfWeek> DAY_TO_DAY_OF_WEEK;
    public static final HashMap<DayOfWeek, Integer> DAY_OF_WEEK_TO_NUM;
    // alphanumeric and special characters
    private static final String HOUR = "[\\d]{2}";
    private static final String MINUTE = "[\\d]{2}";
    private static final String START_TIME = HOUR + ":" + MINUTE;
    private static final String END_TIME = HOUR + ":" + MINUTE;
    private static final String DAY = "[A-z]{3}";
    public static final String VALIDATION_REGEX = START_TIME + "-" + END_TIME + "[\\s]+" + DAY;

    // initialize map from String to DayOfWeek
    static {
        DAY_TO_DAY_OF_WEEK = new HashMap<>();
        DAY_TO_DAY_OF_WEEK.put("MON", DayOfWeek.MONDAY);
        DAY_TO_DAY_OF_WEEK.put("TUE", DayOfWeek.TUESDAY);
        DAY_TO_DAY_OF_WEEK.put("WED", DayOfWeek.WEDNESDAY);
        DAY_TO_DAY_OF_WEEK.put("THU", DayOfWeek.THURSDAY);
        DAY_TO_DAY_OF_WEEK.put("FRI", DayOfWeek.FRIDAY);
        DAY_TO_DAY_OF_WEEK.put("SAT", DayOfWeek.SATURDAY);
        DAY_TO_DAY_OF_WEEK.put("SUN", DayOfWeek.SUNDAY);

        DAY_OF_WEEK_TO_NUM = new HashMap<>();
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.MONDAY, 1);
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.TUESDAY, 2);
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.WEDNESDAY, 3);
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.THURSDAY, 4);
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.FRIDAY, 5);
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.SATURDAY, 6);
        DAY_OF_WEEK_TO_NUM.put(DayOfWeek.SUNDAY, 7);
    }

    public final String value;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final DayOfWeek day;

    /**
     * Constructs a {@code Appointment}.
     *
     * @param appointment A valid appointment.
     */
    public Appointment(String appointment) {
        requireNonNull(appointment);
        checkArgument(isValidAppointment(appointment), MESSAGE_CONSTRAINTS);

        value = appointment.toUpperCase();
        startTime = LocalTime.parse(extractStartTime(appointment));
        endTime = LocalTime.parse(extractEndTime(appointment));
        day = DAY_TO_DAY_OF_WEEK.get(extractDay(appointment));
    }

    /**
     * Returns true if a given collection of appointments overlap.
     */
    public static boolean hasOverlapping(Collection<Appointment> appointments) {
        List<Appointment> appointmentList = new ArrayList<>(appointments);
        int size = appointmentList.size();
        for (int i = 0; i < size - 1; i += 1) {
            for (int j = i + 1; j < size; j += 1) {
                Appointment appointment = appointmentList.get(i);
                Appointment other = appointmentList.get(j);
                if (appointment.overlapsWith(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if a given string is an appointment.
     */
    public static boolean isValidAppointment(String test) {
        if (!(test.matches(VALIDATION_REGEX))) {
            return false;
        }

        boolean isStartTimeValid = isValidTime(extractStartTime(test));
        boolean isEndTimeValid = isValidTime(extractEndTime(test));

        if (!isStartTimeValid || !isEndTimeValid) {
            return false;
        }

        LocalTime startTime = LocalTime.parse(extractStartTime(test));
        LocalTime endTime = LocalTime.parse(extractEndTime(test));
        boolean isStartTimeBeforeEndTime = startTime.isBefore(endTime);

        String day = extractDay(test);
        boolean isDayValid = DAY_TO_DAY_OF_WEEK.containsKey(day);

        return isStartTimeBeforeEndTime && isDayValid;
    }

    private static boolean isValidTime(String appointment) {
        int hour = Integer.parseInt(appointment.substring(0, 2));
        int minute = Integer.parseInt(appointment.substring(3, 5));

        assert(hour > -1);
        assert(minute > -1);
        boolean hourValid = hour < 24;
        boolean minuteValid = minute < 60;

        return hourValid && minuteValid;
    }

    private static String extractStartTime(String appointment) {
        return appointment.substring(0, 5);
    }

    private static String extractEndTime(String appointment) {
        return appointment.substring(6, 11);
    }

    private static String extractDay(String appointment) {
        return appointment.substring(12).trim().toUpperCase();
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public DayOfWeek getDay() {
        return day;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Appointment)) {
            return false;
        }

        Appointment otherAppointment = (Appointment) other;
        return value.equals(otherAppointment.value);
    }

    /**
     * Return true if appointment overlaps with other, otherwise False
     */
    public boolean overlapsWith(Appointment other) {
        // days are different
        if (this.day != other.day) {
            return false;
        }

        // intervals overlap
        if (this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime)) {
            return true;
        } else if (other.startTime.isBefore(this.endTime) && this.startTime.isBefore(other.endTime)) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(Appointment o) {
        if (DAY_OF_WEEK_TO_NUM.get(this.day) < DAY_OF_WEEK_TO_NUM.get(o.day)) {
            return -1;
        } else if (DAY_OF_WEEK_TO_NUM.get(this.day) > DAY_OF_WEEK_TO_NUM.get(o.day)) {
            return 1;
        } else {
            return this.startTime.compareTo(o.startTime);
        }
    }
}
