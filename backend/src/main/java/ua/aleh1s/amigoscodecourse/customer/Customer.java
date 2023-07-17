package ua.aleh1s.amigoscodecourse.customer;

import jakarta.persistence.*;
import lombok.*;

@Entity()
@Getter()
@Setter()
@Table(name = "customer")
@AllArgsConstructor()
@NoArgsConstructor()
@EqualsAndHashCode()
public class Customer {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "age")
    private int age;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public Customer(String name, String email, int age, Gender gender) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.gender = gender;
    }
}
