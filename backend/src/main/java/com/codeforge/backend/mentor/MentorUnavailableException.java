package com.codeforge.backend.mentor;

public class MentorUnavailableException extends RuntimeException {

    public MentorUnavailableException(String message) {
        super(message);
    }
}