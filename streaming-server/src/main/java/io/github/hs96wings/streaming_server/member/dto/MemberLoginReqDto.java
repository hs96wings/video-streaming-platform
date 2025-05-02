package io.github.hs96wings.streaming_server.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginReqDto {
    private String userid;
    private String password;
}
