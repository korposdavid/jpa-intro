package com.codecool.jpaintro;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import com.codecool.jpaintro.repository.AddressRepository;
import com.codecool.jpaintro.repository.SchoolRepository;
import com.codecool.jpaintro.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@SpringBootApplication
public class JpaIntroApplication {
/*
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    */

    @Autowired
    private SchoolRepository schoolRepository;

    public static void main(String[] args) {
        SpringApplication.run(JpaIntroApplication.class, args);
    }

    @Bean
    @Profile("production")
    public CommandLineRunner init() {
        return args -> {
           /* Student john = Student.builder()
                    .email("johnny@cc.com")
                    .name("John")
                    .phoneNumber("555-6666")
                    .phoneNumber("361-3466")
                    .phoneNumber("512-2366")
                    .birthDate(LocalDate.of(1995, 10, 10))
                    .address(Address.builder().city("Miskolc").country("Hungary").build())
                    .build();
            john.calculateAge();

            studentRepository.save(john);*/

            Address address1 = Address.builder().city("Budapest").country("Hungary").address("Nagymezo 44").build();
            Address address2 = Address.builder().city("Miskolc").country("Hungary").address("Alkotmany 20").build();

            Student john = Student.builder()
                    .email("johnny@cc.com")
                    .name("John")
                    .phoneNumber("555-6666")
                    .phoneNumber("361-3466")
                    .phoneNumber("512-2366")
                    .birthDate(LocalDate.of(1995, 10, 10))
                    .address(address1)
                    .build();

            Student barbara = Student.builder()
                    .email("brb@cc.com")
                    .name("Barbara")
                    .phoneNumber("111-6666")
                    .phoneNumber("111-3466")
                    .birthDate(LocalDate.of(1975, 12, 3))
                    .address(address2)
                    .build();

            School school = School.builder()
                    .location(Location.BUDAPEST)
                    .name("Codecool BP")
                    .student(john)
                    .student(barbara)
                    .build();

            barbara.setSchool(school);
            john.setSchool(school);

            schoolRepository.save(school);
        };
    }

}
