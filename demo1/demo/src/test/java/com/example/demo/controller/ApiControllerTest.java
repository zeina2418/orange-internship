package com.example.demo.controller;


import com.example.demo.repository.HistoriqueRepo;
import com.example.demo.response.InquiryResponse;
import com.example.demo.response.OfferResponse;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import com.example.demo.repository.UtilisateurRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiControllerTest {

    @Autowired
    private ApiController controller;

    @Autowired
    private UtilisateurRepo utilisateurRepo;
    @Autowired
    private HistoriqueRepo historiqueRepo;

    @BeforeEach
    void setUp() {

    }

    @Test
    void addingUserTest() {
        controller.addUser(0L, 20L);
        assertTrue(utilisateurRepo.existsByTel(0L),"adding user Test FAILED ☻.");
        utilisateurRepo.removeUtilisateurByTel(0L);
    }

    @Test
    void addingExistantUserTest() {
        controller.addUser(0L, 20L);
        controller.addUser(0L, 20L);
        assertEquals(1, utilisateurRepo.countByTel(0L), "can not add user twice test FAILED ☻.");
        utilisateurRepo.removeUtilisateurByTel(0L);
    }

    @Test
    void getOfferForSegmentedUserTest() {
        InquiryResponse response = controller.getOffer(1212011L);

        List<Long> offerIds = response.offerResponseList()
                .stream()
                .map(OfferResponse::offerId)
                .collect(Collectors.toList());

        assertIterableEquals(List.of(2L, 1L, 3L), offerIds, "Get Offer For User FAILED ☻.");
    }


    @Test
    void getOfferForNonSegmentedUserTest() {
        InquiryResponse response = controller.getOffer(0L);
        assertEquals("1", response.code(), " get offer code for non segmented dial FAILED ☻.");
    }

    @Test
    void segmentedDialIsNotSegmentedTest() {
        assertFalse(controller.notSegmented(1212011L), "get offer code for segmented dial FAILED ☻.");

    }

    @Test
    void nonSegmentedDialIsNotSegmentedTest() {
        assertTrue(controller.notSegmented(0L), "get offer code for non segmented dial FAILED ☻.");
    }

    @Test
    void countOfferUsesForNonSegmentedUserTest() {
        assertEquals(0, controller.countOfferUses(0L, 3L), "count of offer uses for non segmented dial FAILED ☻.");
    }

    @Test
    void countOfferUsesForNonSegmentedOfferTest() {
        assertEquals(0, controller.countOfferUses(1212011L, 80L),"count of offer uses for non segmented offer FAILED ☻.");
    }

    @Test
    void countOfferUsesForWrongOfferTest() {
        assertEquals(0, controller.countOfferUses(1212011L, 80L),"count of offer uses for wrong offer FAILED ☻.");

    }

    @Test
    void countOfferUsesForCorrectOfferTest() {
        controller.addUser(0L, 10L);
        controller.utiliserOffre(0L, 1L);
        assertEquals(1, controller.countOfferUses(0L, 1L),"count of offer uses for non segmented offer FAILED ☻.");
        historiqueRepo.removeByTel(0L);
        utilisateurRepo.removeUtilisateurByTel(0L);
    }

    @Test
    void countOfferUsesWithinTimeRangeForNonSegmentedUserTest() {
    }

    @Test
    void countOfferUsesWithinTimeRangeForNonSegmentedOfferTest() {
    }

    @Test
    void countOfferUsesWithinTimeRangeForWrongOfferTest() {
    }

    @Test
    void countOfferUsesWithinTimeRangeForCorrectOfferTest() {
    }

    @Test
    void lastUsedNonSegmentedDialTest() {
    }

    @Test
    void lastUsedNonSegmentedOfferTest() {
    }

    @Test
    void lastUsedWrongOfferTest() {
    }

    @Test
    void lastUsedUnusedTest() {
    }

    @Test
    void lastUsedTest() {
    }

    @Test
    void timeLeftTillNextUsageTest() {
    }

    @Test
    void timeLeftTillNextUsageUnusedTest() {
    }

    @Test
    void canUseNowTest() {
    }

    @Test
    void canUseNowWrongOfferTest() {
    }

    @Test
    void canNotUseNowTest() {
    }

    @Test
    void maxedOutUsageTimes() {
    }

    @Test
    void utiliserOffreWithWrongOfferId() {
        assertEquals("2",controller.utiliserOffre(1212011L, 8L).code());
    }

    @Test
    void utiliserOffreWithWrongDial() {
        assertEquals("1",controller.utiliserOffre(2011L, 3L).code());

    }

    @Test
    void utiliserOffreWithOfferUsesMaxedOut() {
        controller.utiliserOffre(1212011L, 3L);
        controller.utiliserOffre(1212011L, 3L);
        controller.utiliserOffre(1212011L, 3L);
        controller.utiliserOffre(1212011L, 3L);
        assertEquals("4",controller.utiliserOffre(1212011L, 3L).code());

    }

    @Test
    void utiliserOffre() {
        assertEquals("0",controller.utiliserOffre(1212011L, 3L).code());
    }
}