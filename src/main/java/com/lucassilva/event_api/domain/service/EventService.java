package com.lucassilva.event_api.domain.service;

import com.amazonaws.services.s3.AmazonS3;
import com.lucassilva.event_api.domain.coupon.Coupon;
import com.lucassilva.event_api.domain.event.Event;
import com.lucassilva.event_api.domain.event.EventDetailsDTO;
import com.lucassilva.event_api.domain.event.EventRequestDTO;
import com.lucassilva.event_api.domain.event.EventResponseDTO;
import com.lucassilva.event_api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class EventService {
    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public Event createEvent(EventRequestDTO data) {
        String imgUrl = null;

        if (data.image() != null) {
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        eventRepository.save(newEvent);

        if (!data.remote()) {
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    private String uploadImg(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        try {
            File file = this.convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            System.out.println("Error uploading file, message: " + e.getMessage());
            return null;
        }
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }


    public List<EventResponseDTO> getUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = eventRepository.findUpcomingEvents(new Date(), pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()
                )
        ).stream().toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String title, String city, String uf, Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        title = title != null ? title : "";
        city = city != null ? city : "";
        uf = uf != null ? uf : "";
        startDate = startDate != null ? startDate : new Date();
        if (endDate == null) {
            calendar.add(Calendar.YEAR, 10);
            endDate = calendar.getTime();
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = eventRepository.findFilteredEvents(title, city, uf, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()
                )
        ).stream().toList();
    }

    public EventDetailsDTO getEventDetails(UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        List<EventDetailsDTO.CouponDTO> couponDTOS = coupons.stream()
                .map(coupon -> new EventDetailsDTO.CouponDTO(
                    coupon.getCode(),
                    coupon.getDiscount(),
                    coupon.getValid()))
                .toList();

        return new EventDetailsDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getEventUrl(),
                        event.getImgUrl(),
                        couponDTOS
                );

    }
}
