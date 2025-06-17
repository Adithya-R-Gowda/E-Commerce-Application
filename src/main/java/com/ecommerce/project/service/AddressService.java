package com.ecommerce.project.service;

import com.ecommerce.project.payload.AddressDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getAllAddressesByUser();

    AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO);

    Boolean deleteAddressById(Long id);
}
