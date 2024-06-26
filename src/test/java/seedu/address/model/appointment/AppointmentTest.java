package seedu.address.model.appointment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.jupiter.api.Test;

public class AppointmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Appointment(null));
    }

    @Test
    public void constructor_invalidAppointment_throwsIllegalArgumentException() {
        String invalidAppointment = "";
        assertThrows(IllegalArgumentException.class, () -> new Appointment(invalidAppointment));
    }

    @Test
    public void isValidAppointment() {
        // null Appointment number
        assertThrows(NullPointerException.class, () -> Appointment.isValidAppointment(null));

        // invalid Appointments
        assertFalse(Appointment.isValidAppointment("")); // empty string
        assertFalse(Appointment.isValidAppointment(" ")); // does not match HH:mm DAY
        assertFalse(Appointment.isValidAppointment("FRI")); // times missing
        assertFalse(Appointment.isValidAppointment("23:15-23:16")); // day missing
        assertFalse(Appointment.isValidAppointment("3:15-4:30 FRI")); // H digit missing
        assertFalse(Appointment.isValidAppointment("3:15-04:30 FRI")); // H digit missing
        assertFalse(Appointment.isValidAppointment("12:34-13:33 FOB")); // not a day of the week
        assertFalse(Appointment.isValidAppointment("00:00-00:12FRI")); // no space between times and day
        assertFalse(Appointment.isValidAppointment("0000-1234 FRI")); // no colon between hour and minute
        assertFalse(Appointment.isValidAppointment("23:59-24:59 MON")); // HH not in range 00-23
        assertFalse(Appointment.isValidAppointment("22:60-23:40 MON")); // mm not in range 00-59
        assertFalse(Appointment.isValidAppointment("25:61-26:20 MON")); // invalid range for HH and mm
        assertFalse(Appointment.isValidAppointment("22:30-26:70 MON")); // invalid range for HH and mm
        assertFalse(Appointment.isValidAppointment("23:00-22:00 MON")); // start time not before end time
        assertFalse(Appointment.isValidAppointment("23:59-23:59 MON")); // start time equals end time

        // valid Appointments
        assertTrue(Appointment.isValidAppointment("13:59-14:00 TUE")); // matches HH:mm DAY
        assertTrue(Appointment.isValidAppointment("03:59-04:59 WED")); // matches HH:mm DAY
        assertTrue(Appointment.isValidAppointment("02:00-03:00 THU")); // matches HH:mm DAY
        assertTrue(Appointment.isValidAppointment("12:00-13:00 FRI")); // matches HH:mm DAY
        assertTrue(Appointment.isValidAppointment("13:30-14:00 SUN")); // matches HH:mm DAY
        assertTrue(Appointment.isValidAppointment("03:15-04:30 fri")); // DAY can be in lower case
        assertTrue(Appointment.isValidAppointment("03:15-04:30 fRI")); // DAY can be in lower and upper case
        assertTrue(Appointment.isValidAppointment("03:15-04:30 Fri")); // DAY can be in lower and upper case
        assertTrue(Appointment.isValidAppointment("03:15-04:30 fRi")); // DAY can be in lower and upper case

    }

    @Test
    public void equals() {
        Appointment appointment = new Appointment("13:30-14:00 SUN");

        // same values -> returns true
        assertTrue(appointment.equals(new Appointment("13:30-14:00 SUN")));

        // same object -> returns true
        assertTrue(appointment.equals(appointment));

        // null -> returns false
        assertFalse(appointment.equals(null));

        // different types -> returns false
        assertFalse(appointment.equals(5.0f));

        // different values -> returns false
        assertFalse(appointment.equals(new Appointment("13:30-14:30 FRI")));

        // different values -> returns false
        assertFalse(appointment.equals(new Appointment("13:30-14:30 SAT")));
    }

    @Test
    public void overlapsWith_overlappingAppointment_returnsTrue() {
        Appointment appointment = new Appointment("10:00-12:00 FRI");

        Appointment overlappingAppointment = new Appointment("11:00-13:00 FRI");
        assertTrue(appointment.overlapsWith(overlappingAppointment));
        assertTrue(overlappingAppointment.overlapsWith(appointment));

        overlappingAppointment = new Appointment("10:00-11:00 FRI");
        assertTrue(appointment.overlapsWith(overlappingAppointment));
        assertTrue(overlappingAppointment.overlapsWith(appointment));

        overlappingAppointment = new Appointment("11:00-13:00 FRI");
        assertTrue(appointment.overlapsWith(overlappingAppointment));
        assertTrue(overlappingAppointment.overlapsWith(appointment));

        overlappingAppointment = new Appointment("11:00-12:00 FRI");
        assertTrue(appointment.overlapsWith(overlappingAppointment));
        assertTrue(overlappingAppointment.overlapsWith(appointment));

        overlappingAppointment = new Appointment("10:00-12:00 FRI");
        assertTrue(appointment.overlapsWith(overlappingAppointment));
        assertTrue(overlappingAppointment.overlapsWith(appointment));
    }

    @Test
    public void overlapsWith_nonOverlappingAppointment_returnsFalse() {
        Appointment appointment = new Appointment("10:00-12:00 FRI");
        Appointment notOverlappingAppointment = new Appointment("12:00-14:00 FRI");

        assertFalse(appointment.overlapsWith(notOverlappingAppointment));
        assertFalse(notOverlappingAppointment.overlapsWith(appointment));

        notOverlappingAppointment = new Appointment("10:00-12:00 SUN");

        assertFalse(appointment.overlapsWith(notOverlappingAppointment));
        assertFalse(notOverlappingAppointment.overlapsWith(appointment));
    }

    @Test
    public void hasOverlapping_noOverlappingAppointments_returnsFalse() {
        // Create a collection of non-overlapping appointments
        Collection<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment("10:00-11:00 MON"));
        appointments.add(new Appointment("12:00-13:00 MON"));
        appointments.add(new Appointment("14:00-15:00 MON"));

        // Check if there are any overlapping appointments
        assertFalse(Appointment.hasOverlapping(appointments));
    }

    @Test
    public void hasOverlapping_emptyCollection_returnsFalse() {
        // Create an empty collection of appointments
        Collection<Appointment> appointments = new ArrayList<>();

        // Check if there are any overlapping appointments
        assertFalse(Appointment.hasOverlapping(appointments));
    }

}
