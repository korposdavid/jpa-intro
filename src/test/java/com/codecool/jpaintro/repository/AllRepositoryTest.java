package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Address;
import com.codecool.jpaintro.entity.Location;
import com.codecool.jpaintro.entity.School;
import com.codecool.jpaintro.entity.Student;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class AllRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void saveOneSimple() {
        Student john = Student.builder()
                .email("john@cc.com")
                .name("John")
                .build();

        studentRepository.save(john);
        List<Student> studentList = studentRepository.findAll();
        assertThat(studentList).hasSize(1);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void saveUniqueFieldTwice() {
        Student joe = Student.builder()
                .email("joe@cc.com")
                .name("Joe")
                .build();

        studentRepository.save(joe);

        Student joe2 = Student.builder()
                .email("joe@cc.com")
                .name("Joe2")
                .build();

        studentRepository.saveAndFlush(joe2);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void emailShouldNotBeNull() {
        Student joe = Student.builder()
                .name("Joe")
                .build();

        studentRepository.save(joe);
    }

    @Test
    public void transientIsNotSaved() {
        Student joe = Student.builder()
                .name("Joe")
                .email("joe@cc.com")
                .birthDate(LocalDate.of(1990, 10, 10))
                .build();
        joe.calculateAge();
        assertThat(joe.getAge()).isGreaterThanOrEqualTo(28);

        studentRepository.save(joe);
        entityManager.clear();

        List<Student> students = studentRepository.findAll();
        assertThat(students).allMatch(student -> student.getAge() == 0L);
    }

    @Test
    public void addressIsPersistedWithStudent() {
        Address address = Address.builder()
                .country("Hungary")
                .city("Budapest")
                .address("Nagymező street 44")
                .zipCode(1065)
                .build();

        Student student = Student.builder()
                .email("xy@cc.com")
                .address(address)
                .build();

        studentRepository.save(student);

        List<Address> addresses = addressRepository.findAll();
        assertThat(addresses)
                .hasSize(1)
                .allMatch(address1 -> address1.getId() > 0L);
    }

    @Test
    public void studentsArePersistedAndDeletedWithNewSchool() {
        Set<Student> students = IntStream.range(1, 10)
                .boxed()
                .map(integer -> Student.builder().email("student" + integer + "@cc.com").build())
                .collect(Collectors.toSet());

        School school = School.builder()
                .students(students)
                .location(Location.BUDAPEST)
                .build();

        schoolRepository.save(school);

        assertThat(studentRepository.findAll())
                .hasSize(9)
                .anyMatch(student -> student.getEmail().equals("student9@cc.com"));

        schoolRepository.deleteAll();

        assertThat(studentRepository.findAll())
                .hasSize(0);

    }

    @Test
    public void findByNameStartingWithOrBirthDateBetween() {
        Student john = Student.builder()
                .email("john@cc.com")
                .name("John")
                .build();
        Student jane = Student.builder()
                .email("jane@cc.com")
                .name("Jane")
                .build();
        Student martha = Student.builder()
                .email("martha@cc.com")
                .name("Martha")
                .build();
        Student peter = Student.builder()
                .email("jack@cc.com")
                .birthDate(LocalDate.of(2010, 10, 3))
                .build();
        Student steve = Student.builder()
                .email("steve@cc.com")
                .birthDate(LocalDate.of(2011, 12, 5))
                .build();

        studentRepository.saveAll(Lists.newArrayList(john, jane, martha, steve, peter));

        List<Student> filteredStudents = studentRepository.findByNameStartingWithOrBirthDateBetween("J",
                LocalDate.of(2009, 1, 1),
                LocalDate.of(2011, 1, 1));
        assertThat(filteredStudents).containsExactlyInAnyOrder(john, jane, peter);
    }

    @Test
    public void findAllCountry() {
        Student first = Student.builder()
                .email("first@cc.com")
                .address(Address.builder().country("Hungary").build())
                .build();
        Student second = Student.builder()
                .email("sec@cc.com")
                .address(Address.builder().country("Poland").build())
                .build();
         Student third = Student.builder()
                .email("trd@cc.com")
                .address(Address.builder().country("Germany").build())
                .build();
         Student fourth = Student.builder()
                .email("fourst@cc.com")
                .address(Address.builder().country("Hungary").build())
                .build();

         studentRepository.saveAll(Lists.newArrayList(first, second, third, fourth));

        List<String> allCountry = studentRepository.findAllCountry();
        assertThat(allCountry)
                .hasSize(3)
                .containsOnlyOnce("Poland", "Hungary", "Germany");
    }

    @Test
    public void updateAllToUSAByStudentName() {
        Address address1 = Address.builder().country("Poland").build();
        Address address2 = Address.builder().country("Hungary").build();
        Address address3 = Address.builder().country("Germany").build();

        Student student = Student.builder()
                .name("temp")
                .email("temp@cc.com")
                .address(address2)
                .build();

        addressRepository.save(address1);
        addressRepository.save(address3);
        studentRepository.save(student);

        assertThat(addressRepository.findAll())
                .hasSize(3)
                .noneMatch(address -> address.getCountry().equals("USA"));

        int updatedRows = addressRepository.updateAllToUSAByStudentName("temp");

        assertThat(updatedRows).isEqualTo(1);
        assertThat(addressRepository.findAll())
                .hasSize(3)
                .anyMatch(address -> address.getCountry().equals("USA"));
    }

}

