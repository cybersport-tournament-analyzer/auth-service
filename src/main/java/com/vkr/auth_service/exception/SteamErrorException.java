package com.vkr.auth_service.exception;

public class SteamErrorException extends RuntimeException {

    public SteamErrorException(String message) {
        super(message);
    }
}
