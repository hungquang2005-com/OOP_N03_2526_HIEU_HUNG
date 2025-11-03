const API_BASE_URL = 'http://localhost:8082/api';

let currentUser = null;         
let currentOrder = [];        
let pendingOrders = [];         
let orderHistory = [];          
let menuData = [];
let discountApplied = 0;
let discountCode = '';
let selectedPaymentMethod = null;
let createdOrderId = null;
let orderType = null;
let occupiedTables = new Set();

function getPendingStorageKeyForUser(username) {
  if (!username) return 'pending_orders_guest';
  return `pendingOrders_${username}`;
}

function loadPendingForCurrentUser() {
  if (currentUser && currentUser.username) {
    const key = getPendingStorageKeyForUser(currentUser.username);
    const raw = localStorage.getItem(key);
    pendingOrders = raw ? JSON.parse(raw) : [];
  } else {
    // guest
    const raw = localStorage.getItem(getPendingStorageKeyForUser(null));
    pendingOrders = raw ? JSON.parse(raw) : [];
  }
}

function savePendingForCurrentUser() {
  if (currentUser && currentUser.username) {
    const key = getPendingStorageKeyForUser(currentUser.username);
    localStorage.setItem(key, JSON.stringify(pendingOrders));
  } else {
    // guest pending (optional, useful across reloads in same browser)
    localStorage.setItem(getPendingStorageKeyForUser(null), JSON.stringify(pendingOrders));
  }
}

// Remove a specific pending order and persist
function removePendingById(orderId) {
  pendingOrders = pendingOrders.filter(o => o.orderId !== orderId);
  savePendingForCurrentUser();
}

function getDayDiscount() {
  const now = new Date();
  const day = now.getDay();
  if (day === 1) return { active: true, percent: 5, message: '🎉 Ưu đãi thứ 2: Giảm 5% tất cả món ăn!' };
  return { active: false, percent: 0, message: '' };
}

async function checkAPIConnection() {
  try {
    const res = await fetch(`${API_BASE_URL}/menu`);
    if (res.ok) {
      const el = document.getElementById('apiStatus');
      if (el) { el.innerHTML = '✅ Đã kết nối Backend'; el.className = 'text-success'; }
      return true;
    }
  } catch (e) {
    const el = document.getElementById('apiStatus');
    if (el) { el.innerHTML = '❌ Không kết nối được Backend'; el.className = 'text-danger'; }
    console.error('API Connection Error:', e);
  }
  return false;
}

// ---------------------------
// Menu load & display (keeps original structure)
// ---------------------------
async function loadMenuFromAPI() {
  const loadingEl = document.getElementById('loadingMenu');
  const menuContainer = document.getElementById('menuContainer');
  try {
    if (loadingEl) loadingEl.style.display = 'block';
    const res = await fetch(`${API_BASE_URL}/menu`);
    if (!res.ok) throw new Error('Failed to load menu');
    menuData = await res.json();
    if (loadingEl) loadingEl.style.display = 'none';
    displayMenu();
  } catch (err) {
    if (loadingEl) loadingEl.style.display = 'none';
    if (menuContainer) {
      menuContainer.innerHTML = `<div class="col-12 text-center"><div class="alert alert-warning">
        <h5>⚠️ Không thể tải thực đơn từ server</h5><p>Vui lòng kiểm tra kết nối Backend hoặc thử lại sau.</p></div></div>`;
    }
    console.error('Menu Loading Error:', err);
  }
}

function displayMenu() {
  const menuContainer = document.getElementById('menuContainer');
  const orderMenuItems = document.getElementById('orderMenuItems');
  const dayDiscount = getDayDiscount();
  let menuHTML = '';
  let orderHTML = '';
  if (dayDiscount.active) {
    menuHTML += `<div class="col-12 mb-4"><div class="alert alert-info text-center"><h5 class="mb-0">${dayDiscount.message}</h5></div></div>`;
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

  if (menuContainer) menuContainer.innerHTML = menuHTML;
  if (orderMenuItems) orderMenuItems.innerHTML = orderHTML;
}

// ---------------------------
// Routing UI
// ---------------------------
function showPage(pageId) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  const page = document.getElementById(pageId + 'Page');
  if (page) page.classList.add('active');
  window.scrollTo(0, 0);
  if (pageId === 'menu' && menuData.length === 0) loadMenuFromAPI();
  if (pageId === 'pending') displayPendingOrders();
  if (pageId === 'history') displayOrderHistory();
}

// ---------------------------
// Order type selection
// ---------------------------
function selectOrderType(type) {
  orderType = type;
  document.querySelectorAll('.order-type-card').forEach(card => card.classList.remove('selected'));
  if (typeof event !== 'undefined' && event.currentTarget) event.currentTarget.classList.add('selected');

  if (type === 'dine-in') {
    const t = document.getElementById('tableInfoCard'); if (t) t.style.display = 'block';
    const c = document.getElementById('customerInfoCard'); if (c) c.style.display = 'none';
    const ob = document.getElementById('orderButtonText'); if (ob) ob.textContent = 'Thêm vào Chờ Thanh Toán';
    const rb = document.getElementById('reviewButtonText'); if (rb) rb.textContent = 'Thêm vào Chờ Thanh Toán';
  } else {
    const t = document.getElementById('tableInfoCard'); if (t) t.style.display = 'none';
    const c = document.getElementById('customerInfoCard'); if (c) c.style.display = 'block';
    const ob = document.getElementById('orderButtonText'); if (ob) ob.textContent = 'Xem Lại & Thanh Toán';
    const rb = document.getElementById('reviewButtonText'); if (rb) rb.textContent = 'Xác nhận & Thanh Toán';
  }
}

// ---------------------------
// Register / Login / Logout
// ---------------------------
async function register(e) {
  e.preventDefault();
  const username = document.getElementById('regUsername').value;
  const password = document.getElementById('regPassword').value;
  const confirmPassword = document.getElementById('regPasswordConfirm').value;
  const role = document.getElementById('regRole') ? document.getElementById('regRole').value : 'USER';

  if (password !== confirmPassword) {
    alert('Mật khẩu không khớp!');
    return;
  }

  try {
    const res = await fetch(`${API_BASE_URL}/register`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, confirmPassword, role })
    });
    if (res.ok) {
      const msg = await res.text();
      alert('✅ ' + msg);
      showPage('login');
    } else {
      const err = await res.text();
      alert('❌ ' + err);
    }
  } catch (err) {
    console.error('Register Error:', err);
    alert('❌ Không thể kết nối đến server. Vui lòng kiểm tra Backend.');
  }
}

// LOGIN: loads per-user pendingOrders + sync guest history -> server
async function login(e) {
  e.preventDefault();
  const username = document.getElementById('loginUsername').value.trim();
  const password = document.getElementById('loginPassword').value.trim();

  try {
    const res = await fetch(`${API_BASE_URL}/login`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    if (!res.ok) {
      alert('❌ Sai username hoặc password!');
      return;
    }

    currentUser = await res.json();

    // Load pendingOrders for this user (persisted in localStorage)
    loadPendingForCurrentUser();

    // If there's guest order history, try import to server then clear guest history
    const guestHistoryRaw = localStorage.getItem('guestOrderHistory');
    if (guestHistoryRaw) {
      try {
        const guestOrders = JSON.parse(guestHistoryRaw);
        await fetch(`${API_BASE_URL}/orders/import`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: currentUser.username, orders: guestOrders })
        });
        localStorage.removeItem('guestOrderHistory');
        orderHistory = [];
        console.log('✅ Đồng bộ guest history lên server cho', currentUser.username);
      } catch (err) {
        console.warn('⚠️ Không thể đồng bộ guest history:', err);
      }
    }

    // Update UI
    const auth = document.getElementById('authSection'); if (auth) auth.style.display = 'none';
    const userSec = document.getElementById('userSection'); if (userSec) userSec.style.display = 'flex';
    const unameEl = document.getElementById('username'); if (unameEl) unameEl.textContent = currentUser.username;
    alert('✅ Đăng nhập thành công! Chào ' + currentUser.username);
    showPage('home');
  } catch (err) {
    console.error('Login Error:', err);
    alert('❌ Không thể kết nối đến server. Vui lòng kiểm tra Backend.');
  }
}

// LOGOUT: persist pendingOrders for this user, then reset in-memory state (so other account won't see)
function logout() {
  if (!confirm('Bạn có chắc muốn đăng xuất không?')) return;

  // Save current pendingOrders under this username (so when they log back in it's restored)
  if (currentUser && currentUser.username) {
    localStorage.setItem(getPendingStorageKeyForUser(currentUser.username), JSON.stringify(pendingOrders));
  }

  // Reset state
  currentUser = null;
  currentOrder = [];
  discountApplied = 0;
  discountCode = '';
  selectedPaymentMethod = null;
  createdOrderId = null;
  orderType = null;
  orderHistory = [];

  // Clear current session pending and occupied tables (in-memory)
  pendingOrders = [];
  occupiedTables.clear();

  // Keep guestHistory separate: we remove guest history on logout of guest; if logging out a user, guest data not relevant
  localStorage.removeItem('guestOrderHistory');

  // UI reset
  const auth = document.getElementById('authSection'); if (auth) auth.style.display = 'flex';
  const userSec = document.getElementById('userSection'); if (userSec) userSec.style.display = 'none';
  document.querySelectorAll('.order-type-card').forEach(card => card.classList.remove('selected'));
  const tableCard = document.getElementById('tableInfoCard'); if (tableCard) tableCard.style.display = 'none';
  const custCard = document.getElementById('customerInfoCard'); if (custCard) custCard.style.display = 'none';

  alert('✅ Đã đăng xuất thành công!');
  showPage('home');
}

// ---------------------------
// Add to order / update summary
// ---------------------------
function addToOrder(btn) {
  const input = btn.previousElementSibling;
  const quantity = parseInt(input.value);
  if (quantity <= 0) { alert('Vui lòng nhập số lượng!'); return; }

  const id = parseInt(input.dataset.id);
  const name = input.dataset.name;
  const price = parseInt(input.dataset.price);

  const existing = currentOrder.find(o => o.foodId === id);
  if (existing) existing.quantity += quantity;
  else currentOrder.push({ foodId: id, name, price, quantity });

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

  if (orderList) orderList.innerHTML = html;
  const subtotalEl = document.getElementById('subtotalAmount'); if (subtotalEl) subtotalEl.textContent = subtotal.toLocaleString() + ' VND';
  const discount = Math.floor(subtotal * discountApplied / 100);
  const total = subtotal - discount;
  const discountEl = document.getElementById('discountAmount'); if (discountEl) discountEl.textContent = '-' + discount.toLocaleString() + ' VND';
  const totalEl = document.getElementById('totalAmount'); if (totalEl) totalEl.textContent = total.toLocaleString() + ' VND';
}

function removeFromOrder(id) {
  currentOrder = currentOrder.filter(o => o.foodId !== id);
  updateOrderSummary();
}

// ---------------------------
// Apply discount
// ---------------------------
function applyDiscount() {
  const code = document.getElementById('discountCode').value.trim().toUpperCase();
  const message = document.getElementById('discountMessage');
  if (!code) { if (message) message.textContent = ''; return; }

  if (code === 'DISCOUNT10') {
    discountApplied = 10; discountCode = code;
    if (message) { message.textContent = `✅ Áp dụng mã giảm ${discountApplied}% thành công!`; message.className = 'text-success'; }
    updateOrderSummary();
  } else if (code === 'DISCOUNT20') {
    discountApplied = 20; discountCode = code;
    if (message) { message.textContent = `✅ Áp dụng mã giảm ${discountApplied}% thành công!`; message.className = 'text-success'; }
    updateOrderSummary();
  } else {
    if (message) { message.textContent = '❌ Mã giảm giá không hợp lệ!'; message.className = 'text-danger'; }
    discountApplied = 0; discountCode = '';
    updateOrderSummary();
  }
}

// ---------------------------
// Review / proceed to payment
// ---------------------------
function reviewOrder() {
  if (currentOrder.length === 0) { alert('Vui lòng chọn món trước!'); return; }
  if (!orderType) { alert('Vui lòng chọn loại đơn hàng (Ăn tại chỗ hoặc Mang về)!'); return; }

  if (orderType === 'dine-in') {
    const tableId = document.getElementById('tableId').value;
    const guestCount = document.getElementById('guestCount').value;
    if (!tableId || !guestCount) { alert('Vui lòng điền đầy đủ thông tin đặt bàn!'); return; }
    if (occupiedTables.has(parseInt(tableId))) { alert(`❌ Bàn ${tableId} đang có khách! Vui lòng chọn bàn khác.`); return; }
    document.getElementById('reviewTableInfo').innerHTML = `<p><strong>Loại:</strong> Ăn tại chỗ</p><p><strong>Bàn số:</strong> ${tableId}</p><p><strong>Số khách:</strong> ${guestCount} người</p>`;
  } else {
    const name = document.getElementById('customerName').value;
    const phone = document.getElementById('customerPhone').value;
    if (!name || !phone) { alert('Vui lòng điền đầy đủ thông tin khách hàng!'); return; }
    document.getElementById('reviewTableInfo').innerHTML = `<p><strong>Loại:</strong> Mang về</p><p><strong>Khách hàng:</strong> ${name}</p><p><strong>SĐT:</strong> ${phone}</p>`;
  }

  let html = ''; let subtotal = 0;
  currentOrder.forEach(order => {
    const itemTotal = order.price * order.quantity; subtotal += itemTotal;
    html += `<div class="d-flex justify-content-between mb-2"><span>${order.quantity} x ${order.name}</span><span>${itemTotal.toLocaleString()} VND</span></div>`;
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

// ---------------------------
// Add dine-in to pending (and persist for current user)
// ---------------------------
async function addToPendingOrders() {
  const tableId = parseInt(document.getElementById('tableId').value);
  const guestCount = parseInt(document.getElementById('guestCount').value);

  if (occupiedTables.has(tableId)) { alert(`❌ Bàn ${tableId} đang có khách! Vui lòng chọn bàn khác.`); return; }

  const now = new Date();
  const orderId = Math.floor(Math.random() * 1000000);
  let subtotal = 0;
  currentOrder.forEach(order => subtotal += order.price * order.quantity);
  const discount = Math.floor(subtotal * discountApplied / 100);
  const total = subtotal - discount;

  const pendingOrder = {
    orderId,
    type: 'dine-in',
    tableId,
    guestCount,
    items: [...currentOrder],
    subtotal,
    discount,
    discountPercent: discountApplied,
    discountCode,
    total,
    date: now.toLocaleDateString('vi-VN'),
    time: now.toLocaleTimeString('vi-VN'),
    status: 'Chờ thanh toán'
  };

  pendingOrders.push(pendingOrder);
  occupiedTables.add(tableId);

  // Persist pending for current user (or guest)
  savePendingForCurrentUser();

  alert(`✅ Đã thêm đơn hàng bàn ${tableId} vào danh sách chờ thanh toán!`);
  currentOrder = [];
  discountApplied = 0; discountCode = '';
  orderType = null;
  document.getElementById('discountCode').value = '';
  document.getElementById('discountMessage').textContent = '';
  document.querySelectorAll('.order-type-card').forEach(card => card.classList.remove('selected'));
  document.getElementById('tableInfoCard').style.display = 'none';
  updateOrderSummary();
  showPage('pending');
}

// ---------------------------
// Create order and go to payment (takeaway)
// ---------------------------
async function createOrderAndPay() {
  const name = document.getElementById('customerName').value;
  const phone = document.getElementById('customerPhone').value;

  try {
    const items = currentOrder.map(item => ({ foodId: item.foodId, quantity: item.quantity }));
    const orderData = { items, discountCode: discountCode || null };

    if (currentUser && currentUser.username) orderData.username = currentUser.username;

    const res = await fetch(`${API_BASE_URL}/orders`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(orderData)
    });

    if (!res.ok) throw new Error('Failed to create order');
    const orderResp = await res.json();
    createdOrderId = orderResp.orderId;

    document.getElementById('paymentTotal').textContent = orderResp.total.toLocaleString() + ' VND';
    document.getElementById('displayOrderId').textContent = '#' + orderResp.orderId;

    alert(`✅ Đã tạo đơn mang về cho khách hàng ${name}`);
    showPage('payment');
  } catch (err) {
    console.error('Order Creation Error:', err);
    alert('❌ Không thể tạo đơn hàng. Vui lòng thử lại.');
  }
}

// ---------------------------
// Pay pending or current created order
// ---------------------------
function payPendingOrder(orderId) {
  createdOrderId = orderId;
  const order = pendingOrders.find(o => o.orderId === orderId);
  if (!order) return;
  document.getElementById('paymentTotal').textContent = order.total.toLocaleString() + ' VND';
  document.getElementById('displayOrderId').textContent = '#' + order.orderId;
  showPage('payment');
}

function selectPayment(method) {
  const qr = document.getElementById('qrCodeSection'); if (qr) qr.style.display = 'none';
  const cardSec = document.getElementById('cardPaymentSection'); if (cardSec) cardSec.style.display = 'none';
  document.querySelectorAll('.payment-method').forEach(m => m.classList.remove('selected'));
  const target = document.querySelector(`[data-method="${method}"]`); if (target) target.classList.add('selected');
  selectedPaymentMethod = method;
  if (method === 'qr') showQRCode();
  else if (method === 'card') { if (cardSec) cardSec.style.display = 'block'; }
}

function showQRCode() {
  const qrSection = document.getElementById('qrCodeSection'); if (qrSection) qrSection.style.display = 'block';
  const orderId = createdOrderId || Math.floor(Math.random() * 10000);
  const total = document.getElementById('paymentTotal').textContent || '';
  const content = `NH VIET ${orderId} ${total}`;
  const qrContent = document.getElementById('qrContent'); if (qrContent) qrContent.textContent = content;
  const qrContainer = document.getElementById('qrcode'); if (qrContainer) qrContainer.innerHTML = '';
  new QRCode(qrContainer, { text: content, width: 200, height: 200, colorDark: "#000000", colorLight: "#ffffff", correctLevel: QRCode.CorrectLevel.H });
}

async function completePayment() {
  if (!selectedPaymentMethod) { alert('Vui lòng chọn phương thức thanh toán!'); return; }

  if (selectedPaymentMethod === 'card') {
    const bank = document.getElementById('bankSelect').value;
    const cardNumber = document.getElementById('cardNumber').value;
    const cardExpiry = document.getElementById('cardExpiry').value;
    const cardCVV = document.getElementById('cardCVV').value;
    const cardName = document.getElementById('cardName').value;
    if (!bank || !cardNumber || !cardExpiry || !cardCVV || !cardName) { alert('Vui lòng điền đầy đủ thông tin thẻ!'); return; }
  }

  const methods = { 'cash': 'Tiền mặt', 'qr': 'QR Code', 'card': 'Thẻ tín dụng' };

  try {
    const pendingOrder = pendingOrders.find(o => o.orderId === createdOrderId);
    let totalAmount;
    let orderData;

    if (pendingOrder) {
      // paying a dine-in pending order
      totalAmount = pendingOrder.total;
      orderData = { ...pendingOrder, paymentMethod: methods[selectedPaymentMethod], date: new Date().toLocaleDateString('vi-VN'), time: new Date().toLocaleTimeString('vi-VN'), status: 'Đã thanh toán' };

      // free the table and remove from pending
      occupiedTables.delete(pendingOrder.tableId);
      pendingOrders = pendingOrders.filter(o => o.orderId !== createdOrderId);

      // persist pending change for current user
      if (currentUser && currentUser.username) {
        localStorage.setItem(getPendingStorageKeyForUser(currentUser.username), JSON.stringify(pendingOrders));
      } else {
        localStorage.setItem(getPendingStorageKeyForUser(null), JSON.stringify(pendingOrders));
      }

    } else {
      // paying previously created takeaway (createdOrderId from createOrderAndPay)
      let subtotal = 0;
      currentOrder.forEach(order => subtotal += order.price * order.quantity);
      const discount = Math.floor(subtotal * discountApplied / 100);
      totalAmount = subtotal - discount;
      const name = document.getElementById('customerName').value;
      const phone = document.getElementById('customerPhone').value;
      orderData = { orderId: createdOrderId, type: 'takeaway', customerName: name, customerPhone: phone, items: [...currentOrder], subtotal, discount, discountPercent: discountApplied, discountCode, total: totalAmount, paymentMethod: methods[selectedPaymentMethod], date: new Date().toLocaleDateString('vi-VN'), time: new Date().toLocaleTimeString('vi-VN'), status: 'Đã thanh toán' };
    }

    // Notify backend about payment (existing / optional)
    const payRes = await fetch(`${API_BASE_URL}/payment`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ orderId: createdOrderId, paymentMethod: methods[selectedPaymentMethod], totalAmount }) });
    if (!payRes.ok) { const txt = await payRes.text(); alert('❌ ' + txt); return; }
    const message = await payRes.text();

    // Save history:
    if (!currentUser) {
      // guest: store history in localStorage
      orderHistory.unshift(orderData);
      localStorage.setItem('guestOrderHistory', JSON.stringify(orderHistory));
      console.log('💾 Đã lưu lịch sử guest tạm thời');
    } else {
      // user: server side should have stored order when creating; if not, we can optionally import this payment record to server
      // optionally: call /orders/import with single order if backend requires it
      try {
        await fetch(`${API_BASE_URL}/orders/import`, {
          method: 'POST', headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: currentUser.username, orders: [orderData] })
        });
      } catch (err) {
        console.warn('⚠️ Không thể import payment order to server (non-fatal):', err);
      }
      console.log('✅ Đơn hàng đã được lưu vào DB cho user (hoặc gửi import).');
    }

    // UI messages
    if (orderData.type === 'dine-in') alert(`✅ ${message}\n🎉 Bàn ${orderData.tableId} đã trống!`);
    else alert('✅ ' + message);

    // Clear local state for current order
    currentOrder = [];
    selectedPaymentMethod = null;
    discountApplied = 0; discountCode = '';
    createdOrderId = null; orderType = null;
    document.getElementById('discountCode').value = '';
    document.getElementById('discountMessage').textContent = '';
    document.querySelectorAll('.order-type-card').forEach(card => card.classList.remove('selected'));
    document.getElementById('tableInfoCard').style.display = 'none';
    document.getElementById('customerInfoCard').style.display = 'none';
    updateOrderSummary();
    showPage('home');

  } catch (err) {
    console.error('Payment Error:', err);
    alert('❌ Không thể xử lý thanh toán. Vui lòng thử lại.');
  }
}

// ---------------------------
// Pending list display
// ---------------------------
function displayPendingOrders() {
  const pendingList = document.getElementById('pendingList');
  if (!pendingOrders || pendingOrders.length === 0) {
    pendingList.innerHTML = `<div class="text-center p-5"><div style="font-size: 4rem; margin-bottom: 20px;">🕐</div><h4>Chưa có đơn hàng chờ thanh toán</h4><p class="text-muted">Các đơn ăn tại chỗ sẽ được lưu ở đây cho đến khi thanh toán</p><button class="btn btn-primary mt-3" onclick="showPage('order')">Tạo đơn hàng mới</button></div>`;
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
        <div class="mb-3"><span class="badge bg-secondary">${order.guestCount} khách</span></div>
        <div class="mb-3">
          ${order.items.map(item => `<div class="d-flex justify-content-between"><span>${item.quantity} x ${item.name}</span><span>${(item.price * item.quantity).toLocaleString()} VND</span></div>`).join('')}
        </div>
        ${order.discount > 0 ? `<div class="d-flex justify-content-between text-success mb-2"><span>Giảm giá (${order.discountPercent}%)</span><span>-${order.discount.toLocaleString()} VND</span></div>` : ''}
        <hr>
        <div class="d-flex justify-content-between"><strong>Tổng thanh toán</strong><strong class="text-primary fs-5">${order.total.toLocaleString()} VND</strong></div>
        <button class="btn btn-success w-100 mt-3" onclick="event.stopPropagation(); payPendingOrder(${order.orderId})">Thanh toán ngay →</button>
      </div>
    `;
  });

  pendingList.innerHTML = html;
}

// ---------------------------
// History display (user from server, guest from localStorage)
// ---------------------------
async function displayOrderHistory() {
  const historyList = document.getElementById('historyList');

  if (currentUser && currentUser.username) {
    historyList.innerHTML = '<div class="text-center p-3"><div class="spinner-border"></div><p>Đang tải lịch sử...</p></div>';
    try {
      const res = await fetch(`${API_BASE_URL}/orders/history/${currentUser.username}`);
      if (!res.ok) { showEmptyHistory(historyList); return; }
      const serverOrders = await res.json();
      if (!serverOrders || serverOrders.length === 0) { showEmptyHistory(historyList); return; }
      displayServerOrders(historyList, serverOrders);
      return;
    } catch (err) {
      console.error('Error loading order history:', err);
      historyList.innerHTML = `<div class="text-center p-5"><div style="font-size: 3rem;">❌</div><h4>Không thể tải lịch sử đơn hàng</h4><p class="text-muted">Vui lòng kiểm tra kết nối server</p></div>`;
      return;
    }
  }

  // Guest history
  const guestHistory = localStorage.getItem('guestOrderHistory');
  orderHistory = guestHistory ? JSON.parse(guestHistory) : [];
  if (!orderHistory || orderHistory.length === 0) { showEmptyHistory(historyList); return; }
  displayLocalOrders(historyList);
}

function showEmptyHistory(historyList) {
  const message = currentUser ? 'Bạn chưa có đơn hàng nào' : 'Chưa có đơn hàng nào (Đăng nhập để lưu lịch sử vĩnh viễn)';
  historyList.innerHTML = `<div class="text-center p-5"><div style="font-size: 4rem; margin-bottom: 20px;">📋</div><h4>${message}</h4><p class="text-muted">Hãy đặt món và thanh toán để xem lịch sử đơn hàng</p>${!currentUser ? '<p class="text-warning">⚠️ Lưu ý: Lịch sử guest sẽ bị xóa khi đóng trình duyệt</p>' : ''}<button class="btn btn-primary mt-3" onclick="showPage('order')">Đặt hàng ngay</button></div>`;
}

function displayServerOrders(historyList, serverOrders) {
  let html = '<div class="alert alert-info mb-3">✅ Lịch sử đơn hàng của bạn (đã đăng nhập)</div>';
  serverOrders.forEach(order => {
    html += `<div class="history-card"><div class="d-flex justify-content-between mb-3"><div><h5 class="text-primary">#${order.orderId}</h5><small class="text-muted">${order.formattedOrderDate || new Date(order.orderDate).toLocaleString('vi-VN')}</small></div><span class="badge bg-success">${order.status}</span></div><div class="mb-3">${order.orderDetails.map(detail => `<div class="d-flex justify-content-between"><span>${detail.quantity} x ${detail.food.name}</span><span>${(detail.food.price * detail.quantity).toLocaleString()} VND</span></div>`).join('')}</div><hr><div class="d-flex justify-content-between"><strong>Tổng thanh toán</strong><strong class="text-primary">${order.total.toLocaleString()} VND</strong></div></div>`;
  });
  html += `<div class="mt-3"><button class="btn btn-danger" onclick="clearOrderHistory()">🗑 Xóa toàn bộ lịch sử</button></div>`;
  historyList.innerHTML = html;
}

function displayLocalOrders(historyList) {
  let html = '<div class="alert alert-warning mb-3">⚠️ Lịch sử tạm thời (chưa đăng nhập) - Sẽ bị xóa khi thoát</div>';
  orderHistory.forEach(order => {
    const isTableOrder = order.type === 'dine-in';
    html += `<div class="history-card"><div class="d-flex justify-content-between mb-3"><div><h5 class="text-primary">#${order.orderId}</h5><small class="text-muted">${order.date} ${order.time}</small></div><span class="badge bg-success">${order.status}</span></div><div class="mb-3">${isTableOrder ? `<span class="badge bg-secondary">🍽️ Ăn tại chỗ</span><span class="badge bg-secondary ms-2">Bàn ${order.tableId}</span><span class="badge bg-secondary ms-2">${order.guestCount} khách</span>` : `<span class="badge bg-secondary">🥡 Mang về</span><span class="badge bg-secondary ms-2">${order.customerName || ''}</span><span class="badge bg-secondary ms-2">${order.customerPhone || ''}</span>`}<span class="badge bg-info ms-2">${order.paymentMethod || ''}</span></div><div class="mb-3">${order.items.map(item => `<div class="d-flex justify-content-between"><span>${item.quantity} x ${item.name}</span><span>${(item.price * item.quantity).toLocaleString()} VND</span></div>`).join('')}</div>${order.discount > 0 ? `<div class="d-flex justify-content-between text-success mb-2"><span>Giảm giá (${order.discountPercent}%)</span><span>-${order.discount.toLocaleString()} VND</span></div>` : ''}<hr><div class="d-flex justify-content-between"><strong>Tổng thanh toán</strong><strong class="text-primary">${order.total.toLocaleString()} VND</strong></div></div>`;
  });
  html += `<div class="mt-3"><button class="btn btn-danger" onclick="clearOrderHistory()">🗑 Xóa lịch sử tạm thời</button></div>`;
  historyList.innerHTML = html;
}

// ---------------------------
// Clear order history (user -> server; guest -> localStorage)
// ---------------------------
async function clearOrderHistory() {
  if (!confirm('Bạn có chắc muốn xóa toàn bộ lịch sử đơn hàng không?')) return;
  if (currentUser && currentUser.username) {
    try {
      const res = await fetch(`${API_BASE_URL}/orders/clear/${currentUser.username}`, { method: 'DELETE' });
      if (res.ok) alert('✅ Đã xóa toàn bộ lịch sử vĩnh viễn!');
      else alert('❌ Xóa lịch sử thất bại trên server.');
    } catch (err) {
      console.error(err); alert('❌ Lỗi khi xóa lịch sử trên server.');
    }
  } else {
    localStorage.removeItem('guestOrderHistory'); orderHistory = []; alert('✅ Đã xóa lịch sử tạm thời!');
  }
  displayOrderHistory();
}

// ---------------------------
// Init
// ---------------------------
document.addEventListener('DOMContentLoaded', function() {
  checkAPIConnection();
  loadMenuFromAPI();

  // Load guest pending if no user logged in
  const guestPending = localStorage.getItem(getPendingStorageKeyForUser(null));
  pendingOrders = guestPending ? JSON.parse(guestPending) : [];

  // Load guest history
  const guestHistory = localStorage.getItem('guestOrderHistory');
  if (guestHistory) {
    try { orderHistory = JSON.parse(guestHistory); } catch { orderHistory = []; localStorage.removeItem('guestOrderHistory'); }
  }

  // Card inputs formatting (if present)
  const cardNumberInput = document.getElementById('cardNumber');
  if (cardNumberInput) {
    cardNumberInput.addEventListener('input', function(e) {
      let value = e.target.value.replace(/\s/g, '');
      let formatted = value.match(/.{1,4}/g)?.join(' ') || value;
      e.target.value = formatted;
    });
  }
  const cardExpiryInput = document.getElementById('cardExpiry');
  if (cardExpiryInput) {
    cardExpiryInput.addEventListener('input', function(e) {
      let value = e.target.value.replace(/\D/g, '');
      if (value.length >= 2) value = value.slice(0,2) + '/' + value.slice(2,4);
      e.target.value = value;
    });
  }
});
