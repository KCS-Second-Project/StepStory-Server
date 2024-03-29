package com.kcs.stepstory.service;


import com.kcs.stepstory.domain.Friend;
import com.kcs.stepstory.domain.User;
import com.kcs.stepstory.dto.response.FriendDetailDto;
import com.kcs.stepstory.dto.response.FriendDto;
import com.kcs.stepstory.dto.response.FriendListDto;
import com.kcs.stepstory.dto.response.FriendSearchListDto;
import com.kcs.stepstory.exception.CommonException;
import com.kcs.stepstory.exception.ErrorCode;
import com.kcs.stepstory.repository.FriendRepository;
import com.kcs.stepstory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendsService {


    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    /**
     *  친구 목록 조회 서비스 + 친구요청 목록 조회
     *  베어러 토큰의 user id가 파라미터로 받는다.
     */
    public FriendListDto getAllFriendList(Long userId) {

        //u.userid를 List 컬렉션에 저장
        List<Long> sendFriendIdList = friendRepository.findBySendFriendList1(userId);
        List<Long> receiveFriendIdList = friendRepository.findByReceiveFriendList1(userId);
        List<FriendDto> friendDtoList = new ArrayList<>();


        // u.userid를  fromEntity메소드를 통해
        for (int i = 0; i < sendFriendIdList.size(); i++) {
            //sendFriendIdList를 통해 User엔티티 타입의 객체 반환
            User user = userRepository.getReferenceById(sendFriendIdList.get(i));
            //fromEntity()이용하여 User 객체를 Dto타입으로 변환
            FriendDto friendDto = FriendDto.fromEntity(user);
            friendDtoList.add(friendDto);
        }
        for (Long id : receiveFriendIdList) {
            //userid > User 엔티티
            User user = userRepository.getReferenceById(id);
            //엔티티를 -> dto로 변환
            FriendDto friendDto = FriendDto.fromEntity(user);
            friendDtoList.add(friendDto);
        }

        // 친구요청 목록 조회 서비스 로직
        List<Long> requestFriendList = friendRepository.findByrequestFriendList(userId);
        List<FriendDto> requestFriendDtoList = new ArrayList<>();

        for (Long requestId : requestFriendList) {
            User user = userRepository.getReferenceById(requestId);
            FriendDto friendDto = FriendDto.fromEntity(user);
            requestFriendDtoList.add(friendDto);
        }
        return FriendListDto.builder()
                .friendListDtos(friendDtoList)  // 친구목록 Dtos
                .requestFriendListDtos(requestFriendDtoList) // 친구 요청 목록 Dtos
                .build();
    }



    /**
     * 친구요청 목록 Count 서비스
     */
    public Long getCountFriendList(Long userId) {
        Long requestFriendList = friendRepository.countByRequestFriendList(userId);

        return requestFriendList;
    }


    /**
     * 상세 정보 확인 서비스
     */
    @Transactional
    public FriendDetailDto getFriendDetailsUser(Long userId, Long friendId) {
        Long friend = friendRepository.findBySendFriendDetails(userId, friendId);
        if(friendId == null) {
            friend = friendRepository.findByReceiveFriendDetails(userId, friendId);
        }

        User user = userRepository.getReferenceById(friend);
        FriendDetailDto friendDetailDto = FriendDetailDto.fromEntityDetails(user);

        return friendDetailDto;
    }


    /**
     *  친구닉네임 조회 서비스
     */
    public FriendSearchListDto getFriendNickNameList(Long userId, String nickName) {
        List<Long> SendFriendNicknameList = friendRepository.findBySendFriendNicknameList(userId, nickName);
        List<Long> ReceiveFriendNicknameList = friendRepository.findByReceiveFriendNicknameList(userId, nickName);
        List<FriendDto> friendDtoList = new ArrayList<>();

        for (long sendId : SendFriendNicknameList) {
            User user = userRepository.getReferenceById(sendId);
            FriendDto friendDto = FriendDto.fromEntity(user);
            friendDtoList.add(friendDto);
        }

        for (long receiveId : ReceiveFriendNicknameList) {
            User user = userRepository.getReferenceById(receiveId);
            FriendDto friendDto = FriendDto.fromEntity(user);
            friendDtoList.add(friendDto);
        }

        return FriendSearchListDto.builder()
                .friendSearchListDtos(friendDtoList)
                .build();

    }

    /**
     *  친구 요청 서비스
     */
    @Transactional
    public Friend requestFriendsUser(Long userId, Long friendId) {
        User user = userRepository.getReferenceById(userId);
        User requestUser = userRepository.getReferenceById(friendId);

        if (user == null || requestUser == null) {
            throw new CommonException(ErrorCode.NOT_FOUND_USER);
        }

        if (userId.equals(friendId)) {
            throw new CommonException(ErrorCode.BAD_REQUEST_PARAMETER);
        }

        // 중복된 친구 요청 처리
        if (friendRepository.existsByUser1UserIdAndUser2UserIdAndStatus(userId, friendId, 0) ||
                friendRepository.existsByUser1UserIdAndUser2UserIdAndStatus(friendId, userId, 0)) {
            throw new CommonException(ErrorCode.BAD_REQUEST_PARAMETER);
        }
        if (friendRepository.existsByUser1UserIdAndUser2UserIdAndStatus(userId, friendId, 1) ||
                friendRepository.existsByUser1UserIdAndUser2UserIdAndStatus(friendId, userId, 1)) {
            throw new CommonException(ErrorCode.BAD_REQUEST_PARAMETER);
        }

        // 친구 요청 기능을 구현하기 위해 생성자로 생성
        Friend friend = Friend.builder()
                .user1(user)
                .user2(requestUser)
                .status(0)
                .build();
        friendRepository.save(friend);
        return friend;
    }



    /**
     *  친구 수락 서비스
     */
    @Transactional
    public void acceptFriendsUser(Long userId, Long friendId) {
        // friendRepository를 이용해 userId와 friendId로 Friend 객체 하나를 찾음
        User user = userRepository.getReferenceById(userId);
        User requestUser = userRepository.getReferenceById(friendId);
        Friend targetFriend = friendRepository.findByUser1AndUser2(requestUser,user);
        targetFriend.makeFriendRelation();
    }




    /**
     *  친구 삭제 서비스 OR 친구 거절 서비스
     */
    @Transactional
    public void deleteFriendsUser(Long userId, Long friendId) {
        User user = userRepository.getReferenceById(userId);
        User friendUser = userRepository.getReferenceById(friendId);
        Friend friend = friendRepository.findByUser1AndUser2(user,friendUser);
        if(friend == null) {
            friend = friendRepository.findByUser1AndUser2(friendUser,user);
        }

        // friend = 엔티티로 반환된 값 대입 후 f.엔티티id를 찾아서 deleteById 메서드 실행
        friendRepository.delete(friend);
    }



}