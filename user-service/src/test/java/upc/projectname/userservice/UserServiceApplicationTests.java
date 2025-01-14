package upc.projectname.userservice;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import upc.projectname.upccommon.api.client.TaskClient;
import upc.projectname.upccommon.domain.po.User;
import upc.projectname.upcredisstarter.redisutils.RedisUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceApplicationTests {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TaskClient taskClient;

    @Test
    @Order(1)
    void setObject_shouldReturnTrue_whenObjectIsNotNull() {
        User user = new User();
        user.setUserid(999L);
        user.setUsername("huanghe");
        user.setCreateTime(LocalDateTime.now());
        assertTrue(redisUtils.setObject("upc:user:999", user));
    }

    @Test
    @Order(2)
    void setObject_shouldReturnFalse_whenObjectIsNull() {
        assertFalse(redisUtils.setObject("upc:user:999", null));
    }

    @Test
    @Order(3)
    void setObjectWithSecond_shouldReturnTrue_whenObjectIsNotNull() {
        User user = new User();
        user.setUserid(999L);
        user.setUsername("huanghe");
        user.setCreateTime(LocalDateTime.now());
        assertTrue(redisUtils.setObjectWithSecond("upc:user:999", user, 10));
    }

    @Test
    @Order(4)
    void setObjectWithSecond_shouldReturnFalse_whenObjectIsNull() {
        assertFalse(redisUtils.setObjectWithSecond("upc:user:999", null, 10));
    }

    @Test
    @Order(5)
    void setObjectWithMinute_shouldReturnTrue_whenObjectIsNotNull() {
        User user = new User();
        user.setUserid(999L);
        user.setUsername("huanghe");
        user.setCreateTime(LocalDateTime.now());
        assertTrue(redisUtils.setObjectWithMinute("upc:user:999", user, 1));
    }

    @Test
    @Order(6)
    void setObjectWithMinute_shouldReturnFalse_whenObjectIsNull() {
        assertFalse(redisUtils.setObjectWithMinute("upc:user:999", null, 1));
    }

    @Test
    @Order(7)
    void getObject_shouldReturnObject_whenKeyExists() {
        User user = redisUtils.getObject("upc:user:999", User.class);
        assertNotNull(user);
        assertEquals(999L, user.getUserid());
        assertEquals("huanghe", user.getUsername());
    }

    @Test
    @Order(8)
    void getObject_shouldReturnNull_whenKeyDoesNotExist() {
        User user = redisUtils.getObject("upc:user:nonexistent", User.class);
        assertNull(user);
    }

    @Test
    @Order(9)
    void getList_shouldReturnList_whenKeyExists() {
        User user1 = new User();
        user1.setUserid(1001L);
        user1.setUsername("user1");
        user1.setCreateTime(LocalDateTime.now());

        User user2 = new User();
        user2.setUserid(1002L);
        user2.setUsername("user2");
        user2.setCreateTime(LocalDateTime.now());

        List<User> userList = List.of(user1, user2);
        redisUtils.setObject("upc:user:list", userList);

        List<User> users = redisUtils.getList("upc:user:list", User.class);
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(1001L, users.get(0).getUserid());
        assertEquals("user1", users.get(0).getUsername());
        assertEquals(1002L, users.get(1).getUserid());
        assertEquals("user2", users.get(1).getUsername());
    }

    @Test
    @Order(10)
    void getList_shouldReturnNull_whenKeyDoesNotExist() {
        List<User> users = redisUtils.getList("upc:user:nonexistent", User.class);
        assertNull(users);
    }

    @Test
    @Order(11)
    void setString_shouldReturnTrue_whenValueIsNotNull() {
        assertTrue(redisUtils.setString("upc:string:999", "value1"));
    }

    @Test
    @Order(12)
    void getString_shouldReturnString_whenKeyExists() {
        String value = redisUtils.getString("upc:string:999");
        assertNotNull(value);
        assertEquals("value1", value);
    }

    @Test
    @Order(13)
    void setString_shouldReturnFalse_whenValueIsNull() {
        assertFalse(redisUtils.setString("upc:string:999", null));
    }

    @Test
    @Order(14)
    void setStringWithMinute_shouldReturnTrue_whenValueIsNotNull() {
        assertTrue(redisUtils.setStringWithMinute("upc:string:999", "value", 1));
    }

    @Test
    @Order(15)
    void setStringWithMinute_shouldReturnFalse_whenValueIsNull() {
        assertFalse(redisUtils.setStringWithMinute("upc:string:999", null, 1));
    }

    @Test
    @Order(16)
    void hasKey_shouldReturnTrue_whenKeyExists() {
        assertTrue(redisUtils.hasKey("upc:user:999"));
    }

    @Test
    @Order(17)
    void hasKey_shouldReturnFalse_whenKeyDoesNotExist() {
        assertFalse(redisUtils.hasKey("upc:user:nonexistent"));
    }

    @Test
    @Order(18)
    void deleteKey_shouldReturnTrue_whenKeyExists() {
        User user = new User();
        user.setUserid(998L);
        user.setUsername("huanghe");
        user.setCreateTime(LocalDateTime.now());
        redisUtils.setObject("upc:user:998", user);

        assertTrue(redisUtils.deleteKey("upc:user:998"));
    }

    @Test
    @Order(19)
    void deleteKey_shouldReturnFalse_whenKeyDoesNotExist() {
        assertFalse(redisUtils.deleteKey("upc:user:nonexistent"));
    }
}