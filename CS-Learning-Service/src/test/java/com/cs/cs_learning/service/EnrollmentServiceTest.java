
package com.cs.cs_learning.service;

import com.cs.cs_learning.model.Course;
import com.cs.cs_learning.model.Enrollment;
import com.cs.cs_learning.model.User;
import com.cs.cs_learning.store.InMemoryStore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceTest {

    @Test
    void shouldEnrollAndPromoteWaitlistedLearner() {

        InMemoryStore store = new InMemoryStore();

        store.addUser(new User(
                1, "Martin", "ID1001",
                "martin@gmail.com", "9876543210",
                User.Qualification.BACHELORS
        ));

        store.addUser(new User(
                2, "Hurb Suttor", "ID1002",
                "herbsuttor@gmail.com", "9876543211",
                User.Qualification.BACHELORS
        ));

        store.addCourse(new Course(
                101, "LMAX Disruptor Patterns", 1, 5
        ));

        EnrollmentService service = new EnrollmentService(store);

        // Alice gets the seat.
        service.enroll(1, 101);

        // Bob enters the waitlist.
        service.enroll(2, 101);

        assertEquals(
                Enrollment.Status.ENROLLED,
                findEnrollment(store, 1).getStatus()
        );

        assertEquals(
                Enrollment.Status.WAITLISTED,
                findEnrollment(store, 2).getStatus()
        );

        // Alice withdraws. Bob should be promoted.
        service.withdraw(1, 101);

        assertEquals(
                Enrollment.Status.WITHDRAWN,
                findEnrollment(store, 1).getStatus()
        );

        assertEquals(
                Enrollment.Status.ENROLLED,
                findEnrollment(store, 2).getStatus()
        );

        assertTrue(store.getWaitlist(101).isEmpty());
    }

    @Test
    void shouldRejectDuplicateRegistration() {

        InMemoryStore store = new InMemoryStore();

        store.addUser(new User(
                1, "Martin", "ID1001",
                "maring@gmail.com", "9876543210",
                User.Qualification.BACHELORS
        ));

        store.addCourse(new Course(
                101, "LMAX Disruptor Patterns", 2, 5
        ));

        EnrollmentService service = new EnrollmentService(store);

        service.enroll(1, 101);

        String result = service.enroll(1, 101);

        assertTrue(result.contains("already registered"));

        assertEquals(1, store.getAllEnrollments().size());
    }

    private Enrollment findEnrollment(InMemoryStore store, long userId) {

        for (Enrollment enrollment : store.getAllEnrollments()) {
            if (enrollment.getUserId() == userId) {
                return enrollment;
            }
        }

        return null;
    }
}
