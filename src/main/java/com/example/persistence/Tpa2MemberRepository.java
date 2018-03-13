package com.example.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tpa2MemberRepository extends JpaRepository<Tpa2Member, Integer> {

}
