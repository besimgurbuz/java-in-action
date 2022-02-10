package com.besimgurbuz.refactoringTestingAndDebugging.mockData;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static class Customers {
        static List<Customer> data = new ArrayList<>(List.of(
                new Customer(1, "Ahmet"),
                new Customer(2, "Damla"),
                new Customer(3, "Petek"),
                new Customer(4, "Besim"),
                new Customer(5, "Semiha"),
                new Customer(6, "Ayse")
        ));

        public static Customer getCustomerById(int id) {
            return data.stream().filter(customer -> customer.getId() == id).findFirst().orElse(null);
        }
    }
}
