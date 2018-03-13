package com.example.persistence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.time.Month;

@SpringBootApplication
@ComponentScan
public class PersistenceApplication implements CommandLineRunner {

	private EmployeeRepository employeeRepository;
	private Tpa2MemberRepository tpa2MemberRepository;

	PersistenceApplication(EmployeeRepository employeeRepository, Tpa2MemberRepository tpa2MemberRepository) {

		this.employeeRepository = employeeRepository;
		this.tpa2MemberRepository = tpa2MemberRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(PersistenceApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		Employee employee = new Employee();
		employee.setName("Stephen");
		employee.setBirthDate(LocalDate.of(1980, Month.SEPTEMBER, 4));
		Address address = new Address();
		address.setLine("43 Sedgebrook Crescent");
		address.setProvince(Province.ON);
		employee.setAddress(address);

		EmailAddress emailAddress1 = new EmailAddress();
		emailAddress1.setEmailAddress("stephen@doxsee.org");
		EmailAddress emailAddress2 = new EmailAddress();
		emailAddress2.setEmailAddress("sdoxsee@gmail.com");
		employee.getEmailAddress().add(emailAddress1);
		employee.getEmailAddress().add(emailAddress2);
		Employee saved = employeeRepository.save(employee);
		System.out.println("We did it");
		Employee foundEmployee = employeeRepository.findOne(saved.getId());
		System.out.println(foundEmployee);

		Tpa2Member tpa2Member = new Tpa2Member();
		tpa2Member.setNameFirst("Stephen");
		Tpa2Member save = tpa2MemberRepository.saveAndFlush(tpa2Member);
		System.out.println(save);
	}
}
