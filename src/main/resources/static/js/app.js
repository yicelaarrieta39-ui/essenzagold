(() => {
    "use strict";

    const api = {
        usuarios: "/api/usuarios",
        productos: "/api/productos",
        proveedores: "/api/proveedores",
        compras: "/api/compras",
        ventas: "/api/ventas",
        devoluciones: "/api/devoluciones"
    };
    const money = new Intl.NumberFormat("es-CO", {
        style: "currency",
        currency: "COP",
        maximumFractionDigits: 2
    });
    const escapeHtml = (value) => String(value ?? "").replace(/[&<>"']/g, (character) => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    })[character]);
    const amount = (value) => money.format(Number(value) || 0);

    function notify(message, type = "success") {
        const container = document.getElementById("toastContainer");
        if (!container) return;
        const toast = document.createElement("div");
        toast.className = `toast luxury-toast ${type}`;
        toast.setAttribute("role", type === "error" ? "alert" : "status");
        toast.innerHTML = `<div class="d-flex"><div class="toast-body">${escapeHtml(message)}</div>
            <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button></div>`;
        container.append(toast);
        const instance = bootstrap.Toast.getOrCreateInstance(toast, { delay: 4200 });
        toast.addEventListener("hidden.bs.toast", () => toast.remove(), { once: true });
        instance.show();
    }

    async function request(url, options = {}) {
        const response = await fetch(url, {
            ...options,
            headers: { Accept: "application/json", ...(options.body ? { "Content-Type": "application/json" } : {}), ...options.headers }
        });
        const text = await response.text();
        let body = null;
        if (text) {
            try { body = JSON.parse(text); } catch { body = text; }
        }
        if (!response.ok) {
            const message = body && typeof body === "object" && body.message
                ? body.message
                : `La solicitud no pudo completarse (HTTP ${response.status}).`;
            throw new Error(message);
        }
        return body;
    }

    function statusClass(status) {
        if (["Inactivo", "Agotado", "Anulada", "Rechazada", "Cancelada"].includes(status)) return "inactive";
        if (["Solicitada", "Pendiente"].includes(status)) return "pending";
        return "";
    }

    function badge(status) {
        return `<span class="status-badge ${statusClass(status)}">${escapeHtml(status || "Sin estado")}</span>`;
    }

    function dateLabel(date) {
        if (!date) return "—";
        const parsed = new Date(date);
        return Number.isNaN(parsed.getTime()) ? escapeHtml(date) : parsed.toLocaleString("es-CO", {
            dateStyle: "medium", timeStyle: "short"
        });
    }

    function setupSidebar() {
        const sidebar = document.getElementById("appSidebar");
        const scrim = document.querySelector(".sidebar-scrim");
        document.querySelectorAll("[data-sidebar-toggle]").forEach((button) => {
            button.addEventListener("click", () => {
                sidebar?.classList.toggle("is-open");
                scrim?.classList.toggle("is-visible");
            });
        });
        document.querySelectorAll("[data-form-toggle]").forEach((button) => {
            button.addEventListener("click", () => {
                const card = document.getElementById(button.dataset.formToggle);
                if (!card) return;
                card.classList.toggle("d-none");
                if (!card.classList.contains("d-none")) card.scrollIntoView({ behavior: "smooth", block: "start" });
            });
        });
    }

    function formValues(form) {
        return Object.fromEntries(new FormData(form).entries());
    }

    function nullable(value) {
        const trimmed = String(value ?? "").trim();
        return trimmed === "" ? null : trimmed;
    }

    function integer(value) {
        const number = Number(value);
        return Number.isInteger(number) ? number : null;
    }

    function decimal(value) {
        if (value === "" || value == null) return null;
        const number = Number(value);
        return Number.isFinite(number) ? number : null;
    }

    function togglePasswordValidation(form, editing) {
        const password = form.elements.namedItem("contrasena");
        const hint = document.getElementById("passwordHint");
        if (!password) return;
        password.required = !editing;
        password.minLength = editing ? 0 : 8;
        password.placeholder = editing ? "Dejar en blanco para conservar la actual" : "";
        if (hint) hint.textContent = editing ? "déjala en blanco para conservarla" : "requerida para usuarios nuevos";
    }

    function mapEntity(module, values, editingRecord = null) {
        if (module === "usuarios") {
            return {
                idUsuario: integer(values.idUsuario),
                nombre: values.nombre.trim(),
                apellido: values.apellido.trim(),
                documento: values.documento.trim(),
                correo: values.correo.trim(),
                telefono: nullable(values.telefono),
                direccion: nullable(values.direccion),
                contrasena: nullable(values.contrasena),
                rol: values.rol,
                estado: values.estado,
                fechaRegistro: editingRecord?.fechaRegistro ?? null
            };
        }
        if (module === "productos") {
            return {
                idProducto: integer(values.idProducto),
                codigo: values.codigo.trim(),
                nombre: values.nombre.trim(),
                descripcion: nullable(values.descripcion),
                categoria: values.categoria,
                presentacion: nullable(values.presentacion),
                capacidadMl: decimal(values.capacidadMl),
                concentracion: nullable(values.concentracion),
                precioCompra: decimal(values.precioCompra),
                precioVenta: decimal(values.precioVenta),
                stock: editingRecord ? editingRecord.stock : integer(values.stock),
                stockMinimo: integer(values.stockMinimo),
                estado: values.estado,
                fechaRegistro: editingRecord?.fechaRegistro ?? null
            };
        }
        return {
            idProveedor: integer(values.idProveedor),
            nombreEmpresa: values.nombreEmpresa.trim(),
            nit: values.nit.trim(),
            contacto: nullable(values.contacto),
            telefono: nullable(values.telefono),
            correo: nullable(values.correo),
            direccion: nullable(values.direccion),
            ciudad: nullable(values.ciudad),
            estado: values.estado,
            fechaRegistro: editingRecord?.fechaRegistro ?? null
        };
    }

    function actions(module, record) {
        const id = module === "usuarios" ? record.idUsuario
            : module === "productos" ? record.idProducto : record.idProveedor;
        return `<div class="row-actions">
            <button class="row-action" type="button" data-action="view" data-id="${id}" aria-label="Consultar"><i class="bi bi-eye"></i></button>
            <button class="row-action" type="button" data-action="edit" data-id="${id}" aria-label="Editar"><i class="bi bi-pencil"></i></button>
            <button class="row-action danger" type="button" data-action="delete" data-id="${id}" aria-label="Eliminar"><i class="bi bi-trash3"></i></button>
        </div>`;
    }

    function entityRow(module, record) {
        if (module === "usuarios") return `<tr>
            <td><strong>${escapeHtml(`${record.nombre} ${record.apellido}`)}</strong><small class="d-block">${escapeHtml(record.correo)}</small></td>
            <td>${escapeHtml(record.documento)}</td><td>${escapeHtml(record.telefono || "—")}</td>
            <td>${escapeHtml(record.rol)}</td><td>${badge(record.estado)}</td>
            <td class="text-end">${actions(module, record)}</td></tr>`;
        if (module === "productos") {
            const low = Number(record.stock) <= Number(record.stockMinimo);
            return `<tr>
                <td><strong>${escapeHtml(record.nombre)}</strong><small class="d-block">${escapeHtml(record.codigo)} · ${escapeHtml(record.presentacion || "Sin presentación")}</small></td>
                <td>${escapeHtml(record.categoria)}</td><td>${amount(record.precioVenta)}</td>
                <td><span class="${low ? "stock-warning" : ""}">${escapeHtml(record.stock)}</span><small class="d-block">mín. ${escapeHtml(record.stockMinimo)}</small></td>
                <td>${badge(record.estado)}</td><td class="text-end">${actions(module, record)}</td></tr>`;
        }
        return `<tr>
            <td><strong>${escapeHtml(record.nombreEmpresa)}</strong><small class="d-block">${escapeHtml(record.correo || "Sin correo")}</small></td>
            <td>${escapeHtml(record.nit)}</td><td>${escapeHtml(record.contacto || "—")}<small class="d-block">${escapeHtml(record.telefono || "")}</small></td>
            <td>${escapeHtml([record.ciudad, record.direccion].filter(Boolean).join(" · ") || "—")}</td>
            <td>${badge(record.estado)}</td><td class="text-end">${actions(module, record)}</td></tr>`;
    }

    function detailMarkup(module, record) {
        const labels = module === "usuarios"
            ? [["Nombre", `${record.nombre} ${record.apellido}`], ["Documento", record.documento], ["Correo", record.correo], ["Teléfono", record.telefono], ["Dirección", record.direccion], ["Rol", record.rol], ["Estado", record.estado], ["Registro", dateLabel(record.fechaRegistro)]]
            : module === "productos"
                ? [["Código", record.codigo], ["Nombre", record.nombre], ["Descripción", record.descripcion], ["Categoría", record.categoria], ["Presentación", record.presentacion], ["Capacidad", record.capacidadMl == null ? null : `${record.capacidadMl} ml`], ["Concentración", record.concentracion], ["Costo", amount(record.precioCompra)], ["Precio", amount(record.precioVenta)], ["Existencias", record.stock], ["Stock mínimo", record.stockMinimo], ["Estado", record.estado]]
                : [["Empresa", record.nombreEmpresa], ["NIT", record.nit], ["Contacto", record.contacto], ["Teléfono", record.telefono], ["Correo", record.correo], ["Dirección", record.direccion], ["Ciudad", record.ciudad], ["Estado", record.estado]];
        let dialog = document.getElementById("recordDialog");
        if (!dialog) {
            dialog = document.createElement("dialog");
            dialog.id = "recordDialog";
            dialog.className = "record-dialog";
            document.body.append(dialog);
        }
        dialog.innerHTML = `<div class="dialog-heading"><div><span class="eyebrow">DETALLE DEL REGISTRO</span><h2>${escapeHtml(module === "productos" ? record.nombre : module === "usuarios" ? `${record.nombre} ${record.apellido}` : record.nombreEmpresa)}</h2></div><button type="button" class="icon-button" data-dialog-close aria-label="Cerrar"><i class="bi bi-x-lg"></i></button></div>
            <dl class="detail-grid">${labels.map(([label, value]) => `<div><dt>${escapeHtml(label)}</dt><dd>${escapeHtml(value || "—")}</dd></div>`).join("")}</dl>`;
        dialog.querySelector("[data-dialog-close]").addEventListener("click", () => dialog.close());
        dialog.showModal();
    }

    async function setupEntityPage(module) {
        const form = document.querySelector("[data-entity-form]");
        const table = document.getElementById("recordsTable");
        const empty = document.getElementById("emptyState");
        const search = document.getElementById("tableSearch");
        const categoryFilter = document.getElementById("categoryFilter");
        let records = [];
        let visibleRecords = [];
        search.value = new URLSearchParams(window.location.search).get("q") || "";
        if (categoryFilter) categoryFilter.value = new URLSearchParams(window.location.search).get("categoria") || "";
        const entityId = (record) => module === "usuarios" ? record.idUsuario
            : module === "productos" ? record.idProducto : record.idProveedor;

        const render = () => {
            const query = search?.value.trim().toLocaleLowerCase("es") || "";
            const category = categoryFilter?.value || "";
            visibleRecords = records.filter((record) => {
                const matchesSearch = !query || Object.values(record).join(" ").toLocaleLowerCase("es").includes(query);
                return matchesSearch && (!category || record.categoria === category);
            });
            table.innerHTML = visibleRecords.map((record) => entityRow(module, record)).join("");
            empty?.classList.toggle("d-none", visibleRecords.length > 0);
            table.closest(".table-responsive")?.classList.toggle("d-none", visibleRecords.length === 0);
        };
        const reload = async () => {
            records = await request(api[module]);
            render();
        };

        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const values = formValues(form);
            const id = integer(values[module === "usuarios" ? "idUsuario" : module === "productos" ? "idProducto" : "idProveedor"]);
            if (module === "usuarios" && !id && String(values.contrasena || "").length < 8) {
                notify("La contraseña inicial debe tener al menos 8 caracteres.", "error");
                return;
            }
            const original = records.find((record) => entityId(record) === id) || null;
            const submit = form.querySelector('[type="submit"]');
            submit.disabled = true;
            try {
                const payload = mapEntity(module, values, original);
                await request(id ? `${api[module]}/${id}` : api[module], {
                    method: id ? "PUT" : "POST",
                    body: JSON.stringify(payload)
                });
                notify(id ? "Los cambios se guardaron correctamente." : "El registro se creó correctamente.");
                form.reset();
                form.elements.namedItem(module === "usuarios" ? "idUsuario" : module === "productos" ? "idProducto" : "idProveedor").value = "";
                if (module === "usuarios") togglePasswordValidation(form, false);
                document.getElementById(module === "usuarios" ? "userFormTitle" : module === "productos" ? "productFormTitle" : "supplierFormTitle").textContent =
                    `Registrar ${module === "usuarios" ? "usuario" : module === "productos" ? "producto" : "proveedor"}`;
                document.getElementById(module === "usuarios" ? "userFormCard" : module === "productos" ? "productFormCard" : "supplierFormCard").classList.add("d-none");
                await reload();
            } catch (error) {
                notify(error.message, "error");
            } finally {
                submit.disabled = false;
            }
        });

        table.addEventListener("click", async (event) => {
            const button = event.target.closest("[data-action]");
            if (!button) return;
            const record = records.find((item) => String(entityId(item)) === button.dataset.id);
            if (!record) return;
            if (button.dataset.action === "view") {
                detailMarkup(module, record);
                return;
            }
            if (button.dataset.action === "edit") {
                form.reset();
                const idName = module === "usuarios" ? "idUsuario" : module === "productos" ? "idProducto" : "idProveedor";
                form.elements.namedItem(idName).value = entityId(record);
                Object.entries(record).forEach(([name, value]) => {
                    const input = form.elements.namedItem(name);
                    if (input && name !== "contrasena" && value != null) input.value = value;
                });
                if (module === "usuarios") {
                    togglePasswordValidation(form, true);
                    document.getElementById("userFormTitle").textContent = "Editar usuario";
                } else if (module === "productos") {
                    document.getElementById("productFormTitle").textContent = "Editar producto";
                } else {
                    document.getElementById("supplierFormTitle").textContent = "Editar proveedor";
                }
                const cardId = module === "usuarios" ? "userFormCard" : module === "productos" ? "productFormCard" : "supplierFormCard";
                document.getElementById(cardId).classList.remove("d-none");
                document.getElementById(cardId).scrollIntoView({ behavior: "smooth", block: "start" });
                return;
            }
            if (button.dataset.action === "delete"
                    && window.confirm(`¿Deseas eliminar "${module === "usuarios" ? record.nombre + " " + record.apellido : module === "productos" ? record.nombre : record.nombreEmpresa}"? Esta acción no se puede deshacer.`)) {
                button.disabled = true;
                try {
                    await request(`${api[module]}/${entityId(record)}`, { method: "DELETE" });
                    notify("El registro se eliminó correctamente.");
                    await reload();
                } catch (error) {
                    notify(error.message, "error");
                    button.disabled = false;
                }
            }
        });
        search?.addEventListener("input", render);
        categoryFilter?.addEventListener("change", render);
        if (module === "usuarios") togglePasswordValidation(form, false);
        try { await reload(); } catch (error) { notify(error.message, "error"); }
    }

    function setSelectOptions(select, records, idField, labelFor, include = () => true) {
        const filtered = records.filter(include);
        select.innerHTML = `<option value="">Selecciona una opción</option>${filtered.map((record) =>
            `<option value="${escapeHtml(record[idField])}">${escapeHtml(labelFor(record))}</option>`).join("")}`;
        return filtered;
    }

    function registerItemTable(table, emptyLabel) {
        const items = [];
        const render = (row) => {
            table.innerHTML = items.map((item, index) => row(item, index)).join("");
            const empty = document.getElementById(`${table.id}Empty`);
            if (empty) empty.classList.toggle("d-none", items.length > 0);
        };
        table.addEventListener("click", (event) => {
            const remove = event.target.closest("[data-remove-line]");
            if (!remove) return;
            items.splice(Number(remove.dataset.removeLine), 1);
            render();
        });
        if (emptyLabel) {
            const empty = document.getElementById(`${table.id}Empty`);
            if (empty) empty.textContent = emptyLabel;
        }
        return { items, render };
    }

    async function setupPurchases() {
        const form = document.getElementById("purchaseForm");
        const recordsTable = document.getElementById("recordsTable");
        const search = document.getElementById("tableSearch");
        const supplierSelect = document.getElementById("purchaseSupplier");
        const userSelect = document.getElementById("purchaseUser");
        const productSelect = document.getElementById("purchaseProduct");
        const linesTable = document.getElementById("purchaseLines");
        const lines = registerItemTable(linesTable, "Añade al menos un producto para continuar.");
        search.value = new URLSearchParams(window.location.search).get("q") || "";
        let products = [];
        let suppliers = [];
        let users = [];
        let purchases = [];

        const calculate = () => {
            const total = lines.items.reduce((sum, line) => sum + line.cantidad * line.costoUnitario, 0);
            document.getElementById("purchaseTotal").textContent = amount(total);
        };
        const renderLines = () => {
            lines.render((line, index) => `<tr><td><strong>${escapeHtml(line.nombre)}</strong></td><td>${line.cantidad}</td><td>${amount(line.costoUnitario)}</td><td>${amount(line.cantidad * line.costoUnitario)}</td><td><button class="row-action danger" type="button" data-remove-line="${index}" aria-label="Quitar"><i class="bi bi-x-lg"></i></button></td></tr>`);
            calculate();
        };
        const renderRecords = () => {
            const query = search.value.trim().toLocaleLowerCase("es");
            const filtered = purchases.filter((purchase) => {
                const supplier = suppliers.find((item) => item.idProveedor === purchase.idProveedor);
                return `${purchase.numeroFactura || ""} ${supplier?.nombreEmpresa || ""} ${purchase.estado || ""}`.toLocaleLowerCase("es").includes(query);
            });
            recordsTable.innerHTML = filtered.map((purchase) => {
                const supplier = suppliers.find((item) => item.idProveedor === purchase.idProveedor);
                return `<tr><td><strong>#${purchase.idCompra}</strong><small class="d-block">${escapeHtml(purchase.numeroFactura || "Sin factura")}</small></td><td>${escapeHtml(supplier?.nombreEmpresa || `Proveedor ${purchase.idProveedor}`)}</td><td>${dateLabel(purchase.fechaCompra)}</td><td>${escapeHtml(purchase.metodoPago)}</td><td>${badge(purchase.estado)}</td><td class="text-end">${amount(purchase.total)}</td></tr>`;
            }).join("");
            document.getElementById("emptyState").classList.toggle("d-none", filtered.length > 0);
            recordsTable.closest(".table-responsive").classList.toggle("d-none", filtered.length === 0);
        };

        try {
            [suppliers, users, products] = await Promise.all([
                request(api.proveedores), request(api.usuarios), request(api.productos)
            ]);
            setSelectOptions(supplierSelect, suppliers, "idProveedor", (item) => item.nombreEmpresa, (item) => item.estado === "Activo");
            setSelectOptions(userSelect, users, "idUsuario", (item) => `${item.nombre} ${item.apellido}`, (item) => item.estado === "Activo");
            setSelectOptions(productSelect, products, "idProducto", (item) => `${item.nombre} · ${item.codigo}`, (item) => item.estado !== "Inactivo");
            purchases = await request(api.compras);
            renderRecords();
        } catch (error) {
            notify(error.message, "error");
        }
        productSelect.addEventListener("change", () => {
            const product = products.find((item) => String(item.idProducto) === productSelect.value);
            document.getElementById("purchasePrice").value = product?.precioCompra ?? "";
        });
        document.getElementById("addPurchaseLine").addEventListener("click", () => {
            const product = products.find((item) => String(item.idProducto) === productSelect.value);
            const cantidad = integer(document.getElementById("purchaseQuantity").value);
            const costoUnitario = decimal(document.getElementById("purchasePrice").value);
            if (!product || !cantidad || cantidad <= 0 || costoUnitario == null || costoUnitario < 0) {
                notify("Selecciona un producto e indica una cantidad y costo válidos.", "error");
                return;
            }
            lines.items.push({ idProducto: product.idProducto, nombre: product.nombre, cantidad, costoUnitario });
            renderLines();
        });
        linesTable.addEventListener("click", () => window.setTimeout(renderLines, 0));
        search.addEventListener("input", renderRecords);
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            if (!lines.items.length) { notify("Añade al menos un producto a la compra.", "error"); return; }
            const button = form.querySelector('[type="submit"]');
            button.disabled = true;
            try {
                const values = formValues(form);
                const created = await request("/api/operaciones/compras", {
                    method: "POST",
                    body: JSON.stringify({
                        idProveedor: integer(values.idProveedor),
                        idUsuario: integer(values.idUsuario),
                        numeroFactura: nullable(values.numeroFactura),
                        metodoPago: values.metodoPago,
                        observaciones: nullable(values.observaciones),
                        lineas: lines.items.map(({ idProducto, cantidad, costoUnitario }) => ({ idProducto, cantidad, costoUnitario }))
                    })
                });
                notify(`Compra #${created.idCompra} registrada por ${amount(created.total)}.`);
                form.reset();
                lines.items.splice(0, lines.items.length);
                renderLines();
                purchases = await request(api.compras);
                renderRecords();
            } catch (error) {
                notify(error.message, "error");
            } finally {
                button.disabled = false;
            }
        });
    }

    async function setupSales() {
        const form = document.getElementById("saleForm");
        const recordsTable = document.getElementById("recordsTable");
        const search = document.getElementById("tableSearch");
        const userSelect = document.getElementById("saleUser");
        const productSelect = document.getElementById("saleProduct");
        const linesTable = document.getElementById("saleLines");
        const lines = registerItemTable(linesTable, "Añade al menos una fragancia para continuar.");
        search.value = new URLSearchParams(window.location.search).get("q") || "";
        let products = [];
        let users = [];
        let sales = [];

        const calculate = () => {
            const total = lines.items.reduce((sum, line) => sum + line.cantidad * line.precioUnitario * (1 - line.descuentoPorcentaje / 100), 0);
            document.getElementById("saleTotal").textContent = amount(total);
        };
        const renderLines = () => {
            lines.render((line, index) => {
                const subtotal = line.cantidad * line.precioUnitario * (1 - line.descuentoPorcentaje / 100);
                return `<tr><td><strong>${escapeHtml(line.nombre)}</strong><small class="d-block">Stock: ${line.stock}</small></td><td>${line.cantidad}</td><td>${amount(line.precioUnitario)}</td><td>${line.descuentoPorcentaje}%</td><td>${amount(subtotal)}</td><td><button class="row-action danger" type="button" data-remove-line="${index}" aria-label="Quitar"><i class="bi bi-x-lg"></i></button></td></tr>`;
            });
            calculate();
        };
        const renderRecords = () => {
            const query = search.value.trim().toLocaleLowerCase("es");
            const filtered = sales.filter((sale) =>
                `${sale.idVenta} ${sale.nombreCliente || ""} ${sale.documentoCliente || ""} ${sale.estado}`.toLocaleLowerCase("es").includes(query));
            recordsTable.innerHTML = filtered.map((sale) => `<tr>
                <td><strong>#${sale.idVenta}</strong></td><td>${escapeHtml(sale.nombreCliente || "Venta mostrador")}<small class="d-block">${escapeHtml(sale.documentoCliente || "Sin documento")}</small></td>
                <td>${dateLabel(sale.fechaVenta)}</td><td>${escapeHtml(sale.metodoPago)}</td><td>${badge(sale.estado)}</td><td class="text-end">${amount(sale.total)}</td></tr>`).join("");
            document.getElementById("emptyState").classList.toggle("d-none", filtered.length > 0);
            recordsTable.closest(".table-responsive").classList.toggle("d-none", filtered.length === 0);
        };

        try {
            [users, products, sales] = await Promise.all([
                request(api.usuarios), request(api.productos), request(api.ventas)
            ]);
            setSelectOptions(userSelect, users, "idUsuario", (item) => `${item.nombre} ${item.apellido}`, (item) => item.estado === "Activo");
            setSelectOptions(productSelect, products, "idProducto",
                (item) => `${item.nombre} · ${item.stock} disponibles`, (item) => item.estado === "Disponible" && item.stock > 0);
            renderRecords();
        } catch (error) {
            notify(error.message, "error");
        }
        productSelect.addEventListener("change", () => {
            const product = products.find((item) => String(item.idProducto) === productSelect.value);
            document.getElementById("salePrice").value = product?.precioVenta ?? "";
        });
        document.getElementById("addSaleLine").addEventListener("click", () => {
            const product = products.find((item) => String(item.idProducto) === productSelect.value);
            const cantidad = integer(document.getElementById("saleQuantity").value);
            const precioUnitario = decimal(document.getElementById("salePrice").value);
            const descuentoPorcentaje = decimal(document.getElementById("saleDiscount").value);
            const existing = lines.items.filter((line) => line.idProducto === product?.idProducto)
                .reduce((sum, line) => sum + line.cantidad, 0);
            if (!product || !cantidad || cantidad <= 0 || precioUnitario == null || precioUnitario < 0
                    || descuentoPorcentaje == null || descuentoPorcentaje < 0 || descuentoPorcentaje > 100) {
                notify("Selecciona un producto e indica cantidad, precio y descuento válidos.", "error");
                return;
            }
            if (cantidad + existing > product.stock) {
                notify(`Solo hay ${product.stock - existing} unidades disponibles de ${product.nombre}.`, "error");
                return;
            }
            lines.items.push({ idProducto: product.idProducto, nombre: product.nombre, stock: product.stock, cantidad, precioUnitario, descuentoPorcentaje });
            renderLines();
        });
        linesTable.addEventListener("click", () => window.setTimeout(renderLines, 0));
        search.addEventListener("input", renderRecords);
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            if (!lines.items.length) { notify("Añade al menos una fragancia a la venta.", "error"); return; }
            const quantities = new Map();
            lines.items.forEach((line) => quantities.set(line.idProducto, (quantities.get(line.idProducto) || 0) + line.cantidad));
            const stockError = [...quantities.entries()].find(([id, quantity]) => quantity > products.find((product) => product.idProducto === id)?.stock);
            if (stockError) { notify("Las existencias cambiaron. Actualiza el catálogo antes de confirmar.", "error"); return; }
            const button = form.querySelector('[type="submit"]');
            button.disabled = true;
            try {
                const values = formValues(form);
                const created = await request("/api/operaciones/ventas", {
                    method: "POST",
                    body: JSON.stringify({
                        idUsuario: integer(values.idUsuario),
                        nombreCliente: nullable(values.nombreCliente),
                        documentoCliente: nullable(values.documentoCliente),
                        telefonoCliente: nullable(values.telefonoCliente),
                        metodoPago: values.metodoPago,
                        observaciones: nullable(values.observaciones),
                        lineas: lines.items.map(({ idProducto, cantidad, precioUnitario, descuentoPorcentaje }) => ({
                            idProducto, cantidad, precioUnitario, descuentoPorcentaje
                        }))
                    })
                });
                notify(`Venta #${created.idVenta} registrada por ${amount(created.total)}.`);
                form.reset();
                lines.items.splice(0, lines.items.length);
                renderLines();
                [products, sales] = await Promise.all([request(api.productos), request(api.ventas)]);
                setSelectOptions(productSelect, products, "idProducto",
                    (item) => `${item.nombre} · ${item.stock} disponibles`, (item) => item.estado === "Disponible" && item.stock > 0);
                renderRecords();
            } catch (error) {
                notify(error.message, "error");
            } finally {
                button.disabled = false;
            }
        });
    }

    async function setupReturns() {
        const form = document.getElementById("returnForm");
        const detailsSelect = document.getElementById("returnDetail");
        const userSelect = document.getElementById("returnUser");
        const recordsTable = document.getElementById("recordsTable");
        const search = document.getElementById("tableSearch");
        search.value = new URLSearchParams(window.location.search).get("q") || "";
        let details = [];
        let sales = [];
        let products = [];
        let returns = [];

        const renderRecords = () => {
            const query = search.value.trim().toLocaleLowerCase("es");
            const filtered = returns.filter((item) =>
                `${item.idDevolucion} ${item.motivo || ""} ${item.estado || ""}`.toLocaleLowerCase("es").includes(query));
            recordsTable.innerHTML = filtered.map((item) => {
                const detail = details.find((entry) => entry.idDetalleVenta === item.idDetalleVenta);
                const product = products.find((entry) => entry.idProducto === detail?.idProducto);
                return `<tr><td><strong>#${item.idDevolucion}</strong></td><td>#${escapeHtml(item.idDetalleVenta)}<small class="d-block">${escapeHtml(product?.nombre || "Detalle de venta")}</small></td><td>${dateLabel(item.fechaDevolucion)}</td><td>${escapeHtml(item.cantidad)}</td><td>${escapeHtml(item.motivo)}</td><td>${badge(item.estado)}</td><td>${escapeHtml(item.estadoProducto)}</td></tr>`;
            }).join("");
            document.getElementById("emptyState").classList.toggle("d-none", filtered.length > 0);
            recordsTable.closest(".table-responsive").classList.toggle("d-none", filtered.length === 0);
        };
        try {
            const [usersData, salesData, detailsData, productsData, returnsData] = await Promise.all([
                request(api.usuarios), request(api.ventas), request("/api/detalles-ventas"),
                request(api.productos), request(api.devoluciones)
            ]);
            details = detailsData;
            sales = salesData;
            products = productsData;
            returns = returnsData;
            setSelectOptions(userSelect, usersData, "idUsuario",
                (item) => `${item.nombre} ${item.apellido}`, (item) => item.estado === "Activo");
            const completedSales = new Set(sales.filter((sale) => sale.estado === "Completada").map((sale) => sale.idVenta));
            setSelectOptions(detailsSelect, details.filter((detail) => completedSales.has(detail.idVenta)), "idDetalleVenta", (detail) => {
                const sale = sales.find((item) => item.idVenta === detail.idVenta);
                const product = products.find((item) => item.idProducto === detail.idProducto);
                return `Venta #${detail.idVenta} · ${product?.nombre || "Producto"} · ${detail.cantidad} uds. · ${sale?.nombreCliente || "Mostrador"}`;
            });
            renderRecords();
        } catch (error) {
            notify(error.message, "error");
        }
        search.addEventListener("input", renderRecords);
        form.addEventListener("submit", async (event) => {
            event.preventDefault();
            if (!form.reportValidity()) return;
            const button = form.querySelector('[type="submit"]');
            button.disabled = true;
            try {
                const values = formValues(form);
                const created = await request(api.devoluciones, {
                    method: "POST",
                    body: JSON.stringify({
                        idDevolucion: null,
                        idDetalleVenta: integer(values.idDetalleVenta),
                        idUsuario: integer(values.idUsuario),
                        fechaDevolucion: null,
                        cantidad: integer(values.cantidad),
                        motivo: values.motivo.trim(),
                        estado: values.estado,
                        estadoProducto: values.estadoProducto,
                        observaciones: nullable(values.observaciones)
                    })
                });
                notify(`Devolución #${created.idDevolucion} registrada correctamente.`);
                form.reset();
                returns = await request(api.devoluciones);
                renderRecords();
            } catch (error) {
                notify(error.message, "error");
            } finally {
                button.disabled = false;
            }
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        setupSidebar();
        const module = document.querySelector("[data-module]")?.dataset.module;
        if (["usuarios", "productos", "proveedores"].includes(module)) {
            setupEntityPage(module);
        } else if (module === "compras") {
            setupPurchases();
        } else if (module === "ventas") {
            setupSales();
        } else if (module === "devoluciones") {
            setupReturns();
        }
    });
})();
