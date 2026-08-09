-- =============================================================================
-- seed_dev.sql — Data giả cho môi trường LOCAL (chỉ để xem/thử, KHÔNG dùng prod)
-- -----------------------------------------------------------------------------
-- Chạy SAU khi đã tạo schema bằng các file migration V1..V4.
-- Toàn bộ ID được đặt tường minh để dễ tham chiếu khoá ngoại.
-- Mật khẩu mọi user (bcrypt) đều là: password
--
-- Bộ data (mở rộng cho phong phú):
--   10 danh mục · 48 sản phẩm · 12 user · 9 địa chỉ · 8 voucher
--   34 đánh giá · 5 giỏ hàng · 12 đơn hàng.
--
-- Ghi chú về rating/review_count trên bảng products: đây là số liệu TỔNG HỢP
-- (denormalized) để frontend hiển thị nhanh — bảng `reviews` chỉ chứa MỘT PHẦN
-- đánh giá mẫu gần đây nên số dòng review có thể ít hơn review_count. Điều này
-- là bình thường và giống cách các sàn TMĐT hiển thị.
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Xoá dữ liệu cũ (cho phép chạy lại nhiều lần) -------------------------------
DELETE FROM order_items;
DELETE FROM orders;
DELETE FROM cart_items;
DELETE FROM carts;
DELETE FROM reviews;
DELETE FROM user_addresses;
DELETE FROM product_images;
DELETE FROM products;
DELETE FROM categories;
DELETE FROM user_roles;
DELETE FROM vouchers;
DELETE FROM users;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1) CATEGORIES
-- =============================================================================
INSERT INTO categories (id, name, slug, image_url, position, created_at) VALUES
(1,  'Điện thoại & Phụ kiện', 'dien-thoai-phu-kien', 'https://picsum.photos/seed/cat-phone/200/200',  1,  NOW()),
(2,  'Máy tính & Laptop',      'may-tinh-laptop',      'https://picsum.photos/seed/cat-laptop/200/200', 2,  NOW()),
(3,  'Thời trang Nam',         'thoi-trang-nam',       'https://picsum.photos/seed/cat-men/200/200',    3,  NOW()),
(4,  'Thời trang Nữ',          'thoi-trang-nu',        'https://picsum.photos/seed/cat-women/200/200',  4,  NOW()),
(5,  'Thiết bị điện tử',       'thiet-bi-dien-tu',     'https://picsum.photos/seed/cat-elec/200/200',   5,  NOW()),
(6,  'Nhà cửa & Đời sống',     'nha-cua-doi-song',     'https://picsum.photos/seed/cat-home/200/200',   6,  NOW()),
(7,  'Sắc đẹp',                'sac-dep',              'https://picsum.photos/seed/cat-beauty/200/200', 7,  NOW()),
(8,  'Thể thao & Dã ngoại',    'the-thao-da-ngoai',    'https://picsum.photos/seed/cat-sport/200/200',  8,  NOW()),
(9,  'Sách & Văn phòng phẩm',  'sach-van-phong-pham',  'https://picsum.photos/seed/cat-book/200/200',   9,  NOW()),
(10, 'Mẹ & Bé',                'me-va-be',             'https://picsum.photos/seed/cat-baby/200/200',   10, NOW());

-- =============================================================================
-- 2) PRODUCTS  (status: ACTIVE | INACTIVE | DELETED)
-- =============================================================================
INSERT INTO products
  (id, name, slug, description, price, sale_price, stock, category_id,
   rating, review_count, sold, location, is_official, free_shipping, featured, status, created_at) VALUES
-- Cat 1: Điện thoại & Phụ kiện
(1,  'iPhone 15 Pro Max 256GB', 'iphone-15-pro-max-256gb', 'Chính hãng VN/A, Titan tự nhiên.',      34990000.00, 33490000.00,  50, 1, 4.9, 320, 1200, 'TP. Hồ Chí Minh', 1, 1, 1, 'ACTIVE', NOW()),
(2,  'Samsung Galaxy S24 Ultra','samsung-galaxy-s24-ultra','Snapdragon 8 Gen 3, bút S-Pen.',        31990000.00, NULL,         40, 1, 4.8, 210,  850, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
(3,  'Xiaomi Redmi Note 13',    'xiaomi-redmi-note-13',    'Màn AMOLED 120Hz, pin 5000mAh.',         5490000.00,  4990000.00, 200, 1, 4.6, 540, 3200, 'TP. Hồ Chí Minh', 0, 1, 0, 'ACTIVE', NOW()),
(4,  'Ốp lưng iPhone chống sốc','op-lung-iphone-chong-soc','Silicon cao cấp, viền camera bảo vệ.',     120000.00,    89000.00, 500, 1, 4.4, 260, 8900, 'Đà Nẵng',         0, 0, 0, 'ACTIVE', NOW()),
(5,  'Sạc nhanh 20W USB-C',     'sac-nhanh-20w-usb-c',     'Chuẩn PD, an toàn quá nhiệt.',             199000.00,   149000.00, 300, 1, 4.5, 410, 5400, 'Hà Nội',          0, 0, 0, 'ACTIVE', NOW()),
(19, 'OPPO Reno11 F 5G',        'oppo-reno11-f-5g',        'Camera 64MP, sạc SUPERVOOC 67W.',         8990000.00,  8290000.00,  90, 1, 4.5, 156,  720, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
(20, 'Tai nghe Bluetooth TWS',  'tai-nghe-bluetooth-tws',  'Chống ồn ENC, pin 30h.',                   590000.00,   449000.00, 400, 1, 4.3, 640, 5200, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(21, 'Cáp sạc USB-C to Lightning','cap-sac-usb-c-to-lightning','Chuẩn MFi, dài 1m.',                  250000.00,   189000.00, 600, 1, 4.6, 320, 8800, 'Đà Nẵng',         0, 0, 0, 'ACTIVE', NOW()),
-- Cat 2: Máy tính & Laptop
(6,  'MacBook Air M3 13 inch',  'macbook-air-m3-13',       'Chip Apple M3, 8GB/256GB.',             27990000.00, 26990000.00,  30, 2, 4.9, 190,  420, 'TP. Hồ Chí Minh', 1, 1, 1, 'ACTIVE', NOW()),
(7,  'Dell XPS 13 Plus',        'dell-xps-13-plus',        'Intel Core Ultra 7, màn 3.5K OLED.',    35990000.00, NULL,         15, 2, 4.7,  64,  180, 'Hà Nội',          1, 1, 0, 'ACTIVE', NOW()),
(8,  'Laptop Gaming Acer Nitro 5','laptop-gaming-acer-nitro-5','RTX 4060, 16GB RAM, 144Hz.',         24990000.00, 22990000.00,  25, 2, 4.6, 130,  310, 'TP. Hồ Chí Minh', 0, 1, 1, 'ACTIVE', NOW()),
(9,  'Chuột Logitech MX Master 3S','chuot-logitech-mx-master-3s','Không dây, cuộn MagSpeed.',         2490000.00,  2190000.00, 120, 2, 4.8, 280, 1500, 'Đà Nẵng',         1, 1, 0, 'ACTIVE', NOW()),
(22, 'Bàn phím cơ AKKO 3068',   'ban-phim-co-akko-3068',   'Switch Blue, layout 65%.',               1290000.00,  1090000.00, 110, 2, 4.7, 210,  940, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
(23, 'Màn hình LG UltraGear 27"','man-hinh-lg-ultragear-27','2K 165Hz, tấm nền IPS.',                 6990000.00,  6290000.00,  45, 2, 4.8,  88,  260, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
(24, 'SSD Samsung 980 1TB',     'ssd-samsung-980-1tb',     'NVMe PCIe 3.0, đọc 3500MB/s.',           1890000.00,  1690000.00, 200, 2, 4.9, 430, 1600, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
-- Cat 3: Thời trang Nam
(10, 'Áo thun nam cotton',      'ao-thun-nam-cotton',      'Cotton 100%, form regular.',               199000.00,   149000.00, 800, 3, 4.5, 730,12000, 'TP. Hồ Chí Minh', 0, 1, 0, 'ACTIVE', NOW()),
(11, 'Quần jean nam slim-fit',  'quan-jean-nam-slim-fit',  'Vải co giãn, màu xanh đậm.',               450000.00,   359000.00, 350, 3, 4.4, 290, 4300, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(25, 'Áo sơ mi nam dài tay',    'ao-so-mi-nam-dai-tay',    'Vải oxford, form slim.',                   320000.00,   259000.00, 300, 3, 4.4, 190, 3100, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(26, 'Giày thể thao nam',       'giay-the-thao-nam',       'Đế cao su, thoáng khí.',                   650000.00,   499000.00, 220, 3, 4.5, 275, 4200, 'TP. Hồ Chí Minh', 0, 1, 1, 'ACTIVE', NOW()),
(27, 'Thắt lưng da nam',        'that-lung-da-nam',        'Da bò thật, khoá tự động.',                350000.00,   279000.00, 180, 3, 4.6, 130, 2400, 'Đà Nẵng',         0, 1, 0, 'ACTIVE', NOW()),
-- Cat 4: Thời trang Nữ
(12, 'Váy liền thân nữ',        'vay-lien-than-nu',        'Chất voan mềm, hoạ tiết hoa nhí.',         320000.00,   259000.00, 260, 4, 4.6, 380, 6700, 'TP. Hồ Chí Minh', 0, 1, 1, 'ACTIVE', NOW()),
(13, 'Túi xách nữ da PU',       'tui-xach-nu-da-pu',       'Thiết kế tối giản, dây đeo chéo.',         480000.00,   399000.00, 150, 4, 4.7, 175, 2100, 'Hà Nội',          1, 1, 0, 'ACTIVE', NOW()),
(28, 'Áo khoác nữ dáng dài',    'ao-khoac-nu-dang-dai',    'Chất dạ, giữ ấm tốt.',                     550000.00,   429000.00, 160, 4, 4.6, 145, 1900, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(29, 'Giày cao gót 7cm',        'giay-cao-got-7cm',        'Mũi nhọn, da mềm.',                        420000.00,   329000.00, 140, 4, 4.4,  98, 1500, 'TP. Hồ Chí Minh', 0, 1, 0, 'ACTIVE', NOW()),
(30, 'Đầm công sở thanh lịch',  'dam-cong-so-thanh-lich',  'Form ôm, tôn dáng.',                       480000.00,   389000.00, 130, 4, 4.7, 176, 2600, 'TP. Hồ Chí Minh', 1, 1, 1, 'ACTIVE', NOW()),
-- Cat 5: Thiết bị điện tử
(14, 'Loa Bluetooth JBL Flip 6','loa-bluetooth-jbl-flip-6','Chống nước IP67, bass mạnh.',            2790000.00,  2490000.00, 140, 5, 4.8, 240,  980, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
(15, 'Tai nghe Sony WH-1000XM5','tai-nghe-sony-wh-1000xm5','Chống ồn chủ động hàng đầu.',            8490000.00,  7990000.00,  60, 5, 4.9, 320,  650, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
(31, 'Đồng hồ thông minh Xiaomi','dong-ho-thong-minh-xiaomi','Đo SpO2, pin 14 ngày.',                1290000.00,   990000.00, 250, 5, 4.5, 380, 3300, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
(32, 'Camera an ninh IMOU 360', 'camera-an-ninh-imou-360', 'Full HD, đàm thoại 2 chiều.',              690000.00,   549000.00, 300, 5, 4.6, 220, 2800, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
(33, 'Máy hút bụi cầm tay',     'may-hut-bui-cam-tay',     'Không dây, lực hút 12000Pa.',            1490000.00,  1190000.00,  90, 5, 4.4, 110,  870, 'Đà Nẵng',         0, 1, 0, 'ACTIVE', NOW()),
-- Cat 6: Nhà cửa & Đời sống
(16, 'Nồi chiên không dầu 5L',  'noi-chien-khong-dau-5l',  'Công suất 1500W, 8 chế độ.',             1590000.00,  1290000.00, 180, 6, 4.6, 300, 3400, 'Đà Nẵng',         0, 1, 0, 'ACTIVE', NOW()),
(34, 'Bộ nồi inox 5 món',       'bo-noi-inox-5-mon',       'Đáy từ, dùng mọi loại bếp.',             1290000.00,   990000.00, 120, 6, 4.7, 160, 1400, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(35, 'Đèn ngủ LED cảm ứng',     'den-ngu-led-cam-ung',     '3 mức sáng, sạc USB.',                     220000.00,   159000.00, 400, 6, 4.5, 340, 5600, 'TP. Hồ Chí Minh', 0, 1, 0, 'ACTIVE', NOW()),
(36, 'Máy lọc nước RO 9 lõi',   'may-loc-nuoc-ro-9-loi',   'Loại bỏ 99% vi khuẩn.',                  4990000.00,  4290000.00,  40, 6, 4.8,  75,  380, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
-- Cat 7: Sắc đẹp
(17, 'Son môi lì cao cấp',      'son-moi-li-cao-cap',      'Lâu trôi, lên màu chuẩn.',                 320000.00,   269000.00, 400, 7, 4.5, 520, 9200, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
(18, 'Kem chống nắng SPF50+',   'kem-chong-nang-spf50',    'Không nhờn rít, phù hợp da dầu.',          285000.00,   239000.00, 500, 7, 4.7, 610, 7800, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(37, 'Nước hoa nữ Eau de Parfum','nuoc-hoa-nu-eau-de-parfum','Hương hoa cỏ, lưu hương 8h.',           890000.00,   690000.00, 150, 7, 4.6, 210, 1700, 'TP. Hồ Chí Minh', 1, 1, 0, 'ACTIVE', NOW()),
(38, 'Serum dưỡng da Vitamin C','serum-duong-da-vitamin-c','Làm sáng, mờ thâm.',                       350000.00,   279000.00, 320, 7, 4.7, 430, 6100, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
(39, 'Máy rửa mặt silicon',     'may-rua-mat-silicon',     'Rung sonic, chống nước.',                  450000.00,   349000.00, 180, 7, 4.5, 190, 2200, 'Đà Nẵng',         0, 1, 0, 'ACTIVE', NOW()),
-- Cat 8: Thể thao & Dã ngoại
(40, 'Xe đạp thể thao 26"',     'xe-dap-the-thao-26',      'Khung nhôm, 21 tốc độ.',                 3290000.00,  2890000.00,  60, 8, 4.6,  90,  540, 'TP. Hồ Chí Minh', 0, 1, 1, 'ACTIVE', NOW()),
(41, 'Thảm tập yoga TPE',       'tham-tap-yoga-tpe',       'Dày 8mm, chống trượt.',                    320000.00,   239000.00, 400, 8, 4.7, 520, 7300, 'Hà Nội',          0, 1, 0, 'ACTIVE', NOW()),
(42, 'Bình giữ nhiệt 1 lít',    'binh-giu-nhiet-1-lit',    'Inox 304, giữ nhiệt 12h.',                 290000.00,   219000.00, 500, 8, 4.8, 610, 9100, 'Đà Nẵng',         0, 1, 0, 'ACTIVE', NOW()),
-- Cat 9: Sách & Văn phòng phẩm
(43, 'Sách Đắc Nhân Tâm',       'sach-dac-nhan-tam',       'Bản dịch mới, bìa mềm.',                   108000.00,    86000.00, 800, 9, 4.9,1200,18000, 'Hà Nội',          1, 1, 1, 'ACTIVE', NOW()),
(44, 'Bút bi Thiên Long (set 20)','but-bi-thien-long-set-20','Mực xanh, viết êm.',                      60000.00,    45000.00,1000, 9, 4.7, 480,12000, 'TP. Hồ Chí Minh', 0, 1, 0, 'ACTIVE', NOW()),
(45, 'Balo laptop chống nước',  'balo-laptop-chong-nuoc',  'Ngăn 15.6", cổng USB.',                    450000.00,   329000.00, 260, 9, 4.6, 350, 4400, 'TP. Hồ Chí Minh', 0, 1, 0, 'ACTIVE', NOW()),
-- Cat 10: Mẹ & Bé
(46, 'Bỉm Bobby size L (62 miếng)','bim-bobby-size-l-62-mieng','Siêu thấm, chống hằn.',                320000.00,   269000.00, 350,10, 4.7, 540, 8600, 'Hà Nội',          1, 1, 0, 'ACTIVE', NOW()),
(47, 'Sữa bột Vinamilk 900g',   'sua-bot-vinamilk-900g',   'Dinh dưỡng cho bé 1-3 tuổi.',              385000.00,   355000.00, 300,10, 4.8, 260, 3900, 'TP. Hồ Chí Minh', 1, 1, 1, 'ACTIVE', NOW()),
(48, 'Xe đẩy em bé gấp gọn',    'xe-day-em-be-gap-gon',    'Gấp 1 giây, nằm/ngồi.',                  1890000.00,  1490000.00,  70,10, 4.6,  85,  460, 'Đà Nẵng',         0, 1, 1, 'ACTIVE', NOW());

-- =============================================================================
-- 3) PRODUCT_IMAGES  (PK = product_id + position, position bắt đầu từ 0)
-- =============================================================================
INSERT INTO product_images (product_id, position, url) VALUES
(1,0,'https://picsum.photos/seed/p1a/400/400'),(1,1,'https://picsum.photos/seed/p1b/400/400'),
(2,0,'https://picsum.photos/seed/p2a/400/400'),(2,1,'https://picsum.photos/seed/p2b/400/400'),
(3,0,'https://picsum.photos/seed/p3a/400/400'),(3,1,'https://picsum.photos/seed/p3b/400/400'),
(4,0,'https://picsum.photos/seed/p4a/400/400'),
(5,0,'https://picsum.photos/seed/p5a/400/400'),
(6,0,'https://picsum.photos/seed/p6a/400/400'),(6,1,'https://picsum.photos/seed/p6b/400/400'),
(7,0,'https://picsum.photos/seed/p7a/400/400'),
(8,0,'https://picsum.photos/seed/p8a/400/400'),(8,1,'https://picsum.photos/seed/p8b/400/400'),
(9,0,'https://picsum.photos/seed/p9a/400/400'),
(10,0,'https://picsum.photos/seed/p10a/400/400'),(10,1,'https://picsum.photos/seed/p10b/400/400'),
(11,0,'https://picsum.photos/seed/p11a/400/400'),
(12,0,'https://picsum.photos/seed/p12a/400/400'),(12,1,'https://picsum.photos/seed/p12b/400/400'),
(13,0,'https://picsum.photos/seed/p13a/400/400'),
(14,0,'https://picsum.photos/seed/p14a/400/400'),
(15,0,'https://picsum.photos/seed/p15a/400/400'),(15,1,'https://picsum.photos/seed/p15b/400/400'),
(16,0,'https://picsum.photos/seed/p16a/400/400'),
(17,0,'https://picsum.photos/seed/p17a/400/400'),
(18,0,'https://picsum.photos/seed/p18a/400/400'),
(19,0,'https://picsum.photos/seed/p19a/400/400'),(19,1,'https://picsum.photos/seed/p19b/400/400'),
(20,0,'https://picsum.photos/seed/p20a/400/400'),
(21,0,'https://picsum.photos/seed/p21a/400/400'),
(22,0,'https://picsum.photos/seed/p22a/400/400'),(22,1,'https://picsum.photos/seed/p22b/400/400'),
(23,0,'https://picsum.photos/seed/p23a/400/400'),(23,1,'https://picsum.photos/seed/p23b/400/400'),
(24,0,'https://picsum.photos/seed/p24a/400/400'),
(25,0,'https://picsum.photos/seed/p25a/400/400'),
(26,0,'https://picsum.photos/seed/p26a/400/400'),(26,1,'https://picsum.photos/seed/p26b/400/400'),
(27,0,'https://picsum.photos/seed/p27a/400/400'),
(28,0,'https://picsum.photos/seed/p28a/400/400'),(28,1,'https://picsum.photos/seed/p28b/400/400'),
(29,0,'https://picsum.photos/seed/p29a/400/400'),
(30,0,'https://picsum.photos/seed/p30a/400/400'),(30,1,'https://picsum.photos/seed/p30b/400/400'),
(31,0,'https://picsum.photos/seed/p31a/400/400'),(31,1,'https://picsum.photos/seed/p31b/400/400'),
(32,0,'https://picsum.photos/seed/p32a/400/400'),
(33,0,'https://picsum.photos/seed/p33a/400/400'),
(34,0,'https://picsum.photos/seed/p34a/400/400'),(34,1,'https://picsum.photos/seed/p34b/400/400'),
(35,0,'https://picsum.photos/seed/p35a/400/400'),
(36,0,'https://picsum.photos/seed/p36a/400/400'),
(37,0,'https://picsum.photos/seed/p37a/400/400'),(37,1,'https://picsum.photos/seed/p37b/400/400'),
(38,0,'https://picsum.photos/seed/p38a/400/400'),
(39,0,'https://picsum.photos/seed/p39a/400/400'),
(40,0,'https://picsum.photos/seed/p40a/400/400'),(40,1,'https://picsum.photos/seed/p40b/400/400'),
(41,0,'https://picsum.photos/seed/p41a/400/400'),
(42,0,'https://picsum.photos/seed/p42a/400/400'),
(43,0,'https://picsum.photos/seed/p43a/400/400'),(43,1,'https://picsum.photos/seed/p43b/400/400'),
(44,0,'https://picsum.photos/seed/p44a/400/400'),
(45,0,'https://picsum.photos/seed/p45a/400/400'),
(46,0,'https://picsum.photos/seed/p46a/400/400'),
(47,0,'https://picsum.photos/seed/p47a/400/400'),(47,1,'https://picsum.photos/seed/p47b/400/400'),
(48,0,'https://picsum.photos/seed/p48a/400/400'),(48,1,'https://picsum.photos/seed/p48b/400/400');

-- =============================================================================
-- 4) USERS  (status: ACTIVE | INACTIVE | LOCKED | DELETED)
--    password_hash = bcrypt('password')
-- =============================================================================
INSERT INTO users (id, username, email, password_hash, full_name, phone, avatar_url, status, created_at) VALUES
(1, 'admin',      'admin@shop.local',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Quản Trị Viên',  '0900000001','https://i.pravatar.cc/150?img=1',  'ACTIVE',   NOW()),
(2, 'staff01',    'staff01@shop.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Nhân Viên Kho',  '0900000002','https://i.pravatar.cc/150?img=2',  'ACTIVE',   NOW()),
(3, 'nguyenvana', 'vana@gmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Nguyễn Văn A',   '0911111111','https://i.pravatar.cc/150?img=3',  'ACTIVE',   NOW()),
(4, 'tranthib',   'thib@gmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Trần Thị B',     '0922222222','https://i.pravatar.cc/150?img=4',  'ACTIVE',   NOW()),
(5, 'levanc',     'vanc@gmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Lê Văn C',       '0933333333','https://i.pravatar.cc/150?img=5',  'ACTIVE',   NOW()),
(6, 'phamthid',   'thid@gmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Phạm Thị D',     '0944444444', NULL,                              'INACTIVE', NOW()),
(7, 'hoangvane',  'hoang.e@gmail.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Hoàng Văn E',    '0955555555','https://i.pravatar.cc/150?img=7',  'ACTIVE',   NOW()),
(8, 'dothithuf',  'thu.f@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Đỗ Thị Thu F',   '0966666666','https://i.pravatar.cc/150?img=8',  'ACTIVE',   NOW()),
(9, 'buivang',    'bui.g@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Bùi Văn G',      '0977777777','https://i.pravatar.cc/150?img=9',  'ACTIVE',   NOW()),
(10,'ngothih',    'ngo.h@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Ngô Thị H',      '0988888888','https://i.pravatar.cc/150?img=10', 'ACTIVE',   NOW()),
(11,'dangvani',   'dang.i@gmail.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Đặng Văn I',     '0999999990','https://i.pravatar.cc/150?img=11', 'ACTIVE',   NOW()),
(12,'vuthik',     'vu.k@gmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','Vũ Thị K',       '0912345678', NULL,                              'LOCKED',   NOW());

-- Vai trò (Role: ROLE_ADMIN | ROLE_STAFF | ROLE_CUSTOMER)
INSERT INTO user_roles (user_id, role) VALUES
(1,'ROLE_ADMIN'),
(1,'ROLE_STAFF'),
(2,'ROLE_STAFF'),
(3,'ROLE_CUSTOMER'),
(4,'ROLE_CUSTOMER'),
(5,'ROLE_CUSTOMER'),
(6,'ROLE_CUSTOMER'),
(7,'ROLE_CUSTOMER'),
(8,'ROLE_CUSTOMER'),
(9,'ROLE_CUSTOMER'),
(10,'ROLE_CUSTOMER'),
(11,'ROLE_CUSTOMER'),
(12,'ROLE_CUSTOMER');

-- =============================================================================
-- 5) USER_ADDRESSES
-- =============================================================================
INSERT INTO user_addresses (id, user_id, full_name, phone, province, district, ward, address_line, is_default, created_at) VALUES
(1, 3, 'Nguyễn Văn A',           '0911111111','TP. Hồ Chí Minh','Quận 1',        'Phường Bến Nghé',  '123 Lê Lợi',            1, NOW()),
(2, 3, 'Nguyễn Văn A (Cơ quan)', '0911111111','TP. Hồ Chí Minh','Quận 3',        'Phường 6',         '45 Nguyễn Đình Chiểu', 0, NOW()),
(3, 4, 'Trần Thị B',             '0922222222','Hà Nội',          'Quận Cầu Giấy', 'Phường Dịch Vọng',  '78 Xuân Thủy',          1, NOW()),
(4, 5, 'Lê Văn C',               '0933333333','Đà Nẵng',         'Quận Hải Châu', 'Phường Thạch Thang','12 Bạch Đằng',          1, NOW()),
(5, 7, 'Hoàng Văn E',            '0955555555','Hải Phòng',       'Quận Lê Chân',  'Phường An Biên',    '9 Tô Hiệu',             1, NOW()),
(6, 8, 'Đỗ Thị Thu F',           '0966666666','Cần Thơ',         'Quận Ninh Kiều','Phường Tân An',     '56 Hòa Bình',           1, NOW()),
(7, 9, 'Bùi Văn G',              '0977777777','Hà Nội',          'Quận Đống Đa',  'Phường Khương Thượng','101 Tôn Thất Tùng',   1, NOW()),
(8, 10,'Ngô Thị H',              '0988888888','TP. Hồ Chí Minh','Quận Bình Thạnh','Phường 25',         '200 Điện Biên Phủ',     1, NOW()),
(9, 11,'Đặng Văn I',             '0999999990','Đà Nẵng',         'Quận Thanh Khê','Phường Tân Chính',  '33 Điện Biên Phủ',      1, NOW());

-- =============================================================================
-- 6) VOUCHERS  (discount_type: PERCENT | FIXED)
-- =============================================================================
INSERT INTO vouchers
  (id, code, description, discount_type, discount_value, min_order_amount, max_discount_amount,
   usage_limit, used_count, free_shipping, active, starts_at, expires_at, created_at) VALUES
(1,'GIAM100K',    'Giảm 100K cho đơn từ 500K',   'FIXED',   100000.00, 500000.00, NULL,      1000,  12, 0, 1, NOW() - INTERVAL 10 DAY, NOW() + INTERVAL 20 DAY, NOW()),
(2,'FREESHIP',    'Miễn phí vận chuyển',         'FIXED',        0.00,      0.00, NULL,      5000, 340, 1, 1, NOW() - INTERVAL 10 DAY, NOW() + INTERVAL 20 DAY, NOW()),
(3,'SALE10',      'Giảm 10% tối đa 50K',         'PERCENT',     10.00, 200000.00,  50000.00, 2000,  88, 0, 1, NOW() - INTERVAL 10 DAY, NOW() + INTERVAL 20 DAY, NOW()),
(4,'WELCOME50',   'Giảm 50K cho đơn đầu tiên',   'FIXED',    50000.00, 300000.00, NULL,     10000, 210, 0, 1, NOW() - INTERVAL 30 DAY, NOW() + INTERVAL 60 DAY, NOW()),
(5,'SALE20',      'Giảm 20% tối đa 200K',        'PERCENT',     20.00, 500000.00, 200000.00, 1500, 150, 0, 1, NOW() - INTERVAL  5 DAY, NOW() + INTERVAL 25 DAY, NOW()),
(6,'TET2026',     'Giảm 15% mừng năm mới',       'PERCENT',     15.00, 400000.00, 300000.00, 3000,   0, 0, 1, NOW() + INTERVAL  5 DAY, NOW() + INTERVAL 45 DAY, NOW()),
(7,'FREESHIPXTRA','Freeship đơn từ 150K',        'FIXED',        0.00, 150000.00, NULL,      8000, 900, 1, 1, NOW() - INTERVAL  3 DAY, NOW() + INTERVAL 30 DAY, NOW()),
(8,'EXPIRED10',   'Giảm 10% (đã hết hạn)',       'PERCENT',     10.00, 100000.00,  30000.00, 1000, 500, 0, 0, NOW() - INTERVAL 60 DAY, NOW() - INTERVAL 10 DAY, NOW());

-- =============================================================================
-- 7) REVIEWS  (UNIQUE product_id + user_id) — chỉ là MẪU đánh giá gần đây
-- =============================================================================
INSERT INTO reviews (id, product_id, user_id, rating, comment, created_at) VALUES
(1,  1,  3,  5, 'Máy đẹp, giao nhanh, đóng gói cẩn thận!',   NOW() - INTERVAL 6 DAY),
(2,  1,  4,  4, 'Pin trâu nhưng hơi nóng khi chơi game.',    NOW() - INTERVAL 5 DAY),
(3,  1,  8,  5, 'Chính hãng, quá ưng!',                      NOW() - INTERVAL 4 DAY),
(4,  2,  5,  5, 'Camera zoom cực đỉnh, rất hài lòng.',       NOW() - INTERVAL 4 DAY),
(5,  2,  9,  4, 'Máy mượt, pin ổn.',                         NOW() - INTERVAL 3 DAY),
(6,  3,  7,  4, 'Giá rẻ mà mượt bất ngờ.',                   NOW() - INTERVAL 7 DAY),
(7,  3,  10, 5, 'Pin dùng cả ngày, màn đẹp.',                NOW() - INTERVAL 6 DAY),
(8,  6,  4,  5, 'Mỏng nhẹ, chạy mượt, đáng tiền.',           NOW() - INTERVAL 3 DAY),
(9,  6,  11, 5, 'Làm việc cả ngày không nóng.',              NOW() - INTERVAL 2 DAY),
(10, 8,  9,  4, 'Chơi game phà phà, hơi ồn quạt.',           NOW() - INTERVAL 5 DAY),
(11, 15, 5,  4, 'Chống ồn tốt, đeo hơi chặt tai.',           NOW() - INTERVAL 2 DAY),
(12, 15, 3,  5, 'Âm thanh tuyệt vời, mua lần 2 rồi.',        NOW() - INTERVAL 1 DAY),
(13, 10, 4,  5, 'Vải mát, form đẹp, giá tốt.',               NOW() - INTERVAL 1 DAY),
(14, 10, 8,  4, 'Đúng size, giặt không xù.',                 NOW() - INTERVAL 2 DAY),
(15, 24, 9,  5, 'Cài Win nhanh hẳn, đáng mua.',              NOW() - INTERVAL 8 DAY),
(16, 24, 3,  5, 'Tốc độ tuyệt vời.',                         NOW() - INTERVAL 7 DAY),
(17, 26, 7,  4, 'Đi êm, form đẹp.',                          NOW() - INTERVAL 6 DAY),
(18, 26, 11, 5, 'Nhẹ, thoáng, đá bóng ok.',                  NOW() - INTERVAL 5 DAY),
(19, 30, 8,  5, 'Mặc lên sang, tôn dáng.',                   NOW() - INTERVAL 4 DAY),
(20, 30, 10, 4, 'Vải đẹp nhưng hơi ôm.',                     NOW() - INTERVAL 3 DAY),
(21, 31, 5,  4, 'Pin trâu, đo nhịp tim ổn.',                 NOW() - INTERVAL 5 DAY),
(22, 31, 9,  5, 'Đáng tiền trong tầm giá.',                  NOW() - INTERVAL 4 DAY),
(23, 38, 4,  5, 'Da sáng hẳn sau 2 tuần.',                   NOW() - INTERVAL 6 DAY),
(24, 38, 10, 5, 'Thấm nhanh, không nhờn.',                   NOW() - INTERVAL 5 DAY),
(25, 41, 7,  5, 'Dày, êm gối, không trượt.',                 NOW() - INTERVAL 3 DAY),
(26, 41, 3,  4, 'Mùi cao su nhẹ lúc đầu.',                   NOW() - INTERVAL 2 DAY),
(27, 42, 11, 5, 'Giữ nóng cả ngày, chắc chắn.',              NOW() - INTERVAL 4 DAY),
(28, 42, 8,  5, 'Đựng trà nóng 12h vẫn ấm.',                 NOW() - INTERVAL 3 DAY),
(29, 43, 10, 5, 'Sách hay, nên đọc.',                        NOW() - INTERVAL 9 DAY),
(30, 43, 5,  5, 'Bản dịch dễ hiểu.',                         NOW() - INTERVAL 8 DAY),
(31, 43, 4,  5, 'Giao nhanh, sách mới cứng.',                NOW() - INTERVAL 7 DAY),
(32, 47, 3,  5, 'Bé chịu uống, không táo bón.',              NOW() - INTERVAL 6 DAY),
(33, 23, 4,  5, 'Màn 2K mượt, chơi game đã.',                NOW() - INTERVAL 3 DAY),
(34, 36, 9,  5, 'Nước ngọt, lắp đặt nhanh.',                 NOW() - INTERVAL 5 DAY);

-- =============================================================================
-- 8) CARTS + CART_ITEMS  (UNIQUE cart_id + product_id, và mỗi user 1 giỏ)
-- =============================================================================
INSERT INTO carts (id, user_id, created_at) VALUES
(1, 4, NOW()),
(2, 5, NOW()),
(3, 7, NOW()),
(4, 8, NOW()),
(5, 9, NOW());

INSERT INTO cart_items (id, cart_id, product_id, quantity, created_at) VALUES
(1, 1, 2,  1, NOW()),   -- Trần Thị B: Galaxy S24 Ultra
(2, 1, 12, 2, NOW()),   -- Trần Thị B: 2 Váy liền thân
(3, 2, 6,  1, NOW()),   -- Lê Văn C: MacBook Air M3
(4, 3, 24, 1, NOW()),   -- Hoàng Văn E: SSD Samsung
(5, 3, 41, 2, NOW()),   -- Hoàng Văn E: 2 Thảm yoga
(6, 4, 38, 2, NOW()),   -- Đỗ Thị Thu F: 2 Serum Vitamin C
(7, 4, 20, 1, NOW()),   -- Đỗ Thị Thu F: Tai nghe TWS
(8, 5, 31, 1, NOW());   -- Bùi Văn G: Đồng hồ Xiaomi

-- =============================================================================
-- 9) ORDERS + ORDER_ITEMS
--    total_amount = subtotal + shipping_fee - discount_amount
--    status: PENDING|CONFIRMED|PAID|SHIPPING|DELIVERED|CANCELLED|REFUNDED
--    payment_method: COD|CARD|MOMO|VNPAY
-- =============================================================================
INSERT INTO orders
  (id, user_id, code, status, payment_method, subtotal, shipping_fee, discount_amount, voucher_id,
   total_amount, recipient_name, recipient_phone, shipping_address, note, created_at) VALUES
(1,  3,  'DH0001', 'DELIVERED', 'COD',   34990000.00,     0.00,      0.00, NULL, 34990000.00, 'Nguyễn Văn A','0911111111','123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh',                  NULL,                    NOW() - INTERVAL 12 DAY),
(2,  4,  'DH0002', 'SHIPPING',  'MOMO',    848000.00, 30000.00,  50000.00, 3,      828000.00, 'Trần Thị B','0922222222','78 Xuân Thủy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội',                'Giao giờ hành chính',   NOW() - INTERVAL 3 DAY),
(3,  5,  'DH0003', 'PENDING',   'VNPAY',  8490000.00,     0.00, 100000.00, 1,     8390000.00, 'Lê Văn C','0933333333','12 Bạch Đằng, Phường Thạch Thang, Quận Hải Châu, Đà Nẵng',               NULL,                    NOW() - INTERVAL 1 DAY),
(4,  3,  'DH0004', 'CANCELLED', 'COD',      320000.00, 20000.00,      0.00, NULL,   340000.00, 'Nguyễn Văn A','0911111111','123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh',                'Khách đổi ý',           NOW() - INTERVAL 8 DAY),
(5,  7,  'DH0005', 'DELIVERED', 'CARD',   1690000.00,     0.00,      0.00, NULL,  1690000.00, 'Hoàng Văn E','0955555555','9 Tô Hiệu, Phường An Biên, Quận Lê Chân, Hải Phòng',                    NULL,                    NOW() - INTERVAL 15 DAY),
(6,  8,  'DH0006', 'PAID',      'VNPAY',  1007000.00,     0.00,  50000.00, 4,      957000.00, 'Đỗ Thị Thu F','0966666666','56 Hòa Bình, Phường Tân An, Quận Ninh Kiều, Cần Thơ',                  'Gọi trước khi giao',    NOW() - INTERVAL 2 DAY),
(7,  9,  'DH0007', 'CONFIRMED', 'MOMO',   1428000.00, 25000.00, 200000.00, 5,     1253000.00, 'Bùi Văn G','0977777777','101 Tôn Thất Tùng, Phường Khương Thượng, Quận Đống Đa, Hà Nội',          NULL,                    NOW() - INTERVAL 1 DAY),
(8,  3,  'DH0008', 'SHIPPING',  'COD',      587000.00, 30000.00,      0.00, NULL,   617000.00, 'Nguyễn Văn A','0911111111','123 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh',                NULL,                    NOW() - INTERVAL 2 DAY),
(9,  10, 'DH0009', 'DELIVERED', 'CARD',     718000.00,     0.00,  50000.00, 3,      668000.00, 'Ngô Thị H','0988888888','200 Điện Biên Phủ, Phường 25, Quận Bình Thạnh, TP. Hồ Chí Minh',         NULL,                    NOW() - INTERVAL 20 DAY),
(10, 4,  'DH0010', 'PENDING',   'VNPAY',  6290000.00,     0.00,      0.00, NULL,  6290000.00, 'Trần Thị B','0922222222','78 Xuân Thủy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội',                 NULL,                    NOW() - INTERVAL 1 DAY),
(11, 11, 'DH0011', 'REFUNDED',  'MOMO',   1190000.00, 20000.00,      0.00, NULL,  1210000.00, 'Đặng Văn I','0999999990','33 Điện Biên Phủ, Phường Tân Chính, Quận Thanh Khê, Đà Nẵng',           'Hàng lỗi, đã hoàn tiền',NOW() - INTERVAL 10 DAY),
(12, 5,  'DH0012', 'DELIVERED', 'COD',      893000.00,     0.00,      0.00, 2,      893000.00, 'Lê Văn C','0933333333','12 Bạch Đằng, Phường Thạch Thang, Quận Hải Châu, Đà Nẵng',               NULL,                    NOW() - INTERVAL 18 DAY);

INSERT INTO order_items (id, order_id, product_id, product_name, unit_price, quantity, created_at) VALUES
-- Đơn 1
(1,  1,  1,  'iPhone 15 Pro Max 256GB',        34990000.00, 1, NOW() - INTERVAL 12 DAY),
-- Đơn 2 (Áo thun x2 + Quần jean x1)
(2,  2,  10, 'Áo thun nam cotton',               199000.00, 2, NOW() - INTERVAL 3 DAY),
(3,  2,  11, 'Quần jean nam slim-fit',           450000.00, 1, NOW() - INTERVAL 3 DAY),
-- Đơn 3
(4,  3,  15, 'Tai nghe Sony WH-1000XM5',        8490000.00, 1, NOW() - INTERVAL 1 DAY),
-- Đơn 4
(5,  4,  17, 'Son môi lì cao cấp',               320000.00, 1, NOW() - INTERVAL 8 DAY),
-- Đơn 5
(6,  5,  24, 'SSD Samsung 980 1TB',             1690000.00, 1, NOW() - INTERVAL 15 DAY),
-- Đơn 6 (Serum x2 + Tai nghe TWS x1)
(7,  6,  38, 'Serum dưỡng da Vitamin C',         279000.00, 2, NOW() - INTERVAL 2 DAY),
(8,  6,  20, 'Tai nghe Bluetooth TWS',           449000.00, 1, NOW() - INTERVAL 2 DAY),
-- Đơn 7 (Đồng hồ x1 + Bình giữ nhiệt x2)
(9,  7,  31, 'Đồng hồ thông minh Xiaomi',        990000.00, 1, NOW() - INTERVAL 1 DAY),
(10, 7,  42, 'Bình giữ nhiệt 1 lít',             219000.00, 2, NOW() - INTERVAL 1 DAY),
-- Đơn 8 (Sách x3 + Bút x2 + Thảm yoga x1)
(11, 8,  43, 'Sách Đắc Nhân Tâm',                 86000.00, 3, NOW() - INTERVAL 2 DAY),
(12, 8,  44, 'Bút bi Thiên Long (set 20)',        45000.00, 2, NOW() - INTERVAL 2 DAY),
(13, 8,  41, 'Thảm tập yoga TPE',                239000.00, 1, NOW() - INTERVAL 2 DAY),
-- Đơn 9 (Đầm x1 + Giày cao gót x1)
(14, 9,  30, 'Đầm công sở thanh lịch',           389000.00, 1, NOW() - INTERVAL 20 DAY),
(15, 9,  29, 'Giày cao gót 7cm',                 329000.00, 1, NOW() - INTERVAL 20 DAY),
-- Đơn 10
(16, 10, 23, 'Màn hình LG UltraGear 27"',       6290000.00, 1, NOW() - INTERVAL 1 DAY),
-- Đơn 11
(17, 11, 33, 'Máy hút bụi cầm tay',             1190000.00, 1, NOW() - INTERVAL 10 DAY),
-- Đơn 12 (Bỉm x2 + Sữa bột x1)
(18, 12, 46, 'Bỉm Bobby size L (62 miếng)',      269000.00, 2, NOW() - INTERVAL 18 DAY),
(19, 12, 47, 'Sữa bột Vinamilk 900g',            355000.00, 1, NOW() - INTERVAL 18 DAY);

-- =============================================================================
-- Đồng bộ lại AUTO_INCREMENT sau khi chèn ID tường minh
-- =============================================================================
ALTER TABLE categories      AUTO_INCREMENT = 100;
ALTER TABLE products        AUTO_INCREMENT = 100;
ALTER TABLE users           AUTO_INCREMENT = 100;
ALTER TABLE user_addresses  AUTO_INCREMENT = 100;
ALTER TABLE vouchers        AUTO_INCREMENT = 100;
ALTER TABLE reviews         AUTO_INCREMENT = 100;
ALTER TABLE carts           AUTO_INCREMENT = 100;
ALTER TABLE cart_items      AUTO_INCREMENT = 100;
ALTER TABLE orders          AUTO_INCREMENT = 100;
ALTER TABLE order_items     AUTO_INCREMENT = 100;
