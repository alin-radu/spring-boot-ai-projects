package com.dev.ai_speech_transcriber.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestController
@ControllerAdvice
@Slf4j
public class ExceptionHandlerCustomized{

    // MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceededException (
            MaxUploadSizeExceededException ex){
        log.warn("Request rejected due to a file size limit issue.");

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message("The uploaded file exceeded the maximum file size allowed, of 1MB.")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}


