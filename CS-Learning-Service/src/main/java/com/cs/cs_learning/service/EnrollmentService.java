
package com.cs.cs_learning.service;

import com.cs.cs_learning.model.User;
import com.cs.cs_learning.model.Course;
import com.cs.cs_learning.model.Enrollment;
import com.cs.cs_learning.store.InMemoryStore;

import java.util.Queue;
import java.util.regex.Pattern;

public class EnrollmentService {

    private final InMemoryStore store;


    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{10,13}$");

    public EnrollmentService(InMemoryStore store) {
        this.store = store;
    }

    //enroll
    public String enroll(long userId, long courseId) {

        // Retrieve user and course.
        User user = store.findUser(userId);

        if (user == null) {
            return "Registration failed: User not found.";
        }

        Course course = store.findCourse(courseId);

        if (course == null) {
            return "Registration failed: Course not found.";
        }

        // Now, Validate user information and qualification.
        String validationError = validateUser(user);

        if (validationError != null) {
            return "Registration rejected: " + validationError;
        }

        // Check duplicate registration.
        if (hasDuplicateEnrollment(user, courseId)) {
            return "Registration rejected: User or identity number "
                    + "is already registered for this course.";
        }

        // Calculate current course availability.
        int enrolledCount = countEnrolled(courseId);

        int availableSeats = course.getCapacity() - enrolledCount;

        Queue<Long> waitlist = store.getWaitlist(courseId);

        // Enroll directly if a seat is available
        //   and nobody is already waiting.
        if (availableSeats > 0 && waitlist.isEmpty()) {

            Enrollment enrollment = new Enrollment(
                    store.nextEnrollmentId(),
                    userId,
                    courseId,
                    Enrollment.Status.ENROLLED
            );

            store.saveEnrollment(enrollment);

            return "Enrollment successful!\n"
                    + "User: " + user.getUsername() + "\n"
                    + "Course: " + course.getCourseName() + "\n"
                    + "Status: ENROLLED\n"
                    + "Notification: Enrollment confirmed.";
        }

        // Course is full, or an existing FIFO queue
        // has priority over a new registration.

        if (waitlist.size() >= course.getWaitlistLimit()) {
            return "Registration rejected: Course is full "
                    + "and the waitlist limit has been reached.";
        }

        int waitlistPosition = waitlist.size() + 1;

        Enrollment enrollment = new Enrollment(
                store.nextEnrollmentId(),
                userId,
                courseId,
                Enrollment.Status.WAITLISTED
        );

        store.saveEnrollment(enrollment);

        // FIFO: new learner goes to the rear of the queue.
        waitlist.offer(enrollment.getEnrollmentId());

        return "Registration accepted! Course: " + course.getCourseName() +
                " Status: WAITLISTED and Waitlist position: " + waitlistPosition;
    }

    public String withdraw(long userId, long courseId) {

        // Validate user and course.
        User user = store.findUser(userId);

        if (user == null) {
            return "Withdrawal failed: User not found.";
        }

        Course course = store.findCourse(courseId);

        if (course == null) {
            return "Withdrawal failed: Course not found.";
        }

        // Find the learner's enrollment.
        Enrollment enrollment =
                findEnrollment(userId, courseId);

        if (enrollment == null) {
            return "Withdrawal failed: No registration found "
                    + "for this user and course.";
        }

        // Prevent duplicate withdrawal.
        if (enrollment.getStatus() == Enrollment.Status.WITHDRAWN) {
            return "Withdrawal rejected: Already withdrawn.";
        }

        // In this version, withdrawal is supported only
        // for learners who have actually been enrolled.
        if (enrollment.getStatus() != Enrollment.Status.ENROLLED) {
            return "Withdrawal rejected: Learner is currently "
                    + "waitlisted, not enrolled.";
        }

        // Update enrollment status.
        enrollment.setStatus(Enrollment.Status.WITHDRAWN);

        // Available seats are automatically increased
        // because countEnrolled() will now return one less.

        // Promote the earliest waitlisted learner.
        Enrollment promoted = promoteNextLearner(courseId);

        if (promoted != null) {

            User promotedUser = store.findUser(promoted.getUserId());

            return "Withdrawal successful!"
                    + "User " + userId + " has withdrawn from "
                    + course.getCourseName()
                    + "Promoted learner: "
                    + promotedUser.getUsername()
                    + " (User ID: " + promoted.getUserId() + ")"
                    + "Status: ENROLLED"
                    + "Notification: You have been promoted "
                    + "from the waitlist.";
        }

        return "Withdrawal successful!"
                + "User " + userId + " has withdrawn from "
                + course.getCourseName() + " "
                + "The seat is now available.";
    }

    // simple validation
    private String validateUser(User user) {

        if (user.getUsername() == null
                || user.getUsername().trim().isEmpty()) {
            return "Username is required.";
        }

        if (user.getEmail() == null
                || !EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            return "Invalid email address.";
        }

        if (user.getPhoneNumber() == null
                || !PHONE_PATTERN.matcher(user.getPhoneNumber().trim()).matches()) {
            return "Invalid phone number. Provide 10 to 13 digits.";
        }

        if (user.getIdentityNumber() == null
                || user.getIdentityNumber().trim().isEmpty()) {
            return "Identity number is required.";
        }

        if (user.getQualification() == null) {
            return "Educational qualification is required.";
        }

        if (user.getQualification() != User.Qualification.BACHELORS) {
            return "Not qualified. A Bachelor's degree is required. "
                    + "Provided qualification: " + user.getQualification();
        }

        return null;
    }

    //deduplication logic
    private boolean hasDuplicateEnrollment(User user, long courseId) {

        for (Enrollment existing : store.getAllEnrollments()) {

            if (existing.getCourseId() != courseId) {
                continue;
            }

            // Same userId already registered.
            if (existing.getUserId() == user.getUserId()) {
                return true;
            }

            // Check identity number against existing registrations.
            User existingUser = store.findUser(existing.getUserId());

            if (existingUser != null
                    && existingUser.getIdentityNumber() != null
                    && existingUser.getIdentityNumber().trim()
                    .equals(user.getIdentityNumber().trim())) {
                return true;
            }
        }

        return false;
    }

    private Enrollment findEnrollment(long userId, long courseId) {

        for (Enrollment enrollment : store.getAllEnrollments()) {

            if (enrollment.getUserId() == userId
                    && enrollment.getCourseId() == courseId) {
                return enrollment;
            }
        }

        return null;
    }

    private int countEnrolled(long courseId) {

        int count = 0;

        for (Enrollment enrollment : store.getAllEnrollments()) {

            if (enrollment.getCourseId() == courseId
                    && enrollment.getStatus() == Enrollment.Status.ENROLLED) {
                count++;
            }
        }

        return count;
    }

    private Enrollment promoteNextLearner(long courseId) {

        Queue<Long> waitlist = store.getWaitlist(courseId);

        while (!waitlist.isEmpty()) {

            // Remove the earliest learner.
            Long enrollmentId = waitlist.poll();

            Enrollment enrollment =
                    store.findEnrollment(enrollmentId);

            if (enrollment == null) {
                continue;
            }

            // Only promote an actively waitlisted learner.
            if (enrollment.getStatus() != Enrollment.Status.WAITLISTED) {
                continue;
            }

            // Promote learner.
            enrollment.setStatus(Enrollment.Status.ENROLLED);

            return enrollment;
        }

        return null;
    }
}
