package com.example.nonprofitmanagementsystem.user;

public class BeneficiaryCreator extends UserCreator {
        @Override
        protected User createUser(int userId, SignUpRequest r) {
            return new Beneficiary(userId, r.getFirstName(), r.getLastName(), r.getEmail(), r.getPhoneNumber(), r.getPassword(), r.getAddress(), r.getFamilySize());
        }

        @Override
        public UserRole getSupportedRole() {
            return UserRole.BENEFICIARY;
        }
    }
