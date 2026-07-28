package com.vimalkumar.careerportal.service;

import java.util.List;

/**
 * A known-skills reference list used by both the resume parser (to pull skills
 * out of free text) and the ATS matcher (to compare resume skills against a job's
 * tech stack). In a later phase this could move to a DB table so it's admin-editable
 * instead of hardcoded — noted here as a deliberate MVP shortcut, not an oversight.
 */
public class SkillDictionary {

    public static final List<String> KNOWN_SKILLS = List.of(
            "Java", "Spring Boot", "Spring MVC", "Spring Security", "Spring Data JPA",
            "Hibernate", "REST API", "Microservices", "MySQL", "PostgreSQL", "MongoDB",
            "JDBC", "JPQL", "React", "JavaScript", "TypeScript", "HTML", "CSS", "Angular",
            "Node.js", "Express", "Docker", "Kubernetes", "AWS", "Azure", "GCP",
            "Git", "Maven", "Gradle", "Jenkins", "CI/CD", "JUnit", "Mockito",
            "Kafka", "RabbitMQ", "Redis", "GraphQL", "Python", "Django", "Flask",
            "C++", "C#", ".NET", "Multithreading", "Design Patterns", "Agile", "Scrum",
            "JWT", "OAuth", "Postman", "Swagger", "Linux", "Bash"
    );

    private SkillDictionary() {
    }
}
