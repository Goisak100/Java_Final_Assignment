package finaljava;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMFManager {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public static EntityManagerFactory getInstance() {
        return emf;
    }
}