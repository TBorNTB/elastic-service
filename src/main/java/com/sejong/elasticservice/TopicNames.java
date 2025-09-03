package com.sejong.elasticservice;

public final class TopicNames {
    private TopicNames() {}

    // 프로젝트 이벤트 (예: 생성/수정/삭제 로그)
    public static final String PROJECT = "aws.project.cdc.events.0";

    // 문서 이벤트
    public static final String DOCUMENT = "aws.document.cdc.events.0";

    // 게시글 좋아요 이벤트
    public static final String POSTLIKE = "aws.post.cdc.likes.0";

    // 조회수 이벤트
    public static final String VIEW = "aws.post.cdc.views.0";
}