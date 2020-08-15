package com.example.demo.Data;

import com.example.demo.Model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findAllByFecha(Date fecha);

    @Query("select evento from Evento evento where evento.fecha between ?1 and ?2")
    List<Evento> findByDatesBetween(Date fechaInicio, Date fechaFin);

}
