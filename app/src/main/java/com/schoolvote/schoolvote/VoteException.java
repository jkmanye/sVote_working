package com.schoolvote.schoolvote;

public class VoteException extends Throwable {

    public VoteException(String message) throws Exception {
        throw new Exception(message);
    }
}