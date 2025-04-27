-- 회원 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(255) NOT NULL COMMENT '회원 상태',
    username VARCHAR(255) NOT NULL UNIQUE COMMENT '회원 이메일',
    name VARCHAR(255) NOT NULL COMMENT '회원 이름',
    provider VARCHAR(255) NOT NULL COMMENT 'OAuth Provider',
    provider_id VARCHAR(255) UNIQUE COMMENT 'OAuth Provider ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '회원 생성일',
    updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '회원 수정일',
    deleted_at TIMESTAMP COMMENT '회원 탈퇴일'
);

-- 링크 테이블
CREATE TABLE links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(255) NOT NULL COMMENT '링크 상태',
    original_url VARCHAR(255) NOT NULL COMMENT '원 URL 주소',
    short_path VARCHAR(255) NOT NULL UNIQUE COMMENT '압축 URL Path',
    user_id BIGINT COMMENT '회원 ID',
    current_hits INT DEFAULT 0 COMMENT '현재 조회수',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '링크 생성일',
    updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '링크 수정일',
    expired_at TIMESTAMP COMMENT '링크 만료일',
    deleted_at TIMESTAMP COMMENT '링크 삭제일',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 조회 요청 로그 테이블
CREATE TABLE link_access_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    link_id BIGINT NOT NULL COMMENT '링크 ID',
    client_ip VARCHAR(255) COMMENT 'IP 주소',
    client_device VARCHAR(255) COMMENT '디바이스',  # PC, Mobile
    client_browser VARCHAR(255) COMMENT '브라우저', # Chrome, Safari
    client_os VARCHAR(255) COMMENT '운영체제',      # Windows, Android
    country_code VARCHAR(255) COMMENT '국가 코드',  # KR, US
    region_name VARCHAR(255) COMMENT '지역명',     # Seoul, Chicago
    referer VARCHAR(1024) COMMENT '유입경로',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '접속일',
    FOREIGN KEY (link_id) REFERENCES links(id)
);

-- 인덱스
CREATE INDEX idx_users_username on users(username);
CREATE INDEX idx_users_provider_id on users(provider_id);
CREATE INDEX idx_links_user_id ON links(user_id);
CREATE INDEX idx_links_short_path ON links(short_path);
CREATE INDEX idx_links_original_url ON links(original_url);
CREATE INDEX idx_link_access_logs_link_id ON link_access_logs(link_id);
CREATE INDEX idx_link_access_logs_created_at ON link_access_logs(created_at);
CREATE INDEX idx_link_access_logs_country_code ON link_access_logs(country_code);
CREATE INDEX idx_link_access_logs_region_name ON link_access_logs(region_name);
