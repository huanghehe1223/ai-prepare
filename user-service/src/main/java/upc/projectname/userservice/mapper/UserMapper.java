package upc.projectname.userservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import upc.projectname.upccommon.domain.po.User;


/**
 * <p>
 *  mapper 接口
 * </p>
 *
 * @author lww
 * @since 2024-07-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("update user set balance = balance - #{money} where userid = #{id}")
    int updateUserBalance(Double money,Integer id);
}
