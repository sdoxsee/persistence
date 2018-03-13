package com.example.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=com.example.persistence.PersistenceApplication.class)
public class Tpa2MemberRepositoryTest {

    @Autowired
    private Tpa2MemberRepository tpa2MemberRepository;

    @Test
    public void name() {
        Tpa2Member tpa2Member = new Tpa2Member();
        tpa2Member.setNameFirst("Stephen");
        Tpa2Member save = tpa2MemberRepository.saveAndFlush(tpa2Member);
        System.out.println(save);
    }
}