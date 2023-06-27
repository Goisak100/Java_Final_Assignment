package finaljava;

import finaljava.repository.UserRepository;
import finaljava.view.UsersTable;

public class UsersTableManager {
    private static final UsersTable instance = new UsersTable(
        new UserRepository(EMFManager.getInstance()).findByEmailLike("")
    );

    public static UsersTable getInstance() {
        return instance;
    }
}