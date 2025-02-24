package com.dev.ai_speech_transcriber.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
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
@RequestMapping("/api/v1/transcribe")
public class TranscriberController {

    double FILE_SIZE_LIMIT = 0.14;

    Logger logger = LoggerFactory.getLogger(TranscriberController.class);

    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel;

    public TranscriberController(@Value("${spring.ai.openai.api-key}") String apiKey) {

        OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder()
                .apiKey(apiKey)
                .build();

        this.openAiAudioTranscriptionModel = new OpenAiAudioTranscriptionModel(openAiAudioApi);
    }

    @PostMapping
    public ResponseEntity<String> transcribeAudio(
            @RequestParam("file") MultipartFile file
    ) {
        double fileSize = file.getSize() / (1024.0 * 1024.0);
        double roundedFileSize = Math.round(fileSize * 1000.0) / 1000.0;

        if (roundedFileSize > FILE_SIZE_LIMIT) {
            logger.warn("Request rejected due to a file size limit issue: {}", roundedFileSize + " MB.");

            return new ResponseEntity<>(
                    "The uploaded file is too large. Provided file size: " + roundedFileSize + " MB." + " Please upload a file smaller than " + FILE_SIZE_LIMIT + " MB.",
                    HttpStatus.PAYLOAD_TOO_LARGE
            );
        }

        // TODO limit the request per hour to 10;

        File tempFile = null;
        try {
            tempFile = File.createTempFile("audio", ".wav");
            file.transferTo(tempFile);

            OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                    .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                    .language("en")
                    .temperature(0f)
                    .build();

            FileSystemResource audioFile = new FileSystemResource(tempFile);

            AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
            AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(transcriptionRequest);

            return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK);

            // Note: used for development;
//            return new ResponseEntity<>("This is not an real transcribe of the file, Work in progress. File size: " + roundedFileSize + " MB", HttpStatus.OK);

        } catch (IOException ex) {
            logger.error("Error during file processing", ex);

            return new ResponseEntity<>("File processing error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        } finally {
            if (tempFile != null && tempFile.exists()) {
                boolean isDeletedTempFile = tempFile.delete();
                if (isDeletedTempFile) {
                    logger.info("TempFile was deleted.");
                } else {
                    logger.warn("Failed to delete temp file.");
                }
            }
        }
    }
}
