ALTER TABLE category
    ADD COLUMN icon_key  VARCHAR(50)  NOT NULL DEFAULT 'tag',
    ADD COLUMN color_key VARCHAR(20)  NOT NULL DEFAULT 'violet';
