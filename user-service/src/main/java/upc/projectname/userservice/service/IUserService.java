package upc.projectname.userservice.service;


import upc.projectname.upccommon.domain.po.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lww
 * @since 2024-07-20
 */
public interface IUserService {
    boolean register(User user);

    User login(User user);
    boolean deleteUserById(Integer userid);
    boolean updateUser(User user);
    User getUserById(Integer userid);
    List<User> getAllUsers(Integer pageNum, Integer pageSize);

    List<User> getAllDeliveryUsers(Integer pageNum, Integer pageSize);


    boolean updateUserBalance(Double money, Integer id);


    List<User> getUsersByIds(List<Integer> ids);
}
