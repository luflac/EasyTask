package com.kahlab.easytask.repository;

import com.kahlab.easytask.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByName (String name);

    // Contar total de clientes
    long count();
}
