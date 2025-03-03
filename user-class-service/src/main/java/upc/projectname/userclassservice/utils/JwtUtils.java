package upc.projectname.userclassservice.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    private static final String signKey = "huanghe-test-jwtutils-2024-10-6-14-36-upc-south-hall-wozhenbuxiangxuexi-malegebi";
    private static final Long expire= 24 * 60 * 60 * 1000L; // 24 hours
    private static final int shortTime=60*1000; // 60 seconds
    private static final Long longTime= 2 * 60 * 60 * 1000L; // 2 hours
    // 返回状态常量
    public static final String TOKEN_EXPIRED = "Expired";
    public static final String TOKEN_INVALID = "Invalid";




    public static String createJwt( Map<String, Object> claims) {

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + expire;
        Date exp = new Date(expMillis);

        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey key = Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8));

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(key)
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .claims(claims)
                // 设置过期时间
                .expiration(exp);
        return builder.compact();
    }


    public static Object parseJWT(String token)  {

        //生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        SecretKey key = Keys.hmacShaKeyFor(signKey.getBytes(StandardCharsets.UTF_8));

        try {
            // 得到DefaultJwtParser
            JwtParser jwtParser = Jwts.parser()
                    // 设置签名的秘钥
                    .verifyWith(key)
                    .build();
            Jws<Claims> jws = jwtParser.parseSignedClaims(token);
            return new HashMap<>(jws.getPayload());
        } catch (ExpiredJwtException e) {
            // 令牌已过期
            return TOKEN_EXPIRED;
        } catch (Exception e) {
            // 令牌不合法
            return TOKEN_INVALID;
        }
    }
}

