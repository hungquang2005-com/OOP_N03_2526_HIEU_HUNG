# 🍽️ ỨNG DỤNG ĐẶT BÀN & GỌI MÓN THÔNG MINH 🎯

---

## 1. Giới thiệu chung

**Ứng dụng Đặt bàn & Gọi món Thông minh** là hệ thống được phát triển nhằm hỗ trợ tự động hóa toàn bộ quy trình hoạt động của một nhà hàng hiện đại.  
Phần mềm cho phép **người dùng** dễ dàng đặt bàn, gọi món, theo dõi đơn hàng và thanh toán trực tuyến, đồng thời giúp **quản trị viên** quản lý toàn bộ dữ liệu nhà hàng như món ăn, bàn ăn, đơn hàng, giao dịch và thống kê doanh thu.

### 🎯 Mục tiêu chính:
- Cung cấp nền tảng quản lý tập trung, giảm thiểu thao tác thủ công.  
- Đảm bảo quá trình đặt bàn và gọi món được nhanh chóng, chính xác và thuận tiện.  
- Cung cấp hệ thống API RESTful có thể mở rộng, dễ tích hợp với các nền tảng web và mobile.  

### 💡 Công nghệ cốt lõi:
- **Ngôn ngữ lập trình:** Java  
- **Framework:** Spring Boot  
- **Kiến trúc hệ thống:** 3 tầng (Model – Repository – Controller)  
- **Cơ sở dữ liệu:** MySQL (sử dụng JPA Hibernate ORM)  
- **Quản lý dự án:** Maven  

---

## 2. Thành viên nhóm

| Họ và Tên | Mã số sinh viên | Vai trò |
|------------|-----------------|----------|
| **Nguyễn Quang Hưng** | 23010103 | lập trình , viết báo cáo, xây dựng và triển khai hệ
thống.  |
| **Lều Trung Hiếu** | 23010142 | Phân tích nghiệp vụ, xử lý logics và thiết kế mô hình cơ 
sở dữ liệu , viết readme. |

---

## 3. Thông tin dự án

- **GitHub Repository:** https://github.com/hungquang2005-com/OOP_N03_2526_HIEU_HUNG  
- **Video Demo:** *(Sẽ cập nhật sau khi hoàn thiện)*  
- **Công nghệ:** Java 17, Spring Boot, MySQL, JPA, Maven  

---

## 4. Mục tiêu và yêu cầu kỹ thuật

### 4.1. Mục tiêu chức năng:
Ứng dụng được thiết kế để đáp ứng các nghiệp vụ chính của một nhà hàng thông minh:
- Đặt bàn và theo dõi trạng thái bàn ăn.  
- Quản lý thực đơn và giá món ăn.  
- Gọi món trực tiếp qua hệ thống.  
- Xử lý thanh toán điện tử, lưu lịch sử giao dịch.  
- Thống kê doanh thu và đánh giá hiệu quả kinh doanh.  

### 4.2. Mục tiêu kỹ thuật:
- Xây dựng hệ thống **RESTful API** hoạt động ổn định, có khả năng mở rộng.  
- Ứng dụng **mô hình MVC (Model - View - Controller)** đảm bảo tách biệt rõ vai trò từng lớp.  
- Đảm bảo tính **bảo mật**, **toàn vẹn dữ liệu** và **hiệu năng cao**.  

---

## 5. Chức năng chính của hệ thống

### 🔐 5.1. Quản lý Người dùng & Xác thực
- Đăng ký, đăng nhập, phân quyền (Admin / User).  
- Quản lý thông tin tài khoản, cập nhật hồ sơ người dùng.  

### 🍜 5.2. Quản lý Thực đơn
- CRUD món ăn (thêm, sửa, xóa, xem chi tiết).  
- Phân loại và lọc món theo nhóm (món chính, tráng miệng, đồ uống).  

### 🪑 5.3. Quản lý Bàn ăn
- Đặt bàn, hủy bàn theo trạng thái.  
- Quản lý danh sách bàn và phân bổ khách.  

### 🧾 5.4. Quản lý Đơn hàng
- Tạo đơn hàng với nhiều món ăn.  
- Cập nhật trạng thái: *Đang chế biến, Đang giao, Hoàn thành.*  
- Lọc đơn theo khách hàng hoặc thời gian.  

### 💳 5.5. Quản lý Thanh toán
- Ghi nhận giao dịch thanh toán (tiền mặt / ví điện tử).  
- Lưu lịch sử thanh toán và in hóa đơn điện tử.  

### 📊 5.6. Báo cáo & Thống kê
- Thống kê doanh thu theo ngày, tuần, tháng.  
- Liệt kê món ăn phổ biến nhất.  

---

## 6. UML / Sequence & Màn hình thao tác

### 6.1. UML tổng quan hệ thống
![uml](./img/uml/uml.jpg)

---

### 6.2. CRUD Món ăn (Food)

- **Thêm món ăn**
  
  ![themmonan](./img/food/themmonan.jpg)

- **Xem thực đơn**
  
  ![xemthucdon](./img/food/xemthucdon.jpg)

- **Cập nhật món ăn**
  
  ![capnhapmonan](./img/food/capnhapmonan.jpg)

- **Xóa món ăn**
  
  ![xoamonan](./img/food/xoamonan.jpg)

---

### 6.3. CRUD Đơn hàng (Order)

- **Tạo đơn hàng**
  
  ![taodonhang](./img/order/taodonhang.jpg)

- **Xem lịch sử đơn hàng**
  
  ![xemlsdonhang](./img/order/xemlsdonhang.jpg)

- **Cập nhật trạng thái đơn**
  
  ![capnhaptrangthaidon](./img/order/capnhaptrangthaidon.jpg)

- **Xóa lịch sử đơn hàng**
  
  ![xoalsdonhang](./img/order/xoalsdonhang.jpg)

---

### 6.4. CRUD Thanh toán (Payment)

- **Cập nhật thông tin thanh toán**
  
  ![cap-nhap-thanh-toan](./img/payment/cap-nhap-thanh-toan.jpg)

- **Xem lịch sử thanh toán**
  
  ![xem-ls-thanh-toán](./img/payment/xem-ls-thanh-toán.jpg)

- **Xử lý thanh toán**
  
  ![xu-li-thanh-toan](./img/payment/xu-li-thanh-toan.jpg)

- **Xóa thanh toán**
  
  ![xoa-thanh-toan](./img/payment/xoa-thanh-toan.jpg)

---

### 6.5. CRUD Người dùng (User)

- **Đăng ký tài khoản**
  
  ![dki_taikhoan](./img/user/dki_taikhoan.jpg)

- **Xem danh sách người dùng**
  
  ![xem_ds_user](./img/user/xem_ds_user.jpg)

- **Vô hiệu hóa người dùng**
  
  ![vohieuhoa-user](./img/user/vohieuhoa-user.jpg)

- **Xóa người dùng**
  
  ![xoa-user](./img/user/xoa-user.jpg)

---

## 7. Quy trình chính / Use Case tổng quan
![quytrinhchinh](./img/quytrinhchinh/quytrinhchinh.jpg)
