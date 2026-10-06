/* CRUD genérico: cada sección (estadio, club, jugador...) lo configura desde su propia plantilla. */
// Recibe la configuración de la sección y arma todo: formulario, tabla y eventos.
function Crud(cfg) {
  const $ = id => document.getElementById(id);
  const form = $('crud-form');
  let items = [], editId = null;

  // Devuelve el HTML de un campo según su tipo: texto/número, lista fija (options) o relación (ref).
  function campoHtml(c) {
    const id = 'f-' + c.name;
    const req = c.required === false ? '' : 'required';
    let control;
    if (c.ref) {
      control = `<select class="form-select" id="${id}" ${req} ${c.multiple ? 'multiple size="5"' : ''}></select>
                 <div class="form-text" id="${id}-ayuda">${c.multiple ? 'Mantén Ctrl (⌘ en Mac) para elegir varios.' : ''}</div>`;
    } else if (c.options) {
      control = `<select class="form-select" id="${id}" ${req}><option value="" selected disabled>Elige una</option>
                 ${c.options.map(o => `<option>${o}</option>`).join('')}</select>`;
    } else {
      control = `<input class="form-control" id="${id}" type="${c.type || 'text'}" ${c.type === 'number' ? 'min="0"' : ''} ${req}>`;
    }
    return `<div class="col-12"><label class="form-label" for="${id}">${c.label}</label>${control}</div>`;
  }

  form.innerHTML = `<div class="row g-3">${cfg.campos.map(campoHtml).join('')}</div>
    <div class="d-flex gap-2 mt-4">
      <button type="submit" class="btn btn-pitch" id="btn-guardar">Guardar</button>
      <button type="button" class="btn btn-outline-secondary d-none" id="btn-cancelar">Cancelar</button>
    </div>`;
  $('crud-thead').innerHTML = '<tr>' + cfg.columnas.map(c => `<th>${c.label}</th>`).join('') + '<th class="text-end">Acciones</th></tr>';

  // Para los campos de relación (ref) pide a la API los registros existentes y llena el <select>.
  // Así el usuario solo puede elegir cosas que ya existen: una entidad depende de la otra.
  async function cargarReferencias() {
    for (const c of cfg.campos.filter(c => c.ref)) {
      try {
        const lista = await api(c.ref);
        $('f-' + c.name).innerHTML = (c.multiple ? '' : '<option value="" selected disabled>Elige una</option>') +
          lista.map(o => `<option value="${esc(o.id)}">${esc(o[c.refLabel])}</option>`).join('');
        if (!lista.length) $('f-' + c.name + '-ayuda').textContent = c.vacio;
      } catch (e) { mostrarMensaje('No se pudieron cargar las opciones de ' + c.label); }
    }
  }

  function textoCelda(col, item) {
    const v = col.render(item);
    return Array.isArray(v) ? v.join(' ') : String(v ?? '');
  }

  // Dibuja la tabla con los datos y aplica el filtro del buscador.
  function pintar() {
    const q = $('buscar').value.trim().toLowerCase();
    const filas = items.filter(it => cfg.columnas.map(c => textoCelda(c, it)).join(' ').toLowerCase().includes(q));
    $('crud-count').textContent = filas.length;
    $('crud-tbody').innerHTML = filas.length ? filas.map(it => '<tr>' + cfg.columnas.map(col => {
      const v = col.render(it);
      return '<td>' + (Array.isArray(v) ? v.map(x => `<span class="chip">${esc(x)}</span>`).join('') || '—' : esc(v) || '—') + '</td>';
    }).join('') + `<td class="text-end text-nowrap">
        <button class="btn btn-sm btn-outline-secondary" data-edit="${esc(it.id)}">Editar</button>
        <button class="btn btn-sm btn-outline-danger" data-del="${esc(it.id)}">Eliminar</button></td></tr>`).join('')
      : `<tr><td colspan="${cfg.columnas.length + 1}" class="empty">${cfg.vacio}</td></tr>`;
  }

  // READ: GET a la API y luego pintar la tabla.
  async function cargar() {
    try { items = await api(cfg.endpoint); pintar(); }
    catch (e) { mostrarMensaje('No se pudieron cargar los datos.'); }
  }

  // Vuelve el formulario al modo "crear".
  function limpiar() {
    editId = null; form.reset(); form.classList.remove('was-validated');
    $('btn-guardar').textContent = 'Guardar'; $('btn-cancelar').classList.add('d-none');
  }

  // Pasa el formulario a modo "editar": rellena los campos con el registro elegido.
  function editar(item) {
    editId = item.id;
    for (const c of cfg.campos) {
      const el = $('f-' + c.name);
      if (c.ref && c.multiple) {
        const ids = (item[c.name] || []).filter(Boolean).map(x => x.id);
        [...el.options].forEach(o => o.selected = ids.includes(o.value));
      } else if (c.ref) { el.value = item[c.name]?.id ?? ''; }
      else { el.value = item[c.name] ?? ''; }
    }
    $('btn-guardar').textContent = 'Actualizar'; $('btn-cancelar').classList.remove('d-none');
    form.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  // CREATE o UPDATE: si hay id en edición hace PUT /ruta/{id}, si no POST /ruta.
  // Las relaciones se envían como objeto con solo el id, ej. {"estadio": {"id": "..."}}.
  form.addEventListener('submit', async ev => {
    ev.preventDefault();
    if (!form.checkValidity()) { form.classList.add('was-validated'); return; }
    const body = {};
    for (const c of cfg.campos) {
      const el = $('f-' + c.name);
      if (c.ref && c.multiple) body[c.name] = [...el.selectedOptions].map(o => ({ id: o.value }));
      else if (c.ref) body[c.name] = el.value ? { id: el.value } : null;
      else if (c.type === 'number') body[c.name] = Number(el.value);
      else body[c.name] = el.value.trim();
    }
    try {
      await api(editId ? `${cfg.endpoint}/${editId}` : cfg.endpoint, { method: editId ? 'PUT' : 'POST', body: JSON.stringify(body) });
      mostrarMensaje(editId ? 'Cambios guardados' : 'Registro guardado');
      limpiar(); cargar();
    } catch (e) { mostrarMensaje(e.message); }
  });

  $('btn-cancelar').addEventListener('click', limpiar);
  $('buscar').addEventListener('input', pintar);
  // DELETE y botón Editar (delegación de eventos: un solo listener para todas las filas).
  $('crud-tbody').addEventListener('click', async ev => {
    const be = ev.target.closest('[data-edit]'), bd = ev.target.closest('[data-del]');
    if (be) editar(items.find(i => i.id === be.dataset.edit));
    if (bd && confirm('¿Eliminar este registro?')) {
      try { await api(`${cfg.endpoint}/${bd.dataset.del}`, { method: 'DELETE' }); mostrarMensaje('Registro eliminado'); cargar(); }
      catch (e) { mostrarMensaje(e.message); }
    }
  });

  cargarReferencias().then(cargar);
}
