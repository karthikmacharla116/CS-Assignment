
package com.cs.cs_learning.model;

public class Enrollment {

    public enum Status {
        ENROLLED,
        WAITLISTED,
        WITHDRAWN
    }

    private final long enrollmentId;
    private final long userId;
    private final long courseId;

    private Status status;

    public Enrollment(long enrollmentId, long userId,
                      long courseId, Status status) {

        this.enrollmentId = enrollmentId;
        this.userId = userId;
        this.courseId = courseId;
        this.status = status;
    }

    public long getEnrollmentId() {
        return enrollmentId;
    }

    public long getUserId() {
        return userId;
    }

    public long getCourseId() {
        return courseId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
