// API Configuration
const API_BASE_URL = 'http://localhost:8082/api';

let currentUser = null;
let currentOrder = [];
let selectedPaymentMethod = null;
let discountApplied = 0;
let discountCode = '';
let orderHistory = [];
let pendingOrders = [];
let menuData = [];
let createdOrderId = null;
let orderType = null;
let occupiedTables = new Set(); // Lưu danh sách bàn đang được sử dụng

// Get current day discount
function getDayDiscount() {
  const now = new Date();
  const day = now.getDay();
  
  if (day === 1) {
    return {
      active: true,
      percent: 5,
      message: '🎉 Ưu đãi thứ 2: Giảm 5% tất cả món ăn!'
    };
  }
  return {
    active: false,
    percent: 0,
    message: ''
  };
}

// Check API connection on load
async function checkAPIConnection() {
  try {
    const response = await fetch(`${API_BASE_URL}/menu`);
    if (response.ok) {
      document.getElementById('apiStatus').innerHTML = '✅ Đã kết nối Backend';
      document.getElementById('apiStatus').className = 'text-success';
      return true;
    }
  } catch (error) {
    document.getElementById('apiStatus').innerHTML = '❌ Không kết nối được Backend';
    document.getElementById('apiStatus').className = 'text-danger';
    console.error('API Connection Error:', error);
  }
  return false;
}

// Load menu from API
async function loadMenuFromAPI() {
  const loadingEl = document.getElementById('loadingMenu');
  const menuContainer = document.getElementById('menuContainer');
  
  try {
    loadingEl.style.display = 'block';
    const response = await fetch(`${API_BASE_URL}/menu`);
    
    if (!response.ok) {
      throw new Error('Failed to load menu');
    }
    
    menuData = await response.json();
    loadingEl.style.display = 'none';
    
    displayMenu();
  } catch (error) {
    loadingEl.style.display = 'none';
    menuContainer.innerHTML = `
      <div class="col-12 text-center">
        <div class="alert alert-warning">
          <h5>⚠️ Không thể tải thực đơn từ server</h5>
          <p>Vui lòng kiểm tra kết nối Backend hoặc thử lại sau.</p>
        </div>
      </div>
    `;
    console.error('Menu Loading Error:', error);
  }
}

function displayMenu() {
  const menuContainer = document.getElementById('menuContainer');
  const orderMenuItems = document.getElementById('orderMenuItems');
  
  const dayDiscount = getDayDiscount();
  
  let menuHTML = '';
  let orderHTML = '';
  
  if (dayDiscount.active) {
    menuHTML += `
      <div class="col-12 mb-4">
        <div class="alert alert-info text-center">
          <h5 class="mb-0">${dayDiscount.message}</h5>
        </div>
      </div>
    `;
  }
  
  menuData.forEach(item => {
    const imageUrl = `https://images.unsplash.com/photo-${Math.random() > 0.5 ? '1585032226651-759b368d7246' : '1582878826629-29b7ad1cdc43'}?w=400`;
    
    const originalPrice = item.price;
    const discountedPrice = dayDiscount.active ? Math.floor(originalPrice * (1 - dayDiscount.percent / 100)) : originalPrice;
    const hasDayDiscount = dayDiscount.active && discountedPrice < originalPrice;
    
    menuHTML += `
      <div class="col-md-6 col-lg-4 mb-4">
        <div class="menu-item">
          <img src="${imageUrl}" alt="${item.name}" class="food-image">
          <h3>${item.name}${hasDayDiscount ? '<span class="discount-badge">-' + dayDiscount.percent + '%</span>' : ''}</h3>
          <p class="text-muted">${item.description}</p>
          <div class="d-flex justify-content-between align-items-center">
            <div>
              ${hasDayDiscount ? `<h5 class="text-muted mb-0"><del>${originalPrice.toLocaleString()} VND</del></h5>` : ''}
              <h4 class="text-primary mb-0">${discountedPrice.toLocaleString()} VND</h4>
            </div>
            <button class="btn btn-primary btn-sm" onclick="showPage('order')">Đặt món</button>
          </div>
        </div>
      </div>
    `;
    
    orderHTML += `
      <div class="order-item">
        <img src="${imageUrl}" alt="${item.name}">
        <div class="flex-grow-1">
          <strong>${item.name}${hasDayDiscount ? '<span class="discount-badge">-' + dayDiscount.percent + '%</span>' : ''}</strong><br>
          <small class="text-muted">${item.description}</small><br>
          ${hasDayDiscount ? `<span class="text-muted"><del>${originalPrice.toLocaleString()} VND</del></span><br>` : ''}
          <span class="text-primary">${discountedPrice.toLocaleString()} VND</span>
        </div>
        <div class="d-flex gap-2 align-items-center">
          <input type="number" class="form-control" style="width: 80px" min="0" value="0" 
                 data-id="${item.foodID}" data-name="${item.name}" data-price="${discountedPrice}">
          <button class="btn btn-primary btn-sm" onclick="addToOrder(this)">Thêm</button>
        </div>
      </div>
    `;
  });
  
  menuContainer.innerHTML = menuHTML;
  orderMenuItems.innerHTML = orderHTML;
}

function showPage(pageId) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.getElementById(pageId + 'Page').classList.add('active');
  window.scrollTo(0, 0);
  
  if (pageId === 'menu') {
    if (menuData.length === 0) {
      loadMenuFromAPI();
    }
  }
  
  if (pageId === 'pending') {
    displayPendingOrders();
  }
  
  if (pageId === 'history') {
    displayOrderHistory();
  }
}

function selectOrderType(type) {
  orderType = type;
  
  document.querySelectorAll('.order-type-card').forEach(card => {
    card.classList.remove('selected');
  });
  
  event.currentTarget.classList.add('selected');
  
  if (type === 'dine-in') {
    document.getElementById('tableInfoCard').style.display = 'block';
    document.getElementById('customerInfoCard').style.display = 'none';
    document.getElementById('orderButtonText').textContent = 'Thêm vào Chờ Thanh Toán';
    document.getElementById('reviewButtonText').textContent = 'Thêm vào Chờ Thanh Toán';
  } else {
    document.getElementById('tableInfoCard').style.display = 'none';
    document.getElementById('customerInfoCard').style.display = 'block';
    document.getElementById('orderButtonText').textContent = 'Xem Lại & Thanh Toán';
    document.getElementById('reviewButtonText').textContent = 'Xác nhận & Thanh Toán';
  }
}

async function register(e) {
  e.preventDefault();
  const username = document.getElementById('regUsername').value;
  const password = document.getElementById('regPassword').value;
  const confirmPassword = document.getElementById('regPasswordConfirm').value;
  const role = document.getElementById('regRole').value;

  if (password !== confirmPassword) {
    alert('Mật khẩu không khớp!');
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username,
        password: password,
        confirmPassword: confirmPassword,
        role: role
      })
    });

    if (response.ok) {
      const message = await response.text();
      alert('✅ ' + message);
      showPage('login');
    } else {
      const error = await response.text();
      alert('❌ ' + error);
    }
  } catch (error) {
    console.error('Register Error:', error);
    alert('❌ Không thể kết nối đến server. Vui lòng kiểm tra Backend.');
  }
}

async function login(e) {
  e.preventDefault();
  const username = document.getElementById('loginUsername').value;
  const password = document.getElementById('loginPassword').value;

  try {
    const response = await fetch(`${API_BASE_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username,
        password: password
      })
    });

    if (response.ok) {
      currentUser = await response.json();
      document.getElementById('authSection').style.display = 'none';
      document.getElementById('userSection').style.display = 'flex';
      document.getElementById('username').textContent = currentUser.username;
      alert('✅ Đăng nhập thành công! Chào ' + currentUser.username);
      showPage('home');
    } else {
      alert('❌ Sai username hoặc password!');
    }
  } catch (error) {
    console.error('Login Error:', error);
    alert('❌ Không thể kết nối đến server. Vui lòng kiểm tra Backend.');
  }
}

function logout() {
  currentUser = null;
  document.getElementById('authSection').style.display = 'flex';
  document.getElementById('userSection').style.display = 'none';
  currentOrder = [];
  discountApplied = 0;
  orderType = null;
  document.querySelectorAll('.order-type-card').forEach(card => {
    card.classList.remove('selected');
  });
  document.getElementById('tableInfoCard').style.display = 'none';
  document.getElementById('customerInfoCard').style.display = 'none';
  showPage('home');
}

function addToOrder(btn) {
  const input = btn.previousElementSibling;
  const quantity = parseInt(input.value);
  if (quantity <= 0) {
    alert('Vui lòng nhập số lượng!');
    return;
  }

  const id = parseInt(input.dataset.id);
  const name = input.dataset.name;
  const price = parseInt(input.dataset.price);
  
  const existingItem = currentOrder.find(o => o.foodId === id);
  if (existingItem) {
    existingItem.quantity += quantity;
  } else {
    currentOrder.push({ foodId: id, name, price, quantity });
  }

  updateOrderSummary();
  alert(`✅ Đã thêm ${quantity} x ${name}`);
  input.value = 0;
}

function updateOrderSummary() {
  const orderList = document.getElementById('orderList');
  let html = '';
  let subtotal = 0;

  if (currentOrder.length === 0) {
    html = '<p class="text-muted text-center">Chưa có món nào</p>';
  } else {
    currentOrder.forEach(order => {
      const itemTotal = order.price * order.quantity;
      subtotal += itemTotal;
      html += `
        <div class="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom">
          <div>
            <div><strong>${order.name}</strong></div>
            <small class="text-muted">${order.quantity} x ${order.price.toLocaleString()}</small>
          </div>
          <div class="text-end">
            <div>${itemTotal.toLocaleString()} VND</div>
            <button class="btn btn-sm btn-link text-danger p-0" onclick="removeFromOrder(${order.foodId})">Xóa</button>
          </div>
        </div>
      `;
    });
  }

  orderList.innerHTML = html;
  document.getElementById('subtotalAmount').textContent = subtotal.toLocaleString() + ' VND';
  
  const discount = Math.floor(subtotal * discountApplied / 100);
  const total = subtotal - discount;
  
  document.getElementById('discountAmount').textContent = '-' + discount.toLocaleString() + ' VND';
  document.getElementById('totalAmount').textContent = total.toLocaleString() + ' VND';
}

function removeFromOrder(id) {
  currentOrder = currentOrder.filter(o => o.foodId !== id);
  updateOrderSummary();
}

function applyDiscount() {
  const code = document.getElementById('discountCode').value.trim().toUpperCase();
  const message = document.getElementById('discountMessage');
  
  if (!code) {
    message.textContent = '';
    return;
  }
  
  if (code === 'DISCOUNT10') {
    discountApplied = 10;
    discountCode = code;
    message.textContent = `✅ Áp dụng mã giảm ${discountApplied}% thành công!`;
    message.className = 'text-success';
    updateOrderSummary();
  } else if (code === 'DISCOUNT20') {
    discountApplied = 20;
    discountCode = code;
    message.textContent = `✅ Áp dụng mã giảm ${discountApplied}% thành công!`;
    message.className = 'text-success';
    updateOrderSummary();
  } else {
    message.textContent = '❌ Mã giảm giá không hợp lệ!';
    message.className = 'text-danger';
    discountApplied = 0;
    discountCode = '';
    updateOrderSummary();
  }
}

function reviewOrder() {
  if (currentOrder.length === 0) {
    alert('Vui lòng chọn món trước!');
    return;
  }

  if (!orderType) {
    alert('Vui lòng chọn loại đơn hàng (Ăn tại chỗ hoặc Mang về)!');
    return;
  }

  if (orderType === 'dine-in') {
    const tableId = document.getElementById('tableId').value;
    const guestCount = document.getElementById('guestCount').value;
    
    if (!tableId || !guestCount) {
      alert('Vui lòng điền đầy đủ thông tin đặt bàn!');
      return;
    }

    // Kiểm tra bàn đã có người chưa
    if (occupiedTables.has(parseInt(tableId))) {
      alert(`❌ Bàn ${tableId} đang có khách! Vui lòng chọn bàn khác.`);
      return;
    }

    document.getElementById('reviewTableInfo').innerHTML = `
      <p><strong>Loại:</strong> Ăn tại chỗ</p>
      <p><strong>Bàn số:</strong> ${tableId}</p>
      <p><strong>Số khách:</strong> ${guestCount} người</p>
    `;
  } else {
    const name = document.getElementById('customerName').value;
    const phone = document.getElementById('customerPhone').value;
    
    if (!name || !phone) {
      alert('Vui lòng điền đầy đủ thông tin khách hàng!');
      return;
    }

    document.getElementById('reviewTableInfo').innerHTML = `
      <p><strong>Loại:</strong> Mang về</p>
      <p><strong>Khách hàng:</strong> ${name}</p>
      <p><strong>SĐT:</strong> ${phone}</p>
    `;
  }

  let html = '';
  let subtotal = 0;
  
  currentOrder.forEach(order => {
    const itemTotal = order.price * order.quantity;
    subtotal += itemTotal;
    html += `
      <div class="d-flex justify-content-between mb-2">
        <span>${order.quantity} x ${order.name}</span>
        <span>${itemTotal.toLocaleString()} VND</span>
      </div>
    `;
  });

  document.getElementById('reviewOrderList').innerHTML = html;
  
  const discount = Math.floor(subtotal * discountApplied / 100);
  const total = subtotal - discount;
  
  document.getElementById('reviewSubtotal').textContent = subtotal.toLocaleString() + ' VND';
  document.getElementById('reviewDiscount').textContent = '-' + discount.toLocaleString() + ' VND';
  document.getElementById('reviewTotal').textContent = total.toLocaleString() + ' VND';
  
  showPage('review');
}

async function proceedToPayment() {
  if (orderType === 'dine-in') {
    await addToPendingOrders();
  } else {
    await createOrderAndPay();
  }
}

async function addToPendingOrders() {
  const tableId = parseInt(document.getElementById('tableId').value);
  const guestCount = parseInt(document.getElementById('guestCount').value);
  
  // Kiểm tra lại bàn trước khi thêm vào pending
  if (occupiedTables.has(tableId)) {
    alert(`❌ Bàn ${tableId} đang có khách! Vui lòng chọn bàn khác.`);
    return;
  }
  
  const now = new Date();
  const orderId = Math.floor(Math.random() * 1000000);
  
  let subtotal = 0;
  currentOrder.forEach(order => {
    subtotal += order.price * order.quantity;
  });
  
  const discount = Math.floor(subtotal * discountApplied / 100);
  const total = subtotal - discount;
  
  const pendingOrder = {
    orderId: orderId,
    type: 'dine-in',
    tableId: tableId,
    guestCount: guestCount,
    items: [...currentOrder],
    subtotal: subtotal,
    discount: discount,
    discountPercent: discountApplied,
    discountCode: discountCode,
    total: total,
    date: now.toLocaleDateString('vi-VN'),
    time: now.toLocaleTimeString('vi-VN'),
    status: 'Chờ thanh toán'
  };
  
  pendingOrders.push(pendingOrder);
  
  // Đánh dấu bàn đã có người
  occupiedTables.add(tableId);
  
  alert(`✅ Đã thêm đơn hàng bàn ${tableId} vào danh sách chờ thanh toán!`);
  
  currentOrder = [];
  discountApplied = 0;
  discountCode = '';
  orderType = null;
  document.getElementById('discountCode').value = '';
  document.getElementById('discountMessage').textContent = '';
  document.querySelectorAll('.order-type-card').forEach(card => {
    card.classList.remove('selected');
  });
  document.getElementById('tableInfoCard').style.display = 'none';
  
  updateOrderSummary();
  showPage('pending');
}

async function createOrderAndPay() {
  const name = document.getElementById('customerName').value;
  const phone = document.getElementById('customerPhone').value;
  
  try {
    const items = currentOrder.map(item => ({
      foodId: item.foodId,
      quantity: item.quantity
    }));

    const orderResponse = await fetch(`${API_BASE_URL}/orders`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        items: items,
        discountCode: discountCode || null
      })
    });

    if (!orderResponse.ok) {
      throw new Error('Failed to create order');
    }

    const orderData = await orderResponse.json();
    createdOrderId = orderData.orderId;
    
    document.getElementById('paymentTotal').textContent = orderData.total.toLocaleString() + ' VND';
    document.getElementById('displayOrderId').textContent = '#' + orderData.orderId;
    
    alert(`✅ Đã tạo đơn mang về cho khách hàng ${name}`);
    showPage('payment');
    
  } catch (error) {
    console.error('Order Creation Error:', error);
    alert('❌ Không thể tạo đơn hàng. Vui lòng thử lại.');
  }
}

function payPendingOrder(orderId) {
  const order = pendingOrders.find(o => o.orderId === orderId);
  if (!order) return;
  
  createdOrderId = order.orderId;
  document.getElementById('paymentTotal').textContent = order.total.toLocaleString() + ' VND';
  document.getElementById('displayOrderId').textContent = '#' + order.orderId;
  
  showPage('payment');
}

function selectPayment(method) {
  document.getElementById('qrCodeSection').style.display = 'none';
  document.getElementById('cardPaymentSection').style.display = 'none';
  
  document.querySelectorAll('.payment-method').forEach(m => m.classList.remove('selected'));
  
  document.querySelector(`[data-method="${method}"]`).classList.add('selected');
  selectedPaymentMethod = method;
  
  if (method === 'qr') {
    showQRCode();
  } else if (method === 'card') {
    document.getElementById('cardPaymentSection').style.display = 'block';
  }
}

function showQRCode() {
  const qrSection = document.getElementById('qrCodeSection');
  qrSection.style.display = 'block';
  
  const orderId = createdOrderId || Math.floor(Math.random() * 10000);
  const total = document.getElementById('paymentTotal').textContent;
  const content = `NH VIET ${orderId} ${total}`;
  
  document.getElementById('qrContent').textContent = content;
  
  const qrContainer = document.getElementById('qrcode');
  qrContainer.innerHTML = '';
  
  new QRCode(qrContainer, {
    text: content,
    width: 200,
    height: 200,
    colorDark: "#000000",
    colorLight: "#ffffff",
    correctLevel: QRCode.CorrectLevel.H
  });
}

async function completePayment() {
  if (!selectedPaymentMethod) {
    alert('Vui lòng chọn phương thức thanh toán!');
    return;
  }

  if (selectedPaymentMethod === 'card') {
    const bank = document.getElementById('bankSelect').value;
    const cardNumber = document.getElementById('cardNumber').value;
    const cardExpiry = document.getElementById('cardExpiry').value;
    const cardCVV = document.getElementById('cardCVV').value;
    const cardName = document.getElementById('cardName').value;
    
    if (!bank || !cardNumber || !cardExpiry || !cardCVV || !cardName) {
      alert('Vui lòng điền đầy đủ thông tin thẻ!');
      return;
    }
  }

  const methods = {
    'cash': 'Tiền mặt',
    'qr': 'QR Code',
    'card': 'Thẻ tín dụng'
  };

  try {
    const pendingOrder = pendingOrders.find(o => o.orderId === createdOrderId);
    
    let totalAmount;
    let orderData;
    
    if (pendingOrder) {
      totalAmount = pendingOrder.total;
      orderData = {
        orderId: pendingOrder.orderId,
        type: pendingOrder.type,
        tableId: pendingOrder.tableId,
        guestCount: pendingOrder.guestCount,
        items: pendingOrder.items,
        subtotal: pendingOrder.subtotal,
        discount: pendingOrder.discount,
        discountPercent: pendingOrder.discountPercent,
        discountCode: pendingOrder.discountCode,
        total: pendingOrder.total,
        paymentMethod: methods[selectedPaymentMethod],
        date: new Date().toLocaleDateString('vi-VN'),
        time: new Date().toLocaleTimeString('vi-VN'),
        status: 'Đã thanh toán'
      };
      
      // Giải phóng bàn khi thanh toán xong
      occupiedTables.delete(pendingOrder.tableId);
      
      pendingOrders = pendingOrders.filter(o => o.orderId !== createdOrderId);
    } else {
      let subtotal = 0;
      currentOrder.forEach(order => {
        subtotal += order.price * order.quantity;
      });
      const discount = Math.floor(subtotal * discountApplied / 100);
      totalAmount = subtotal - discount;
      
      const name = document.getElementById('customerName').value;
      const phone = document.getElementById('customerPhone').value;
      
      orderData = {
        orderId: createdOrderId,
        type: 'takeaway',
        customerName: name,
        customerPhone: phone,
        items: [...currentOrder],
        subtotal: subtotal,
        discount: discount,
        discountPercent: discountApplied,
        discountCode: discountCode,
        total: totalAmount,
        paymentMethod: methods[selectedPaymentMethod],
        date: new Date().toLocaleDateString('vi-VN'),
        time: new Date().toLocaleTimeString('vi-VN'),
        status: 'Đã thanh toán'
      };
    }

    const response = await fetch(`${API_BASE_URL}/payment`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        orderId: createdOrderId,
        paymentMethod: methods[selectedPaymentMethod],
        totalAmount: totalAmount
      })
    });

    if (!response.ok) {
      const error = await response.text();
      alert('❌ ' + error);
      return;
    }

    const message = await response.text();
    
    orderHistory.unshift(orderData);
    
    if (orderData.type === 'dine-in') {
      alert(`✅ ${message}\n🎉 Bàn ${orderData.tableId} đã trống!`);
    } else {
      alert('✅ ' + message);
    }
    
    currentOrder = [];
    selectedPaymentMethod = null;
    discountApplied = 0;
    discountCode = '';
    createdOrderId = null;
    orderType = null;
    document.getElementById('discountCode').value = '';
    document.getElementById('discountMessage').textContent = '';
    document.querySelectorAll('.order-type-card').forEach(card => {
      card.classList.remove('selected');
    });
    document.getElementById('tableInfoCard').style.display = 'none';
    document.getElementById('customerInfoCard').style.display = 'none';
    
    updateOrderSummary();
    showPage('home');
    
  } catch (error) {
    console.error('Payment Error:', error);
    alert('❌ Không thể xử lý thanh toán. Vui lòng thử lại.');
  }
}

function displayPendingOrders() {
  const pendingList = document.getElementById('pendingList');
  
  if (!pendingOrders || pendingOrders.length === 0) {
    pendingList.innerHTML = `
      <div class="text-center p-5">
        <div style="font-size: 4rem; margin-bottom: 20px;">🕐</div>
        <h4>Chưa có đơn hàng chờ thanh toán</h4>
        <p class="text-muted">Các đơn ăn tại chỗ sẽ được lưu ở đây cho đến khi thanh toán</p>
        <button class="btn btn-primary mt-3" onclick="showPage('order')">Tạo đơn hàng mới</button>
      </div>
    `;
    return;
  }
  
  let html = '';
  pendingOrders.forEach(order => {
    html += `
      <div class="pending-card" onclick="payPendingOrder(${order.orderId})">
        <div class="d-flex justify-content-between mb-3">
          <div>
            <h5 class="text-primary">Bàn ${order.tableId}</h5>
            <small class="text-muted">${order.date} ${order.time}</small>
          </div>
          <span class="badge bg-warning text-dark">${order.status}</span>
        </div>
        
        <div class="mb-3">
          <span class="badge bg-secondary">${order.guestCount} khách</span>
        </div>
        
        <div class="mb-3">
          ${order.items.map(item => `
            <div class="d-flex justify-content-between">
              <span>${item.quantity} x ${item.name}</span>
              <span>${(item.price * item.quantity).toLocaleString()} VND</span>
            </div>
          `).join('')}
        </div>
        
        ${order.discount > 0 ? `
          <div class="d-flex justify-content-between text-success mb-2">
            <span>Giảm giá (${order.discountPercent}%)</span>
            <span>-${order.discount.toLocaleString()} VND</span>
          </div>
        ` : ''}
        
        <hr>
        <div class="d-flex justify-content-between">
          <strong>Tổng thanh toán</strong>
          <strong class="text-primary fs-5">${order.total.toLocaleString()} VND</strong>
        </div>
        
        <button class="btn btn-success w-100 mt-3" onclick="event.stopPropagation(); payPendingOrder(${order.orderId})">
          Thanh toán ngay →
        </button>
      </div>
    `;
  });
  
  pendingList.innerHTML = html;
}

function displayOrderHistory() {
  const historyList = document.getElementById('historyList');
  
  if (!orderHistory || orderHistory.length === 0) {
    historyList.innerHTML = `
      <div class="text-center p-5">
        <div style="font-size: 4rem; margin-bottom: 20px;">📋</div>
        <h4>Chưa có đơn hàng nào</h4>
        <p class="text-muted">Hãy đặt món và thanh toán để xem lịch sử đơn hàng</p>
        <button class="btn btn-primary mt-3" onclick="showPage('order')">Đặt hàng ngay</button>
      </div>
    `;
    return;
  }
  
  let html = '';
  orderHistory.forEach(order => {
    const isTableOrder = order.type === 'dine-in';
    html += `
      <div class="history-card">
        <div class="d-flex justify-content-between mb-3">
          <div>
            <h5 class="text-primary">#${order.orderId}</h5>
            <small class="text-muted">${order.date} ${order.time}</small>
          </div>
          <span class="badge bg-success">${order.status}</span>
        </div>
        
        <div class="mb-3">
          ${isTableOrder ? `
            <span class="badge bg-secondary">🍽️ Ăn tại chỗ</span>
            <span class="badge bg-secondary ms-2">Bàn ${order.tableId}</span>
            <span class="badge bg-secondary ms-2">${order.guestCount} khách</span>
          ` : `
            <span class="badge bg-secondary">🥡 Mang về</span>
            <span class="badge bg-secondary ms-2">${order.customerName}</span>
            <span class="badge bg-secondary ms-2">${order.customerPhone}</span>
          `}
          <span class="badge bg-info ms-2">${order.paymentMethod}</span>
        </div>
        
        <div class="mb-3">
          ${order.items.map(item => `
            <div class="d-flex justify-content-between">
              <span>${item.quantity} x ${item.name}</span>
              <span>${(item.price * item.quantity).toLocaleString()} VND</span>
            </div>
          `).join('')}
        </div>
        
        ${order.discount > 0 ? `
          <div class="d-flex justify-content-between text-success mb-2">
            <span>Giảm giá (${order.discountPercent}%)</span>
            <span>-${order.discount.toLocaleString()} VND</span>
          </div>
        ` : ''}
        
        <div class="d-flex justify-content-between">
          <strong>Tổng thanh toán</strong>
          <strong class="text-primary">${order.total.toLocaleString()} VND</strong>
        </div>
      </div>
    `;
  });
  
  historyList.innerHTML = html;
}

document.addEventListener('DOMContentLoaded', function() {
  checkAPIConnection();
  loadMenuFromAPI();
  
  const cardNumberInput = document.getElementById('cardNumber');
  if (cardNumberInput) {
    cardNumberInput.addEventListener('input', function(e) {
      let value = e.target.value.replace(/\s/g, '');
      let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
      e.target.value = formattedValue;
    });
  }
  
  const cardExpiryInput = document.getElementById('cardExpiry');
  if (cardExpiryInput) {
    cardExpiryInput.addEventListener('input', function(e) {
      let value = e.target.value.replace(/\D/g, '');
      if (value.length >= 2) {
        value = value.slice(0, 2) + '/' + value.slice(2, 4);
      }
      e.target.value = value;
    });
  }
});