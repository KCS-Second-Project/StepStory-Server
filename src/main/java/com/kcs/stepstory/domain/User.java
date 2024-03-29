package com.kcs.stepstory.domain;

import com.kcs.stepstory.dto.request.UserUpdateDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.kcs.stepstory.dto.request.AuthSignUpDto;
import com.kcs.stepstory.dto.type.EProvider;
import com.kcs.stepstory.dto.type.ERole;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "User")
public class User {
    /* Default Column */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "serialId", nullable = false, unique = true)
    private String serialId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private EProvider provider;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ERole role;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    /* User Info */
    @Column(name = "nickname", nullable = false)
    private String nickname="";

    @Column(name = "profileImgUrl", nullable = false)
    private String profileImgUrl = "default_profile.png";

    @Column(name = "selfIntro",nullable = true)
    private String selfIntro;

    /* User Status */
    @Column(name = "isLogin", columnDefinition = "TINYINT(1)")
    private Boolean isLogin;

    @Column(name = "refreshToken")
    private String refreshToken;

    @Builder
    public User(String serialId, String password, EProvider provider, ERole role) {
        this.serialId = serialId;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.nickname = "";
        this.selfIntro = "";
        this.profileImgUrl = "default_profile.png";
        this.isLogin = false;
    }

    public void register(String nickname) {
        this.nickname = nickname;
        this.role = ERole.USER;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateInfoOnlyMessage(UserUpdateDto userUpdateDto) {
        if (userUpdateDto.nickname() != null && userUpdateDto.selfIntro() != null) {
            this.nickname = userUpdateDto.nickname();
            this.selfIntro = userUpdateDto.selfIntro();
        }
        else if (userUpdateDto.nickname() != null) {
            this.nickname = userUpdateDto.nickname();
        }
        else if (userUpdateDto.selfIntro() != null) {
            this.selfIntro = userUpdateDto.selfIntro();
        } else throw new IllegalArgumentException("닉네임 또는 자기소개가 필요합니다.");
    }

    public void updateInfoOnlyImage(String profileImageName) {
        if (profileImageName != null && (!Objects.equals(this.profileImgUrl, profileImageName))) {
            this.profileImgUrl = profileImageName;
        }
    }

    public void updateInfo (UserUpdateDto userUpdateDto, String profileImageName) {
        if (userUpdateDto.nickname() != null && userUpdateDto.selfIntro() != null) {
            this.nickname = userUpdateDto.nickname();
            this.selfIntro = userUpdateDto.selfIntro();
        }
        else if (userUpdateDto.nickname() != null) {
            this.nickname = userUpdateDto.nickname();
        }
        else if (userUpdateDto.selfIntro() != null) {
            this.selfIntro = userUpdateDto.selfIntro();
        } else throw new IllegalArgumentException("닉네임 또는 자기소개가 필요합니다.");

        if (profileImageName != null && (!Objects.equals(this.profileImgUrl, profileImageName))) {
            this.profileImgUrl = profileImageName;
        }
    }

    public static User signUp(AuthSignUpDto authSignUpDto, String encodedPassword) {
        return User.builder()
                .serialId(authSignUpDto.serialId())
                .password(encodedPassword)
                .provider(EProvider.DEFAULT)
                .role(ERole.USER)
                .build();
    }
}
