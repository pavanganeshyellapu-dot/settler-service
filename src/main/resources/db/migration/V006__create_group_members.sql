CREATE TABLE IF NOT EXISTS group_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,

    CONSTRAINT fk_group_member_group FOREIGN KEY (group_id)
    REFERENCES groups (id) ON DELETE CASCADE,

    CONSTRAINT fk_group_member_user FOREIGN KEY (user_id)
    REFERENCES users (id) ON DELETE CASCADE
    );
