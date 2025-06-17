package com.ecommerce.project.Controller;

import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO addressDTOs = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(addressDTOs, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressesByUser() {
        List<AddressDTO> addressDTOS = addressService.getAllAddressesByUser();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@Valid @PathVariable Long addressId,
                                                        @RequestBody AddressDTO addressDTO) {
        AddressDTO addressDTOs = addressService.updateAddressById(addressId, addressDTO);
        return new ResponseEntity<>(addressDTOs, HttpStatus.OK);
    }

    @DeleteMapping("addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long addressId) {
        Boolean isDeleted = addressService.deleteAddressById(addressId);
        if (isDeleted) {
            return new ResponseEntity<>("Address with id: " + addressId + " Deleted Successfully ", HttpStatus.OK);
        }
        return new ResponseEntity<>("Address deletion unsuccessful", HttpStatus.BAD_REQUEST);
    }
}
