package com.dev.ai_speech_transcriber_2.controller;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.Transcript;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class TranscriberController {

    private final AssemblyAI assemblyAI;

    public TranscriberController(@Value("${spring.ai.assemblyai.api-key}") String apiKey) {
        this.assemblyAI = AssemblyAI.builder()
                .apiKey(apiKey)
                .build();
    }

    @PostMapping("/v1/transcribe")
    public ResponseEntity<String> transcribeAudioV1(@RequestParam("file") MultipartFile file) {

        File tempFile = null;
        try {
            tempFile = File.createTempFile("audio", ".wav");
            file.transferTo(tempFile);

            FileSystemResource audioFile = new FileSystemResource(tempFile);

            // Note: used for production;
            Transcript transcript = assemblyAI.transcripts().transcribe(audioFile.getFile());

            System.out.println(transcript);

//            return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK);

            // Note: used for development;
            return new ResponseEntity<>("Work in progress...", HttpStatus.OK);

        } catch (IOException ex) {
            return new ResponseEntity<>("File processing error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean isDeletedTempFile = tempFile.delete();
                if (isDeletedTempFile) {
                    System.out.println(("TempFile was deleted."));
                } else {
                    System.out.println(("Failed to delete temp file."));
                }
            }
        }
    }

}