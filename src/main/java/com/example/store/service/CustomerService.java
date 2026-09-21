package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public List<CustomerDTO> getCustomers(String query) {

        List<Customer> customers = (query == null || query.isBlank())
                ? customerRepository.findAll()
                : customerRepository.findByNameContainingIgnoreCase(query);

        return customerMapper.customersToCustomerDTOs(customers);
    }

    public CustomerDTO createCustomer(Customer customer) {
        return customerMapper.customerToCustomerDTO(
                customerRepository.save(customer)
        );
    }
}