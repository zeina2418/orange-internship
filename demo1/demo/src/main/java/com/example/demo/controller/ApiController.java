package com.example.demo.controller;

import com.example.demo.cache.LinkCache;
import com.example.demo.entity.Historique;
import com.example.demo.entity.Offre;
import com.example.demo.entity.Utilisateur;
import com.example.demo.repository.HistoriqueRepo;
import com.example.demo.repository.LinkRepo;
import com.example.demo.repository.OffreRepo;
import com.example.demo.repository.UtilisateurRepo;
import com.example.demo.response.InquiryResponse;
import com.example.demo.response.OfferResponse;
import com.example.demo.response.RedeemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
public class ApiController {
    private final UtilisateurRepo utilisateurRepo;
    private final HistoriqueRepo historiqueRepo;
    private final OffreRepo offreRepo;
    private final LinkRepo linkRepo;
    final Duration minDurationBetweenUses = Duration.ofMinutes(1);

    private final LinkCache linkCache;


    @Autowired
    public ApiController(UtilisateurRepo utilisateurRepo, HistoriqueRepo historiqueRepo, OffreRepo offreRepo, LinkRepo linkRepo, LinkCache linkCache){
        this.utilisateurRepo = utilisateurRepo;
        this.historiqueRepo = historiqueRepo;
        this.offreRepo = offreRepo;
        this.linkRepo = linkRepo;
        this.linkCache = linkCache;
    }

    @GetMapping("/addUser")
    public String addUser(@RequestParam Long tel, @RequestParam Long segment) {
        if (utilisateurRepo.existsByTel(tel)){
            return "dial already exists";
        }
        if (!offreRepo.existsBySegment(segment)){
            return "invalid segment: " + segment;
        }
        utilisateurRepo.save(new Utilisateur(tel, segment));
        return "successfully added : " + tel;
    }

    @GetMapping("/offers")
    public InquiryResponse getOffer(@RequestParam Long tel) {
        if(notSegmented(tel)){
            return new InquiryResponse("1",
                    "dial not segmented",
                    null);
        }
        Long seg = utilisateurRepo.findByTel(tel).getSegment();
        List<Long> offersForThisClient = new ArrayList<>(linkCache.link.getOrDefault(seg, Collections.emptyList()));
        List<Offre> offers = offreRepo.findByIdIn(offersForThisClient);
        if(offers.isEmpty()){
            return new InquiryResponse("2",
                    "missing offer in offer table" + offersForThisClient,
                    null);
        }
        List<OfferResponse> offerResponses = new ArrayList<>();
        for(Offre offre : offers){
            OfferResponse offerResponse = new OfferResponse(offre.getId(),offre.getNom());
            offerResponses.add(offerResponse);
        }
        return new InquiryResponse("0",
                "Success",
                offerResponses);
    }



    public boolean notSegmented(Long tel) {
        return !utilisateurRepo.existsByTel(tel);
    }

    public boolean existsInCache(Long segmentId, Long offerId) {
        return linkCache.getOrDefault(segmentId, Collections.emptyList()).contains(offerId);
    }


    public int countOfferUses(Long tel, Long offerId) {    // désormé compte le nombre d'usage d'un certain offre specifier dans les parametres
        if (notSegmented(tel) || existsInCache(utilisateurRepo.findByTel(tel).getSegment() , offerId)) {
            return 0;
        }
        return historiqueRepo.countByTelAndOfferId(tel, offerId);
    }

    public int countOfferUsesWithinTimeRange(Long tel, Long offreId) { // de meme que la fonction avant mais dans un temps specifier
        if (notSegmented(tel) || existsInCache(utilisateurRepo.findByTel(tel).getSegment(), offreId)) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minus(minDurationBetweenUses);

        return historiqueRepo.countByTelAndOfferIdAndDateBetween(tel, offreId, since, now);
    }

    public LocalDateTime lastUsed(Long tel, Long offerId) {
        Historique historique = historiqueRepo.findTopByTelAndOfferIdOrderByDateDesc(tel, offerId); // on chercher avec le tel et l'id de l'offre
        return historique != null ? historique.getDate() : null;
    }



    public Duration timeLeftTillNextUsage(Long tel,Long offerId) {
        LocalDateTime now = LocalDateTime.now();
        Integer times = offreRepo.findById(offerId).get().getTimes();
        List<Historique> recentUses = historiqueRepo.findByTelAndOfferIdAndDateAfterOrderByDateAsc(tel, offerId, now.minus(minDurationBetweenUses));

        if (recentUses.size() < times) {
            return Duration.ZERO;
        }
        LocalDateTime oldestUse = recentUses.get(0).getDate();
        Duration timePassed = Duration.between(oldestUse, now);
        return minDurationBetweenUses.minus(timePassed);
    }


    public boolean canUseNow(Long tel, Long offerId) {
        if(historiqueRepo.existsByTel(tel)){

            Integer times =offreRepo.findById(offerId).get().getTimes();
            return countOfferUsesWithinTimeRange(tel,offerId) < times;
        }
        return true;
    }

    public boolean maxedOutUsageTimes(Long tel, Long offerId) {
        if(historiqueRepo.existsByTel(tel)){
            Integer times =offreRepo.findById(offerId).get().getTimes();
            return countOfferUses(tel,offerId) > times;
        }
        return false;
    }

    @GetMapping("/use")
    public RedeemResponse utiliserOffre(@RequestParam Long tel, @RequestParam Long idOffre){
        if(notSegmented(tel)){
            return new RedeemResponse("1",
                    "dial not segmented");
        }

        if (!offreRepo.existsById(idOffre)) {
            return new RedeemResponse("2",
                    "no for this segment");
        }

        Long seg = utilisateurRepo.findByTel(tel).getSegment();
        if(!existsInCache(seg, idOffre)){
            return new RedeemResponse("3",
                    "Unavailable offer for this segment");
        }

        List<Long> offersForThisClient = new ArrayList<>(linkCache.link.getOrDefault(seg, Collections.emptyList()));
        Offre offre = offreRepo.findById(idOffre).get();
        String nom = offre.getNom();
        Integer times = offre.getTimes();

        if ( countOfferUsesWithinTimeRange(tel,idOffre) >= times ) {
            return new RedeemResponse("4",
             "Offer " + nom + " has already been used " + times + " times within the last "+ minDurationBetweenUses.toSeconds() + " seconds.  Wait : " + timeLeftTillNextUsage(tel, idOffre).toSeconds() +" seconds till next use.");
        }else if(!canUseNow(tel, idOffre)){
            return new RedeemResponse("5",
                    "Can't use now.   Wait : " + timeLeftTillNextUsage(tel, idOffre).toSeconds() + " seconds");

        }else{
            historiqueRepo.save(new Historique(tel,offre.getId()));
            return new RedeemResponse("0",
             "Offer : " + nom +" used successfully.  Number of uses : " + countOfferUses(tel, idOffre) + "  At " + lastUsed(tel, idOffre) + "  Number of uses within the last " + minDurationBetweenUses.toSeconds() + " seconds: " + countOfferUsesWithinTimeRange(tel, idOffre)
);
        }
    }


}
