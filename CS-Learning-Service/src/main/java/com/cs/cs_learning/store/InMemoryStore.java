
package com.cs.cs_learning.store;

import com.cs.cs_learning.model.User;
import com.cs.cs_learning.model.Course;
import com.cs.cs_learning.model.Enrollment;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class InMemoryStore {

    private final Map<Long, User> users = new HashMap<>();

    private final Map<Long, Course> courses = new HashMap<>();

    private final Map<Long, Enrollment> enrollments = new HashMap<>();

    // Each course maintains its own FIFO waitlist.
    // Key   = courseId
    // Value = Queue of enrollmentIds
    private final Map<Long, Queue<Long>> waitlists = new HashMap<>();

    private long nextEnrollmentId = 1;

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public void addCourse(Course course) {
        courses.put(course.getCourseId(), course);
    }

    public User findUser(long userId) {
        return users.get(userId);
    }

    public Course findCourse(long courseId) {
        return courses.get(courseId);
    }

    public void saveEnrollment(Enrollment enrollment) {
        enrollments.put(enrollment.getEnrollmentId(), enrollment);
    }

    public Enrollment findEnrollment(long enrollmentId) {
        return enrollments.get(enrollmentId);
    }

    public Collection<Enrollment> getAllEnrollments() {
        return enrollments.values();
    }

    public Queue<Long> getWaitlist(long courseId) {
        Queue<Long> queue = waitlists.get(courseId);
        if (queue == null) {
            queue = new ArrayDeque<>();
            waitlists.put(courseId, queue);
        }
        return queue;
    }

    public long nextEnrollmentId() {
        return nextEnrollmentId++;
    }
}
