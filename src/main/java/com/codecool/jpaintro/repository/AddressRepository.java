package com.codecool.jpaintro.repository;

import com.codecool.jpaintro.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("update Address a set a.country = 'USA' where a.id in " +
            "(select s.address.id from Student s where s.name LIKE :name)")
    @Modifying(clearAutomatically = true)
    int updateAllToUSAByStudentName(@Param("name") String name);

}
