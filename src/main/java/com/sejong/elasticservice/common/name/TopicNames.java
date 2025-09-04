package com.sejong.elasticservice.common.name;

public final class TopicNames {
    private TopicNames() {}

    // 프로젝트 이벤트 (예: 생성/수정/삭제 로그)
    public static final String PROJECT = "project";

    // 문서 이벤트
    public static final String DOCUMENT = "document";

    // 게시글 좋아요 이벤트
    public static final String POSTLIKE = "postlike";

    // 조회수 이벤트
    public static final String VIEW = "view";
}