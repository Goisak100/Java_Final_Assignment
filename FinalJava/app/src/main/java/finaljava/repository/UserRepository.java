package finaljava.repository;

import java.util.List;
import java.util.Optional;

import finaljava.PasswordEncoder;
import finaljava.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class UserRepository {
    private final EntityManager em;
    
    public UserRepository(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    // 회원 가입할 때 이메일 중복 확인
    public boolean isDuplicateEmail(String email) {
        Optional<User> findUser = findByEmail(email).stream().findFirst();
        if (findUser.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    // 회원 가입
    public void register(String email, String password, String name, String phoneNumber, String address) {
        try {
            em.getTransaction().begin();
            TypedQuery<User> query = em.createQuery("select u from User as u where email = :email", User.class);
            query.setParameter("email", email);
            Optional<User> existingUser = query.getResultList().stream().findFirst();
            if (existingUser.isPresent()) {
                throw new Exception();
            }
            User user = new User();
            user.setEmail(email);
            user.setPassword(PasswordEncoder.encoded(password));
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setAddress(address);
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }

    // 회원 조회 (100% 일치)
    // CRUD를 할 때 단 하나의 결과만 나와야 하는 경우
    // 에러가 발생하지 않게 하기 위해서 getResultList() 반환, 호출한 측에서 처리하기;
    public List<User> findByEmail(String email) {
        TypedQuery<User> query = em.createQuery("select u from User u where email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    // 회원 조회 Like 기반
    // # 해당 기능은 회원 관리에서 검색할 때 사용한다.
    public List<User> findByEmailLike(String email) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.email like :email", User.class);
        query.setParameter("email", "%" + email + "%");
        return query.getResultList();
    }

    public List<User> findByNameLike(String name) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.name like :name", User.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    public List<User> findByPhoneNumberLike(String phoneNumber) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.phoneNumber like :phoneNumber", User.class);
        query.setParameter("phoneNumber", "%" + phoneNumber + "%");
        return query.getResultList();
    }

    public List<User> findByAddressLike(String address) {
        TypedQuery<User> query = em.createQuery("select u from User u where u.address like :address", User.class);
        query.setParameter("address", "%" + address + "%");
        return query.getResultList();
    }
    
    // 회원 비밀번호 수정
    // 이름, phone_number, address 수정을 할 수 있어야 한다. // 해당 기능은 넣지 않음
    // public void updatePassword(String email, String newPassword) {
    //     try {
    //         em.getTransaction().begin();
    //         User user = findByEmail(email).get(0);
    //         user.setPassword(PasswordEncoder.encoded(newPassword));
    //         em.merge(user);
    //         em.getTransaction().commit();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //         em.getTransaction().rollback();
    //         return;
    //     }
    // }

    // 회원 수정
    public void updateUser(User user) {
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }

    // 회원 삭제
    public void deleteByEmail(String email) {
        try {
            em.getTransaction().begin();

            User user = findByEmail(email).get(0);
            em.remove(user);

            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }

    // 회원 로그인
    public boolean login(String email, String rawPassword) {
        Optional<User> findUser = findByEmail(email).stream().findFirst();

        if (findUser.isEmpty()) {
            System.out.println("아이디가 일치하지 않음");
            return false;
        }

        User user = findUser.get();
        String hasingPassword = PasswordEncoder.encoded(rawPassword);
        if (hasingPassword.equals(user.getPassword())) {
            System.out.println("로그인 성공");
            return true;
        } else {
            System.out.println("비밀번호 일치하지 않음");
            return false;
        }
    }
}