
package com.cs.cs_learning;

import com.cs.cs_learning.model.Course;
import com.cs.cs_learning.model.User;
import com.cs.cs_learning.store.InMemoryStore;
import com.cs.cs_learning.service.EnrollmentService;

public class Main {

    public static void main(String[] args) {

        InMemoryStore store = new InMemoryStore();

        // Demo users
        store.addUser(new User(
                1, "Martin", "ID1001",
                "martin@gmail.com", "9876543210",
                User.Qualification.BACHELORS
        ));

        store.addUser(new User(
                2, "Herb Suttor", "ID1002",
                "herbsuttor@gmail.com", "9876543211",
                User.Qualification.BACHELORS
        ));

        // Course with one available seat
        store.addCourse(new Course(
                101, "LMAX Disruptor Patterns", 1, 5
        ));

        EnrollmentService service = new EnrollmentService(store);

        // Alice enrolls
        System.out.println(service.enroll(1, 101));

        // Bob enters the waitlist
        System.out.println(service.enroll(2, 101));

        // Alice withdraws; Bob gets promoted
        System.out.println(service.withdraw(1, 101));
    }
}
