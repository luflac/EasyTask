package com.kahlab.easytask.controller;

import com.kahlab.easytask.model.Client;
import com.kahlab.easytask.model.Task;
import com.kahlab.easytask.service.ClientService;
import com.kahlab.easytask.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client savedClient = clientService.saveOrUpdateClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @PutMapping("/{idClient}")
    public ResponseEntity<Client> updateClient(@PathVariable Long idClient, @RequestBody Client client) {
        try {
            Client updatedClient = clientService.updateClient(idClient, client);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{idClient}")
    public ResponseEntity<Client> getClientById(@PathVariable Long idClient) {
        Optional<Client> client = clientService.findClientById(idClient);
        return client.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Client>> findClientsByName(@RequestParam String name) {
        List<Client> clients = clientService.findClientsByName(name);
        return clients.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(clients);
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.findAllClients();
    }

    @DeleteMapping("/{idClient}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long idClient) {
        clientService.deleteClient(idClient);
        return ResponseEntity.noContent().build();
    }

    // Listar tarefas associadas a um cliente específico
    @GetMapping("/{idClient}/tasks")
    public ResponseEntity<List<Task>> getTasksByClient(@PathVariable Long idClient) {
        List<Task> tasks = taskService.findTasksByClientId(idClient);
        return ResponseEntity.ok(tasks);
    }

}
