package com.example.demo.response;

import java.util.List;

public record InquiryResponse(
        String code,
        String description,
        List<OfferResponse> offerResponseList
) {
}
