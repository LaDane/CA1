package entities;

import dtos.AddressDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address")
@NamedQuery(name = "Address.deleteAllRows", query = "DELETE from Address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "street")
    private String street;

    @Column(name = "additional_info")
    private String additionalInfo;

    @OneToMany(mappedBy = "address", cascade = CascadeType.PERSIST)
    private List<Person> personList = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "city_info_id")
    private CityInfo cityInfo;


    public Address() {}

    public Address(String street, String additionalInfo) {
        this.street = street;
        this.additionalInfo = additionalInfo;
    }

    public Address(AddressDTO aDTO) {
        if (aDTO.getId() != null)
            this.id = aDTO.getId();
        this.street = aDTO.getStreet();
        this.additionalInfo = aDTO.getAdditionalInfo();
    }

//    public Address(String street, String additionalInfo, List<Person> personList, CityInfo cityInfo) {
//        this.street = street;
//        this.additionalInfo = additionalInfo;
//        this.personList
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public void addPerson(Person person) {
        this.personList.add(person);
        person.setAddress(this);
    }

    public CityInfo getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(CityInfo cityInfo) {
        this.cityInfo = cityInfo;
    }


}
