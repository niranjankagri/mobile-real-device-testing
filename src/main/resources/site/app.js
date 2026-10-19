// PocketShop: products, the cart (kept in localStorage) and each page's behaviour. Plain JavaScript, no build step.

const PRODUCTS = [
  { id: 'mug', name: 'Enamel camping mug', price: 12.5, image: 'mug.svg' },
  { id: 'lamp', name: 'Folding desk lamp', price: 34.0, image: 'lamp.svg' },
  { id: 'bottle', name: 'Steel water bottle', price: 19.9, image: 'bottle.svg' },
];

// Prices in euros with two decimals, for example "€ 12.50"
const money = (value) => `€ ${value.toFixed(2)}`;
/** The product with this id, or undefined. */
const byId = (id) => PRODUCTS.find((p) => p.id === id);

/** The cart: { productId: quantity }. localStorage survives page loads, like a real shop's client-side cart. */
function readCart() {
  try {
    return JSON.parse(localStorage.getItem('cart') || '{}');
  } catch {
    return {};
  }
}

/** Saves the cart and updates the badge in the header. */
function writeCart(cart) {
  localStorage.setItem('cart', JSON.stringify(cart));
  showCartCount();
}

/** Number of items in the cart (quantities added up). */
function cartCount() {
  return Object.values(readCart()).reduce((sum, quantity) => sum + quantity, 0);
}

/** Shows the item count on the cart badge, on every page. */
function showCartCount() {
  const badge = document.getElementById('cart-count');
  if (badge) badge.textContent = String(cartCount());
}

/** The menu button opens and closes the navigation on phones; aria-expanded tells screen readers its state. */
function setUpMenu() {
  const button = document.getElementById('menu-button');
  const nav = document.getElementById('main-nav');
  if (!button || !nav) return;
  button.addEventListener('click', () => {
    const open = nav.classList.toggle('open');
    button.setAttribute('aria-expanded', String(open));
  });
}

/** Product list page: one card per product. The CSS stacks them in one column on a phone. */
function renderProducts() {
  const list = document.getElementById('products');
  if (!list) return;
  list.innerHTML = PRODUCTS.map(
    (p) => `<li class="product-card" data-product="${p.id}">
      <img src="${p.image}" alt="" width="96" height="96">
      <div><a href="product.html?id=${p.id}">${p.name}</a><p class="price">${money(p.price)}</p></div>
    </li>`,
  ).join('');
}

/** Product page: shows the product from ?id=, the quantity stepper (1 to 9) and the Add to cart button. */
function setUpProduct() {
  const page = document.getElementById('product');
  if (!page) return;
  const product = byId(new URLSearchParams(location.search).get('id')) || PRODUCTS[0];
  document.getElementById('product-name').textContent = product.name;
  document.getElementById('product-price').textContent = money(product.price);
  document.getElementById('product-image').src = product.image;
  document.title = `${product.name} | PocketShop`;

  const quantity = document.getElementById('quantity');
  let amount = 1;
  /** Shows the current amount between the - and + buttons. */
  const show = () => (quantity.textContent = String(amount));
  document.getElementById('less').addEventListener('click', () => {
    amount = Math.max(1, amount - 1);
    show();
  });
  document.getElementById('more').addEventListener('click', () => {
    amount = Math.min(9, amount + 1);
    show();
  });
  document.getElementById('add-to-cart').addEventListener('click', () => {
    const cart = readCart();
    cart[product.id] = (cart[product.id] || 0) + amount;
    writeCart(cart);
    // A role="status" element: screen readers announce the message, and the tests wait for it
    document.getElementById('added').textContent = `Added ${amount} to your cart.`;
  });
}

/** Cart page: one line per product, the total, and no checkout link when the cart is empty. */
function setUpCart() {
  const lines = document.getElementById('cart-lines');
  if (!lines) return;
  const cart = readCart();
  const ids = Object.keys(cart);
  if (ids.length === 0) {
    lines.innerHTML = '<li>Your cart is empty.</li>';
    document.getElementById('checkout-link').hidden = true;
  } else {
    lines.innerHTML = ids
      .map((id) => `<li data-line="${id}"><span>${cart[id]} × ${byId(id).name}</span><span>${money(cart[id] * byId(id).price)}</span></li>`)
      .join('');
  }
  const total = ids.reduce((sum, id) => sum + cart[id] * byId(id).price, 0);
  document.getElementById('cart-total').textContent = money(total);
}

/**
 * Checkout page: checks every field on submit. Each invalid field gets its message and aria-invalid, and the cursor
 * goes to the first one. When everything is valid the cart is emptied and the confirmation page opens.
 */
function setUpCheckout() {
  const form = document.getElementById('checkout-form');
  if (!form) return;
  form.addEventListener('submit', (event) => {
    event.preventDefault();
    // Each rule returns true, or the message to show
    const rules = {
      name: (v) => v.trim().length > 0 || 'Enter your name',
      email: (v) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || 'Enter an email like name@example.com',
      phone: (v) => /^[0-9 +()-]{6,}$/.test(v) || 'Enter a phone number',
      postcode: (v) => /^[0-9]{4,6}$/.test(v) || 'Enter a postcode of 4 to 6 digits',
    };
    let firstInvalid = null;
    for (const [field, rule] of Object.entries(rules)) {
      const input = form.elements.namedItem(field);
      const result = rule(input.value);
      const message = result === true ? '' : result;
      document.getElementById(`${field}-error`).textContent = message;
      input.setAttribute('aria-invalid', String(Boolean(message)));
      if (message && !firstInvalid) firstInvalid = input;
    }
    if (firstInvalid) {
      firstInvalid.focus();
      return;
    }
    localStorage.removeItem('cart');
    location.href = `done.html?order=${String(Date.now()).slice(-6)}`;
  });
}

/** Confirmation page: shows the order number from ?order=. */
function setUpDone() {
  const order = document.getElementById('order-number');
  if (order) order.textContent = new URLSearchParams(location.search).get('order') || '';
}

// Every page loads this script; each set-up function does nothing on pages without its elements.
setUpMenu();
showCartCount();
renderProducts();
setUpProduct();
setUpCart();
setUpCheckout();
setUpDone();
