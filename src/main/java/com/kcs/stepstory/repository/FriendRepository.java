package com.kcs.stepstory.repository;

import com.kcs.stepstory.domain.Friend;
import com.kcs.stepstory.dto.response.FriendDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {


    /**
     *  친구 목록 조회 & 친구 요청 목록 기능 & 친구 닉네임 검색 기능
     *  user2라는 테이블에
     */

    @Query("select FriendDto.fromNicknameAndProfileImgUrl(u.nickname, u.profileImgUrl) " +
            "from Friend f join f.user2 u  where f.user1 = :userId and f.status = 1")
    List<FriendDto> findBySendFriendList(@Param("userId") Long userId);


    @Query("select FriendDto.fromNicknameAndProfileImgUrl(u.nickname, u.profileImgUrl) " +
            "from Friend f join f.user1 u  where f.user2 = :userId and f.status = 1")
    List<FriendDto> findByReceiveFriendList(@Param("userId") Long userId);

    /**
     * 친구요청 목록조회 기능
     */
    @Query("select FriendDto.fromNicknameAndProfileImgUrl(u.nickname, u.profileImgUrl) " +
            "from Friend f join f.user1 u where f.user2 = :userId and f.status =0")
    List<FriendDto> findByrequestFriendList(@Param("userId") Long userId);

    /**
     * 친구요청 목록 count 기능
     */
    @Query("SELECT COUNT(f) " +
            "FROM Friend f " +
            "WHERE f.user1 = :friendId AND f.user2 = :userId AND f.status = 0")
    Long countByRequestFriendList(@Param("userId") Long userId, @Param("friendId") Long friendId);


    /**
     *  친구 상세 정보 확인 기능
     */
    @Query("select FriendListDto.fromNicknameAndProfileImgUrl(u.nickname, u.profileImgUrl, u.selfIntro) " +
            "from Friend f JOIN f.user2 u  where f.user2 = :userId and f.status = 1")
    List<Friend> findFriendDetails(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") int status);



    /**
     *  친구 삭제 기능
     */
    @Modifying
    @Query("delete from Friend f where (f.user1 = :userId and f.user2 = :friendId or f.userId1 = :friendId and f.userId2 = :userId) and f.status = 1")
    void deleteFriendByUserId(@Param("userId") Long userId, @Param("friendId") Long friendId);


    /**
     *  친구 요청 기능
     */
    @Modifying
    @Query("insert into Friend f (f.user1, f.user2, f.status) values (:userId1, :friendId, :status)")
    void insertFriendByUserId(@Param("userId1") Long userId1, @Param("friendId") Long friendId, @Param("status") int status);


    /**
     *  친구 수락 기능
     */
    @Modifying
    @Query("update Friend f set f.status = 1 WHERE f.user1 = :friendId AND f.user2 = :userId")
    void acceptFriendRequest(@Param("userId") Long userId, @Param("friendId") Long friendId);


    /**
     *  친구요청 거절 기능
     */
    @Modifying
    @Query("delete from Friend f where (f.user1 = :friendId and f.user2 = :userId AND f.status = 0")
    void refuseFriendByUserId(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 친구 닉네임 검색 기능
     * !! 닉네임 한글자라도 입력하면 반환해줘야하므로 like 추가
     */
    @Query("select FriendDto.fromNicknameAndProfileImgUrl(u.nickname, u.profileImgUrl) " +
            "from Friend f join f.user2 u " +
            "where f.user1 = :userId and f.status = 1 and u.nickname LIKE CONCAT('%', :friendNickname, '%')")
    List<FriendDto> findBySendFriendNicknameList(@Param("userId") Long userId, @Param("friendNickname") String friendNickname);

    @Query("SELECT FriendDto.fromNicknameAndProfileImgUrl(u.nickname, u.profileImgUrl) " +
            "FROM Friend f JOIN f.user1 u " +
            "WHERE f.user2 = :userId AND f.status = 1 AND u.nickname LIKE CONCAT('%', :friendNickname, '%')")
    List<FriendDto> findByReceiveFriendNicknameList(@Param("userId") Long userId, @Param("friendNickname") String friendNickname);








}