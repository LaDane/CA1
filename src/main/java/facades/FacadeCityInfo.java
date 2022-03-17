package facades;

import dtos.CityInfoDTO;
import entities.CityInfo;
import errorhandling.EntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FacadeCityInfo {
    private static FacadeCityInfo instance;
    private static EntityManagerFactory emf;

    public FacadeCityInfo() {}

    public static FacadeCityInfo getFacadeCityInfo(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadeCityInfo();
        }
        return instance;
    }

    public CityInfoDTO create(CityInfoDTO ciDTO) {
        CityInfo ci = new CityInfo(ciDTO);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(ci);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new CityInfoDTO(ci);
    }

//    public List<CityInfoDTO> create(List<CityInfoDTO> ciDTOs) {
//        List<CityInfoDTO> cityInfoDTOList = new ArrayList<>();
//        EntityManager em = emf.createEntityManager();
//        try {
//            em.getTransaction().begin();
//            for (CityInfoDTO ciDTO : ciDTOs) {
//                CityInfo cityInfo = new CityInfo(ciDTO);
//                em.persist(cityInfo);
//                cityInfoDTOList.add(new CityInfoDTO(cityInfo));
//            }
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//        return cityInfoDTOList;
//    }

    public List<CityInfoDTO> create(List<CityInfoDTO> ciDTOs) {
        List<CityInfoDTO> cityInfoDTOList = new ArrayList<>();

        for (CityInfoDTO ciDTO : ciDTOs) {
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                CityInfo cityInfo = new CityInfo(ciDTO);
                em.persist(cityInfo);
                em.getTransaction().commit();
                cityInfoDTOList.add(new CityInfoDTO(cityInfo));
            } finally {
                em.close();
            }
        }
        return cityInfoDTOList;
    }

    public long getCityInfoCount() {
        EntityManager em = emf.createEntityManager();
        try {
            return (long) em.createQuery("SELECT COUNT(ci) FROM CityInfo ci").getSingleResult();
        } finally {
            em.close();
        }
    }

//    public CityInfoDTO updateCityInfo(long personId, String zipCode, String city) {
//        EntityManager em = emf.createEntityManager();
//        Person person = em.find(Person.class, personId);
//        CityInfo cityInfo = person.getAddress().getCityInfo();
//        cityInfo.setZipCode(zipCode);
//        cityInfo.setCity(city);
//
//        try {
//            em.getTransaction().begin();
//            em.merge(cityInfo);
//            em.getTransaction().commit();
//        } finally {
//            em.close();
//        }
//        return new CityInfoDTO(cityInfo);
//    }

    public CityInfo getCityInfoById(long id) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        CityInfo cityInfo = em.find(CityInfo.class, id);
        if (cityInfo == null)
            throw new EntityNotFoundException("The CityInfo entity with ID: '"+id+"' was not found");
        return cityInfo;
    }

    public CityInfo getCityInfoByZip(String zipCode) throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CityInfo> tq = em.createQuery("SELECT c FROM CityInfo c  WHERE c.zipCode = " + zipCode, CityInfo.class);
        List<CityInfo> cityInfoList = tq.getResultList();

        if (cityInfoList.size() == 0) {
            throw new EntityNotFoundException("Zipcode: '"+ zipCode +"' was not found");
        }
        return cityInfoList.get(0);
    }

    public CityInfoDTO getCityInfoDTOByZip(String zipCode) throws EntityNotFoundException {
        return new CityInfoDTO(getCityInfoByZip(zipCode));
    }

    public List<CityInfoDTO> getAllCityInfo() throws EntityNotFoundException {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CityInfo> typedQueryCI
                = em.createQuery("SELECT ci FROM CityInfo ci", CityInfo.class);
        List<CityInfo> cityInfoList = typedQueryCI.getResultList();

        if (cityInfoList.size() == 0)
            throw new EntityNotFoundException("No CityInfo entities exist. Call '.../api/cityinfo/populate' to populate the database with valid CityInfo data");

        List<CityInfoDTO> cityInfoDTOList = new ArrayList<>();
        for (CityInfo ci : cityInfoList) {
            cityInfoDTOList.add(new CityInfoDTO(ci));
        }
        return cityInfoDTOList;
    }

    public List<CityInfoDTO> populateCityInfo() throws IOException, InterruptedException {
        URL url = new URL("https://api.dataforsyningen.dk/postnumre");
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("content-type", "application/json; charset=UTF-8");

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());

        String result = getResponse(http.getInputStream());
        http.disconnect();

        JSONArray jsonArray = new JSONArray(result);
        List<CityInfoDTO> ciDTOs = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject o = (JSONObject) jsonArray.get(i);
            String zipCode = (String) o.get("nr");
            String city = (String) o.get("navn");

            ciDTOs.add(new CityInfoDTO(zipCode, city));
        }
        return create(ciDTOs);
    }

    public String getResponse(InputStream i) throws IOException {
        String res = "";
        InputStreamReader in = new InputStreamReader(i, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(in);
        String output;
        while ((output = br.readLine()) != null) {
            res += (output);
        }

        return res;
    }
}
