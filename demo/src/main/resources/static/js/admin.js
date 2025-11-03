const API_BASE_URL = 'http://localhost:8082/api';
let editMenuModal; 

document.addEventListener('DOMContentLoaded', () => {
  loadAdminMenu();
  loadAdminUsers();

  showAdminPage('menu');

  document.getElementById('form-add-menu').addEventListener('submit', handleAddMenu);

  // ===============================================
  // KHỞI TẠO MODAL SỬA VÀ GẮN SỰ KIỆN (ĐÃ THÊM VÀO)
  // ===============================================
  editMenuModal = new bootstrap.Modal(document.getElementById('editMenuModal'));
  document.getElementById('form-edit-menu').addEventListener('submit', handleUpdateMenu);
});


function showAdminPage(pageId) {
  document.querySelectorAll('.page').forEach(page => {
    page.classList.remove('active');
  });
  document.getElementById(`page-${pageId}`).classList.add('active');
}


// ===============================================
// 1. QUẢN LÝ THỰC ĐƠN (MENU)
// ===============================================

async function loadAdminMenu() {
  try {
    const response = await fetch(`${API_BASE_URL}/menu`);
    const menuItems = await response.json();

    const menuListBody = document.getElementById('menu-list-body');
    menuListBody.innerHTML = ''; 

    menuItems.forEach(item => {
      const row = `
        <tr>
          <td>${item.foodID}</td> <td>${item.name}</td>
          <td>${item.price.toLocaleString()} VND</td>
          <td>
            <button class="btn btn-sm btn-primary me-2" onclick="showEditMenuModal(${item.foodID})">
              Sửa
            </button>
            <button class="btn btn-sm btn-danger" onclick="deleteMenu(${item.foodID})">
              Xóa
            </button>
          </td>
        </tr>
      `;
      menuListBody.innerHTML += row;
    });
  } catch (error) {
    console.error('Lỗi tải thực đơn:', error);
  }
}

async function handleAddMenu(event) {
  event.preventDefault();

  const food = {
    name: document.getElementById('menu-name').value,
    price: parseFloat(document.getElementById('menu-price').value),
    description: document.getElementById('menu-description').value
  };

  try {
    const response = await fetch(`${API_BASE_URL}/menu`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(food)
    });

    if (response.ok) {
      alert('Thêm món ăn thành công!');
      document.getElementById('form-add-menu').reset();
      loadAdminMenu(); 
    } else {
      alert('Lỗi: ' + await response.text());
    }
  } catch (error) {
    console.error('Lỗi thêm món ăn:', error);
  }
}

async function deleteMenu(id) {
  if (!confirm(`Bạn có chắc muốn xóa món ăn ID: ${id}?`)) {
    return; 
  }

  try {
    const response = await fetch(`${API_BASE_URL}/menu/${id}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      alert('Xóa món ăn thành công!');
      loadAdminMenu(); 
    } else {
      alert('Lỗi: ' + await response.text());
    }
  } catch (error) {
    console.error('Lỗi xóa món ăn:', error);
  }
}

// ===============================================
// CHỨC NĂNG SỬA MÓN ĂN (UPDATE) - ĐÃ THÊM VÀO
// ===============================================

async function showEditMenuModal(id) {
  try {
    const response = await fetch(`${API_BASE_URL}/menu/${id}`);
    if (!response.ok) {
      throw new Error('Không tìm thấy món ăn');
    }
    const item = await response.json();

    document.getElementById('edit-menu-id').value = item.foodID;
    document.getElementById('edit-menu-name').value = item.name;
    document.getElementById('edit-menu-price').value = item.price;
    document.getElementById('edit-menu-description').value = item.description;

    editMenuModal.show();

  } catch (error) {
    console.error('Lỗi tải thông tin món ăn:', error);
    alert('Lỗi: ' + error.message);
  }
}

async function handleUpdateMenu(event) {
  event.preventDefault();

  const id = document.getElementById('edit-menu-id').value;
  const updatedFood = {
    foodID: parseInt(id),
    name: document.getElementById('edit-menu-name').value,
    price: parseFloat(document.getElementById('edit-menu-price').value),
    description: document.getElementById('edit-menu-description').value
  };

  try {
    const response = await fetch(`${API_BASE_URL}/menu/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedFood)
    });

    if (response.ok) {
      alert('Cập nhật món ăn thành công!');
      editMenuModal.hide(); 
      loadAdminMenu(); 
    } else {
      alert('Lỗi: ' + await response.text());
    }
  } catch (error) {
    console.error('Lỗi cập nhật món ăn:', error);
  }
}

// ===============================================
// 2. QUẢN LÝ NGƯỜI DÙNG (USER)
// ===============================================

async function loadAdminUsers() {
  try {
    const response = await fetch(`${API_BASE_URL}/users`);
    if (!response.ok) {
      throw new Error(`Lỗi ${response.status}: Không thể tải danh sách người dùng. Backend có thể đang thiếu API /api/users`);
    }
    const users = await response.json();

    const userListBody = document.getElementById('user-list-body');
    userListBody.innerHTML = '';

    if (users.length === 0) {
        userListBody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">Không có người dùng nào.</td></tr>';
        return;
    }

    users.forEach(user => {
      const row = `
        <tr>
          <td>${user.username}</td>
          <td>${user.role}</td>
          <td>
            <button class="btn btn-sm btn-danger" onclick="deleteUser('${user.username}')">
              Xóa
            </button>
          </td>
        </tr>
      `;
      userListBody.innerHTML += row;
    });
  } catch (error) {
    console.error('Lỗi tải người dùng:', error);
    document.getElementById('user-list-body').innerHTML = `<tr><td colspan="3" class="text-center text-danger">${error.message}</td></tr>`;
  }
}

async function deleteUser(username) {
  if (!confirm(`Bạn có chắc muốn xóa người dùng: ${username}?`)) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/users/${username}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      alert('Xóa người dùng thành công!');
      loadAdminUsers(); 
    } else {
      alert('Lỗi: ' + await response.text());
    }
  } catch (error) {
    console.error('Lỗi xóa người dùng:', error);
  }
}

