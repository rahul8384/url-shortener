package com.urishortner.service;

import com.urishortner.model.Url;
import com.urishortner.repository.UrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlService {


    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public UrlService(UrlRepository urlRepository, RedisTemplate<String, String> redisTemplate) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
    }


    public String shortenUrl(String longUrl){
        Url url = new Url();
        url.setLongUrl(longUrl);

        Url savedUrl = urlRepository.save(url);
        String shortCode = generateShortCode(savedUrl.getId());

        savedUrl.setShortCode(shortCode);
        urlRepository.save(savedUrl);

        redisTemplate.opsForValue().set(shortCode, longUrl);
        return "http://localhost:8080/" + shortCode;
    }

    public String getOriginalUrl(String shortCode){
        String longUrl = redisTemplate.opsForValue().get(shortCode);
        Url url = urlRepository.findByShortCode(shortCode);
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        if(longUrl != null){
            return longUrl;
        }else{

             longUrl = url.getLongUrl();
            redisTemplate.opsForValue().set(shortCode, longUrl);
            return longUrl;
        }
    }

    public int getStats(String shortCode){
        Url url = urlRepository.findByShortCode(shortCode);
       return url.getClickCount();
    }

    private String generateShortCode(Long id) {
        StringBuilder shortCode = new StringBuilder();
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        while (id > 0) {
            int remainder = (int)(id % 62);
            shortCode.append(characters.charAt(remainder));
            id = id / 62;
        }

        while (shortCode.length() < 6) {
            shortCode.append("0");
        }

        return shortCode.reverse().toString();
    }
}
