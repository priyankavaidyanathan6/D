package com.insurance.demo.repository;

import com.insurance.demo.model.db.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
