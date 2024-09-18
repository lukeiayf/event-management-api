package com.lucassilva.event_api.domain.service;

import com.lucassilva.event_api.domain.coupon.Coupon;
import com.lucassilva.event_api.domain.coupon.CouponRequestDTO;
import com.lucassilva.event_api.domain.event.Event;
import com.lucassilva.event_api.repositories.CouponRepository;
import com.lucassilva.event_api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EventRepository eventRepository;

    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponData) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Coupon coupon = new Coupon();
        coupon.setCode(couponData.code());
        coupon.setDiscount(couponData.discount());
        coupon.setValid(new Date(couponData.valid()));
        coupon.setEvent(event);

        return couponRepository.save(coupon);
    }

    public List<Coupon> consultCoupons(UUID eventId, Date currentDate) {
        //Spring Data implements methods based on naming convention so this translates into a query. Crazy stuff.
        return couponRepository.findByEventIdAndValidAfter(eventId, currentDate);
    }
}
