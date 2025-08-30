package com.example.startlight.member.dao;

import com.example.startlight.member.entity.Member;

public interface MemberDao {
    Member createMember(Member member);

    Member selectMember(Long id);

    Member updateMemberName(Long id, String nickname);

    void deleteMember(Long id);

    void updateMemberMemory(Long id);

    Integer getMemoryNum(Long id);

    String getMemberName(Long id);
}
