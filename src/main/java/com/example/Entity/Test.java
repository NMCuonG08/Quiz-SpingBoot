package com.example.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name="test")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "a", nullable = false)
    private String a;


    @OneToOne( mappedBy = "test",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Employee employee;



}
