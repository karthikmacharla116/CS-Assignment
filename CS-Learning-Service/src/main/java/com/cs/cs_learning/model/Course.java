
package com.cs.cs_learning.model;

/**
 * Note, For availableSeats = capacity - enrolledCount
 */
public class Course {

    private final long courseId;
    private final String courseName;
    private final int capacity;
    private final int waitlistLimit;

    public Course(long courseId, String courseName,
                  int capacity, int waitlistLimit) {

        if (courseName == null || courseName.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name is required.");
        }

        if (capacity < 0 || waitlistLimit < 0) {
            throw new IllegalArgumentException(
                    "Capacity and waitlist limit cannot be negative.");
        }

        this.courseId = courseId;
        this.courseName = courseName;
        this.capacity = capacity;
        this.waitlistLimit = waitlistLimit;
    }

    public long getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getWaitlistLimit() {
        return waitlistLimit;
    }
}
