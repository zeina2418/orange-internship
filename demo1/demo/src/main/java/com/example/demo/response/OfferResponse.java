package com.example.demo.response;

public record OfferResponse (
        Long offerId,
        String nom
) {
    public Long getOfferId() {
        return offerId;
    }
}
