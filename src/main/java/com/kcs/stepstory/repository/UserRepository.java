package com.kcs.stepstory.repository;

import com.kcs.stepstory.domain.User;
import com.kcs.stepstory.dto.type.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdAndRefreshTokenAndIsLogin(Long id, String refreshToken, Boolean isLogin);

    User findByNickname(String nickname);

    @Query("select u.userId as userId, u.role as role, u.password as password from User u where u.serialId = :serialId")
    Optional<UserSecurityForm> findSecurityFormBySerialId(String serialId);

    @Query("select u.userId as userId, u.role as role, u.password as password from User u where u.userId = :id and u.isLogin = true")
    Optional<UserSecurityForm> findSecurityFormById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.refreshToken = :refreshToken, u.isLogin = :isLogin where u.userId = :userId")
    void updateRefreshTokenAndLoginStatus(Long userId, String refreshToken, Boolean isLogin);

    boolean existsByNickname(String nickname);

    interface UserSecurityForm {
        Long getUserId();
        ERole getRole();
        String getPassword();
        static UserSecurityForm invoke(User user) {
            return new UserSecurityForm() {
                @Override
                public Long getUserId() {
                    return user.getUserId();
                }

                @Override
                public ERole getRole() {
                    return user.getRole();
                }

                @Override
                public String getPassword() {
                    return user.getPassword();
                }
            };
        }
    }
}
