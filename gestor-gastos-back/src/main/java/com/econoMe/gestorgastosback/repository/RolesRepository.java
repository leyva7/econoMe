package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RolesRepository extends JpaRepository<Roles, RolesId> {

    Optional<Roles> findByUserUsernameAndAccountingId(String username, Long accountingId);

    List<Roles> findAllByUser(User user);

    Boolean deleteByAccounting(Accounting accounting);

}
