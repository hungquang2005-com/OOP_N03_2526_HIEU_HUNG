const API_BASE_URL = 'http://localhost:8082/api';
let editMenuModal; 
let revenueChartInstance = null;

document.addEventListener('DOMContentLoaded', () => {
  loadAdminMenu();
  loadAdminUsers();

  const activePage = localStorage.getItem('adminActivePage') || 'menu';
  showAdminPage(activePage); 

  const formAddMenu = document.getElementById('form-add-menu');
  if(formAddMenu) formAddMenu.addEventListener('submit', handleAddMenu);

  const editModalElement = document.getElementById('editMenuModal');
  if(editModalElement) editMenuModal = new bootstrap.Modal(editModalElement);
  
  const formEditMenu = document.getElementById('form-edit-menu');
  if(formEditMenu) formEditMenu.addEventListener('submit', handleUpdateMenu);

  const today = new Date().toISOString().split('T')[0];
  const datePicker = document.getElementById('stats-date-picker');
  if(datePicker) datePicker.value = today;
  
  const periodSelect = document.getElementById('stats-period-select');
  if(periodSelect) periodSelect.addEventListener('change', handleStatsChange);
  if(datePicker) datePicker.addEventListener('change', handleStatsChange);
  
  if (activePage === 'stats') {
      handleStatsChange();
  }
});


function showAdminPage(pageId) {
  document.querySelectorAll('.page').forEach(page => {
    page.classList.remove('active');
  });
  
  const targetPage = document.getElementById(`page-${pageId}`);
  if (targetPage) {
    targetPage.classList.add('active');
  }
  
  localStorage.setItem('adminActivePage', pageId); 

  if (pageId === 'stats') {
    handleStatsChange();
  }
}


// ===============================================
// 1. QUẢN LÝ THỰC ĐƠN (BỎ CATEGORY)
// ===============================================

async function loadAdminMenu() {
  const menuListBody = document.getElementById('menu-list-body');
  if (!menuListBody) return;

  try {
    const response = await fetch(`${API_BASE_URL}/menu`);
    const menuItems = await response.json();

    menuListBody.innerHTML = ''; 

    menuItems.forEach(item => {
      // ✅ BỎ CATEGORY TRONG BẢNG
      const row = `
        <tr>
          <td>${item.foodID}</td> 
          <td>${item.name}</td>
          <td>${item.price.toLocaleString('vi-VN')} VND</td>
          <td>${item.description || ''}</td>
          <td>${item.image ? `<a href="${item.image}" target="_blank">Xem ảnh</a>` : 'Không có'}</td>
          <td>
            <button class="btn btn-warning btn-sm me-2" 
                    onclick="openEditMenuModal('${item.foodID}', '${item.name}', ${item.price}, '${item.description || ''}', '${item.image || ''}')">
              Sửa
            </button>
            <button class="btn btn-danger btn-sm" onclick="handleDeleteMenu('${item.foodID}', '${item.name}')">Xóa</button>
          </td>
        </tr>
      `;
      menuListBody.innerHTML += row;
    });

  } catch (error) {
    console.error('Lỗi tải thực đơn:', error);
    menuListBody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Không thể tải thực đơn. Vui lòng kiểm tra API.</td></tr>';
  }
}

// ✅ BỎ CATEGORY TRONG FORM THÊM MÓN
async function handleAddMenu(e) {
  e.preventDefault();
  
  const foodName = document.getElementById('add-foodName').value.trim();
  const description = document.getElementById('add-description').value.trim();
  const price = document.getElementById('add-price').value.trim();
  const image = document.getElementById('add-image-url').value.trim(); 

  if (!foodName || !price) {
      alert('Tên món và Giá không được để trống.');
      return;
  }
  
  const payload = { 
      name: foodName, 
      description: description, 
      price: parseFloat(price), 
      image: image 
  };
  
  if (isNaN(payload.price) || payload.price <= 0) {
       alert('Giá phải là một số hợp lệ và lớn hơn 0.');
       return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/menu`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      alert('Thêm món ăn thành công!');
      document.getElementById('form-add-menu').reset();
      loadAdminMenu(); 
    } else {
      const errorData = await response.text();
      alert(`Lỗi khi thêm món: ${errorData}`);
      console.error('API Error:', errorData);
    }
  } catch (error) {
    console.error('Lỗi thêm món ăn:', error);
    alert('Lỗi kết nối server khi thêm món ăn.');
  }
}

// ✅ BỎ CATEGORY TRONG FORM SỬA MÓN
async function handleUpdateMenu(e) {
    e.preventDefault();
    
    const foodID = document.getElementById('edit-foodID').value; 
    const foodName = document.getElementById('edit-foodName').value.trim();
    const description = document.getElementById('edit-description').value.trim();
    const price = document.getElementById('edit-price').value.trim();
    const image = document.getElementById('edit-image-url').value.trim();

    const payload = { 
        name: foodName, 
        description: description, 
        price: parseFloat(price), 
        image: image 
    };

    if (isNaN(payload.price) || payload.price <= 0) {
       alert('Giá phải là một số hợp lệ và lớn hơn 0.');
       return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/menu/${foodID}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert('Cập nhật món ăn thành công!');
            editMenuModal.hide(); 
            loadAdminMenu(); 
        } else {
            const errorData = await response.text();
            alert(`Lỗi khi cập nhật món: ${errorData}`);
        }
    } catch (error) {
        console.error('Lỗi cập nhật món ăn:', error);
        alert('Lỗi kết nối server khi cập nhật món ăn.');
    }
}

// ✅ BỎ CATEGORY TRONG MODAL
function openEditMenuModal(foodID, name, price, description, image) {
    document.getElementById('edit-foodID').value = foodID; 
    document.getElementById('edit-foodName').value = name;
    document.getElementById('edit-price').value = price;
    document.getElementById('edit-description').value = description;
    document.getElementById('edit-image-url').value = image || ''; 
    
    editMenuModal.show();
}

async function handleDeleteMenu(foodID, foodName) {
    if (!confirm(`Bạn có chắc chắn muốn xóa món "${foodName}"?`)) return;

    try {
        const response = await fetch(`${API_BASE_URL}/menu/${foodID}`, { 
            method: 'DELETE'
        });

        if (response.ok) {
            alert('Xóa món ăn thành công!');
            loadAdminMenu(); 
        } else {
            const errorData = await response.text();
            alert(`Lỗi khi xóa món: ${errorData}`);
        }
    } catch (error) {
        console.error('Lỗi xóa món ăn:', error);
        alert('Lỗi kết nối server khi xóa món ăn.');
    }
}


// ===============================================
// LOGIC: KIỂM TRA TÌNH TRẠNG HOẠT ĐỘNG
// ===============================================

function checkInactivity(lastLoginDate, isEnabled) {
    
    if (!isEnabled) {
        return { 
            status: '<span class="badge bg-danger">VÔ HIỆU HÓA</span>', 
            action: 'Kích hoạt lại', 
            displayDate: lastLoginDate ? new Date(lastLoginDate).toLocaleDateString('vi-VN') : 'N/A'
        };
    }
    
    if (!lastLoginDate) {
        return { 
            status: '<span class="badge bg-secondary">Chưa đăng nhập</span>', 
            action: 'Vô hiệu hóa', 
            displayDate: 'N/A'
        };
    }
    
    const lastLogin = new Date(lastLoginDate);
    const twoMonthsInMs = 60 * 24 * 60 * 60 * 1000; 
    const today = new Date();
    const displayDate = lastLogin.toLocaleDateString('vi-VN');

    if (today - lastLogin > twoMonthsInMs) {
        return { 
            status: '<span class="badge bg-warning text-dark">INACTIVE (> 60 ngày)</span>', 
            action: 'Vô hiệu hóa', 
            displayDate: displayDate
        };
    } else {
        return { 
            status: '<span class="badge bg-success">Hoạt động</span>', 
            action: 'Vô hiệu hóa', 
            displayDate: displayDate
        };
    }
}


// ===============================================
// 2. QUẢN LÝ NGƯỜI DÙNG
// ===============================================

async function loadAdminUsers() {
  const userListBody = document.getElementById('user-list-body');
  if (!userListBody) return;
  
  try {
    userListBody.innerHTML = '<tr><td colspan="5" class="text-center"><div class="spinner-border spinner-border-sm"></div> Đang tải...</td></tr>';

    const response = await fetch(`${API_BASE_URL}/users`);
    if (!response.ok) {
      throw new Error(`Lỗi ${response.status}: Không thể tải danh sách người dùng`);
    }
    const users = await response.json(); 

    userListBody.innerHTML = ''; 

    if (users.length === 0) {
        userListBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Không có người dùng nào.</td></tr>';
        return;
    }

    users.forEach(user => {
      const inactivityInfo = checkInactivity(user.lastLoginDate, user.enabled); 

      const row = `
        <tr>
          <td>${user.username}</td>
          <td>${user.role}</td>
          <td>${inactivityInfo.displayDate}</td> 
          <td>${inactivityInfo.status}</td> 
          <td>
            <button class="btn btn-sm btn-${user.enabled ? 'danger' : 'success'}" 
                    onclick="toggleUserStatus('${user.username}', ${!user.enabled})"
                    ${user.role === 'ADMIN' ? 'disabled' : ''}
                    title="Hành động: ${inactivityInfo.action}">
              ${user.enabled ? 'Vô hiệu hóa' : 'Kích hoạt lại'}
            </button>
          </td>
        </tr>
      `;
      userListBody.innerHTML += row;
    });
  } catch (error) {
    console.error('Lỗi tải người dùng:', error);
    userListBody.innerHTML = 
      `<tr><td colspan="5" class="text-center text-danger">${error.message}</td></tr>`;
  }
}

async function toggleUserStatus(username, newStatus) {
    if (username === 'admin') {
        alert("Không thể thay đổi trạng thái tài khoản Admin mặc định.");
        return;
    }
    
    const actionText = newStatus ? 'Kích hoạt lại' : 'Vô hiệu hóa';

    if (!confirm(`Bạn có chắc chắn muốn ${actionText} tài khoản "${username}" không?\n\nNgười dùng đang sử dụng dịch vụ sẽ bị tự động đăng xuất.`)) return;

    try {
        const response = await fetch(`${API_BASE_URL}/users/${username}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ enabled: newStatus })
        });

        if (response.ok) {
            alert(`Đã ${actionText} tài khoản ${username} thành công.`);
            loadAdminUsers();
        } else {
            const errorData = await response.text();
            alert(`Lỗi khi ${actionText} user: ${errorData}`);
        }
    } catch (error) {
        console.error(`Lỗi ${actionText} user:`, error);
        alert('Lỗi kết nối server.');
    }
}


// ===============================================
// 3. QUẢN LÝ THỐNG KÊ (STATS)
// ===============================================

function handleStatsChange() {
    const periodSelect = document.getElementById('stats-period-select');
    if (!periodSelect) return;
    
    const period = periodSelect.value;
    loadRevenueStats(period);
    loadPopularItems(period);
    loadLeastPopularItems(period);
    
    const datePickerContainer = document.getElementById('stats-date-picker-container');
    if (datePickerContainer) {
        if (period === 'daily' || period === 'monthly' || period === 'weekly') {
            datePickerContainer.classList.remove('d-none');
            const label = datePickerContainer.querySelector('label');
            if (period === 'monthly') {
                label.textContent = 'Chọn Tháng (Chọn ngày bất kỳ trong tháng)';
            } else if (period === 'weekly') {
                label.textContent = 'Chọn Ngày (Để xác định tuần)';
            } else {
                label.textContent = 'Chọn Ngày';
            }
        } else {
            datePickerContainer.classList.add('d-none');
        }
    }
}


async function loadRevenueStats(period) {
  const datePicker = document.getElementById('stats-date-picker');
  const date = datePicker ? datePicker.value : null; 
  const revenueTotalElement = document.getElementById('stats-revenue');
  const orderCountElement = document.getElementById('stats-orders');
  const revenueChartContainer = document.getElementById('revenueChartContainer');

  let url = `${API_BASE_URL}/stats/revenue?period=${period}`;
  
  if ((period === 'daily' || period === 'weekly') && date) {
      url += `&date=${date}`;
  } else if (period === 'monthly' && date) {
      const [year, month] = date.split('-').slice(0, 2); 
      url = `${API_BASE_URL}/stats/revenue?period=monthly&year=${year}&month=${month}`;
  }

  if (revenueTotalElement) revenueTotalElement.textContent = 'Đang tải...';
  if (orderCountElement) orderCountElement.textContent = 'Đang tải...';
  if (revenueChartContainer) revenueChartContainer.innerHTML = '<div class="text-center p-5"><div class="spinner-border text-primary"></div><p>Đang tải biểu đồ...</p></div>';

  try {
    const response = await fetch(url);
    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`API Error ${response.status}: ${errorText}`);
    }
    
    const data = await response.json(); 
    
    const total = data.totalRevenue || 0;
    const orderCount = data.orderCount || 0;
    
    if (revenueTotalElement) revenueTotalElement.textContent = total.toLocaleString('vi-VN') + ' VND';
    if (orderCountElement) orderCountElement.textContent = orderCount;
    
    // ✅ VẼ BIỂU ĐỒ TỪ chartData
    if (data.chartData && data.chartData.length > 0) {
        if (revenueChartContainer) revenueChartContainer.innerHTML = '<canvas id="revenueChart"></canvas>';
        drawRevenueChart(data.chartData, period); 
    } else {
        if (revenueChartContainer) revenueChartContainer.innerHTML = '<div class="text-center text-muted p-5">Không có dữ liệu chi tiết trong khoảng thời gian này.</div>';
        if (revenueChartInstance) {
            revenueChartInstance.destroy();
            revenueChartInstance = null;
        }
    }

  } catch (error) {
    console.error(`Lỗi tải thống kê doanh thu (${period}):`, error);
    if (revenueTotalElement) revenueTotalElement.textContent = 'Lỗi tải';
    if (orderCountElement) orderCountElement.textContent = 'Lỗi tải';
    if (revenueChartContainer) revenueChartContainer.innerHTML = `<div class="text-center text-danger p-5">❌ Lỗi tải biểu đồ doanh thu.<br><small>${error.message}</small></div>`;
  }
}

function drawRevenueChart(chartData, period) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;

    const labels = chartData.map(item => item.label); 
    const data = chartData.map(item => item.revenue);

    if (revenueChartInstance) {
        revenueChartInstance.destroy();
    }

    revenueChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu (VND)',
                data: data,
                backgroundColor: 'rgba(0, 123, 255, 0.5)',
                borderColor: 'rgba(0, 123, 255, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Doanh thu (VND)'
                    },
                    ticks: {
                        callback: function(value, index, ticks) {
                            return value.toLocaleString('vi-VN'); 
                        }
                    }
                }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                label += context.parsed.y.toLocaleString('vi-VN') + ' VND';
                            }
                            return label;
                        }
                    }
                }
            }
        }
    });
}


async function loadPopularItems(period) {
  const tableBody = document.getElementById('stats-popular-table');
  if (!tableBody) return;
  
  let url = `${API_BASE_URL}/stats/popular-items?period=${period}`;
  const date = document.getElementById('stats-date-picker').value;
  if ((period === 'daily' || period === 'weekly') && date) {
      url += `&date=${date}`;
  } else if (period === 'monthly' && date) {
      const [year, month] = date.split('-').slice(0, 2); 
      url += `&year=${year}&month=${month}`;
  }
  
  try {
    tableBody.innerHTML = '<tr><td colspan="2" class="text-center"><div class="spinner-border spinner-border-sm"></div></td></tr>';

    const response = await fetch(url);
    const data = await response.json();
    
    const items = data.items || data;
    
    if (items.length === 0) {
      tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-muted">Chưa có dữ liệu</td></tr>';
      return;
    }

    tableBody.innerHTML = '';
    items.forEach((item, index) => {
      let medal = '';
      if (index === 0) medal = '🥇';
      else if (index === 1) medal = '🥈';
      else if (index === 2) medal = '🥉';

      const row = `
        <tr>
          <td>${medal} ${item.foodName}</td>
          <td><span class="badge bg-success">${item.quantity}</span></td>
        </tr>
      `;
      tableBody.innerHTML += row;
    });

  } catch (error) {
    console.error('Lỗi tải món ăn bán chạy:', error);
    tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-danger">Lỗi tải dữ liệu</td></tr>';
  }
}

async function loadLeastPopularItems(period) {
  const tableBody = document.getElementById('stats-least-popular-table');
  if (!tableBody) return;

  let url = `${API_BASE_URL}/stats/least-popular-items?period=${period}`;
  const date = document.getElementById('stats-date-picker').value;
  if ((period === 'daily' || period === 'weekly') && date) {
      url += `&date=${date}`;
  } else if (period === 'monthly' && date) {
      const [year, month] = date.split('-').slice(0, 2); 
      url += `&year=${year}&month=${month}`;
  }
  
  try {
    tableBody.innerHTML = '<tr><td colspan="2" class="text-center"><div class="spinner-border spinner-border-sm"></div></td></tr>';

    const response = await fetch(url);
    const data = await response.json();
    
    const items = data.items || data;

    if (items.length === 0) {
      tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-muted">Chưa có dữ liệu</td></tr>';
      return;
    }

    tableBody.innerHTML = '';
    items.forEach(item => {
      const row = `
        <tr>
          <td>${item.foodName}</td>
          <td><span class="badge bg-warning text-dark">${item.quantity}</span></td>
        </tr>
      `;
      tableBody.innerHTML += row;
    });

  } catch (error) {
    console.error('Lỗi tải món ăn bán ít nhất:', error);
    tableBody.innerHTML = '<tr><td colspan="2" class="text-center text-danger">Lỗi tải dữ liệu</td></tr>';
  }
}