package entities;

import dtos.CityInfoDTO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "city_info")
@NamedQuery(name = "CityInfo.deleteAllRows", query = "DELETE from CityInfo")
public class CityInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "city")
    private String city;

    @OneToMany(mappedBy = "cityInfo", cascade = CascadeType.PERSIST)
    private List<Address> addressList = new ArrayList<>();

    public CityInfo() {}

    public CityInfo(String zipCode, String city) {
        this.zipCode = zipCode;
        this.city = city;
    }

    public CityInfo(CityInfoDTO ciDTO) {
        if (ciDTO.getId() != null)
            this.id = ciDTO.getId();
        this.zipCode = ciDTO.getZipCode();
        this.city = ciDTO.getCity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public void addAddress(Address address) {
        this.addressList.add(address);
        address.setCityInfo(this);
    }
}
