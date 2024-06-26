package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.DisjointAppointmentList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSamePerson comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final DisjointAppointmentList appointments;
    private final UniquePersonList persons;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        appointments = new DisjointAppointmentList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     *
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
        this.appointments.setAppointments(persons
                .stream()
                .flatMap(person -> person.getAppointments()
                        .asUnmodifiableObservableList()
                        .stream())
                .collect(Collectors.toList()));
        this.appointments.sort();
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setPersons(newData.getPersonList());
        this.appointments.sort();
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Returns a list of persons with similar name as {@code person}.
     */
    public List<String> findNearDuplicates(Person person) {
        requireNonNull(person);
        return persons.findNearDuplicates(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
        for (Appointment appointment : p.getAppointments()) {
            addAppointment(appointment);
        }
        this.appointments.sort();
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);

        // remove target's appointments
        for (Appointment appointment : target.getAppointments()) {
            appointments.remove(appointment);
        }

        // add editedPerson's appointments
        for (Appointment appointment : editedPerson.getAppointments()) {
            addAppointment(appointment);
        }
        this.appointments.sort();
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);

        // remove key's appointments
        for (Appointment appointment : key.getAppointments()) {
            appointments.remove(appointment);
        }

        this.appointments.sort();
    }

    //// appointment-level operations
    /**
     * Adds an appointment to the address book.
     * The appointment must not overlap with existing appointments in the address book.
     */
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        this.appointments.sort();
    }

    /**
     * Replaces the given appointment {@code target} in the list with {@code editedAppointment}.
     * {@code target} must exist in the address book.
     * The appointment {@code editedAppointment} must not overlap with other existing appointments in the address book.
     */
    public void setAppointment(Appointment target, Appointment editedAppointment) {
        requireNonNull(editedAppointment);

        appointments.setAppointment(target, editedAppointment);
        this.appointments.sort();
    }

    /**
     * Returns true if an appointment {@code appointment} overlaps with existing appointments in the address book.
     */
    public boolean appointmentsOverlap(Appointment appointment) {
        requireNonNull(appointment);
        return appointments.overlaps(appointment);
    }

    /**
     * Returns true if an appointment in {@code appointments} overlaps with existing appointments in the address book.
     */
    public boolean appointmentsOverlap(Collection<Appointment> appointments) {
        requireNonNull(appointments);
        for (Appointment ap : appointments) {
            if (this.appointments.overlaps(ap)) {
                return true;
            }
        }
        return false;
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Appointment> getAppointmentList() {
        return appointments.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook)) {
            return false;
        }

        AddressBook otherAddressBook = (AddressBook) other;
        boolean isPersonsEqual = persons.equals(otherAddressBook.persons);
        boolean isAppointmentsEqual = appointments.equals(otherAddressBook.appointments);
        return isPersonsEqual && isAppointmentsEqual;
    }

    @Override
    public int hashCode() {
        return persons.hashCode();
    }
}
