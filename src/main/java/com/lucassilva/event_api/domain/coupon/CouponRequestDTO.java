package com.lucassilva.event_api.domain.coupon;

public record CouponRequestDTO(String code, Integer discount, Long valid) {
}
