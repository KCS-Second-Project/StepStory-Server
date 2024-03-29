package com.kcs.stepstory.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "UserUpdateDto", description = "유저 정보 수정 요청")
public record UserUpdateDto(
        @JsonProperty("nickname")
        @Schema(description = "닉네임", example = "개똥이")
        @Size(min = 2, max = 10, message = "닉네임은 2~10자리로 입력해주세요.")
        @NotNull(message = "닉네임은 null이 될 수 없습니다.")
        String nickname,
        @JsonProperty("self_intro")
        @Schema(description = "자기 소개", example = "안녕하세요. 개똥이입니다.")
        @Size(min = 0, max = 100, message = "자기 소개는 1자 이상 100자 이하로 입력해주세요.")
        String selfIntro
) {
}
