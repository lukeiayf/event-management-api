package com.lucassilva.event_api.domain.service;

import com.lucassilva.event_api.domain.address.Address;
import com.lucassilva.event_api.domain.event.Event;
import com.lucassilva.event_api.domain.event.EventRequestDTO;
import com.lucassilva.event_api.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    public void createAddress(EventRequestDTO data, Event event) {
        Address address = new Address();
        address.setCity(data.city());
        address.setUf(data.state());
        address.setUf(data.state());
        address.setEvent(event);

        addressRepository.save(address);
    }

}
