package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.types.EmployeeType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"assignedAnimals"})
@ToString(exclude = {"assignedAnimals"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    private String username;

    @OneToOne
    @PrimaryKeyJoinColumn
    private UserLogin userLogin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Date birthday;

    @Column(nullable = false)
    private EmployeeType type;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private LocalTime workTimeStart;

    @Column(nullable = false)
    private LocalTime workTimeEnd;

    @JoinTable(
        name = "ANIMALS_CARETAKERS",
        joinColumns = @JoinColumn(name = "EMPLOYEE_USERNAME"),
        inverseJoinColumns = @JoinColumn(name = "ANIMAL_ID")
    )
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Animal> assignedAnimals;

    @OneToMany(mappedBy = "assignedEmployee", fetch = FetchType.LAZY)
    private List<Task> tasks;

    public Employee(String username, Date birthday, String email, String name, EmployeeType type, LocalTime workTimeStart, LocalTime workTimeEnd ) {
        this.username = username;
        this.birthday = birthday;
        this.email = email;
        this.name = name;
        this.type = type;
        this.workTimeStart = workTimeStart;
        this.workTimeEnd = workTimeEnd;
    }

    //
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Employee employee = (Employee) o;
//        return Objects.equals(username, employee.username) &&
//            Objects.equals(name, employee.name) &&
//            Objects.equals(birthday, employee.birthday) &&
//            Objects.equals(type, employee.type) &&
//            Objects.equals(email, employee.email);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(username, name, birthday, type, email);
//    }
//
//
//
//    @Override
//    public String toString() {
//        return "Employee{" +
//            "username='" + username + '\'' +
//            ", name='" + name + '\'' +
//            ", birthday=" + birthday +
//            ", type='" + type + '\'' +
//            ", email='" + email + '\'' +
//            '}';
//    }
//
//    public static final class EmployeeBuilder{
//        private String username;
//        private UserLogin userLogin;
//        private String name;
//        private Date birthday;
//        private EmployeeType type;
//        private String email;
//        private List<Animal> assignedAnimals;
//
//        private EmployeeBuilder() {
//        }
//
//        public static EmployeeBuilder anEmployee() {
//            return new EmployeeBuilder();
//        }
//
//        public Employee build(){
//            Employee employee= new Employee();
//            employee.setUserLogin(userLogin);
//            employee.setUsername(username);
//            employee.setName(name);
//            employee.setBirthday(birthday);
//            employee.setType(type);
//            employee.setEmail(email);
//            employee.setAssignedAnimals(assignedAnimals);
//            return employee;
//        }
//
//        public EmployeeBuilder withEmail(String email){
//            this.email=email;
//            return this;
//        }
//
//        public EmployeeBuilder withType(EmployeeType type){
//            this.type=type;
//            return this;
//        }
//
//        public EmployeeBuilder withBirthday(Date birthday){
//            this.birthday=birthday;
//            return this;
//        }
//
//        public EmployeeBuilder withName(String name){
//            this.name=name;
//            return this;
//        }
//
//        public EmployeeBuilder withUsername(String username){
//            this.username=username;
//            return this;
//        }
//
//        public EmployeeBuilder withUserLogin(UserLogin userLogin){
//            this.userLogin=userLogin;
//            return this;
//        }
//        public EmployeeBuilder withAssignedAnimals(List<Animal> assignedAnimals){
//            this.assignedAnimals=assignedAnimals;
//            return this;
//        }
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public EmployeeType getType() {
//        return type;
//    }
//
//    public void setType(EmployeeType type) {
//        this.type = type;
//    }
//
//    public Date getBirthday() {
//        return birthday;
//    }
//
//    public void setBirthday(Date birthday) {
//        this.birthday = birthday;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public UserLogin getUserLogin() {
//        return userLogin;
//    }
//
//    public void setUserLogin(UserLogin userLogin) {
//        this.userLogin = userLogin;
//    }
//
//    public List<Animal> getAssignedAnimals() { return assignedAnimals; }
//
//    public void setAssignedAnimals(List<Animal> assignedAnimals) { this.assignedAnimals = assignedAnimals; }

}
