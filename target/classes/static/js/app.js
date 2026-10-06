// Funciones compartidas por todas las páginas. El navegador es el CLIENTE; esta API es el SERVIDOR.
const API = '/api';

// Llama a la API con fetch (petición HTTP). Si la respuesta es un error (400, 409...),
// lee el mensaje {"error": "..."} del servidor y lo lanza para mostrarlo al usuario.
async function api(path, options = {}) {
  const res = await fetch(API + path, { headers: { 'Content-Type': 'application/json' }, ...options });
  if (!res.ok) {
    let msg = 'Error ' + res.status;
    try { const j = await res.json(); if (j.error) msg = j.error; } catch (e) { /* sin cuerpo */ }
    throw new Error(msg);
  }
  return res.status === 204 ? null : res.json();
}

// Muestra una notificación (toast de Bootstrap) abajo a la derecha.
function mostrarMensaje(texto) {
  document.getElementById('toast-msg').textContent = texto;
  bootstrap.Toast.getOrCreateInstance(document.getElementById('toast'), { delay: 4500 }).show();
}

// Escapa caracteres especiales (<, >, &...) antes de insertar texto en HTML.
// Evita que un dato malicioso se ejecute como código (ataque XSS).
function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}
