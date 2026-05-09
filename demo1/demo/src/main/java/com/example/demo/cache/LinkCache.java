package com.example.demo.cache;

import com.example.demo.entity.Link;
import com.example.demo.repository.LinkRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LinkCache {

    private final LinkRepo linkRepo;
    public HashMap<Long, List<Long>> link = new HashMap<>();


    @Autowired
    public LinkCache(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

    public void loadCache() {
        List<Link> entries = linkRepo.findAll();

        for (Link entry : entries) {
            long segment = entry.getSegment();
            long offerId = entry.getOfferId();

            link.computeIfAbsent(segment, k -> new ArrayList<>()).add(offerId);
        }
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void refreshCache() {
        link.clear();
        loadCache();
    }

    public Collection<Long> getOrDefault(Long segmentId, List<Long> defaultSet) {
        return link.getOrDefault(segmentId, defaultSet);
    }

    @PostConstruct
    public void init() {
        loadCache(); // Load once at startup
    }


}