package com.urishortner.controller;

import com.urishortner.dto.ShortenRequest;
import com.urishortner.model.Url;
import com.urishortner.repository.UrlRepository;
import com.urishortner.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")

public class UrlController{

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody ShortenRequest request){

       String shortUrl = urlService.shortenUrl(request.getLongUrl());
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode){
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<String> getStats(@PathVariable String shortCode){
        int clicks = urlService.getStats(shortCode);
        return ResponseEntity.ok("Total Clicks " + clicks);
    }





}
